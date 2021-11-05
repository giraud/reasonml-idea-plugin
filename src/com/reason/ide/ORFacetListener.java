package com.reason.ide;

import com.intellij.facet.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.reason.ide.console.*;
import com.reason.ide.facet.*;
import org.jetbrains.annotations.*;

public class ORFacetListener implements ProjectWideFacetListener<Facet> {

    private final @NotNull Project m_project;

    public static void ensureSubscribed(@NotNull Project project) {
        project.getService(ORFacetListener.class);
    }

    private ORFacetListener(@NotNull Project project) {
        ProjectWideFacetListenersRegistry facetListenerRegistry = ProjectWideFacetListenersRegistry.getInstance(project);
        facetListenerRegistry.registerListener(this);
        m_project = project;
    }

    @Override
    public void firstFacetAdded() {
    }

    @Override
    public void facetAdded(@NotNull Facet facet) {
        showHideDuneToolWindow(m_project, facet);
    }

    @Override
    public void beforeFacetRemoved(@NotNull Facet facet) {
    }

    @Override
    public void facetRemoved(@NotNull Facet facet) {
        showHideDuneToolWindow(m_project, facet);
    }

    @Override
    public void allFacetsRemoved() {
    }

    @Override
    public void facetConfigurationChanged(@NotNull Facet facet) {
    }

    private static void showHideDuneToolWindow(@NotNull Project project, Facet facet) {
        if (facet instanceof DuneFacet) {
            ORToolWindowManager toolWindowManager = project.getService(ORToolWindowManager.class);
            ApplicationManager.getApplication().invokeLater(toolWindowManager::showShowToolWindows);
        }
    }
}
