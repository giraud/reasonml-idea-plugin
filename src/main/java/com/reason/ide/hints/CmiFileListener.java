package com.reason.ide.hints;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.insight.InsightManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CmiFileListener implements ProjectComponent {

    private final Logger m_log;
    private final Project m_project;
    private final Path m_pathToWatch;
    private final InsightManager m_insightManager;

    public static CmiFileListener getInstance(Project project) {
        return project.getComponent(CmiFileListener.class);
    }

    private CmiFileListener(Project project) {
        m_log = Logger.getInstance("ReasonML.vfs");
        m_project = project;
        m_pathToWatch = getPathToWatch(project, "lib/bs");
        m_insightManager = project.getComponent(InsightManager.class);
    }

    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }

    public void onChange(VirtualFile file) {
        Path path = FileSystems.getDefault().getPath(file.getPath());
        Path relativeCmi;

        //Sdk projectSDK = ProjectRootManager.getInstance(m_project).getProjectSdk();
        //if (projectSDK != null && projectSDK.getSdkType().getName().equals("OCaml SDK")) {
        //    Path pathToWatch = getPathToWatch(m_project, "_build/default");
        //    relativeCmi = pathToWatch.relativize(path);
        //} else {
        relativeCmi = m_pathToWatch.relativize(path);
        //}

        m_log.info("Detected change on file " + relativeCmi + ", reading types");

        VirtualFile sourceFile = CmiFileManager.toSource(m_project, relativeCmi);
        if (sourceFile == null) {
            m_log.warn("can't convert " + relativeCmi + " to " + CmiFileManager.toRelativeSourceName(m_project, relativeCmi));
        } else {
            m_insightManager.queryTypes(path, inferredTypes -> InferredTypesService.annotateFile(m_project, inferredTypes, sourceFile));
        }
    }

    @NotNull
    private Path getPathToWatch(@NotNull Project project, String base) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        return basePath.resolve(base);
    }
}
