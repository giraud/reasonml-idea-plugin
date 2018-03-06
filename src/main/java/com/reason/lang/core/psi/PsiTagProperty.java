package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface PsiTagProperty extends PsiElement {
    @NotNull
    String getName();
}
