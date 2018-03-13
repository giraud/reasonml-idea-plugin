package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

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

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> open() {
            return psiElement().inside(PsiOpen.class);
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement().andNot(psiElement().andOr(psiElement().inside(PsiTagStart.class), jsxName()));
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
                    psiElement().inside(PsiTagStart.class),
                    psiElement().inside(PsiLowerSymbol.class).withParent(PsiTagStart.class)
            );
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> upperSymbol() {
            return psiElement().inside(PsiUpperSymbol.class);
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> expression() {
            return psiElement().afterLeaf(psiElement(RmlTypes.INSTANCE.DOT));
        }

        private static ElementPattern<? extends PsiElement> baseDeclarationPattern() {
            return psiElement().withSuperParent(2, psiElement(PsiFileModuleImpl.class));
        }
    }
}
