package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class PsiFunction extends ASTWrapperPsiElement {

    public PsiFunction(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    public PsiFunctionBody getBody() {
        return PsiTreeUtil.findChildOfType(this, PsiFunctionBody.class);
    }

    @Override
    public String toString() {
        return "Function";
    }

    @NotNull
    public Collection<PsiFunctionParameter> getParameterList() {
        PsiParameters parameters = findChildByClass(PsiParameters.class);
        Collection<PsiFunctionParameter> params = PsiTreeUtil.findChildrenOfType(parameters, PsiFunctionParameter.class);
        if (params.isEmpty()) {
            return Collections.emptyList();
        }

        return params;
    }
}