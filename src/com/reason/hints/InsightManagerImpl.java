package com.reason.hints;


import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.build.bs.compiler.BsProcess;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.reason.Platform.getOsPrefix;

public class InsightManagerImpl implements InsightManager, ProjectComponent {

    @NotNull
    public AtomicBoolean isDownloaded = new AtomicBoolean(false);
    @NotNull
    AtomicBoolean isDownloading = new AtomicBoolean(false);

    private final Project m_project;

    private InsightManagerImpl(Project project) {
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
    public void downloadRincewindIfNeeded() {
        if (!isDownloaded.get()) {
            ProgressManager.getInstance().run(
                    new Task.Backgroundable(m_project, "Download Rincewind") {
                        @Override
                        public void run(@NotNull ProgressIndicator indicator) {
                            if (!getProject().isDisposed()) {
                                RincewindDownloader.getInstance(getProject());
                            }
                        }
                    });
        }
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
        String ocamlVersion = BsProcess.getInstance(m_project).getOCamlVersion();
        return "rincewind_" + getOsPrefix() + ocamlVersion + "-" + getAppVersion(ocamlVersion) + ".exe";
    }

    @Override
    public void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ProcessTerminated runAfter) {
        if (isDownloaded.get()) {
            RincewindProcess.getInstance(m_project).types(sourceFile, getRincewindFile().getPath(), cmtPath.toString(), runAfter);
        }
    }

    private String getAppVersion(String ocamlVersion) {
        switch (ocamlVersion) {
            case "4.02":
                return "0.4";
            default:
                return "0.5-dev";
        }
    }
}
