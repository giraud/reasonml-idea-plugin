package com.reason.build.bs.console;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.Platform;
import com.reason.build.bs.Bucklescript;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.bs.compiler.BsCompiler;
import com.reason.build.bs.compiler.CliType;
import com.reason.hints.InsightManagerImpl;

public class MakeAction extends DumbAwareAction {

    private final ConsoleView m_console;
    private final Project m_project;

    MakeAction(ConsoleView console, Project project) {
        super("Make", "Make", AllIcons.Actions.Compile);
        m_project = project;
        m_console = console;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Bucklescript bucklescript = BucklescriptManager.getInstance(m_project);
        BsCompiler bsc = bucklescript.getOrCreateCompiler();
        if (bsc != null) {
            VirtualFile baseDir = Platform.findBaseRoot(m_project);

            // Try to detect the current active editor
            Editor editor = FileEditorManager.getInstance(m_project).getSelectedTextEditor();
            if (editor == null) {
                m_console.print("No active text editor found, using " + baseDir.getPath() + " as root directory\n", ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                Document document = editor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(m_project).getPsiFile(document);
                if (psiFile != null) {
                    baseDir = Platform.findBaseRootFromFile(m_project, psiFile.getVirtualFile());
                }
            }

            VirtualFile sourceFile = baseDir.findChild("bsconfig.json");
            if (sourceFile != null) {
                ProcessHandler bscProcess = bsc.recreate(sourceFile, CliType.standard);
                if (bscProcess != null) {
                    m_console.attachToProcess(bscProcess);
                    bsc.startNotify();
                    InsightManagerImpl.getInstance(m_project).downloadRincewindIfNeeded();
                }
            }
        }
    }
}
