package com.reason.build.bs.console;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.bs.compiler.BsCompiler;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;

public class BsToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow bucklescriptWindow) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        BsConsole console = new BsConsole(project);
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = createToolbar(project, console);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        bucklescriptWindow.getContentManager().addContent(content);
        bucklescriptWindow.setIcon(Icons.BUCKLESCRIPT);

        Disposer.register(project, console);

        // Start compiler
        BsCompiler bsc = BucklescriptManager.getInstance(project).getCompiler();
        if (bsc != null) {
            bsc.addListener(new BsOutputListener(project, bsc));
            ProcessHandler handler = bsc.getHandler();
            if (handler != null) {
                console.attachToProcess(handler);
            }
            bsc.startNotify();
        }
    }

    private ActionToolbar createToolbar(@NotNull Project project, @NotNull BsConsole console) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ScrollToTheEndToolbarAction(console.getEditor()));
        group.add(new ClearLogAction(console));
        group.add(new MakeAction(console, project));
        group.add(new MakeWorldAction(console, project));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("left", group, false);
        toolbar.setTargetComponent(console.getComponent());

        return toolbar;
    }

}
