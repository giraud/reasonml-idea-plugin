package com.reason.ide.console.bs;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.actions.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class BsToolWindowFactory extends ORToolWindowFactory {
    public static final String ID = "BuckleScript:";

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public @NotNull String getStripeTitle() {
        return "BuckleScript";
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        BsConsoleView console = new BsConsoleView(project);
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(console);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.getInstance().createContent(panel, "", true);

        window.getContentManager().addContent(content);

        Disposer.register(window.getDisposable(), console);
    }

    private @NotNull ActionToolbar createToolbar(@NotNull BsConsoleView console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        group.add(new BsMakeAction());
        group.add(new BsMakeWorldAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }
}
