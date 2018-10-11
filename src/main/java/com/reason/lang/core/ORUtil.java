package com.reason.lang.core;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ORUtil {

    private ORUtil() {
    }

    @NotNull
    public static String fileNameToModuleName(@NotNull PsiFile file) {
        return fileNameToModuleName(file.getName());
    }

    @NotNull
    static String moduleNameToFileName(@NotNull String name) {
        if (name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toLowerCase(Locale.getDefault()) + name.substring(1);
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
    public static PsiElement prevSibling(@NotNull PsiElement element) {
        // previous sibling without considering whitespace
        PsiElement prevSibling = element.getPrevSibling();
        while (prevSibling != null && prevSibling.getNode().getElementType() == TokenType.WHITE_SPACE) {
            prevSibling = prevSibling.getPrevSibling();
        }
        return prevSibling;
    }

    @NotNull
    public static List<PsiAnnotation> prevAnnotations(@NotNull PsiElement element) {
        List<PsiAnnotation> annotations = new ArrayList<>();

        PsiElement prevSibling = prevSibling(element);
        while (prevSibling instanceof PsiAnnotation) {
            annotations.add((PsiAnnotation) prevSibling);
            prevSibling = prevSibling(prevSibling);
        }

        return annotations;
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
                sibling = sibling.getNextSibling();
            }
        }

        return found;
    }

    public static String getTextUntilTokenType(@NotNull PsiElement root, @NotNull IElementType elementType) {
        String text = root.getText();

        PsiElement sibling = root.getNextSibling();
        while (sibling != null) {
            if (sibling.getNode().getElementType() == elementType) {
                sibling = null;
            } else {
                text += sibling.getText();
                sibling = sibling.getNextSibling();
            }
        }

        return text;
    }

    @NotNull
    private static TextRange rangeInParent(@NotNull TextRange parent, @NotNull TextRange child) {
        int start = child.getStartOffset() - parent.getStartOffset();
        if (start < 0) {
            return TextRange.EMPTY_RANGE;
        }

        return TextRange.create(start, start + child.getLength());
    }

    @NotNull
    public static ASTNode nextSiblingNode(@NotNull ASTNode node) {
        ASTNode nextSibling = node.getTreeNext();
        while (nextSibling.getElementType() == TokenType.WHITE_SPACE) {
            nextSibling = nextSibling.getTreeNext();
        }
        return nextSibling;
    }

    @Nullable
    public static <T> T nextSiblingOfClass(@NotNull PsiElement element, @NotNull Class<T> clazz) {
        PsiElement nextSibling = element.getNextSibling();
        while (nextSibling != null && !(nextSibling.getClass().isAssignableFrom(clazz))) {
            nextSibling = nextSibling.getNextSibling();
        }
        return nextSibling != null && nextSibling.getClass().isAssignableFrom(clazz) ? (T) nextSibling : null;
    }

    @NotNull
    public static <T> List<T> findImmediateChildrenOfClass(@NotNull PsiElement element, @NotNull Class<T> clazz) {
        PsiElement child = element.getFirstChild();
        if (child == null) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>();

        while (child != null) {
            if (clazz.isInstance(child)) {
                result.add((T) child);
            }
            child = child.getNextSibling();
        }

        return result;
    }

    public static <T> T findImmediateFirstChildOfClass(@NotNull PsiElement element, @NotNull Class<T> clazz) {
        PsiElement child = element.getFirstChild();

        while (child != null) {
            if (clazz.isInstance(child)) {
                return (T) child;
            }
            child = child.getNextSibling();
        }

        return null;
    }
}
