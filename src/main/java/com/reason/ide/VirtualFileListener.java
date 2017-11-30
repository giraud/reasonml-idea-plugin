package com.reason.ide;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Listener that detects all modifications on project files
 */
class VirtualFileListener implements com.intellij.openapi.vfs.VirtualFileListener {

    private final Bucklescript m_bucklescript;

    VirtualFileListener(Project project) {
        m_bucklescript = BucklescriptProjectComponent.getInstance(project);
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {

    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        FileType fileType = file.getFileType();

        if (fileType instanceof JsonFileType) {
            if (file.getName().equals("bsconfig.json")) {
                m_bucklescript.refresh();
            }
        } else if (event.isFromSave()) {
            m_bucklescript.run(fileType);
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        m_bucklescript.run(event.getFile().getFileType());
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        m_bucklescript.run(event.getFile().getFileType());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        m_bucklescript.run(event.getFile().getFileType());
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        m_bucklescript.run(event.getFile().getFileType());
    }

    @Override
    public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {
    }

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event) {
    }

    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
    }

    @Override
    public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {
    }
}
