package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface PsiParameter extends PsiElement, PsiSignatureElement {
    boolean hasDefaultValue();

    @Nullable
    String getName();
}
