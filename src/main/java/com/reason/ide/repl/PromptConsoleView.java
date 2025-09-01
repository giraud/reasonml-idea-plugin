package com.reason.ide.repl;

import com.intellij.execution.impl.*;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.editor.ex.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.search.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class PromptConsoleView extends ConsoleViewImpl {
    @NotNull private final PromptConsole m_promptConsole;

    PromptConsoleView(@NotNull Project project, boolean viewer, final boolean attachToStdOut) {
        super(
                project,
                GlobalSearchScope.allScope(project),
                viewer,
                new ConsoleState.NotStartedStated() {
                    @NotNull
                    @Override
                    public ConsoleState attachTo(
                            @NotNull ConsoleViewImpl console, @NotNull ProcessHandler processHandler) {
                        return new ConsoleViewRunningState(console, processHandler, this, attachToStdOut, true);
                    }
                },
                true);
        m_promptConsole = new PromptConsole(project, this);
        Disposer.register(this, m_promptConsole);
    }

    @NotNull
    @Override
    protected JComponent createCenterComponent() {
        return m_promptConsole.getCenterComponent();
    }

    @NotNull
    @Override
    protected EditorEx doCreateConsoleEditor() {
        return m_promptConsole.getOutputEditor();
    }

    @NotNull
    @Override
    public JComponent getPreferredFocusableComponent() {
        return m_promptConsole.getInputComponent();
    }

    @Override
    public void print(@NotNull String text, @NotNull ConsoleViewContentType contentType) {
        super.print(text, contentType);
    }

    @Override
    public void attachToProcess(@NotNull ProcessHandler processHandler) {
        super.attachToProcess(processHandler);

        processHandler.addProcessListener(
                new ProcessListener() {
                    @Override
                    public void processTerminated(@NotNull ProcessEvent event) {
                        ApplicationManager.getApplication()
                                .invokeLater(
                                        () ->
                                                ApplicationManager.getApplication()
                                                        .runWriteAction(m_promptConsole::disablePrompt));
                    }
                });
    }
}
