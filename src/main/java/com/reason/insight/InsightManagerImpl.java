package com.reason.insight;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.ModuleConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    private final Project m_project;
    @Nullable
    private RincewindProcess m_rincewindProcess;

    private InsightManagerImpl(Project project) {
        m_project = project;
    }

    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }


    @Override
    public void projectOpened() {
        m_rincewindProcess = new RincewindProcess(new ModuleConfiguration(m_project));
    }

    @Override
    public void projectClosed() {
        m_rincewindProcess = null;
    }

    @Override
    public void queryTypes(@NotNull Path path, @NotNull ProcessTerminated runAfter) {
        if (m_rincewindProcess != null) {
            m_rincewindProcess.types(path.toString(), runAfter);
        }
    }

    @Override
    public void queryTypes(@NotNull VirtualFile file, @NotNull ProcessTerminated runAfter) {
        if (m_rincewindProcess != null) {
            m_rincewindProcess.types(file.getCanonicalPath(), runAfter);
        }
    }

}
