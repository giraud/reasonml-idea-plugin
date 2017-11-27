package com.reason.ide;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.bs.BsbCompiler;
import com.reason.bs.console.BsbConsole;
import com.reason.ide.files.OclFileType;
import com.reason.ide.files.RmlFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Listener that detects all modifications on project files
 */
class VirtualFileListener implements com.intellij.openapi.vfs.VirtualFileListener {
    private final Project m_project;
    private final BsbCompiler m_bsb;

    VirtualFileListener(Project project) {
        m_project = project;
        m_bsb = ServiceManager.getService(m_project, BsbCompiler.class);
    }

    private BsbConsole getBsbConsole() { // once for all ?
        BsbConsole console = null;

        ToolWindow window = ToolWindowManager.getInstance(m_project).getToolWindow("Bucklescript");
        Content windowContent = window.getContentManager().getContent(0);
        if (windowContent != null) {
            SimpleToolWindowPanel component = (SimpleToolWindowPanel) windowContent.getComponent();
            JComponent panelComponent = component.getComponent();
            if (panelComponent != null) {
                console = (BsbConsole) panelComponent.getComponent(0);
            }
        }

        return console;
    }

    private void runBsb(FileType eventFileType) {
        if (eventFileType instanceof RmlFileType || eventFileType instanceof OclFileType) {
            ProcessHandler recreate = m_bsb.recreate();
            if (recreate != null) {
                getBsbConsole().attachToProcess(recreate);
                m_bsb.startNotify();
            }
        }
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {

    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        if (event.isFromSave()) {
            runBsb(event.getFile().getFileType());
        }
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        runBsb(event.getFile().getFileType());

    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        runBsb(event.getFile().getFileType());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        runBsb(event.getFile().getFileType());
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        runBsb(event.getFile().getFileType());
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
