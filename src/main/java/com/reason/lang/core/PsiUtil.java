package com.reason.lang.core;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PsiUtil {

    @NotNull
    public static String fileNameToModuleName(@NotNull PsiFile file) {
        return fileNameToModuleName(file.getName());
    }

    @NotNull
    public static String fileNameToModuleName(@NotNull String filename) {
        String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(filename);
        if (nameWithoutExtension.isEmpty()) {
            return "";
        }
        return nameWithoutExtension.substring(0, 1).toUpperCase(Locale.getDefault()) + nameWithoutExtension.substring(1);
    }

    @NotNull
    public static TextRange getTextRangeForReference(@NotNull PsiNamedElement name) {
        PsiElement nameIdentifier = name.getNameIdentifier();
        return rangeInParent(name.getTextRange(), nameIdentifier == null ? TextRange.EMPTY_RANGE : name.getTextRange());
    }

    @Nullable
    public static PsiElement nextSiblingWithTokenType(@NotNull PsiElement root, @NotNull IElementType elementType) {
        PsiElement found = null;

        PsiElement sibling = root.getNextSibling();
        while (sibling != null) {
            if (sibling.getNode().getElementType() == elementType) {
                found = sibling;
                sibling = null;
            } else {
                sibling = root.getNextSibling();
            }
        }

        return found;
    }

    @NotNull
    private static TextRange rangeInParent(@NotNull TextRange parent, @NotNull TextRange child) {
        int start = child.getStartOffset() - parent.getStartOffset();
        if (start < 0) {
            return TextRange.EMPTY_RANGE;
        }

        return TextRange.create(start, start + child.getLength());
    }
}
