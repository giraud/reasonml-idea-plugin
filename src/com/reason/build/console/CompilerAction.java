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
import com.reason.build.bs.BucklescriptManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

abstract class CompilerAction extends DumbAwareAction {

    CompilerAction(@NotNull String text, @NotNull String description, @NotNull Icon icon) {
        super(text, description, icon);
    }

    void doAction(@NotNull Project project, @NotNull CliType cliType) {
        Compiler compiler = CompilerManager.getInstance().getCompiler(project);

        // Try to detect the current active editor
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            VirtualFile baseDir = Platform.findBaseRoot(project);
            ConsoleView console = BucklescriptManager.getInstance(project).getBsbConsole();
            console.print("No active text editor found, using root directory " + baseDir.getPath() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            compiler.run(baseDir, cliType, null);
        } else {
            Document document = editor.getDocument();
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null) {
                compiler.run(psiFile.getVirtualFile(), cliType, null);
            }
        }
    }
}
