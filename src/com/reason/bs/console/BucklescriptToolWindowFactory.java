package com.reason.bs.console;

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
import com.reason.bs.BsbOutputListener;
import com.reason.bs.BucklescriptCompiler;
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
        bsc.addListener(new BsbOutputListener(new ConsoleBusImpl(console, toolbar), project));
        ProcessHandler handler = bsc.getHandler();
        if (handler == null) {
            console.print("Bsb not found, check the event logs.", ERROR_OUTPUT);
        } else {
            console.attachToProcess(handler);
        }
        bsc.startNotify();
    }

    static class ConsoleBusImpl implements ConsoleBus {

        private final ConsoleViewImpl m_console;
        private final ActionToolbar m_toolbar;

        ConsoleBusImpl(ConsoleViewImpl console, ActionToolbar toolbar) {
            m_console = console;
            m_toolbar = toolbar;
        }

        @Override
        public void processTerminated() {
            m_console.print("\nProcess has terminated, fix the problem before restarting it.", ERROR_OUTPUT);
            StartAction startAction = (StartAction) m_toolbar.getActions().get(2);
            startAction.setEnable(true);
        }
    }
}
