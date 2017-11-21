package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.RmlFile;
import com.reason.bs.BscQueryTypesService;
import com.reason.bs.BscQueryTypesServiceComponent;
import com.reason.psi.PsiLet;
import com.reason.psi.PsiModule;
import com.reason.psi.PsiValueName;

import java.util.Collection;

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

        BscQueryTypesServiceComponent.InferredTypes inferredTypes = bscTypes.types(project, m_psiFile.getVirtualFile());

        ApplicationManager.getApplication().runReadAction(() -> {
            for (PsiLet letStatement : m_letExpressions) {
                PsiElement letParent = letStatement.getParent();
                if (letParent instanceof RmlFile) {
                    applyType(inferredTypes, letStatement);
                } else {
                    PsiModule letModule = PsiTreeUtil.getParentOfType(letStatement, PsiModule.class);
                    if (letModule != null) {
                        BscQueryTypesServiceComponent.InferredTypes inferredModuleTypes = inferredTypes.getModuleType(letModule.getName());
                        if (inferredModuleTypes != null) {
                            applyType(inferredModuleTypes, letStatement);
                        }
                    }
                }
            }
        });
    }

    private void applyType(BscQueryTypesService.InferredTypes inferredTypes, PsiLet letStatement) {
        PsiValueName letName = letStatement.getLetName();
        if (letName != null) {
            String type = inferredTypes.getLetType(letName.getValue());
            if (type != null) {
                letStatement.setInferredType(type);
            }
        }
    }
}
