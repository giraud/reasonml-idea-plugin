package com.reason.ide.console;

import com.intellij.execution.ui.*;
import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class ClearLogAction extends DumbAwareAction {
    private final ConsoleView myConsoleView;

    public ClearLogAction(ConsoleView consoleView) {
        super("Clear All", "Clear the contents of the logs", AllIcons.Actions.GC);
        myConsoleView = consoleView;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabled(editor != null && editor.getDocument().getTextLength() > 0);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        myConsoleView.clear();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
