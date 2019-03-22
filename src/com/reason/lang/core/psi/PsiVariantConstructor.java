package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static java.util.Collections.emptyList;

public class PsiVariantConstructor extends ASTWrapperPsiElement {

    public PsiVariantConstructor(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        PsiElement name = getFirstChild();
        return name == null ? "" : name.getText();
    }

    @NotNull
    public Collection<PsiParameter> getParameterList() {
        PsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class);
        return parameters == null ? emptyList() : ORUtil.findImmediateChildrenOfClass(parameters, PsiParameter.class);
    }

    @NotNull
    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getName();
    }

}
