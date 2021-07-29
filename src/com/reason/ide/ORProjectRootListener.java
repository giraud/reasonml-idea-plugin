package com.reason.ide;

import com.intellij.openapi.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import com.intellij.util.messages.*;
import com.reason.comp.dune.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

import static com.intellij.ProjectTopics.*;

// see AndroidProjectRootListener
public class ORProjectRootListener implements Disposable {
    private final @NotNull MessageBusConnection m_messageBusConnection;

    public static void ensureSubscribed(@NotNull Project project) {
        ServiceManager.getService(project, ORProjectRootListener.class);
    }

    private ORProjectRootListener(@NotNull Project project) {
        m_messageBusConnection = project.getMessageBus().connect(this);
        m_messageBusConnection.subscribe(PROJECT_ROOTS, new ModuleRootListener() {
            @Override
            public void rootsChanged(@NotNull ModuleRootEvent event) {
                DumbService.getInstance(project)
                        .smartInvokeLater(() -> {
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

//                indicator.setText("Updating resource repository roots");
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
                        });
            }
        });
    }

    @Override
    public void dispose() {
        m_messageBusConnection.disconnect();
    }
}
