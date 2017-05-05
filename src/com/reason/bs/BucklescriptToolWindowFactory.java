package com.reason.bs;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;

public class BucklescriptToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        BucklescriptCompiler bsc = ServiceManager.getService(project, BucklescriptCompiler.class);

        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        ConsoleViewImpl console = (ConsoleViewImpl) TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(console, bsc);
        toolbar.setTargetComponent(console.getComponent());
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);

        // Start compiler
        ProcessHandler handler = bsc.getHandler();
        if (handler == null) {
            console.print("Bsb not found, check the event logs.", ERROR_OUTPUT);
        } else {
            handler.addProcessListener(new BsbOutputListener(console, toolbar, project));
            console.attachToProcess(handler);
        }
        bsc.startNotify();
    }

    private static ActionToolbar createToolbar(ConsoleViewImpl console, BucklescriptCompiler bsc) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new BucklescriptConsole.ClearLogAction(console));
        group.add(new BucklescriptConsole.StartAction(bsc));
        return ActionManager.getInstance().createActionToolbar("left", group, false);
    }

    static class BucklescriptConsole {

        public static class ClearLogAction extends DumbAwareAction {
            private ConsoleView myConsole;

            ClearLogAction(/*BucklescriptConsole*/ConsoleView console) {
                super("Clear All", "Clear the contents of the Event Log", AllIcons.Actions.GC);
                myConsole = console;
            }

            @Override
            public void update(AnActionEvent e) {
                Editor editor = e.getData(CommonDataKeys.EDITOR);
                e.getPresentation().setEnabled(editor != null && editor.getDocument().getTextLength() > 0);
            }

            @Override
            public void actionPerformed(final AnActionEvent e) {
                myConsole.clear();
            }
        }

        public static class StartAction extends DumbAwareAction {
            private final BucklescriptCompiler bsc;
            private boolean enable = false;

            StartAction(BucklescriptCompiler bsc) {
                super("Start", "Start bucklescript process", AllIcons.Actions.Execute);
                this.bsc = bsc;
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
                this.bsc.restart();
            }
        }
    }

}
