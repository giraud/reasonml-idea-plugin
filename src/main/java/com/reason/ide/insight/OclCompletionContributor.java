package com.reason.ide.insight;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.ocaml.OclTypes;

public class OclCompletionContributor extends CompletionContributor {

    static CompletionPatterns PATTERNS = new OclCompletionPatterns();

    OclCompletionContributor() {
        super(OclTypes.INSTANCE, PATTERNS);
    }

    private static class OclCompletionPatterns implements CompletionPatterns {

        @Override
        public PsiElementPattern.Capture<PsiElement> declaration() {
            return PlatformPatterns.psiElement();
        }

        @Override
        public PsiElementPattern.Capture<PsiElement> open() {
            return PlatformPatterns.psiElement();
        }
    }
}
