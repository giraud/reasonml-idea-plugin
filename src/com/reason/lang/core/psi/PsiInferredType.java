package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORSignature;

public interface PsiInferredType extends PsiElement {

    void setInferredType(ORSignature inferredType);

    ORSignature getInferredType();

    boolean hasInferredType();
}
