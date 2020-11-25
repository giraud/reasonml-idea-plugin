package com.reason.dune;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static com.reason.Platform.*;

// TODO: what if ocamlformat is not installed into opam switch
// TODO: err stream ?
public class OcamlFormatProcess {

  private static final Log LOG = Log.create("ocamlformat.process");

  private final Project m_project;

  public OcamlFormatProcess(Project project) {
    m_project = project;
  }

  public static OcamlFormatProcess getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, OcamlFormatProcess.class);
  }

  public @NotNull String format(@NotNull VirtualFile file, @NotNull String textToFormat) {
    GeneralCommandLine commandLine = new OCamlFormatCommandLine(m_project, "ocamlformat").create(file);
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
    OCamlFormatCommandLine(@NotNull Project project, @NotNull String binary) {
      super(project, binary);
    }

    @Override
    protected @NotNull List<String> getParameters() {
      List<String> parameters = new ArrayList<>();
      parameters.add("-"); // use stdin
      parameters.add("--enable-outside-detected-project"); // works even if no .ocamlformat found
      // TODO: use --root?
      parameters.add("--impl"); // TODO: it depends
      return parameters;
    }
  }
}