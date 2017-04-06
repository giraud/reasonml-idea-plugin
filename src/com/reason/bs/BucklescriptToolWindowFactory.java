package com.reason.bs;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
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

public class BucklescriptToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        ConsoleViewImpl console = (ConsoleViewImpl) TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(console);
        toolbar.setTargetComponent(console.getComponent());
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);

        // Start compiler
        BucklescriptCompiler bsb = ServiceManager.getService(project, BucklescriptCompiler.class);
        console.attachToProcess(bsb.getHandler());
        bsb.startNotify();
    }

    private static ActionToolbar createToolbar(ConsoleViewImpl console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new BucklescriptConsole.ClearLogAction(console));
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
    }
}
