package com.reason.ide;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Condition;
import com.reason.Log;
import com.reason.dune.OpamEnv;
import com.reason.ide.console.ORToolWindowManager;
import com.reason.sdk.OCamlSdkType;
import org.jetbrains.annotations.NotNull;

public class ORPostStartupActivity implements StartupActivity, DumbAware {

    private static final Log LOG = Log.create("activity.startup");

    @Override
    public void runActivity(@NotNull Project project) {
        ORProjectRootListener.ensureSubscribed(project);
        ORFacetListener.ensureSubscribed(project);
        ORFileDocumentListener.ensureSubscribed(project);
        LOG.debug("Subscribed project and document listeners.");

        ProjectSdksModel model = new ProjectSdksModel();
        model.reset(project);
        OpamEnv opamEnv = ServiceManager.getService(project, OpamEnv.class);
        for (Sdk sdk : model.getSdks()) {
            if (sdk.getSdkType() instanceof OCamlSdkType) {
                opamEnv.computeEnv(sdk, data -> LOG.debug("Computed opam env for " + sdk));
            }
        }

        showToolWindowsLater(project);
    }

    /* show tool windows after indexing finishes */
    private static void showToolWindowsLater(@NotNull Project project) {
        ORToolWindowManager toolWindowManager = ServiceManager.getService(project, ORToolWindowManager.class);
        DumbService.getInstance(project).smartInvokeLater(toolWindowManager::showHideToolWindows);
    }
}
