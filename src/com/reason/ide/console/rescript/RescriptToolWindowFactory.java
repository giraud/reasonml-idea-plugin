package com.reason.ide.console.rescript;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.actions.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.reason.ide.console.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RescriptToolWindowFactory extends ORToolWindowFactory {
    private static final String TITLE = "Rescript";
    public static final String ID = TITLE + ":";

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.RESCRIPT_TOOL;
    }

    @Nls
    @Override
    public @NotNull String getTitle() {
        return "Process";
    }

    @Override
    public @NotNull String getStripeTitle() {
        return TITLE;
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        RescriptConsoleView console = new RescriptConsoleView(project);
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(console);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        window.getContentManager().addContent(content);

        Disposer.register(window.getDisposable(), console);
    }

    private @NotNull ActionToolbar createToolbar(@NotNull RescriptConsoleView console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        group.add(new RescriptBuildAction(console));
        group.add(new RescriptResetAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }
}
