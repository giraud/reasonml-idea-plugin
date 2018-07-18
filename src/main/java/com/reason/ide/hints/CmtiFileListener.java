package com.reason.ide.hints;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.FileManager;
import com.reason.Platform;
import com.reason.hints.InsightManager;
import com.reason.ide.ReasonProjectTracker;
import com.reason.ide.files.CmiFileType;
import com.reason.ide.files.CmtFileType;
import com.reason.ide.sdk.OCamlSDK;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CmtiFileListener implements ProjectComponent {

    private final Logger m_log;
    private final Project m_project;
    private final InsightManager m_insightManager;
    private final ReasonProjectTracker m_projectTracker;

    public static CmtiFileListener getInstance(Project project) {
        return project.getComponent(CmtiFileListener.class);
    }

    private CmtiFileListener(Project project) {
        m_log = Logger.getInstance("ReasonML.vfs");
        m_project = project;
        m_insightManager = project.getComponent(InsightManager.class);
        m_projectTracker = project.getComponent(ReasonProjectTracker.class);
    }

    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }

    public void onChange(VirtualFile file) {
        boolean useCmt = m_insightManager.useCmt();
        if (useCmt && file.getFileType() instanceof CmiFileType) {
            return;
        }
        if (!useCmt && file.getFileType() instanceof CmtFileType) {
            return;
        }

        Path path = FileSystems.getDefault().getPath(file.getPath());
        Path relativeCmti;

        Sdk projectSDK = OCamlSDK.getSDK(m_project);
        if (projectSDK == null) {
            Path relativeRoot = FileSystems.getDefault().getPath("lib", "bs");
            Path pathToWatch = getPathToWatch(m_project, relativeRoot);
            relativeCmti = pathToWatch.relativize(path);
        } else {
            Path relativeRoot = FileSystems.getDefault().getPath("_build", "default");
            Path pathToWatch = getPathToWatch(m_project, relativeRoot);
            relativeCmti = pathToWatch.relativize(path);
        }

        m_log.info("Detected change on file " + relativeCmti + ", reading types");

        VirtualFile sourceFile = FileManager.toSource(m_project, file, relativeCmti);
        if (sourceFile == null) {
            m_log.warn("can't convert " + relativeCmti + " to " + FileManager.toRelativeSourceName(m_project, file, relativeCmti));
        } else if (m_projectTracker.isOpen(sourceFile)) {
            m_insightManager.queryTypes(file, path, inferredTypes -> InferredTypesService.annotateFile(m_project, inferredTypes, sourceFile));
        }
    }

    @NotNull
    private Path getPathToWatch(@NotNull Project project, Path relativeRoot) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        return basePath.resolve(relativeRoot);
    }
}
