package com.reason.ide.hints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import io.methvin.watcher.DirectoryWatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CmiDirectoryWatcher {

    private DirectoryWatcher m_watcher;

    private CmiDirectoryWatcher(@NotNull Project project) {
        Logger log = Logger.getInstance("ReasonML.types");

        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getCanonicalPath());
        Path pathToWatch = basePath.resolve("lib/bs");

        try {
            m_watcher = DirectoryWatcher.create(pathToWatch, new CmiDirectoryChangeListener(project, pathToWatch));
            log.info("Watch directory " + pathToWatch + " for .cmi modifications");
            m_watcher.watchAsync();
        } catch (IOException e) {
            log.error(e);
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
}
