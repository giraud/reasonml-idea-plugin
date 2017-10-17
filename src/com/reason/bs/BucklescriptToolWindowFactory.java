package com.reason.bs;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.reason.bs.console.BucklescriptConsole;
import org.jetbrains.annotations.NotNull;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;

public class BucklescriptToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        BucklescriptCompiler bsc = ServiceManager.getService(project, BucklescriptCompiler.class);

        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true);
        BucklescriptConsole console = new BucklescriptConsole(project);
        ActionToolbar toolbar = console.createToolbar(bsc);

        panel.setContent(console.getComponent());
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);
        toolWindow.getContentManager().addContent(content);

        // Start compiler
        bsc.addListener(new BsbOutputListener(console, project));
        ProcessHandler handler = bsc.getHandler();
        if (handler == null) {
            console.print("Bsb not found, check the event logs.", ERROR_OUTPUT);
        } else {
            console.attachToProcess(handler);
        }
        bsc.startNotify();
    }
}
