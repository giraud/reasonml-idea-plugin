package com.reason.ide;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbModeTask;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.reason.sdk.OCamlSdkType;

import static com.intellij.ProjectTopics.PROJECT_ROOTS;

// see AndroidProjectRootListener
public class ORProjectRootListener {
    public static void ensureSubscribed(@NotNull Project project) {
        ServiceManager.getService(project, ORProjectRootListener.class);
    }

    private ORProjectRootListener(@NotNull Project project) {
        project.getMessageBus().
                connect(project).
                subscribe(PROJECT_ROOTS, new ModuleRootListener() {
                    @Override
                    public void rootsChanged(@NotNull ModuleRootEvent event) {
                        DumbService.getInstance(project).queueTask(new DumbModeTask() {
                            @Override
                            public void performInDumbMode(@NotNull ProgressIndicator indicator) {
                                if (!project.isDisposed()) {
                                    indicator.setText("Updating resource repository roots");
                                    // should be done per module
                                    //ModuleManager moduleManager = ModuleManager.getInstance(project);
                                    //for (Module module : moduleManager.getModules()) {
                                    //    moduleRootsOrDependenciesChanged(module);
                                    //}
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
}
