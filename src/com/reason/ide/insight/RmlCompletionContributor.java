package com.reason.ide.insight;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.insight.pattern.ORElementPattern;
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
            return psiElement().andNot(or(jsxName(), jsObject(), jsxAttribute(), dotExpression()));
        }

        @NotNull
        @Override
        public ElementPattern<? extends PsiElement> dotExpression() {
            return ORElementPattern.create((element, context) -> {
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
