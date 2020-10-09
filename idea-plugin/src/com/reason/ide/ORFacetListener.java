package com.reason.ide;

import com.intellij.facet.Facet;
import com.intellij.facet.ProjectWideFacetListener;
import com.intellij.facet.ProjectWideFacetListenersRegistry;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.reason.ide.console.ORToolWindowManager;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;

public class ORFacetListener implements ProjectWideFacetListener<Facet> {

  private final @NotNull Project m_project;

  public static void ensureSubscribed(@NotNull Project project) {
    ServiceManager.getService(project, ORFacetListener.class);
  }

  private ORFacetListener(@NotNull Project project) {
    ProjectWideFacetListenersRegistry facetListenerRegistry =
        ProjectWideFacetListenersRegistry.getInstance(project);
    facetListenerRegistry.registerListener(this);
    m_project = project;
  }

  @Override
  public void firstFacetAdded() {}

  @Override
  public void facetAdded(@NotNull Facet facet) {
    showHideDuneToolWindow(m_project, facet);
  }

  @Override
  public void beforeFacetRemoved(@NotNull Facet facet) {}

  @Override
  public void facetRemoved(@NotNull Facet facet) {
    showHideDuneToolWindow(m_project, facet);
  }

  @Override
  public void allFacetsRemoved() {}

  @Override
  public void facetConfigurationChanged(@NotNull Facet facet) {}

  private static void showHideDuneToolWindow(@NotNull Project project, Facet facet) {
    if (facet instanceof DuneFacet) {
      ORToolWindowManager toolWindowManager = ORToolWindowManager.getInstance(project);
      ApplicationManager.getApplication().invokeLater(toolWindowManager::showHideToolWindows);
    }
  }
}
