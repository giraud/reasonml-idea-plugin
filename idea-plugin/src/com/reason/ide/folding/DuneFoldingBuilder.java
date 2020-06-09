package com.reason.ide.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiDuneFields;
import com.reason.lang.core.psi.PsiStanza;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DuneFoldingBuilder extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        PsiTreeUtil.processElements(root, element -> {
            if (isMultiline(element.getTextRange(), document)) {
                FoldingDescriptor fold = null;
                if (element instanceof PsiStanza) {
                    fold = foldStanza((PsiStanza) element);
                } else if (DuneTypes.INSTANCE.C_SEXPR == element.getNode().getElementType()) {
                    fold = fold(element);
                }
                if (fold != null) {
                    descriptors.add(fold);
                }
            }

            return true;
        });

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private static boolean isMultiline(@NotNull TextRange range, @NotNull Document document) {
        return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset());
    }

    @Nullable
    private FoldingDescriptor foldStanza(@NotNull PsiStanza root) {
        PsiDuneFields fields = ORUtil.findImmediateFirstChildOfClass(root, PsiDuneFields.class);
        return fields == null ? null : new FoldingDescriptor(root, fields.getTextRange());
    }

    @Nullable
    private FoldingDescriptor fold(@Nullable PsiElement root) {
        if (root == null) {
            return null;
        }

        // find next element
        ASTNode element = root.getFirstChild().getNode();
        ASTNode nextElement = element == null ? null : ORUtil.nextSiblingNode(element);
        ASTNode nextNextElement = nextElement == null ? null : ORUtil.nextSiblingNode(nextElement);

        if (nextNextElement != null) {
            TextRange rootRange = root.getTextRange();
            TextRange nextRange = nextElement.getTextRange();
            return new FoldingDescriptor(root, TextRange.create(nextRange.getEndOffset(), rootRange.getEndOffset() - 1));
        }

        return null;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
