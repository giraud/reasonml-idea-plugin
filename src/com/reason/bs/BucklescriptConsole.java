package com.reason.bs;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.DumbAwareAction;

class BucklescriptConsole {

    static ActionToolbar createToolbar(ConsoleViewImpl console, BucklescriptCompiler bsc) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new BucklescriptConsole.ClearLogAction(console));
        group.add(new BucklescriptConsole.StartAction(bsc, console));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }

    static class ClearLogAction extends DumbAwareAction {
        private ConsoleView console;

        ClearLogAction(ConsoleView console) {
            super("Clear All", "Clear the contents of the Event Log", AllIcons.Actions.GC);
            this.console = console;
        }

        @Override
        public void update(AnActionEvent e) {
            Editor editor = e.getData(CommonDataKeys.EDITOR);
            e.getPresentation().setEnabled(editor != null && editor.getDocument().getTextLength() > 0);
        }

        @Override
        public void actionPerformed(final AnActionEvent e) {
            this.console.clear();
        }
    }

    static class StartAction extends DumbAwareAction {
        private final BucklescriptCompiler bsc;
        private final ConsoleView console;
        private boolean enable = false;

        StartAction(BucklescriptCompiler bsc, ConsoleView console) {
            super("Start", "Start bucklescript process", AllIcons.Actions.Execute);
            this.bsc = bsc;
            this.console = console;
        }

        void setEnable(boolean value) {
            this.enable = value;
        }

        @Override
        public void update(AnActionEvent e) {
            e.getPresentation().setEnabled(this.enable);
        }

        @Override
        public void actionPerformed(final AnActionEvent e) {
            this.enable = false;
            console.attachToProcess(this.bsc.recreate());
            this.bsc.startNotify();
        }
    }

}
