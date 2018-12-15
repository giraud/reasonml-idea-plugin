package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

interface CompletionPatterns {

    @NotNull
    ElementPattern<? extends PsiElement> declaration();

    @NotNull
    ElementPattern<? extends PsiElement> openInclude();

    @NotNull
    ElementPattern<? extends PsiElement> keyword();

    @NotNull
    ElementPattern<? extends PsiElement> jsxName();

    @NotNull
    ElementPattern<? extends PsiElement> jsxAttribute();

    @NotNull
    ElementPattern<? extends PsiElement> freeExpression();

    @NotNull
    ElementPattern<? extends PsiElement> dotExpression();

    @NotNull
    ElementPattern<? extends PsiElement> jsObject();

}
