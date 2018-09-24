package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiFunctionParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PsiFunctionCallParamsImpl extends ASTWrapperPsiElement implements PsiFunctionCallParams {

    public PsiFunctionCallParamsImpl(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "function call params";
    }

    @Override
    @NotNull
    public Collection<PsiFunctionParameter> getParameterList() {
        return PsiTreeUtil.findChildrenOfType(this, PsiFunctionParameter.class);
    }
}
