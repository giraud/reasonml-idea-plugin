package com.reason.bs.console;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.reason.bs.BsCompiler;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;

public class BsToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow bucklescriptWindow) {
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);

        BsConsole console = new BsConsole(project);
        panel.setContent(console.getComponent());

        ActionToolbar toolbar = console.createToolbar();
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        bucklescriptWindow.getContentManager().addContent(content);
        bucklescriptWindow.setIcon(Icons.BUCKLESCRIPT);

        Disposer.register(project, console);

        // Start compiler
        BsCompiler bsc = BucklescriptProjectComponent.getInstance(project).getCompiler();
        if (bsc != null) {
            bsc.addListener(new BsOutputListener(project));
            ProcessHandler handler = bsc.getHandler();
            if (handler == null) {
                console.print("Bsb not found, check the event logs.", ERROR_OUTPUT);
            } else {
                console.attachToProcess(handler);
            }
            bsc.startNotify();
        }
    }
}
