package com.reason.ide;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import com.intellij.openapi.startup.*;
import com.reason.*;
import com.reason.dune.*;
import com.reason.ide.console.*;
import com.reason.sdk.*;
import org.jetbrains.annotations.*;

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
      OpamEnv opamEnv = ServiceManager.getService(project, OpamEnv.class);
      for (Sdk sdk : model.getSdks()) {
        if (sdk.getSdkType() instanceof OCamlSdkType) {
          opamEnv.computeEnv(sdk, data -> LOG.debug("Computed opam env for " + sdk));
        }
      }

      ServiceManager.getService(project, ORToolWindowManager.class).showHideToolWindows();
    });
  }

}
