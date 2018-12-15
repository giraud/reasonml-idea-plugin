package com.reason.ide.insight;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.reason.ide.insight.pattern.ORElementPattern;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;
import static com.intellij.patterns.StandardPatterns.or;
import static com.reason.ide.insight.CompletionUtils.getParentWithoutIdeaRulezzz;
import static com.reason.ide.insight.CompletionUtils.getPrevNodeType;

public class RmlCompletionContributor extends CompletionContributor {

    @NotNull
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

        private boolean testOpenInclude(@NotNull PsiElement element, @Nullable ProcessingContext context) {
            IElementType prevNodeType = getPrevNodeType(element);
            if (prevNodeType == RmlTypes.INSTANCE.OPEN || prevNodeType == RmlTypes.INSTANCE.INCLUDE) {
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
            return psiElement().andNot(psiElement().andOr(psiElement().inside(PsiTagStart.class), openInclude(), jsxName(), dotExpression(), jsObject()));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsxName() {
            return ORElementPattern.create((element, context) -> element.getNode().getElementType() == RmlTypes.INSTANCE.TAG_NAME); // PsiTagName!
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsxAttribute() {
            return ORElementPattern.create((element, context) -> {
                PsiElement parent = element.getParent();
                return parent instanceof PsiTagProperty /*inside the prop name*/ || parent instanceof PsiTagStart;
            });
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> freeExpression() {
            return psiElement().andNot(or(openInclude(), jsxName(), jsObject(), jsxAttribute(), dotExpression()));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> dotExpression() {
            return ORElementPattern.create((element, context) -> {
                if (testOpenInclude(element, context)) {
                    return false;
                }

                PsiElement prevLeaf = PsiTreeUtil.prevLeaf(element);
                return prevLeaf != null && prevLeaf.getNode().getElementType() == RmlTypes.INSTANCE.DOT;
            });
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> jsObject() {
            return psiElement().afterLeaf(psiElement(RmlTypes.INSTANCE.SHARPSHARP));
        }

    }
}
