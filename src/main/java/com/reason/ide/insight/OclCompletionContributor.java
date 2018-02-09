package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.ocaml.OclTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class OclCompletionContributor extends CompletionContributor {

    static CompletionPatterns PATTERNS = new OclCompletionPatterns();

    OclCompletionContributor() {
        super(OclTypes.INSTANCE, PATTERNS);
    }

    private static class OclCompletionPatterns implements CompletionPatterns {

        @Override
        public ElementPattern<? extends PsiElement> declaration() {
            return psiElement();
        }

        @Override
        public ElementPattern<? extends PsiElement> open() {
            return psiElement();
        }

        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement();
        }
    }
}
