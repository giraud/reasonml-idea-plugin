package com.reason.ide.console;

import java.util.*;
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
import com.reason.ORCompilerManager;

abstract class CompilerAction extends DumbAwareAction {

    CompilerAction(@NotNull String text, @NotNull String description, @NotNull Icon icon) {
        super(text, description, icon);
    }

    void doAction(@NotNull Project project, @NotNull CliType cliType) {
        Optional<Editor> editorOptional = getActiveEditor(project);
        if (editorOptional.isPresent()) {
            compileFile(project, editorOptional.get(), cliType);
            return;
        }
        compileDirectory(project, cliType);
    }

    private static void compileDirectory(@NotNull Project project, CliType cliType) {
        ORCompilerManager compilerManager = ServiceManager.getService(project, ORCompilerManager.class);
        Optional<Compiler> compilerOptional = compilerManager.getCompiler(cliType);
        if (!compilerOptional.isPresent()) {
            return;
        }
        Compiler compiler = compilerOptional.get();
        ConsoleView consoleView = compiler.getConsoleView();
        if (consoleView == null) {
            return;
        }
        Optional<VirtualFile> baseDir = compiler.findFirstContentRoot(project);
        if (baseDir.isPresent()) {
            consoleView.print("No active text editor found, using root directory " + baseDir.get().getPath() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            compiler.run(baseDir.get(), cliType, null);
        } else {
            consoleView.print("Can't find content root\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }

    private static void compileFile(@NotNull Project project, @NotNull Editor editor, @NotNull CliType cliType) {
        Optional<PsiFile> activeFileOptional = getActiveFile(project, editor);
        if (activeFileOptional.isPresent()) {
            PsiFile activeFile = activeFileOptional.get();
            // We always use the opened file to detect the config file
            ServiceManager.getService(project, ORCompilerManager.class).
                    getCompiler(cliType).
                    ifPresent(compiler -> compiler.run(activeFile.getVirtualFile(), cliType, null));
        }
    }

    private static Optional<PsiFile> getActiveFile(@NotNull Project project, @NotNull Editor editor) {
        Document document = editor.getDocument();
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        return Optional.ofNullable(psiDocumentManager.getPsiFile(document));
    }

    private static Optional<Editor> getActiveEditor(Project project) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        return Optional.ofNullable(fileEditorManager.getSelectedTextEditor());
    }
}