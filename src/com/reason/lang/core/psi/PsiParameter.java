package com.reason.lang.core.psi;

import org.jetbrains.annotations.Nullable;

public interface PsiParameter extends PsiQualifiedElement, PsiSignatureElement {
    boolean hasDefaultValue();

    @Nullable
    String getName();
}
