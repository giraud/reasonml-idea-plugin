package com.reason.lang.core;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

public interface ExpressionFilter {
  boolean accept(@NotNull PsiNamedElement element);
}
