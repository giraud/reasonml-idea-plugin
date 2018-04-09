package com.reason.ide.hints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import io.methvin.watcher.DirectoryWatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class CmiDirectoryWatcher {

    private DirectoryWatcher m_watcher;

    private CmiDirectoryWatcher(@NotNull Project project) {
        Logger log = Logger.getInstance("ReasonML.types");

        try {
            createWatcher(project, log);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void createWatcher(@NotNull Project project, Logger log) throws IOException {
        Path pathToWatch = getPathToWatch(project);
        if (pathToWatch != null) {
            m_watcher = DirectoryWatcher.create(pathToWatch, new CmiDirectoryChangeListener(project, pathToWatch));
            log.info("Watch directory " + pathToWatch + " for .cmi modifications");

            CompletableFuture<Void> voidCompletableFuture = m_watcher.watchAsync();
            voidCompletableFuture.thenAccept(aVoid -> {
                // When 'clean make', the lib/bs directory is removed and the watcher is stopped
                log.info("Directory watcher ends! A new one is re-created");
                try {
                    createWatcher(project, log);
                } catch (IOException e) {
                    log.error(e);
                }
            });
        }
    }

    @NotNull
    public static CmiDirectoryWatcher start(@NotNull Project project) {
        return new CmiDirectoryWatcher(project);
    }

    public void close() {
        try {
            if (m_watcher != null) {
                m_watcher.close();
                m_watcher = null;
            }
        } catch (IOException e) {
            // can't do anything
        }
    }

    @Nullable
    private Path getPathToWatch(@NotNull Project project) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getCanonicalPath());
        Path pathToWatch = basePath.resolve("lib/bs");
        if (!pathToWatch.toFile().exists()) {
            boolean mkdirs = pathToWatch.toFile().mkdirs();
            if (!mkdirs) {
                return null;
            }
        }
        return pathToWatch;
    }
}
