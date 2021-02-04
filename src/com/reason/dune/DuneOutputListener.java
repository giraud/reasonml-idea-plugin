package com.reason.dune;

import static java.lang.Integer.parseInt;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.CompilerProcess;
import com.reason.Log;
import com.reason.ide.annotations.ErrorsManager;
import com.reason.ide.annotations.OutputInfo;
import com.reason.ide.hints.InferredTypesService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DuneOutputListener implements ProcessListener {

  private static final Pattern FILE_LOCATION =
      Pattern.compile("File \"(.+)\", line (\\d+), characters (\\d+)-(\\d+):\n");

  private static final Log LOG = Log.create("dune.output");

  private final @NotNull Project m_project;
  private final CompilerProcess m_compilerLifecycle;
  private final List<OutputInfo> m_outputInfo = new ArrayList<>();

  @Nullable private OutputInfo m_latestInfo = null;

  public DuneOutputListener(@NotNull Project project, CompilerProcess compilerLifecycle) {
    m_project = project;
    m_compilerLifecycle = compilerLifecycle;
  }

  @Override
  public void startNotified(@NotNull ProcessEvent event) {
    m_outputInfo.clear();
    ServiceManager.getService(m_project, ErrorsManager.class).clearErrors();
  }

  @Override
  public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {}

  @Override
  public void processTerminated(@NotNull ProcessEvent event) {
    m_compilerLifecycle.terminate();

    if (!m_outputInfo.isEmpty() && !m_project.isDisposed()) {
      LOG.debug("Update errors manager with output results");
      ServiceManager.getService(m_project, ErrorsManager.class).addAllInfo(m_outputInfo);
    }

    reset();
    m_outputInfo.clear();

    ApplicationManager.getApplication()
        .invokeLater(
            () -> {
              // When build is done, we need to refresh editors to be notified of the latest
              // modifications
              if (!m_project.isDisposed()) {
                LOG.debug("Refresh editors / inferred types");
                InferredTypesService.queryForSelectedTextEditor(m_project);
                DaemonCodeAnalyzer.getInstance(m_project).restart();
                EditorFactory.getInstance().refreshAllEditors();
              }
            },
            ModalityState.NON_MODAL);
  }

  @Override
  public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
    String text = event.getText();

    /*
    File "CmtExtractor.ml", line 80, characters 67-70:
    Error: Unbound value cmt
    Hint: Did you mean cmtx?
    */
    if (text.startsWith("File")) {
      m_latestInfo = extractExtendedFilePositions(text);
      if (m_latestInfo != null) {
        m_latestInfo.isError = false;
      }
    } else if (text.startsWith("Error:")) {
      if (m_latestInfo != null) {
        m_latestInfo.isError = true;
        m_latestInfo.message = text.substring(6);
      }
    } else if (text.startsWith("Hint:")) {
      if (m_latestInfo != null) {
        m_latestInfo.message += " (" + text + ")";
      }
    }

    // m_previousText = text;
  }

  // File "...path/src/Source.ml", line 111, characters 0-3:
  @Nullable
  private OutputInfo extractExtendedFilePositions(@Nullable String text) {
    if (text != null) {
      Matcher matcher = FILE_LOCATION.matcher(text);
      if (matcher.matches()) {
        String path = matcher.group(1);
        String line = matcher.group(2);
        String colStart = matcher.group(3);
        String colEnd = matcher.group(4);
        OutputInfo info = addInfo(path, line, colStart, colEnd);
        if (info.colStart < 0 || info.colEnd < 0) {
          LOG.error("Can't decode columns for [" + text.replace("\n", "") + "]");
          return null;
        }
        return info;
      }
    }

    return null;
  }

  @NotNull
  private OutputInfo addInfo(
      @NotNull String path,
      @NotNull String line,
      @NotNull String colStart,
      @NotNull String colEnd) {
    OutputInfo info = new OutputInfo();

    info.path = path;
    info.lineStart = parseInt(line);
    info.colStart = parseInt(colStart) + 1;
    info.lineEnd = info.lineStart;
    info.colEnd = parseInt(colEnd) + 1;
    if (info.colEnd == info.colStart) {
      info.colEnd += 1;
    }

    m_outputInfo.add(info);
    return info;
  }

  private void reset() {
    m_latestInfo = null;
  }
}
