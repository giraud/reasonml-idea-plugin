package com.reason.ide.insight;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;

interface CompletionPatterns {

    PsiElementPattern.Capture<PsiElement> declaration();

    PsiElementPattern.Capture<PsiElement> open();
}
