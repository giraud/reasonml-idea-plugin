package com.reason.insight;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.ModuleConfiguration;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    public AtomicBoolean isDownloaded = new AtomicBoolean(false);

    private static final String OCAML_VERSION = "4.02";
    private static final String RINCEWIND_VERSION = "0.2";

    private final Project m_project;
    @Nullable
    private RincewindProcess m_rincewindProcess;
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
        m_rincewindProcess = new RincewindProcess(moduleConfiguration);
        m_queryTypes = new BsQueryTypesServiceComponent(moduleConfiguration);
    }

    @Override
    public void projectClosed() {
        m_rincewindProcess = null;
        m_queryTypes = null;
    }

    @NotNull
    @Override
    public File getRincewindFile(@NotNull String osPrefix) {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
        if (plugin != null) {
            String rincewindFilename = getRincewindFilename(osPrefix);
            return new File(plugin.getPath(), rincewindFilename);
        }

        return new File(System.getProperty("java.io.tmpdir"), getRincewindFilename(osPrefix));
    }

    @NotNull
    @Override
    public String getRincewindFilename(@NotNull String osPrefix) {
        return "rincewind_" + osPrefix + OCAML_VERSION + "-" + RINCEWIND_VERSION + ".exe";
    }

    @Override
    public void queryTypes(@NotNull Path path, @NotNull ProcessTerminated runAfter) {
        if (m_rincewindProcess != null && isDownloaded.get()) {
            m_rincewindProcess.types(path.toString(), runAfter);
        } else if (m_queryTypes != null) {
            m_queryTypes.types(path.toString());
        }
    }

    @Override
    public void queryTypes(@NotNull VirtualFile file, @NotNull ProcessTerminated runAfter) {
        if (m_rincewindProcess != null && isDownloaded.get()) {
            m_rincewindProcess.types(file.getCanonicalPath(), runAfter);
        } else if (m_queryTypes != null) {
            m_queryTypes.types(file);
        }
    }

}
