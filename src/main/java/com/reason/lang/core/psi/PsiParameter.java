package com.reason.lang.core.psi;

public interface PsiParameter extends PsiNamedElement, PsiSignatureElement {
    boolean hasDefaultValue();
}
