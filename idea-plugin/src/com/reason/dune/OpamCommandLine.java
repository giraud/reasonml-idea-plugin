package com.reason.dune;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.containers.*;
import com.reason.*;
import com.reason.ide.facet.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.notification.NotificationListener.*;
import static com.reason.dune.DuneProcess.*;

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