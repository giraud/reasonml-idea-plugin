package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

interface CompletionPatterns {

    @NotNull
    ElementPattern<? extends PsiElement> declaration();

    @NotNull
    ElementPattern<? extends PsiElement> open();

    @NotNull
    ElementPattern<? extends PsiElement> keyword();

    @NotNull
    ElementPattern<? extends PsiElement> jsxName();

    @NotNull
    ElementPattern<? extends PsiElement> jsxAttribute();

    ElementPattern<? extends PsiElement> upperSymbol();
}
