package com.reason.insight;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.ModuleConfiguration;
import com.reason.bs.insight.BsQueryTypesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.reason.Platform.getOsPrefix;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    public AtomicBoolean isDownloaded = new AtomicBoolean(false);

    private static final String OCAML_VERSION = "4.02";
    private static final String RINCEWIND_VERSION = "0.1";

    private final Project m_project;
    @Nullable
    private RincewindProcess m_rincewindProcess;
    @Nullable
    private BsQueryTypesService m_queryTypes;

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
        m_queryTypes = new BsQueryTypesService(moduleConfiguration);
    }

    @Override
    public void projectClosed() {
        m_rincewindProcess = null;
        m_queryTypes = null;
    }

    @Override
    public boolean useCmt() {
        return isDownloaded.get();
    }

    @NotNull
    @Override
    public File getRincewindFile() {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
        if (plugin != null) {
            String rincewindFilename = getRincewindFilename();
            return new File(plugin.getPath(), rincewindFilename);
        }

        return new File(System.getProperty("java.io.tmpdir"), getRincewindFilename());
    }

    @NotNull
    @Override
    public String getRincewindFilename() {
        return "rincewind_" + getOsPrefix() + OCAML_VERSION + "-" + RINCEWIND_VERSION + ".exe";
    }

    @Override
    public void queryTypes(@NotNull Path path, @NotNull ProcessTerminated runAfter) {
        if (m_rincewindProcess != null && isDownloaded.get()) {
            m_rincewindProcess.types(getRincewindFile().getPath(), path.toString(), runAfter);
        } else if (m_queryTypes != null) {
            m_queryTypes.types(path.toString(), runAfter);
        }
    }

    @Override
    public void queryTypes(@NotNull VirtualFile file, @NotNull ProcessTerminated runAfter) {
        if (m_rincewindProcess != null && isDownloaded.get()) {
            String canonicalPath = file.getCanonicalPath();
            if (canonicalPath != null) {
                m_rincewindProcess.types(getRincewindFile().getPath(), canonicalPath, runAfter);
            }
        } else if (m_queryTypes != null) {
            m_queryTypes.types(file, runAfter);
        }
    }

}
