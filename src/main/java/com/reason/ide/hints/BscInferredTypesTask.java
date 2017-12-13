package com.reason.ide.hints;

import java.util.*;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.RmlFile;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.bs.hints.BsQueryTypesService;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiLet;

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
                if (letParent instanceof RmlFile) {
                    applyType(inferredTypes, letStatement);
                } else {
                    PsiModule letModule = PsiTreeUtil.getParentOfType(letStatement, PsiModule.class);
                    if (letModule != null && inferredTypes != null) {
                        BsQueryTypesServiceComponent.InferredTypes inferredModuleTypes = inferredTypes.getModuleType(letModule.getName());
                        if (inferredModuleTypes != null) {
                            applyType(inferredModuleTypes, letStatement);
                        }
                    }
                }
            }
        });
    }

    private void applyType(@Nullable BsQueryTypesService.InferredTypes inferredTypes, PsiLet letStatement) {
        PsiElement letName = letStatement.getLetName();
        if (letName != null && inferredTypes != null) {
            String type = inferredTypes.getLetType(letName.getText());
            if (type != null) {
                letStatement.setInferredType(type);
            }
        }
    }
}
