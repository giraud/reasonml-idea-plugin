package com.reason.comp.ocaml;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static jpsplugin.com.reason.Platform.*;

@Service(Service.Level.PROJECT)
public final class OcamlFormatProcess {
    private static final Log LOG = Log.create("format.ocaml.process");

    private final Project myProject;

    public OcamlFormatProcess(Project project) {
        myProject = project;
    }

    public @NotNull String format(@NotNull VirtualFile file, @NotNull String textToFormat) {
        GeneralCommandLine commandLine = new OCamlFormatCommandLine(myProject, "ocamlformat").addParameters(file).create(file);
        if (commandLine != null && !textToFormat.isEmpty()) {
            Process fmt = null;
            try {
                fmt = commandLine.createProcess();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fmt.getOutputStream(), UTF8));
                BufferedReader reader = new BufferedReader(new InputStreamReader(fmt.getInputStream(), UTF8));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(fmt.getErrorStream(), UTF8));

                writer.write(textToFormat);
                writer.flush();
                writer.close();

                Streams.waitUntilReady(reader, errReader);

                StringBuilder msgBuffer = new StringBuilder();
                if (!errReader.ready()) {
                    final boolean[] empty = {true};
                    reader
                            .lines()
                            .forEach(line -> {
                                if (empty[0]) {
                                    empty[0] = false;
                                } else {
                                    msgBuffer.append('\n');
                                }
                                msgBuffer.append(line);
                            });
                    String newText = msgBuffer.toString();
                    if (!newText.isEmpty()) { // additional protection
                        return newText;
                    }
                } else {
                    errReader.lines().forEach(line -> msgBuffer.append(line).append('\n'));
                    LOG.warn(StringUtil.trimLastCR(msgBuffer.toString()));
                }
            } catch (IOException | RuntimeException | ExecutionException e) {
                LOG.warn(e);
            } finally {
                if (fmt != null) {
                    fmt.destroyForcibly();
                }
            }
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
