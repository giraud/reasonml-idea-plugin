package com.reason.ide.hints;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.reason.bs.BscQueryTypesService;
import com.reason.psi.PsiLet;
import com.reason.psi.PsiValueName;

import java.util.Collection;
import java.util.Map;

public class BscInferredTypesTask implements Runnable {

    private final Collection<PsiLet> m_letExpressions;
    private final PsiFile m_psiFile;

    BscInferredTypesTask(PsiFile psiFile, Collection<PsiLet> letExpressions) {
        m_psiFile = psiFile;
        m_letExpressions = letExpressions;
    }

    @Override
    public void run() {
        Project project = m_psiFile.getProject();
        BscQueryTypesService bscTypes = ServiceManager.getService(project, BscQueryTypesService.class);
        if (bscTypes == null) {
            return;
        }

        Map<String, String> letTypes = bscTypes.types(project, m_psiFile.getVirtualFile());

        for (PsiLet letStatement : m_letExpressions) {
            PsiValueName letName = letStatement.getLetName();
            if (letName != null) {
                String type = letTypes.get(letName.getValue());
                if (type != null) {
                    letStatement.setInferredType(type);
                }
            }
        }
    }
}
