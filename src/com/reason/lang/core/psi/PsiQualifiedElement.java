package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface PsiQualifiedElement extends PsiElement {
    @NotNull
    String getQualifiedName();
}
