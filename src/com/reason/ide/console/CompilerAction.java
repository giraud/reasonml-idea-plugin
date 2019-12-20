package com.reason.ide.console;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.Compiler;
import com.reason.Platform;
import com.reason.bs.Bucklescript;
import com.reason.ide.CompilerManager;

abstract class CompilerAction extends DumbAwareAction {

    CompilerAction(@NotNull String text, @NotNull String description, @NotNull Icon icon) {
        super(text, description, icon);
    }

    void doAction(@NotNull Project project, @NotNull CliType cliType) {
        Compiler compiler = CompilerManager.getInstance().getCompiler(project);

        // Try to detect the current active editor
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            VirtualFile baseDir = Platform.findORPackageJsonContentRoot(project);
            ConsoleView console = ServiceManager.getService(project, Bucklescript.class).getBsbConsole();
            if (console != null) {
                if (baseDir == null) {
                    console.print("Can't find content root\n", ConsoleViewContentType.NORMAL_OUTPUT);
                } else {
                    console.print("No active text editor found, using root directory " + baseDir.getPath() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    compiler.run(baseDir, cliType, null);
                }
            }
        } else {
            Document document = editor.getDocument();
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null) {
                compiler.run(psiFile.getVirtualFile(), cliType, null);
            }
        }
    }
}
