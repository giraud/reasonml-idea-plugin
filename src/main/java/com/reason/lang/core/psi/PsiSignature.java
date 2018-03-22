package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.HMSignature;

public interface PsiSignature extends PsiElement {
    HMSignature asHMSignature();
}
