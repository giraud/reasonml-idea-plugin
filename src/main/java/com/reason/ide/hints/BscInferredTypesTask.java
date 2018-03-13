package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.bs.hints.BsQueryTypesService;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class BscInferredTypesTask implements Runnable {

    private final Editor m_selectedTextEditor;
    private final Collection<PsiLet> m_letExpressions;
    private final long m_timestamp;
    private final PsiFile m_psiFile;

    BscInferredTypesTask(PsiFile psiFile, Editor selectedTextEditor, Collection<PsiLet> letExpressions, long timetamp) {
        m_psiFile = psiFile;
        m_selectedTextEditor = selectedTextEditor;
        m_letExpressions = letExpressions;
        m_timestamp = timetamp;
    }

    @Override
    public void run() {
        Project project = m_psiFile.getProject();
        VirtualFile virtualFile = m_psiFile.getVirtualFile();

        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);
        BsQueryTypesServiceComponent.InferredTypes inferredTypes = bucklescript.queryTypes(virtualFile);

        ApplicationManager.getApplication().runReadAction(() -> {
            CodeLensView.CodeLensInfo userData = project.getUserData(CodeLensView.CODE_LENS);
            if (userData == null) {
                userData = new CodeLensView.CodeLensInfo();
                project.putUserData(CodeLensView.CODE_LENS, userData);
            } else {
                userData.clearInternalData();
            }

            for (PsiLet letStatement : m_letExpressions) {
                PsiElement letParent = letStatement.getParent();
                if (letParent instanceof PsiFileModuleImpl) {
                    String signature = applyType(inferredTypes, letStatement);
                    if (signature != null) {
                        int letOffset = letStatement.getTextOffset();
                        LogicalPosition logicalPosition = m_selectedTextEditor.offsetToLogicalPosition(letOffset);
                        userData.put(virtualFile, logicalPosition, signature, m_timestamp);
                    }
                } else {
                    PsiModule letModule = PsiTreeUtil.getParentOfType(letStatement, PsiModule.class);
                    if (letModule != null && inferredTypes != null) {
                        BsQueryTypesServiceComponent.InferredTypes inferredModuleTypes = inferredTypes.getModuleType(letModule.getName());
                        if (inferredModuleTypes != null) {
                            String signature = applyType(inferredModuleTypes, letStatement);
                            if (signature != null) {
                                int letOffset = letStatement.getTextOffset();
                                LogicalPosition logicalPosition = m_selectedTextEditor.offsetToLogicalPosition(letOffset);
                                userData.put(virtualFile, logicalPosition, signature, m_timestamp);
                            }
                        }
                    }
                }
            }
        });
    }

    private String applyType(@Nullable BsQueryTypesService.InferredTypes inferredTypes, PsiLet letStatement) {
        String signature = null;

        PsiElement letName = letStatement.getNameIdentifier();
        if (letName != null && inferredTypes != null) {
            signature = inferredTypes.getLetType(letName.getText());
            if (signature != null) {
                letStatement.setInferredType(signature);
            }
        }

        return signature;
    }
}
