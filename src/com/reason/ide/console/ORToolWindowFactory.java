package com.reason.ide.console;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

abstract class ORToolWindowFactory implements ToolWindowFactory, DumbAware {

    /* id provided in plugin.xml. this is displayed in the UI */
    public abstract String getId();

    public abstract Icon getIcon();

    public abstract String getTitle();

    public abstract String getStripeTitle();

    @Override
    public abstract boolean shouldBeAvailable(@NotNull Project project);

    @Override
    public abstract void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window);

    @Override
    public void init(ToolWindow window) {
        window.setIcon(getIcon());
        window.setTitle(getTitle());
        window.setStripeTitle(getStripeTitle());
    }
}
