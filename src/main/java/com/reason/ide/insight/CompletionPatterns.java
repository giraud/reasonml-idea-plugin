package com.reason.ide.insight;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;

import static com.intellij.patterns.PlatformPatterns.psiElement;

class CompletionPatterns {

    static PsiElementPattern.Capture<PsiElement> declarationPattern() {
        return baseDeclarationPattern()/*.and(statementBeginningPattern())*/;
    }

    static PsiElementPattern.Capture<PsiElement> openPattern() {
        return psiElement().inside(PsiOpen.class);
    }

    private static PsiElementPattern.Capture<PsiElement> statementBeginningPattern() {
        return null;
    }

    private static PsiElementPattern.Capture<PsiElement> baseDeclarationPattern() {
        return psiElement().withSuperParent(2, psiElement(PsiFileModuleImpl.class));
    }
}
