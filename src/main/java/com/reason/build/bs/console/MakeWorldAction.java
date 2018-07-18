package com.reason.build.bs.console;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.build.bs.Bucklescript;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.bs.compiler.BsCompiler;
import com.reason.build.bs.compiler.CliType;
import com.reason.hints.InsightManagerImpl;

public class MakeWorldAction extends DumbAwareAction {

    private final ConsoleView m_console;
    private final Project m_project;

    MakeWorldAction(ConsoleView console, Project project) {
        super("Clean and make world", "Clean and make world", AllIcons.General.Web);
        m_project = project;
        m_console = console;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Bucklescript bucklescript = BucklescriptManager.getInstance(m_project);
        BsCompiler bsc = bucklescript.getOrCreateCompiler();
        if (bsc != null) {
            VirtualFile baseRoot = Platform.findBaseRoot(m_project);
            VirtualFile sourceFile = baseRoot.findChild("bsconfig.json");
            if (sourceFile != null) {
                ProcessHandler bscProcess = bsc.recreate(sourceFile, CliType.cleanMake);
                if (bscProcess != null) {
                    m_console.attachToProcess(bscProcess);
                    bsc.startNotify();
                    InsightManagerImpl.getInstance(m_project).downloadRincewindIfNeeded();
                }
            }
        }
    }
}
