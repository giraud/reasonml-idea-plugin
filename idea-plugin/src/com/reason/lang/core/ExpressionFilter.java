package com.reason.lang.core;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiNameIdentifierOwner;

public interface ExpressionFilter {
    boolean accept(@NotNull PsiNameIdentifierOwner element);
}
