package com.reason.dune;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.facet.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.containers.*;
import com.reason.Compiler;
import com.reason.Platform;
import com.reason.*;
import com.reason.ide.console.*;
import com.reason.ide.facet.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static com.intellij.notification.NotificationListener.*;

public final class DuneProcess implements CompilerProcess {
  private static final Log LOG = Log.create("dune.compiler");

  public static final String CONFIGURE_DUNE_SDK = "<html>"
                                                      + "When using a dune config file, you need to create an OCaml SDK and associate it to the project.\n"
                                                      + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin/blob/master/docs/configuring-ocaml-project.md\">github</a>."
                                                      + "</html>";

  private final @NotNull Project m_project;
  private final @NotNull ProcessListener m_outputListener;
  private final AtomicBoolean m_started = new AtomicBoolean(false);

  private @Nullable KillableColoredProcessHandler m_processHandler;

  DuneProcess(@NotNull Project project) {
    m_project = project;
    m_outputListener = new DuneOutputListener(m_project, this);
  }

  // Wait for the tool window to be ready before starting the process
  @Override
  public void startNotify() {
    if (m_processHandler != null && !m_processHandler.isStartNotified()) {
      try {
        m_processHandler.startNotify();
      } catch (Throwable e) {
        // already done ?
      }
    }
  }

  @Override
  public @Nullable ProcessHandler create(@Nullable VirtualFile source, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
    try {
      killIt();
      GeneralCommandLine cli = getGeneralCommandLine(source, (CliType.Dune) cliType);
      if (cli != null) {
        m_processHandler = new KillableColoredProcessHandler(cli);
        m_processHandler.addProcessListener(m_outputListener);
        if (onProcessTerminated != null) {
          m_processHandler.addProcessListener(
              new ProcessAdapter() {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                  onProcessTerminated.run();
                }
              });
        }
      }
      return m_processHandler;
    } catch (ExecutionException e) {
      ORNotification.notifyError("Dune", "Execution exception", e.getMessage(), null);
      return null;
    }
  }

  private void killIt() {
    if (m_processHandler != null) {
      m_processHandler.killProcess();
      m_processHandler = null;
    }
  }

  private @Nullable GeneralCommandLine getGeneralCommandLine(@Nullable VirtualFile source, @NotNull CliType.Dune cliType) {
    DuneFacet duneFacet = getDuneFacet(source);
    Sdk odk = duneFacet == null ? null : duneFacet.getODK();
    VirtualFile homeDirectory = odk == null ? null : odk.getHomeDirectory();
    if (homeDirectory == null) {
      ORNotification.notifyError("Dune", "Can't find sdk", CONFIGURE_DUNE_SDK, URL_OPENING_LISTENER);
    } else {
      String binPath = odk.getHomePath() + "/bin";

      Module module = duneFacet.getModule();
      VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
      if (contentRoots.length > 0) {
        GeneralCommandLine cli = new GeneralCommandLine(ContainerUtil.prepend(getCliParameters(cliType), "dune"));
        cli.setWorkDirectory(contentRoots[0].getPath());
        cli.setRedirectErrorStream(true);

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

  // TODO: Platform
  public @Nullable DuneFacet getDuneFacet(@Nullable VirtualFile source) {
    Module module = Platform.getModule(m_project, source);
    return module == null ? null : FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
  }

  private List<String> getCliParameters(CliType.Dune cliType) {
    List<String> result = new ArrayList<>();

    switch (cliType) {
      case CLEAN:
        result.add("clean");
        break;
      case BUILD:
      default:
        result.add("build");
    }
    result.add("--root=.");

    return result;
  }

  @Override
  public boolean start() {
    return m_started.compareAndSet(false, true);
  }

  @Override
  public void terminate() {
    m_started.set(false);
  }
}
