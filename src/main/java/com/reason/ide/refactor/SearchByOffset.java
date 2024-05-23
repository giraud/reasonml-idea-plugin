package com.reason.ide.refactor;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public final class SearchByOffset {
    private SearchByOffset() {
    }

    public static @Nullable RPsiExpression findExpressionInRange(@NotNull FileBase file, int startOffset, int endOffset) {
        ElementRange elementRange = getElementRange(file, startOffset, endOffset);
        if (elementRange.element1 == null || elementRange.element2 == null) {
            return null;
        }
        PsiElement parent = PsiTreeUtil.findCommonParent(elementRange.element1, elementRange.element2);
        if (parent instanceof RPsiExpression parentExpression) {
            return parentExpression;
        }
        return PsiTreeUtil.getParentOfType(parent, RPsiExpression.class);
    }

    public static PsiElement findExpressionAtCaret(@NotNull FileBase file, int offset) {
        PsiElement element = file.findElementAt(offset);
        PsiElement elementBefore = file.findElementAt(offset - 1);
        if (element == null) {
            return elementBefore;
        }
        if (elementBefore == null) {
            return element;
        }
        if (PsiTreeUtil.isAncestor(element, elementBefore, false)) {
            return elementBefore;
        }
        return element;
    }

    public static ElementRange getElementRange(@NotNull FileBase file, int startOffset, int endOffset) {
        PsiElement element1 = findElementAtIgnoreWhitespaceBefore(file, startOffset);
        PsiElement element2 = findElementAtIgnoreWhitespaceBefore(file, endOffset - 1);

        //Elements have crossed (for instance when selection was inside single whitespace block)
        //if (element1.getStartOffsetInParent() >= element2.getEnd)
        return new ElementRange(element1, element2);
    }

    private static PsiElement findElementAtIgnoreWhitespaceBefore(@NotNull FileBase file, int offset) {
        PsiElement element = file.findElementAt(offset);
        if (element instanceof PsiWhiteSpace) {
            return file.findElementAt(element.getTextRange().getEndOffset());
        }
        return element;
    }

    record ElementRange(PsiElement element1, PsiElement element2) {
    }
}
