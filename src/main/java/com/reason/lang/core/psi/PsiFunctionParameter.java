package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;

public interface PsiFunctionParameter extends PsiNamedElement {
    @NotNull
    PsiSignature getSignature();

    boolean hasDefaultValue();
}
