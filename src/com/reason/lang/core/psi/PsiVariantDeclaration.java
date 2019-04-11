package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static java.util.Collections.emptyList;

public class PsiVariantDeclaration extends ASTWrapperPsiElement {

    public PsiVariantDeclaration(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiVariant getVariant() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiVariant.class);
    }

    @NotNull
    public Collection<PsiParameter> getParameterList() {
        PsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class);
        return parameters == null ? emptyList() : ORUtil.findImmediateChildrenOfClass(parameters, PsiParameter.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "Variant declaration";
    }

}
