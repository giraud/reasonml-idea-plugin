package com.reason.hints;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Log;
import com.reason.build.bs.compiler.BsProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.reason.Platform.getOsPrefix;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    private static final Log LOG = Log.create("hints.insight");

    @NotNull
    public AtomicBoolean isDownloaded = new AtomicBoolean(false);
    @NotNull
    AtomicBoolean isDownloading = new AtomicBoolean(false);

    private final Project m_project;

    private InsightManagerImpl(@NotNull Project project) {
        m_project = project;
    }

    public static InsightManager getInstance(Project project) {
        return project.getComponent(InsightManager.class);
    }

    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }

    @Override
    public boolean useCmt() {
        return isDownloaded.get();
    }

    @Override
    public void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile) {
        if (!isDownloaded.get()) {
            File rincewind = getRincewindFile(sourceFile);
            if (rincewind == null || !rincewind.exists()) {
                if (!m_project.isDisposed()) {
                    LOG.debug("Downloading rincewind in background");
                    ProgressManager.getInstance().run(RincewindDownloader.getInstance(m_project, sourceFile));
                }
            } else {
                isDownloaded.compareAndSet(false, true);
            }
        }
    }

    @Nullable
    @Override
    public File getRincewindFile(@NotNull VirtualFile sourceFile) {
        String filename = getRincewindFilename(sourceFile);
        if (filename == null) {
            return null;
        }

        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
        if (plugin != null) {
            return new File(plugin.getPath(), filename);
        }

        return new File(System.getProperty("java.io.tmpdir"), filename);
    }

    @Nullable
    @Override
    public String getRincewindFilename(@NotNull VirtualFile sourceFile) {
        String ocamlVersion = BsProcess.getInstance(m_project).getOCamlVersion(sourceFile);
        return ocamlVersion == null ? null : "rincewind_" + getOsPrefix() + ocamlVersion + "-" + getAppVersion(ocamlVersion) + ".exe";
    }

    @Override
    public void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ProcessTerminated runAfter) {
        if (isDownloaded.get()) {
            File rincewindFile = getRincewindFile(sourceFile);
            LOG.debug("rincewind", rincewindFile);
            if (rincewindFile != null) {
                RincewindProcess.getInstance(m_project).types(sourceFile, rincewindFile.getPath(), cmtPath.toString(), runAfter);
            }
        }
    }

    private String getAppVersion(@NotNull String ocamlVersion) {
        switch (ocamlVersion) {
            case "4.02":
                return "0.4";
            default:
                return "0.5-dev";
        }
    }
}
