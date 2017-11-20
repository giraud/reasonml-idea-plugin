package com.reason.bs.console;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.Project;
import com.reason.bs.BsbCompiler;
import org.jetbrains.annotations.NotNull;

public class BsbConsole extends ConsoleViewImpl {

    public BsbConsole(@NotNull Project project) {
        super(project, true);
    }

    public ActionToolbar createToolbar(BsbCompiler bsc) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(this.getEditor()));
        group.add(new ClearLogAction(this));
        group.add(new StartAction(this, bsc));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(this.getComponent());

        return toolbar;
    }

}
