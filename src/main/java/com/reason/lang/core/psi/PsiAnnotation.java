package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiAnnotation extends ASTWrapperPsiElement {

    public PsiAnnotation(ASTNode node) {
        super(node);
    }

    @NotNull
    private PsiMacroName getAnnotationNameElement() {
        return findNotNullChildByClass(PsiMacroName.class);
    }

    @Override
    public String getName() {
        return getAnnotationNameElement().getText();
    }

    @Override
    public String toString() {
        return "Annotation(" + getName() + ")";
    }
}
