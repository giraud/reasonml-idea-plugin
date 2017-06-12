package com.reason.bs;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
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

        ActionToolbar toolbar = BucklescriptConsole.createToolbar(console, bsc);
        panel.setToolbar(toolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(panel, "", true);

        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);

        // Start compiler
        bsc.addListener(new BsbOutputListener(console, toolbar, project));
        ProcessHandler handler = bsc.getHandler();
        if (handler == null) {
            console.print("Bsb not found, check the event logs.", ERROR_OUTPUT);
        } else {
            console.attachToProcess(handler);
        }
        bsc.startNotify();
    }

}
