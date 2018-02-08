package com.reason.ide.insight;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class RmlCompletionContributor extends CompletionContributor {

    static CompletionPatterns PATTERNS = new RmlCompletionPatterns();

    RmlCompletionContributor() {
        super(RmlTypes.INSTANCE, PATTERNS);
    }

    private static class RmlCompletionPatterns implements CompletionPatterns {
        @Override
        public PsiElementPattern.Capture<PsiElement> declaration() {
            return baseDeclarationPattern()/*.and(statementBeginningPattern())*/;
        }

        @Override
        public PsiElementPattern.Capture<PsiElement> open() {
            return psiElement().inside(PsiOpen.class);
        }

        //private static PsiElementPattern.Capture<PsiElement> statementBeginningPattern() {
        //    return null;
        //}

        private static PsiElementPattern.Capture<PsiElement> baseDeclarationPattern() {
            return psiElement().withSuperParent(2, psiElement(PsiFileModuleImpl.class));
        }

    }
}
