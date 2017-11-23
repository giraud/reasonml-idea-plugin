package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTreeUtil {

    public static <T extends PsiElement> T getNextSiblingOfType(@Nullable PsiElement sibling, @NotNull IElementType type) {
        if (sibling == null) {
            return null;
        }
        for (PsiElement child = sibling.getNextSibling(); child != null; child = child.getNextSibling()) {
            if (child.getNode().getElementType().equals(type)) {
                //noinspection unchecked
                return (T) child;
            }
        }
        return null;
    }

}
