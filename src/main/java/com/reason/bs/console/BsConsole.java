package com.reason.bs.console;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BsConsole extends ConsoleViewImpl {

    public BsConsole(@NotNull Project project) {
        super(project, true);
    }

    public ActionToolbar createToolbar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(this.getEditor()));
        group.add(new ClearLogAction(this));
        group.add(new MakeWorldAction(this, getProject()));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(this.getComponent());

        return toolbar;
    }

}
