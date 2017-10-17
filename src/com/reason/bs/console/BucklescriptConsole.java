package com.reason.bs.console;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.reason.bs.BucklescriptCompiler;

public class BucklescriptConsole {

    public static ActionToolbar createToolbar(ConsoleViewImpl console, BucklescriptCompiler bsc) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        group.add(new StartAction(console, bsc));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }

}
