package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.alwaysFalse;
import static com.intellij.patterns.StandardPatterns.or;

public class OclCompletionContributor extends CompletionContributor {

    static CompletionPatterns PATTERNS = new OclCompletionPatterns();

    OclCompletionContributor() {
        super(OclTypes.INSTANCE, new OclModulePathFinder(), PATTERNS);
    }

    private static class OclCompletionPatterns implements CompletionPatterns {

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> declaration() {
            return psiElement().withSuperParent(2, psiElement(PsiFileModuleImpl.class));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> open() {
            return alwaysFalse();
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement().andNot(psiElement().andOr(dotExpression(), jsObject()));
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
            return psiElement().andNot(or(jsxName(), jsObject(), jsxAttribute(), dotExpression()));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> dotExpression() {
            return psiElement().afterLeaf(psiElement(OclTypes.INSTANCE.DOT));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsObject() {
            return alwaysFalse();
        }

    }
}
