package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.or;

public class RmlCompletionContributor extends CompletionContributor {

    static CompletionPatterns PATTERNS = new RmlCompletionPatterns();

    RmlCompletionContributor() {
        super(RmlTypes.INSTANCE, new RmlModulePathFinder(), PATTERNS);
    }

    private static class RmlCompletionPatterns implements CompletionPatterns {
        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> declaration() {
            return psiElement().withSuperParent(2, psiFile());
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> open() {
            return psiElement().inside(PsiOpen.class);
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement().andNot(psiElement().andOr(psiElement().inside(PsiTagStart.class), jsxName(), dotExpression(), jsObject()));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsxName() {
            return psiElement(RmlTypes.INSTANCE.TAG_NAME);
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsxAttribute() {
            return or(
                    psiElement().inside(PsiTagStart.class).andNot(psiElement().inside(PsiTagProperty.class)),
                    psiElement().inside(PsiLowerSymbol.class).withParent(PsiTagStart.class)
            );
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> freeExpression() {
            return psiElement().andNot(or(jsxName(), jsObject(), jsxAttribute(), dotExpression()));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> dotExpression() {
            return psiElement().afterLeaf(psiElement(RmlTypes.INSTANCE.DOT));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsObject() {
            return psiElement().afterLeaf(psiElement(RmlTypes.INSTANCE.SHARPSHARP));
        }
    }
}
