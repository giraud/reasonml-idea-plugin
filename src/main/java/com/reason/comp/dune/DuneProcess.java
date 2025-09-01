package com.reason.comp.dune;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.ocaml.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public final class DuneProcess {
    private final @NotNull Project myProject;
    private @Nullable KillableColoredProcessHandler myProcessHandler;

    DuneProcess(@NotNull Project project) {
        myProject = project;
    }

    // Wait for the tool window to be ready before starting the process
    public void startNotify() {
        if (myProcessHandler != null && !myProcessHandler.isStartNotified()) {
            try {
                myProcessHandler.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    public @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        try {
            killIt();

            GeneralCommandLine cli = new DuneCommandLine(myProject, DunePlatform.DUNE_EXECUTABLE_NAME)
                    .addParameters((CliType.Dune) cliType)
                    .create(source);

            if (cli != null) {
                myProcessHandler = new KillableColoredProcessHandler(cli);
                if (onProcessTerminated != null) {
                    myProcessHandler.addProcessListener(new ProcessListener() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            onProcessTerminated.run(null);
                        }
                    });
                }
            }
            return myProcessHandler;
        } catch (ExecutionException e) {
            ORNotification.notifyError("Dune", "Execution exception", e.getMessage());
            return null;
        }
    }

    private void killIt() {
        if (myProcessHandler != null) {
            myProcessHandler.killProcess();
            myProcessHandler = null;
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

        @NotNull DuneCommandLine addParameters(@NotNull CliType.Dune cliType) {
            switch (cliType) {
                case VERSION:
                    m_parameters.add("--version");
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
