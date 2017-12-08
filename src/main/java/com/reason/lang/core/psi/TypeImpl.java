package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface PsiType extends PsiNamedElement {
    @Nullable
    PsiElement getScopedExpression();
}
