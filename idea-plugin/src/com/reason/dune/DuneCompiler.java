package com.reason.dune;

import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.facet.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.reason.Compiler;
import com.reason.*;
import com.reason.hints.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.facet.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class DuneCompiler implements Compiler {

  private static final Log LOG = Log.create("dune.compiler");

  @NotNull
  private final Project project;

  DuneCompiler(@NotNull Project project) {
    this.project = project;
  }

  @Override
  public @NotNull CompilerType getType() {
    return CompilerType.DUNE;
  }

  @Override
  public boolean isConfigured(@NotNull Project project) {
    Module[] modules = ModuleManager.getInstance(project).getModules();
    for (Module module : modules) {
      DuneFacet duneFacet = FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
      if (duneFacet != null) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @NotNull Optional<VirtualFile> findFirstContentRoot(@NotNull Project project) {
    return ORProjectManager.findFirstDuneContentRoot(project);
  }

  @Override
  public Set<VirtualFile> findContentRoots(@NotNull Project project) {
    return ORProjectManager.findDuneContentRoots(project);
  }

  @Override
  public void refresh(@NotNull VirtualFile configFile) {
    // Nothing to do
  }

  @Override
  public void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated) {
    run(file, CliType.Dune.BUILD, onProcessTerminated);
  }

  @Override
  public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
    if (!(cliType instanceof CliType.Dune)) {
      LOG.error("Invalid cliType for dune compiler. cliType = " + cliType);
      return;
    }
    if (project.isDisposed()) {
      return;
    }

    DuneProcess process = ServiceManager.getService(project, DuneProcess.class);
    if (process.start()) {
      ProcessHandler duneHandler = process.create(file, cliType, onProcessTerminated);
      if (duneHandler != null) {
        ConsoleView console = getConsoleView();
        if (console != null) {
          console.attachToProcess(duneHandler);
          duneHandler.addProcessListener(new ProcessFinishedListener());
        }
        process.startNotify();
        ServiceManager.getService(project, InsightManager.class).downloadRincewindIfNeeded(file);
      } else {
        process.terminate();
      }
    }
  }

  @Nullable
  @Override
  public ConsoleView getConsoleView() {
    ORToolWindowProvider windowProvider = ORToolWindowProvider.getInstance(project);
    ToolWindow duneToolWindow = windowProvider.getDuneToolWindow();
    Content windowContent =
        duneToolWindow == null ? null : duneToolWindow.getContentManager().getContent(0);
    if (windowContent == null) {
      return null;
    }
    SimpleToolWindowPanel component = (SimpleToolWindowPanel) windowContent.getComponent();
    JComponent panelComponent = component.getComponent();
    if (panelComponent == null) {
      return null;
    }
    return (ConsoleView) panelComponent.getComponent(0);
  }
}
