package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.alwaysFalse;

public class OclCompletionContributor extends CompletionContributor {

    static CompletionPatterns PATTERNS = new OclCompletionPatterns();

    OclCompletionContributor() {
        super(OclTypes.INSTANCE, PATTERNS);
    }

    private static class OclCompletionPatterns implements CompletionPatterns {

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> declaration() {
            return psiElement();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> open() {
            return psiElement();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsxName() {
            return alwaysFalse();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsxAttribute() {
            return alwaysFalse();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> freeExpression() {
            return psiElement().inside(PsiUpperSymbol.class);
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> dotExpression() {
            return alwaysFalse();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsObject() {
            return alwaysFalse();
        }
    }
}
