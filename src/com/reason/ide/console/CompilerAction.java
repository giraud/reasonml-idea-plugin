package com.reason.ide.console;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public abstract class CompilerAction extends DumbAwareAction {
    protected CompilerAction(@NotNull String text, @NotNull String description, @NotNull Icon icon) {
        super(text, description, icon);
    }

    protected void doAction(@NotNull Project project, @NotNull CliType cliType) {
        Editor editor = getActiveEditor(project);
        if (editor == null) {
            compileDirectory(project, cliType);
        } else {
            compileFile(project, editor, cliType);
        }
    }

    private static void compileDirectory(@NotNull Project project, @NotNull CliType cliType) {
        Compiler compiler = project.getService(ORCompilerManager.class).getCompiler(cliType);
        if (compiler != null) {
            compiler.run(null, cliType, null);
        }
    }

    private static void compileFile(@NotNull Project project, @NotNull Editor editor, @NotNull CliType cliType) {
        Compiler compiler = project.getService(ORCompilerManager.class).getCompiler(cliType);
        PsiFile activeFile = getActiveFile(project, editor);

        // We always use the opened file to detect the config file
        if (activeFile != null && compiler != null) {
            compiler.run(activeFile.getVirtualFile(), cliType, null);
        }
    }

    private static @Nullable PsiFile getActiveFile(@NotNull Project project, @NotNull Editor editor) {
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        return documentManager == null ? null : documentManager.getPsiFile(editor.getDocument());
    }

    private static @Nullable Editor getActiveEditor(@NotNull Project project) {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        return editorManager == null ? null : editorManager.getSelectedTextEditor();
    }
}
