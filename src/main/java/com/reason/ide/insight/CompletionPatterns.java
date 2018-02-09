package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;

interface CompletionPatterns {

    ElementPattern<? extends PsiElement> declaration();

    ElementPattern<? extends PsiElement> open();

    ElementPattern<? extends PsiElement> keyword();
}
