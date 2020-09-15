package com.reason.lang.core;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiNamedElement;

public interface ExpressionFilter {
    boolean accept(@NotNull PsiNamedElement element);
}
