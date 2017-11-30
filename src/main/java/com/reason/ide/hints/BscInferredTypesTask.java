package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.bs.hints.BsQueryTypesService;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleFile;
import com.reason.lang.core.psi.PsiValueName;

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
        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(m_psiFile.getProject());
        BsQueryTypesServiceComponent.InferredTypes inferredTypes = bucklescript.queryTypes(m_psiFile.getVirtualFile());

        ApplicationManager.getApplication().runReadAction(() -> {
            for (PsiLet letStatement : m_letExpressions) {
                PsiElement letParent = letStatement.getParent();
                if (letParent instanceof PsiModuleFile) {
                    applyType(inferredTypes, letStatement);
                } else {
                    PsiModule letModule = PsiTreeUtil.getParentOfType(letStatement, PsiModule.class);
                    if (letModule != null) {
                        BsQueryTypesServiceComponent.InferredTypes inferredModuleTypes = inferredTypes.getModuleType(letModule.getName());
                        if (inferredModuleTypes != null) {
                            applyType(inferredModuleTypes, letStatement);
                        }
                    }
                }
            }
        });
    }

    private void applyType(BsQueryTypesService.InferredTypes inferredTypes, PsiLet letStatement) {
        PsiValueName letName = letStatement.getLetName();
        if (letName != null) {
            String type = inferredTypes.getLetType(letName.getName());
            if (type != null) {
                letStatement.setInferredType(type);
            }
        }
    }
}
