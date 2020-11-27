package com.reason.bs;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static com.reason.Platform.*;

public class RefmtProcess {

  private static final Log LOG = Log.create("refmt");

  private final Project m_project;

  public static @NotNull RefmtProcess getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, RefmtProcess.class);
  }

  public RefmtProcess(@NotNull Project project) {
    m_project = project;
  }

  @NotNull
  public String convert(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull String code) {
    Optional<VirtualFile> refmtPath = BsPlatform.findRefmtExecutable(m_project, sourceFile);
    if (!refmtPath.isPresent()) {
      LOG.debug("No refmt binary found, reformat cancelled");
      return code;
    }

    String columnsWidth = ORSettings.getInstance(m_project).getFormatColumnWidth();
    ProcessBuilder processBuilder =
        new ProcessBuilder(refmtPath.get().getPath(), "-i", Boolean.toString(isInterface), "--parse" + "=" + fromFormat, "-p", toFormat, "-w", columnsWidth);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Reformating " + sourceFile.getPath() + " (" + fromFormat + " -> " + toFormat + ") using " + columnsWidth + " cols for project [" + m_project + "]");
    }

    Process refmt = null;
    try {
      refmt = processBuilder.start();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream(), UTF8));
      BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream(), UTF8));
      BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream(), UTF8));

      writer.write(code);
      writer.flush();
      writer.close();

      Streams.waitUntilReady(reader, errReader);

      StringBuilder msgBuffer = new StringBuilder();
      if (!errReader.ready()) {
        final boolean[] empty = {true};
        reader
            .lines()
            .forEach(
                line -> {
                  if (empty[0]) {
                    empty[0] = false;
                  } else {
                    msgBuffer.append('\n');
                  }
                  msgBuffer.append(line);
                });
        String newText = msgBuffer.toString();
        if (!code.isEmpty() && !newText.isEmpty()) { // additional protection
          return newText;
        }
      }
    } catch (@NotNull IOException | RuntimeException e) {
      LOG.warn(e);
    } finally {
      if (refmt != null) {
        refmt.destroyForcibly();
      }
    }

    // Something bad happened, do nothing
    return code;
  }
}
