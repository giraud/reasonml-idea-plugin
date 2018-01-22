package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;

public interface PsiExternal extends PsiNamedElement, PsiStructuredElement {
    @NotNull
    String getSignature();
}
