package com.reason.ide.insight;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;

public class CompletionUtils {
    public static final int KEYWORD_PRIORITY = 10;

    static final String INTELLIJ_IDEA_RULEZZZ = "IntellijIdeaRulezzz";
    static final int INTELLIJ_IDEA_RULEZZZ_LENGTH = INTELLIJ_IDEA_RULEZZZ.length();

    static PsiElement getParentWithoutIdeaRulezzz(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiUpperSymbol) {
            // Idea always add "IntellijIdeaRulezzz" for completion, and it's always the current UpperSymbol
            parent = parent.getParent();
        }
        return parent;
    }

    static IElementType getPrevNodeType(@NotNull PsiElement element) {
        PsiElement prevLeaf = PsiTreeUtil.prevLeaf(element);
        if (prevLeaf instanceof PsiWhiteSpace) {
            prevLeaf = PsiTreeUtil.prevLeaf(prevLeaf);
        }

        return prevLeaf == null ? null : prevLeaf.getNode().getElementType();
    }

}
