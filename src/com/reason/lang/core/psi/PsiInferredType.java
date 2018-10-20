package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.HMSignature;

public interface PsiInferredType extends PsiElement {

    void setInferredType(HMSignature inferredType);

    HMSignature getInferredType();

    boolean hasInferredType();
}
