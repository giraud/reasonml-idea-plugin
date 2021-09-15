package com.reason.ide;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import com.intellij.openapi.startup.*;
import com.reason.comp.dune.*;
import com.reason.ide.console.*;
import jpsplugin.com.reason.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

/**
 * Ensure all services have started after the startup.
 */
public class ORPostStartupActivity implements StartupActivity, DumbAware {
    private static final Log LOG = Log.create("activity.startup");

    @Override
    public void runActivity(@NotNull Project project) {
        ORProjectRootListener.ensureSubscribed(project);
        ORFacetListener.ensureSubscribed(project);
        ORFileDocumentListener.ensureSubscribed(project);
        LOG.debug("Subscribed project and document listeners.");

        DumbService.getInstance(project).smartInvokeLater(() -> {
            ProjectSdksModel model = new ProjectSdksModel();
            model.reset(project);
            OpamEnv opamEnv = project.getService(OpamEnv.class);
            for (Sdk sdk : model.getSdks()) {
                if (sdk.getSdkType() instanceof OCamlSdkType) {
                    opamEnv.computeEnv(sdk, data -> LOG.debug("Computed opam env for " + sdk));
                }
            }

            project.getService(ORToolWindowManager.class).showHideToolWindows();
        });
    }
}
