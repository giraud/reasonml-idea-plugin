package com.reason.bs;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.Platform;
import com.reason.bs.annotations.BsErrorsManager;
import com.reason.bs.annotations.BsErrorsManagerImpl;
import com.reason.bs.console.BsConsole;
import com.reason.bs.hints.BsQueryTypesService;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import com.reason.ide.RmlNotification;
import com.reason.ide.files.OclFileType;
import com.reason.ide.files.RmlFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Collection;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public class BucklescriptProjectComponent implements Bucklescript, ProjectComponent {

    private final Project m_project;
    @Nullable
    private BsConfig m_config;
    @Nullable
    private BsCompiler m_compiler;
    @Nullable
    private BsQueryTypesServiceComponent m_queryTypes;
    @Nullable
    private BsErrorsManagerImpl m_errorsManager;

    private BucklescriptProjectComponent(Project project) {
        m_project = project;
    }

    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }

    /**
     * Returns the bucklescript instance for the specified project.
     *
     * @param project the project for which the bucklescript is requested.
     * @return the bucklescript instance.
     */
    public static Bucklescript getInstance(Project project) {
        return project.getComponent(Bucklescript.class);
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "reason.bucklescript";
    }

    @Override
    public void projectOpened() {
        VirtualFile baseDir = Platform.findBaseRoot(m_project);
        VirtualFile bsconfig = baseDir.findChild("bsconfig.json");

        boolean disabled = Boolean.getBoolean("reasonBsbDisabled");
        if (disabled) {
            // But you should NEVER do that
            Notifications.Bus.notify(new RmlNotification("Bsb", "Bucklescript is disabled", NotificationType.WARNING));
            return;
        }

        if (bsconfig != null) {
            m_config = BsConfig.read(bsconfig);
            String bsbBin = Platform.getBinary("REASON_BSB_BIN", "reasonBsb", "node_modules/bs-platform/lib/bsb.exe");
            String bsbPath = Platform.getBinaryPath(m_project, bsbBin);

            if (bsbPath == null) {
                bsbPath = Platform.getBinaryPath(m_project, "node_modules/bs-platform/bin/bsb.exe");
            }

            if (bsbPath == null) {
                Notifications.Bus.notify(new RmlNotification("Bsb",
                        "<html>Can't find bsb using value '" + bsbBin + "' from property 'reasonBsb'.\n"
                                + "Base directory is '" + baseDir.getCanonicalPath() + "'.\n"
                                + "Be sure that bsb is installed and reachable from base directory, "
                                + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#bucklescript\">github</a>.</html>",
                        ERROR, URL_OPENING_LISTENER));
            } else {
                m_compiler = new BsCompiler(baseDir, bsbPath);
                m_queryTypes = new BsQueryTypesServiceComponent(baseDir, bsbPath.replace("bsb.exe", "bsc.exe"));
                m_errorsManager = new BsErrorsManagerImpl();
            }
        }
    }

    @Override
    public void projectClosed() {
        if (m_compiler != null) {
            m_compiler.killIt();
        }
        m_config = null;
        m_compiler = null;
        m_queryTypes = null;
        m_errorsManager = null;
    }

    @Nullable
    @Override
    public BsCompiler getCompiler() {
        return m_compiler;
    }

    @Nullable
    @Override
    public BsCompiler getOrCreateCompiler() {
        if (m_compiler == null) {
            // Try again
            projectOpened();
        }

        return m_compiler;
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

    @Nullable
    @Override
    public Collection<BsErrorsManager.BsbInfo> getErrors(String path) {
        return m_errorsManager == null ? null : m_errorsManager.getErrors(path);
    }

    @Override
    public void clearErrors() {
        if (m_errorsManager != null) {
            m_errorsManager.clearErrors();
        }
    }

    @Override
    public void setError(String path, BsErrorsManager.BsbInfo error) {
        if (m_errorsManager != null) {
            m_errorsManager.setError(path, error);
        }
    }

    @Override
    public void addAllInfo(@NotNull String fileProcessed, @NotNull Iterable<BsErrorsManager.BsbInfo> bsbInfo) {
        for (BsErrorsManager.BsbInfo info : bsbInfo) {
            m_errorsManager.setError(fileProcessed, info);
        }
    }

    @NotNull
    @Override
    public String getNamespace() {
        return m_config == null ? "" : m_config.getNamespace();
    }

    @Override
    public void run(FileType fileType) {
        if (m_compiler != null && (fileType instanceof RmlFileType || fileType instanceof OclFileType)) {
            ProcessHandler recreate = m_compiler.recreate(CliType.standard);
            if (recreate != null) {
                getBsbConsole().attachToProcess(recreate);
                m_compiler.startNotify();
            }
        }
    }

    @Override
    public boolean isDependency(@Nullable String path) {
        return m_config == null || m_config.accept(path);
    }

    @Override
    public void refresh() {
        VirtualFile bsconfig = Platform.findBaseRoot(m_project).findChild("bsconfig.json");
        if (bsconfig != null) {
            m_config = BsConfig.read(bsconfig);
        }
    }

    private BsConsole getBsbConsole() {
        BsConsole console = null;

        ToolWindow window = ToolWindowManager.getInstance(m_project).getToolWindow("Bucklescript");
        Content windowContent = window.getContentManager().getContent(0);
        if (windowContent != null) {
            SimpleToolWindowPanel component = (SimpleToolWindowPanel) windowContent.getComponent();
            JComponent panelComponent = component.getComponent();
            if (panelComponent != null) {
                console = (BsConsole) panelComponent.getComponent(0);
            }
        }

        return console;
    }

}
