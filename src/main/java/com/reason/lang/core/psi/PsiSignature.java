package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.HMSignature;
import org.jetbrains.annotations.NotNull;

public interface PsiSignature extends PsiElement {
    @NotNull
    HMSignature asHMSignature();

    @NotNull
    String asString();
}
