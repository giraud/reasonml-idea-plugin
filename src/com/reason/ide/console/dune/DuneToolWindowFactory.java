package com.reason.ide.console.dune;

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

public class DuneToolWindowFactory extends ORToolWindowFactory {
    public static final String ID = "Dune:";

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.DUNE_TOOL;
    }

    @Override
    public @NotNull String getStripeTitle() {
        return "Dune";
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        DuneConsoleView console = new DuneConsoleView(project);
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(console);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        window.getContentManager().addContent(content);

        Disposer.register(window.getDisposable(), console);
    }

    @NotNull
    private ActionToolbar createToolbar(@NotNull DuneConsoleView console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        group.add(new DuneBuildAction());
        group.add(new DuneCleanAction());
        //group.add(new DuneInstallAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }
}
