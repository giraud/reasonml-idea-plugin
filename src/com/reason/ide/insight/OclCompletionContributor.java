package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.ide.insight.pattern.ORElementPattern;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.alwaysFalse;
import static com.intellij.patterns.StandardPatterns.or;

public class OclCompletionContributor extends CompletionContributor {

    @NotNull
    static CompletionPatterns PATTERNS = new OclCompletionPatterns();

    OclCompletionContributor() {
        super(OclTypes.INSTANCE, new OclModulePathFinder(), PATTERNS);
    }

    private static class OclCompletionPatterns implements CompletionPatterns {

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> declaration() {
            return psiElement().withSuperParent(2, psiFile());
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> openInclude() {
            return ORElementPattern.create((element, context) -> {
                PsiElement parent = element.getParent();
                return parent instanceof PsiOpen || parent instanceof PsiInclude;
            });
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
