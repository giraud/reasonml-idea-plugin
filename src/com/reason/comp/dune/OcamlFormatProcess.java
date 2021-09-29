package com.reason.comp.dune;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.atomic.*;

public class OcamlFormatProcess {
    private static final Log LOG = Log.create("ocamlformat.process");

    private final Project myProject;
    private final AtomicBoolean myConfigurationWarning = new AtomicBoolean(false);

    public OcamlFormatProcess(Project project) {
        myProject = project;
    }

    public @NotNull String format(@NotNull VirtualFile file, @NotNull String textToFormat) {
        GeneralCommandLine commandLine = new OCamlFormatCommandLine(myProject, "ocamlformat").addParameters(file).create(file, myConfigurationWarning);
        if (commandLine != null && !textToFormat.isEmpty()) {
            String output = ProcessUtils.parseOutputFromCommandLine(commandLine, LOG, textToFormat);
            if (output != null) return output;
        }

        return textToFormat;
    }

    static class OCamlFormatCommandLine extends OpamCommandLine {
        private final List<String> m_parameters = new ArrayList<>();

        OCamlFormatCommandLine(@NotNull Project project, @NotNull String binary) {
            super(project, binary, false);
        }

        @NotNull OCamlFormatCommandLine addParameters(@NotNull VirtualFile file) {
            m_parameters.add("-"); // use stdin
            m_parameters.add("--name");
            m_parameters.add(file.getName());
            return this;
        }

        @Override
        protected @NotNull List<String> getParameters() {
            return m_parameters;
        }
    }
}
