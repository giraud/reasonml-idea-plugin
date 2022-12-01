package com.reason.ide.console.esy;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.actions.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class EsyToolWindowFactory extends ORToolWindowFactory {
    public static final String ID = "Esy:";

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public @NotNull String getStripeTitle() {
        return "Esy";
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        EsyConsoleView consoleView = new EsyConsoleView(project);
        panel.setContent(consoleView.getComponent());

        ActionToolbar toolbar = createToolbar(consoleView);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        window.getContentManager().addContent(content);

        Disposer.register(window.getDisposable(), consoleView);
    }

    @NotNull
    private ActionToolbar createToolbar(@NotNull EsyConsoleView console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        group.add(new EsyBuildAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }
}
