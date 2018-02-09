package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
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
        public ElementPattern<? extends PsiElement> declaration() {
            return baseDeclarationPattern()/*.and(statementBeginningPattern())*/;
        }

        @Override
        public ElementPattern<? extends PsiElement> open() {
            return psiElement().inside(PsiOpen.class);
        }

        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement();
        }

        //private static PsiElementPattern.Capture<PsiElement> statementBeginningPattern() {
        //    return null;
        //}

        private static ElementPattern<? extends PsiElement> baseDeclarationPattern() {
            return psiElement().withSuperParent(2, psiElement(PsiFileModuleImpl.class));
        }

    }
}
