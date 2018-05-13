package com.reason.insight;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.ModuleConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    private static final String OCAML_VERSION = "4.02";
    private static final String RINCEWIND_VERSION = "0.2";
    public static final String DOWNLOAD_URL = "https://dl.bintray.com/giraud/ocaml/";

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

    @NotNull
    @Override
    public String getRincewindFilename(@NotNull String osPrefix) {
        return "rincewind_" + osPrefix + OCAML_VERSION + "-" + RINCEWIND_VERSION + ".exe";
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
