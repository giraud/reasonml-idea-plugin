package com.reason.ide;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.reason.hints.InsightManager;
import com.reason.hints.InsightManagerImpl;
import com.reason.ide.files.CmtFileType;
import com.reason.ide.hints.CmtiFileListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener that detects all modifications on project files
 */
class ORVirtualFileListener implements VirtualFileListener {

    private final CmtiFileListener m_cmtiFileListener;
    private final InsightManager m_insightManager;

    ORVirtualFileListener(@NotNull Project project) {
        m_cmtiFileListener = CmtiFileListener.getInstance(project);
        m_insightManager = InsightManagerImpl.getInstance(project);
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
//                if (m_compiler != null) {
//                    m_compiler.refresh(file);
//                }

                m_insightManager.downloadRincewindIfNeeded();
            }
        } else if (fileType instanceof CmtFileType) {
            m_cmtiFileListener.onChange(file);
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
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
