package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;

public interface PsiVal extends PsiStructuredElement, PsiNamedElement {
    @NotNull
    String getSignature();
}
