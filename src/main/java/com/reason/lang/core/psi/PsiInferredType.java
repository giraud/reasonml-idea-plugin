package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;

public interface PsiInferredType extends PsiElement {

    void setInferredType(String inferredType);

    String getInferredType();

    boolean hasInferredType();
}
