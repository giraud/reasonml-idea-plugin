package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiTagProperty extends PsiElement {
    @NotNull
    String getName();

    @Nullable
    PsiElement getValue();
}
