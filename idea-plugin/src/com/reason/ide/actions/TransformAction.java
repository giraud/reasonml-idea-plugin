package com.reason.ide.actions;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class TransformAction extends ConvertAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();

        if (project != null && file != null) {
            apply(project, file, true);
        }
    }
}
