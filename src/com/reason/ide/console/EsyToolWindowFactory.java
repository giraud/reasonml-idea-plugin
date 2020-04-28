package com.reason.ide.console;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import icons.ORIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EsyToolWindowFactory extends ORToolWindowFactory {

    public static final String ID = "Esy:";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Icon getIcon() {
        return ORIcons.ESY_TOOL;
    }

    @Nls
    @Override
    public String getTitle() {
        return "Process";
    }

    @Override
    public String getStripeTitle() {
        return "Esy";
    }

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow window) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        BsConsole console = new BsConsole(project);
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(console);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        window.getContentManager().addContent(content);

        Disposer.register(project, console);
    }

    @NotNull
    private ActionToolbar createToolbar(@NotNull BsConsole console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        // @TODO replace with esy actions once EsyProcess supports them
        group.add(new DuneInstallAction());
        group.add(new DuneBuildAction());
        group.add(new DuneCleanAction());
        // group.add(new EsyInstallAction());
        // group.add(new EsyBuildAction());
        // group.add(new EsyShellAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }
}
