package com.reason.ide;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.*;
import com.reason.build.Compiler;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.bs.compiler.CliType;
import com.reason.build.dune.DuneManager;
import com.reason.hints.InsightManager;
import com.reason.hints.InsightManagerImpl;
import com.reason.ide.files.CmiFileType;
import com.reason.ide.files.CmtFileType;
import com.reason.ide.hints.CmtiFileListener;
import com.reason.ide.sdk.OCamlSDK;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Listener that detects all modifications on project files
 */
class VirtualFileListener implements com.intellij.openapi.vfs.VirtualFileListener {

    private final CmtiFileListener m_cmtiFileListener;
    private final InsightManager m_insightManager;

    @Nullable
    private final Compiler m_compiler;

    VirtualFileListener(@NotNull Project project) {
        m_cmtiFileListener = CmtiFileListener.getInstance(project);
        m_insightManager = InsightManagerImpl.getInstance(project);

        Sdk projectSDK = OCamlSDK.getSDK(project);
        if (projectSDK == null) {
            m_compiler = BucklescriptManager.getInstance(project);
        } else {
            m_compiler = DuneManager.getInstance(project);
        }
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
                if (m_compiler != null) {
                    m_compiler.refresh(file);
                }
                m_insightManager.downloadRincewindIfNeeded();
            }
            //} else if (fileType instanceof DuneFileType) {
            // OCaml SDK mandatory
        } else if (fileType instanceof CmiFileType) {
            m_cmtiFileListener.onChange(file);
        } else if (fileType instanceof CmtFileType) {
            m_cmtiFileListener.onChange(file);
        } else if (event.isFromSave() && m_compiler != null) {
            m_compiler.run(file, CliType.standard);
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        if (m_compiler != null) {
            m_compiler.run(event.getFile(), CliType.standard);
        }
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        //if (m_compiler != null) {
        //    m_compiler.run(event.getFile().getFileType());
        //}
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        if (m_compiler != null) {
            m_compiler.run(event.getFile(), CliType.standard);
        }
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        if (m_compiler != null) {
            m_compiler.run(event.getFile(), CliType.standard);
        }
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
