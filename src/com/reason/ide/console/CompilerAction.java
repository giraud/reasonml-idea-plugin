package com.reason.ide.console;

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
import com.reason.CompilerType;
import com.reason.bs.BsCompiler;
import com.reason.bs.BsConstants;
import com.reason.ide.ORCompilerManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

abstract class CompilerAction extends DumbAwareAction {

    CompilerAction(@NotNull String text, @NotNull String description, @NotNull Icon icon) {
        super(text, description, icon);
    }

    public abstract CompilerType getCompilerType();

    void doAction(@NotNull Project project, @NotNull CliType cliType) {
        Compiler compiler = ORCompilerManager.getInstance().getCompiler(project);

        // Try to detect the current active editor
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            ConsoleView console = ServiceManager.getService(project, BsCompiler.class).getBsbConsole();
            if (console != null) {
                Optional<VirtualFile> baseDirectoryOptional = compiler.findFirstContentRoot(project);
                if (!baseDirectoryOptional.isPresent()) {
                    console.print("Can't find content root\n", ConsoleViewContentType.NORMAL_OUTPUT);
                } else {
                    VirtualFile baseDirectory = baseDirectoryOptional.get();
                    console.print("No active text editor found, using root directory " +
                                    baseDirectory.getPath() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    VirtualFile bsConfig = baseDirectory.findChild(BsConstants.BS_CONFIG_FILENAME);
                    assert bsConfig != null;
                    compiler.run(bsConfig, cliType, null);
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
