package com.reason.insight;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.ModuleConfiguration;
import com.reason.bs.hints.BsQueryTypesService;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    private final Project m_project;

    @Nullable
    private BsQueryTypesServiceComponent m_queryTypes;

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
        ModuleConfiguration moduleConfiguration = new ModuleConfiguration(m_project);
        m_queryTypes = new BsQueryTypesServiceComponent(moduleConfiguration);
    }

    @Override
    public void projectClosed() {
        m_queryTypes = null;
    }

    @Nullable
    @Override
    public BsQueryTypesService.InferredTypes queryTypes(@NotNull Path path) {
        return m_queryTypes == null ? null : m_queryTypes.types(path.toString());
    }

    @Nullable
    @Override
    public BsQueryTypesService.InferredTypes queryTypes(@NotNull VirtualFile file) {
        return m_queryTypes == null ? null : m_queryTypes.types(file);
    }

}
