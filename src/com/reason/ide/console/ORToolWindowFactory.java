package com.reason.ide.console;

import com.intellij.openapi.project.*;
import com.intellij.openapi.wm.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public abstract class ORToolWindowFactory implements ToolWindowFactory, DumbAware {
    /* id provided in plugin.xml. this is displayed in the UI */
    public abstract String getId();

    public abstract Icon getIcon();

    public abstract String getTitle();

    public abstract String getStripeTitle();

    /**
     * Initially hide all tool windows until indexing has completed. See {@link com.reason.ide.ORPostStartupActivity}
     */
    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return false;
    }

    @Override
    public abstract void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window);

    @Override
    public void init(@NotNull ToolWindow window) {
        window.setIcon(getIcon());
        window.setTitle(getTitle());
        window.setStripeTitle(getStripeTitle());
    }
}
