package com.reason.ide;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.Nullable;

public class ORProjectTracker implements ProjectComponent {

    private final Project m_project;
    @Nullable
    private ORVirtualFileListener m_vfListener;

    protected ORProjectTracker(Project project) {
        m_project = project;
    }

    @Override
    public void projectOpened() {
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
    }

}
