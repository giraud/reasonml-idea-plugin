package com.reason.build.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.Platform;
import com.reason.build.Compiler;
import com.reason.build.CompilerManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class CompilerAction extends DumbAwareAction {

    private final ConsoleView m_console;
    private final Project m_project;

    CompilerAction(@NotNull String text, @NotNull String description, @NotNull Icon icon,@NotNull ConsoleView console, @NotNull Project project) {
        super(text, description, icon);
        m_console = console;
        m_project = project;
    }

    void doAction(CliType cliType) {
        Compiler compiler = CompilerManager.getInstance().getCompiler(m_project);

        // Try to detect the current active editor
        Editor editor = FileEditorManager.getInstance(m_project).getSelectedTextEditor();
        if (editor == null) {
            VirtualFile baseDir = Platform.findBaseRoot(m_project);
            m_console.print("No active text editor found, using root directory " + baseDir.getPath() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            compiler.run(baseDir, cliType);
        } else {
            Document document = editor.getDocument();
            PsiFile psiFile = PsiDocumentManager.getInstance(m_project).getPsiFile(document);
            if (psiFile != null) {
                compiler.run(psiFile.getVirtualFile(), cliType);
            }
        }
    }
}
