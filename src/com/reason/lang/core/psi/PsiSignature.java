package com.reason.lang.core.psi;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORSignature;
import org.jetbrains.annotations.NotNull;

public interface PsiSignature extends PsiElement {
    @NotNull
    ORSignature asHMSignature();

    @NotNull
    String asString(@NotNull Language lang);
}
