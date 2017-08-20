package com.reason.ide.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.reason.lang.RmlTypes.*;

public class RmlFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        PsiTreeUtil.processElements(root, element -> {
            IElementType elementType = element.getNode().getElementType();
            if (COMMENT.equals(elementType)) {
                descriptors.add(fold(element));
            } else if (TYPE_EXPRESSION.equals(elementType)) {
                foldType(descriptors, (ReasonMLType) element);
            } else if (LET_EXPRESSION.equals(elementType)) {
                foldLet(descriptors, (ReasonMLLet) element);
            } else if (MODULE_EXPRESSION.equals(elementType)) {
                foldModule(descriptors, (ReasonMLModule) element);
            }
            return true;
        });

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void foldType(List<FoldingDescriptor> descriptors, ReasonMLType typeExpression) {
        ReasonMLScopedExpr scopedExpression = typeExpression.getScopedExpression();
        if (scopedExpression != null) {
            descriptors.add(fold(scopedExpression));
        }
    }

    private void foldLet(List<FoldingDescriptor> descriptors, ReasonMLLet letExpression) {
        ReasonMLFunBody functionBody = letExpression.getFunctionBody();
        if (functionBody != null) {
            descriptors.add(fold(functionBody));
        } else {
            ReasonMLLetBinding letBinding = letExpression.getLetBinding();
            if (letBinding != null) {
                descriptors.add((fold(letBinding)));
            }
        }
    }

    private void foldModule(List<FoldingDescriptor> descriptors, ReasonMLModule module) {
        ReasonMLScopedExpr moduleBody = module.getModuleBody();
        if (moduleBody != null) {
            descriptors.add(fold(moduleBody));
        }
        // PsiElement lBrace = RmlPsiTreeUtil.getNextSiblingOfType(element, LBRACE);
        // PsiElement rBrace = RmlPsiTreeUtil.getNextSiblingOfType(lBrace, RBRACE);
        // if (lBrace != null && rBrace != null) {
        //     FoldingDescriptor fold = foldBetween(element, lBrace, rBrace, 5);
        //     if (fold != null)
        //         descriptors.add(fold);
        // }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        if (node.getElementType().equals(COMMENT)) {
            return "/*...*/";
        }
        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }

    // @Nullable
    // private FoldingDescriptor foldBetween(PsiElement element, PsiElement left, PsiElement right, int minWidth) {
    //     if (right.getTextOffset() - left.getTextOffset() < minWidth) {
    //         return null;
    //     }
    //     TextRange range = new TextRange(left.getTextOffset() + 1, right.getTextOffset());
    //     return new FoldingDescriptor(element, range);
    // }

    private FoldingDescriptor fold(PsiElement element) {
        return new FoldingDescriptor(element, element.getTextRange());
    }
}
