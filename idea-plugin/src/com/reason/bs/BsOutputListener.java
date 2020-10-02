package com.reason.bs;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.Log;
import org.jetbrains.annotations.NotNull;

public class BsOutputListener implements RawProcessListener {

  private static final Log LOG = Log.create("build");

  @NotNull private final Project m_project;
  @NotNull private final BsProcess m_compiler;
  @NotNull private final BsLineProcessor m_lineProcessor;

  BsOutputListener(@NotNull Project project, @NotNull BsProcess bsc) {
    m_project = project;
    m_compiler = bsc;
    m_lineProcessor = new BsLineProcessor(LOG);
  }

  @Override
  public void startNotified(@NotNull ProcessEvent event) {
    // ServiceManager.getService(m_project, ErrorsManager.class).clearErrors();
    // m_lineProcessor.m_bsbInfo.clear();
  }

  @Override
  public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {}

  @Override
  public void onRawTextAvailable(@NotNull String text) {
    m_lineProcessor.onRawTextAvailable(text);
  }

  @Override
  public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {}

  @Override
  public void processTerminated(@NotNull ProcessEvent event) {
    m_compiler.terminate();

    // if (m_lineProcessor.hasInfo() && !m_project.isDisposed()) {
    //    ErrorsManager errorsService = ServiceManager.getService(m_project, ErrorsManager.class);
    //    errorsService.addAllInfo(m_lineProcessor.getInfo());
    // }

    // m_lineProcessor.reset();
    // m_lineProcessor.m_bsbInfo.clear();

    // ApplicationManager.getApplication().invokeLater(() -> {
    //    if (!m_project.isDisposed()) {
    // When build is done, we need to refresh editors to be notified of the latest modifications
    // DaemonCodeAnalyzer.getInstance(m_project).restart();
    // EditorFactory.getInstance().refreshAllEditors();
    // InferredTypesService.queryForSelectedTextEditor(m_project);
    // }
    // });
  }
}
