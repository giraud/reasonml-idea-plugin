package com.reason.ide.insight.pattern;

import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public
interface ORElementPatternMatcher {
    boolean accepts(@NotNull PsiElement element, @Nullable ProcessingContext context);
}
