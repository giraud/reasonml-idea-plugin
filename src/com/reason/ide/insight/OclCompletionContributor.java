package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.reason.ide.insight.pattern.ORElementPattern;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.alwaysFalse;
import static com.intellij.patterns.StandardPatterns.or;
import static com.reason.ide.insight.CompletionUtils.getParentWithoutIdeaRulezzz;
import static com.reason.ide.insight.CompletionUtils.getPrevNodeType;

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

        private boolean testOpenInclude(@NotNull PsiElement element, @Nullable ProcessingContext context) {
            IElementType prevNodeType = getPrevNodeType(element);
            if (prevNodeType == OclTypes.INSTANCE.OPEN || prevNodeType == OclTypes.INSTANCE.INCLUDE) {
                return true;
            }

            PsiElement parent = getParentWithoutIdeaRulezzz(element);
            return parent instanceof PsiOpen || parent instanceof PsiInclude;
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> openInclude() {
            return ORElementPattern.create(this::testOpenInclude);
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> keyword() {
            return psiElement().andNot(psiElement().andOr(openInclude(), dotExpression(), jsObject()));
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
            return psiElement().andNot(or(openInclude(), jsxName(), jsObject(), jsxAttribute(), dotExpression()));
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
