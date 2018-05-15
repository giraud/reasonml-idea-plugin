package com.reason.ide;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptManager;
import com.reason.ide.files.CmiFileType;
import com.reason.ide.files.CmtFileType;
import com.reason.ide.files.DuneFileType;
import com.reason.ide.hints.CmtiFileListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener that detects all modifications on project files
 */
class VirtualFileListener implements com.intellij.openapi.vfs.VirtualFileListener {

    private final Bucklescript m_bucklescript;
    private final CmtiFileListener m_cmtiFileListener;

    VirtualFileListener(Project project) {
        m_bucklescript = BucklescriptManager.getInstance(project);
        m_cmtiFileListener = CmtiFileListener.getInstance(project);
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
        } else if (fileType instanceof DuneFileType) {
            // OCaml SDK mandatory
        } else if (fileType instanceof CmiFileType) {
            m_cmtiFileListener.onChange(file);
        } else if (fileType instanceof CmtFileType) {
            m_cmtiFileListener.onChange(file);
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
