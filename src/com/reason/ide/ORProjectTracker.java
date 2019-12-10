package com.reason.ide;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;

public class ORProjectTracker implements ProjectComponent {

    private final Project m_project;
    @Nullable
    private MessageBusConnection m_messageBusConnection;
    @Nullable
    private ORVirtualFileListener m_vfListener;
    @Nullable
    private ORFileEditorListener m_fileEditorListener;

    protected ORProjectTracker(Project project) {
        m_project = project;
    }

    @Override
    public void projectOpened() {
        m_messageBusConnection = m_project.getMessageBus().connect(m_project);

        m_fileEditorListener = new ORFileEditorListener(m_project);
        m_messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, m_fileEditorListener);

        // ZZZ USE com.intellij.openapi.vfs.AsyncFileListener
        m_vfListener = new ORVirtualFileListener(m_project);
        VirtualFileManager.getInstance().addVirtualFileListener(m_vfListener);
    }

    @Override
    public void projectClosed() {
        if (m_vfListener != null) {
            VirtualFileManager.getInstance().removeVirtualFileListener(m_vfListener);
            m_vfListener = null;
        }
        if (m_messageBusConnection != null) {
            m_messageBusConnection.disconnect();
            m_messageBusConnection = null;
        }
    }

    public boolean isOpen(@NotNull VirtualFile file) {
        return m_fileEditorListener != null && m_fileEditorListener.isOpen(file);
    }
}
