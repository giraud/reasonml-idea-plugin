package com.reason.ide.insight;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;

public class CompletionUtils {
    public static final int KEYWORD_PRIORITY = 10;

    static PsiElement getParentWithoutIdeaRulezzz(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiUpperSymbol) {
            // Idea always add "IntellijIdeaRulezzz" for completion, and it's always the current UpperSymbol
            parent = parent.getParent();
        }
        return parent;
    }

    static IElementType getPrevNodeType(@NotNull PsiElement element) {
        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        return prevLeaf == null ? null : prevLeaf.getNode().getElementType();
    }

}
