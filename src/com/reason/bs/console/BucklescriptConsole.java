package com.reason.bs.console;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.Project;
import com.reason.bs.BucklescriptCompiler;
import org.jetbrains.annotations.NotNull;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;

public class BucklescriptConsole extends ConsoleViewImpl implements ConsoleBus {

    private ActionToolbar m_toolbar;

    public BucklescriptConsole(@NotNull Project project) {
        super(project, true);
    }

    public ActionToolbar createToolbar(BucklescriptCompiler bsc) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(this.getEditor()));
        group.add(new ClearLogAction(this));
        group.add(new StartAction(this, bsc));

        m_toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        m_toolbar.setTargetComponent(this.getComponent());

        return m_toolbar;
    }

    @Override
    public void processTerminated() {
        print("\nProcess has terminated, fix the problem before restarting it.", ERROR_OUTPUT);
        StartAction startAction = (StartAction) m_toolbar.getActions().get(2);
        startAction.setEnable(true);
    }

}
