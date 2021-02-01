package com.reason.dune;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.Compiler;
import com.reason.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public final class DuneProcess implements CompilerProcess {
    public static final String CONFIGURE_DUNE_SDK = "<html>"
            + "When using a dune config file, you need to create an OCaml SDK and associate it to the project.\n"
            + "see <a href=\"https://reasonml-editor.github.io/reasonml-idea-plugin/docs/build-tools/dune\">github</a>."
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
    public @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            killIt();
            GeneralCommandLine cli = new DuneCommandLine(m_project, "dune").addParameters((CliType.Dune) cliType).create(source);
            if (cli != null) {
                m_processHandler = new KillableColoredProcessHandler(cli);
                m_processHandler.addProcessListener(m_outputListener);
                if (onProcessTerminated != null) {
                    m_processHandler.addProcessListener(new ProcessAdapter() {
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

    @Override
    public boolean start() {
        return m_started.compareAndSet(false, true);
    }

    @Override
    public void terminate() {
        m_started.set(false);
    }

    static class DuneCommandLine extends OpamCommandLine {
        private final List<String> m_parameters = new ArrayList<>();

        DuneCommandLine(@NotNull Project project, @NotNull String binary) {
            super(project, binary);
        }

        @Override
        protected @NotNull List<String> getParameters() {
            return m_parameters;
        }

        DuneCommandLine addParameters(CliType.Dune cliType) {
            switch (cliType) {
                case CLEAN:
                    m_parameters.add("clean");
                    break;
                case BUILD:
                default:
                    m_parameters.add("build");
            }
            m_parameters.add("--root=.");
            return this;
        }
    }
}
