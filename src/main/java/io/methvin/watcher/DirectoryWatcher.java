/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.methvin.watcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import com.google.common.hash.HashCode;
import com.sun.nio.file.ExtendedWatchEventModifier;
import io.methvin.watcher.DirectoryChangeEvent.EventType;
import io.methvin.watchservice.MacOSXListeningWatchService;
import io.methvin.watchservice.WatchablePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class DirectoryWatcher {

  static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

  private final WatchService watchService;
  private final List<Path> paths;
  private final boolean isMac;
  private final DirectoryChangeListener listener;
  private final Map<Path, HashCode> pathHashes;
  private final Map<WatchKey, Path> keyRoots;

  // this is set to true/false depending on whether recursive watching is supported natively
  private Boolean fileTreeSupported = null;

  public static DirectoryWatcher create(Path path, DirectoryChangeListener listener) throws IOException {
    return create(Collections.singletonList(path), listener);
  }

  public static DirectoryWatcher create(List<Path> paths, DirectoryChangeListener listener) throws IOException {
    boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
    WatchService ws = isMac ? new MacOSXListeningWatchService() : FileSystems.getDefault().newWatchService();
    return new DirectoryWatcher(paths, listener, ws);
  }

  public DirectoryWatcher(List<Path> paths, DirectoryChangeListener listener, WatchService watchService) throws IOException {
    this.paths = paths;
    this.listener = listener;
    this.watchService = watchService;
    this.isMac = watchService instanceof MacOSXListeningWatchService;
    this.pathHashes = PathUtils.createHashCodeMap(paths);
    this.keyRoots = PathUtils.createKeyRootsMap();

    for (Path path : paths) {
      registerAll(path);
    }
  }

  /**
   * Asynchronously watch the directories using ForkJoinPool.commonPool() as the executor
   */
  public CompletableFuture<Void> watchAsync() {
    return watchAsync(ForkJoinPool.commonPool());
  }

  /**
   * Asynchronously watch the directories.
   *
   * @param executor the executor to use to watch asynchronously
   */
  public CompletableFuture<Void> watchAsync(Executor executor) {
    return CompletableFuture.supplyAsync(() -> {
      watch();
      return null;
    }, executor);
  }

  /**
   * Watch the directories. Block until either the listener stops watching or the DirectoryWatcher is closed.
   */
  public void watch() {
    for (;;) {
      if (!listener.isWatching()) {
        return;
      }
      // wait for key to be signalled
      WatchKey key;
      try {
        key = watchService.take();
      } catch (InterruptedException x) {
        return;
      }
      for (WatchEvent<?> event : key.pollEvents()) {
        try {
          WatchEvent.Kind<?> kind = event.kind();
          // Context for directory entry event is the file name of entry
          WatchEvent<Path> ev = PathUtils.cast(event);
          int count = ev.count();
          Path eventPath = ev.context();
          if (!keyRoots.containsKey(key)) {
            throw new IllegalStateException(
                "WatchService returned key [" + key + "] but it was not found in keyRoots!");
          }
          Path childPath = eventPath == null ? null : keyRoots.get(key).resolve(eventPath);
          logger.debug("{} [{}]", kind, childPath);
          // if directory is created, and watching recursively, then register it and its sub-directories
          if (kind == OVERFLOW) {
            listener.onEvent(new DirectoryChangeEvent(EventType.OVERFLOW, childPath, count));
          } else if (eventPath == null) {
            throw new IllegalStateException("WatchService returned a null path for " + kind.name());
          } else if (kind == ENTRY_CREATE) {
            if (Files.isDirectory(childPath, NOFOLLOW_LINKS)) {
              if (!Boolean.TRUE.equals(fileTreeSupported)) {
                registerAll(childPath);
              }
              // Our custom Mac service sends subdirectory changes but the Windows/Linux do not.
              // Walk the file tree to make sure we send create events for any files that were created.
              if (!isMac) {
                Files.walkFileTree(childPath, new SimpleFileVisitor<Path>() {
                  @Override
                  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    notifyCreateEvent(dir, count);
                    return FileVisitResult.CONTINUE;
                  }

                  @Override
                  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    notifyCreateEvent(file, count);
                    return FileVisitResult.CONTINUE;
                  }

                });
              }
            }
            notifyCreateEvent(childPath, count);
          } else if (kind == ENTRY_MODIFY) {
            // Note that existingHash may be null due to the file being created before we start listening
            // It's important we don't discard the event in this case
            HashCode existingHash = pathHashes.get(childPath);

            // newHash can be null when using File#delete() on windows - it generates MODIFY and DELETE in succession
            // in this case the MODIFY event can be safely ignored
            HashCode newHash = PathUtils.hash(childPath);

            if (newHash != null && !newHash.equals(existingHash)) {
              pathHashes.put(childPath, newHash);
              listener.onEvent(new DirectoryChangeEvent(EventType.MODIFY, childPath, count));
            } else if (newHash == null) {
              logger.debug("Failed to hash modified file [{}]. It may have been deleted.", childPath);
            }
          } else if (kind == ENTRY_DELETE) {
            pathHashes.remove(childPath);
            listener.onEvent(new DirectoryChangeEvent(EventType.DELETE, childPath, count));
          }
        } catch (Exception e) {
          listener.onException(e);
        }
      }
      boolean valid = key.reset();
      if (!valid) {
        logger.debug("WatchKey for [{}] no longer valid; removing.", key.watchable());
        // remove the key from the keyRoots
        keyRoots.remove(key);
        // if there are no more keys left to watch, we can break out
        if (keyRoots.isEmpty()) {
          logger.debug("No more directories left to watch; terminating watcher.");
          break;
        }
      }
    }
  }


  public DirectoryChangeListener getListener() {
    return listener;
  }

  public void close() throws IOException {
    watchService.close();
  }

  private void registerAll(final Path start) throws IOException {
    if (!Boolean.FALSE.equals(fileTreeSupported)) {
      // Try using FILE_TREE modifier since we aren't certain that it's unsupported
      try {
        register(start, true);
        // We didn't get an UnsupportedOperationException so assume FILE_TREE is supported
        fileTreeSupported = true;
      } catch (UnsupportedOperationException e) {
        // UnsupportedOperationException should only happen if FILE_TREE is unsupported
        logger.debug("Assuming ExtendedWatchEventModifier.FILE_TREE is not supported", e);
        fileTreeSupported = false;
        // If we failed to use the FILE_TREE modifier, try again without
        registerAll(start);
      }
    } else {
      // Since FILE_TREE is unsupported, register root directory and sub-directories
      Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          register(dir, false);
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }

  // Internal method to be used by registerAll
  private void register(Path directory, boolean useFileTreeModifier) throws IOException {
    logger.debug("Registering [{}].", directory);
    Watchable watchable = isMac ? new WatchablePath(directory) : directory;
    WatchEvent.Modifier[] modifiers = useFileTreeModifier
        ? new WatchEvent.Modifier[] {ExtendedWatchEventModifier.FILE_TREE}
        : new WatchEvent.Modifier[] {};
    WatchEvent.Kind<?>[] kinds = new WatchEvent.Kind<?>[] {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};
    WatchKey watchKey = watchable.register(watchService, kinds, modifiers);
    keyRoots.put(watchKey, directory);
  }

  private void notifyCreateEvent(Path path, int count) throws IOException {
    HashCode newHash = PathUtils.hash(path);
    if (newHash == null) {
      logger.debug("Failed to hash created file [{}]. It may have been deleted.", path);
      return;
    }
    // Notify for the file create if not already notified
    if (!pathHashes.containsKey(path)) {
      logger.debug("{} [{}]", EventType.CREATE, path);
      listener.onEvent(new DirectoryChangeEvent(EventType.CREATE, path, count));
      pathHashes.put(path, newHash);
    }
  }

}
