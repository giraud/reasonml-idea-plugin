package com.reason.lang.core.psi;

import com.reason.lang.core.HMSignature;
import org.jetbrains.annotations.NotNull;

public interface PsiVal extends PsiStructuredElement, PsiNamedElement {
    @NotNull
    HMSignature getSignature();
}
