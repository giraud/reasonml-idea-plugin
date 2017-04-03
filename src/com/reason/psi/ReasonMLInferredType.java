package com.reason.psi;

import com.intellij.psi.PsiElement;

public interface ReasonMLInferredType extends PsiElement {

    void setInferredType(String inferredType);

    String getInferredType();

    boolean hasInferredType();
}
