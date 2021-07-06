package com.reason.comp.dune;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.*;
import com.reason.comp.Compiler;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public final class DuneProcess {
    private final @NotNull Project myProject;

    private @Nullable KillableColoredProcessHandler m_processHandler;

    DuneProcess(@NotNull Project project) {
        myProject = project;
    }

    // Wait for the tool window to be ready before starting the process
    public void startNotify() {
        if (m_processHandler != null && !m_processHandler.isStartNotified()) {
            try {
                m_processHandler.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    public @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @NotNull AtomicBoolean configurationWarning, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            killIt();

            GeneralCommandLine cli = new DuneCommandLine(myProject, "dune")
                    .addParameters((CliType.Dune) cliType)
                    .create(source, configurationWarning);

            if (cli != null) {
                m_processHandler = new KillableColoredProcessHandler(cli);
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
