package com.reason.dune;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.reason.dune.DuneProcess.CONFIGURE_DUNE_SDK;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.reason.Log;
import com.reason.OCamlExecutable;
import com.reason.ORNotification;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

abstract class OpamCommandLine {
  private static final Log LOG = Log.create("opam");

  private final Project m_project;
  private final String m_binary;
  private final boolean m_redirectErrorStream;

  OpamCommandLine(@NotNull Project project, @NotNull String binary, boolean redirectErrorStream) {
    m_project = project;
    m_binary = binary;
    m_redirectErrorStream = redirectErrorStream;
  }

  OpamCommandLine(@NotNull Project project, @NotNull String binary) {
    this(project, binary, true);
  }

  protected abstract @NotNull List<String> getParameters();

  @Nullable GeneralCommandLine create(@NotNull VirtualFile source) {
    DuneFacet duneFacet = Dune.getFacet(m_project, source);
    Sdk odk = duneFacet == null ? null : duneFacet.getODK();
    VirtualFile homeDirectory = odk == null ? null : odk.getHomeDirectory();
    if (homeDirectory == null) {
      ORNotification.notifyError("Opam", "Can't find sdk", CONFIGURE_DUNE_SDK, URL_OPENING_LISTENER);
    } else {
      String binPath = odk.getHomePath() + "/bin";

      Module module = duneFacet.getModule();
      VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
      if (contentRoots.length > 0) {
        GeneralCommandLine cli = new GeneralCommandLine(ContainerUtil.prepend(getParameters(), m_binary));
        cli.setWorkDirectory(contentRoots[0].getPath());
        cli.setRedirectErrorStream(m_redirectErrorStream);

        Map<String, String> env = ServiceManager.getService(m_project, OpamEnv.class).getEnv(odk);
        if (env != null) {
          for (Map.Entry<String, String> entry : env.entrySet()) {
            cli.withEnvironment(entry.getKey(), entry.getValue());
          }
        }

        OCamlExecutable executable = OCamlExecutable.getExecutable(odk);
        return executable.patchCommandLine(cli, binPath, false, m_project);
      } else {
        LOG.debug("Content roots", contentRoots);
        LOG.debug("Binary directory", binPath);
      }
    }

    return null;
  }
}