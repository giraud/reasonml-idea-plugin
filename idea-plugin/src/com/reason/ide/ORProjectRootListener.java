package com.reason.ide;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbModeTask;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.util.messages.MessageBusConnection;
import com.reason.dune.OpamEnv;
import com.reason.sdk.OCamlSdkType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.ProjectTopics.PROJECT_ROOTS;

// see AndroidProjectRootListener
public class ORProjectRootListener implements @NotNull Disposable {
    private final @NotNull MessageBusConnection m_messageBusConnection;

    public static void ensureSubscribed(@NotNull Project project) {
        ServiceManager.getService(project, ORProjectRootListener.class);
    }

    private ORProjectRootListener(@NotNull Project project) {
        m_messageBusConnection = project.getMessageBus().connect(this);
        m_messageBusConnection.subscribe(PROJECT_ROOTS, new ModuleRootListener() {
            @Override
            public void rootsChanged(@NotNull ModuleRootEvent event) {
                DumbService.getInstance(project).queueTask(
                        new DumbModeTask(project) {
                            @Override
                            public void performInDumbMode(@NotNull ProgressIndicator indicator) {
                                if (!project.isDisposed()) {
                                    // Event fired each time a SDK is updated
                                    ProjectSdksModel model = new ProjectSdksModel();
                                    model.reset(project);
                                    OpamEnv opamEnv = ServiceManager.getService(project, OpamEnv.class);
                                    for (Sdk sdk : model.getSdks()) {
                                        if (sdk.getSdkType() instanceof OCamlSdkType) {
                                            opamEnv.computeEnv(sdk, null);
                                        }
                                    }

                                    indicator.setText("Updating resource repository roots");
                                    // should be done per module
                                    // ModuleManager moduleManager = ModuleManager.getInstance(project);
                                    // for (Module module : moduleManager.getModules()) {
                                    //    moduleRootsOrDependenciesChanged(module);
                                    // }
                                    Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
                                    if (projectSdk != null && projectSdk.getSdkType() instanceof OCamlSdkType) {
                                        OCamlSdkType.reindexSourceRoots(projectSdk);
                                    }
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void dispose() {
        m_messageBusConnection.disconnect();
    }
}
