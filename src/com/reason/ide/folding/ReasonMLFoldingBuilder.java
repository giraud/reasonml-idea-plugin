package com.reason.ide.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.ReasonMLTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReasonMLFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        PsiTreeUtil.processElements(root, element -> {
            IElementType elementType = element.getNode().getElementType();
            if (elementType.equals(ReasonMLTypes.COMMENT)) {
                descriptors.add(fold(element));
            }
            if (elementType.equals(ReasonMLTypes.MODULE)) {
                PsiElement lBrace = ReasonMLPsiTreeUtil.getNextSiblingOfType(element, ReasonMLTypes.LBRACE);
                PsiElement rBrace = ReasonMLPsiTreeUtil.getNextSiblingOfType(lBrace, ReasonMLTypes.RBRACE);
                if (lBrace != null && rBrace != null) {
                    FoldingDescriptor fold = foldBetween(element, lBrace, rBrace, 5);
                    if (fold != null)
                        descriptors.add(fold);
                }
            }
            return true;
        });

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        if (node.getElementType().equals(ReasonMLTypes.COMMENT)) {
            return "/*...*/";
        }
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }

    @Nullable
    private FoldingDescriptor foldBetween(PsiElement element, PsiElement left, PsiElement right, int minWidth) {
        if (right.getTextOffset() - left.getTextOffset() < minWidth) {
            return null;
        }
        TextRange range = new TextRange(left.getTextOffset() + 1, right.getTextOffset());
        return new FoldingDescriptor(element, range);
    }

    private FoldingDescriptor fold(PsiElement element) {
        return new FoldingDescriptor(element, element.getTextRange());
    }
}
