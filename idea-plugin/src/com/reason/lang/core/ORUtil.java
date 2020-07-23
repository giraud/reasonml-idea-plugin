package com.reason.lang.core;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.napkin.NsLanguage;
import com.reason.lang.napkin.NsTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;

public class ORUtil {

    private ORUtil() {
    }

    @NotNull
    public static String fileNameToModuleName(@NotNull PsiFile file) {
        return fileNameToModuleName(file.getName());
    }

    @NotNull
    public static String moduleNameToFileName(@NotNull String name) {
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
    public static TextRange getTextRangeForReference(@NotNull PsiNameIdentifierOwner name) {
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

    @Nullable
    public static PsiElement nextSiblingWithTokenType(@NotNull PsiElement root, @NotNull ORCompositeType elementType) {
        return nextSiblingWithTokenType(root, (IElementType) elementType);
    }

    @NotNull
    public static String getTextUntilTokenType(@NotNull PsiElement root, @Nullable IElementType elementType) {
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

        return text.trim();
    }

    @NotNull
    public static String getTextUntilClass(@NotNull PsiElement root, @Nullable Class<?> clazz) {
        String text = root.getText();

        PsiElement sibling = root.getNextSibling();
        while (sibling != null) {
            if (clazz != null && sibling.getClass().isAssignableFrom(clazz)) {
                sibling = null;
            } else {
                text += sibling.getText();
                sibling = sibling.getNextSibling();
            }
        }

        return text.trim();
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
    public static PsiElement nextSibling(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }

        PsiElement nextSibling = element.getNextSibling();
        while (nextSibling != null && nextSibling.getNode().getElementType() == TokenType.WHITE_SPACE) {
            nextSibling = nextSibling.getNextSibling();
        }

        return nextSibling;
    }

    @Nullable
    public static <T> T nextSiblingOfClass(@Nullable PsiElement element, @NotNull Class<T> clazz) {
        if (element == null) {
            return null;
        }

        PsiElement nextSibling = element.getNextSibling();
        while (nextSibling != null && !(nextSibling.getClass().isAssignableFrom(clazz))) {
            nextSibling = nextSibling.getNextSibling();
        }
        return nextSibling != null && nextSibling.getClass().isAssignableFrom(clazz) ? (T) nextSibling : null;
    }

    @NotNull
    public static <T> List<T> findImmediateChildrenOfClass(@Nullable PsiElement element, @NotNull Class<T> clazz) {
        if (element == null) {
            return Collections.emptyList();
        }

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

    @NotNull
    public static Collection<PsiElement> findImmediateChildrenOfType(@Nullable PsiElement element, @NotNull IElementType elementType) {
        PsiElement child = element == null ? null : element.getFirstChild();
        if (child == null) {
            return Collections.emptyList();
        }

        List<PsiElement> result = new ArrayList<>();

        while (child != null) {
            if (child.getNode().getElementType() == elementType) {
                result.add(child);
            }
            child = child.getNextSibling();
        }

        return result;
    }

    @NotNull
    public static Collection<PsiElement> findImmediateChildrenOfType(@Nullable PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateChildrenOfType(element, (IElementType) elementType);
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfType(@NotNull PsiElement element, @NotNull IElementType elementType) {
        Collection<PsiElement> children = findImmediateChildrenOfType(element, elementType);
        return children.isEmpty() ? null : children.iterator().next();
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfType(@NotNull PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateFirstChildOfType(element, (IElementType) elementType);
    }

    @Nullable
    public static <T extends PsiElement> T findImmediateFirstChildOfClass(@NotNull PsiElement element, @NotNull Class<T> clazz) {
        PsiElement child = element.getFirstChild();

        while (child != null) {
            if (clazz.isInstance(child)) {
                return (T) child;
            }
            child = child.getNextSibling();
        }

        return null;
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfAnyClass(@NotNull PsiElement element, @NotNull Class<?>... clazz) {
        PsiElement child = element.getFirstChild();

        while (child != null) {
            for (Class<?> aClazz : clazz) {
                if (aClazz.isInstance(child)) {
                    return child;
                }
            }
            child = child.getNextSibling();
        }

        return null;
    }

    @Nullable
    public static PsiElement findImmediateFirstChildWithoutClass(@NotNull PsiElement element, @NotNull Class<?> clazz) {
        PsiElement child = element.getFirstChild();

        while (child != null) {
            if (!clazz.isInstance(child) && !(child instanceof PsiWhiteSpace)) {
                return child;
            }
            child = child.getNextSibling();
        }

        return null;
    }

    @NotNull
    public static String getQualifiedPath(@NotNull PsiNameIdentifierOwner element) {
        String path = "";

        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof PsiQualifiedElement) {
                if (parent instanceof PsiNameIdentifierOwner && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
                    return ((PsiQualifiedElement) parent).getPath();
                }
                return ((PsiQualifiedElement) parent).getQualifiedName() + (path.isEmpty() ? "" : "." + path);
            } else {
                if (parent instanceof PsiNameIdentifierOwner) {
                    String parentName = ((PsiNamedElement) parent).getName();
                    if (parentName != null && !parentName.isEmpty()) {
                        path = parentName + (path.isEmpty() ? "" : "." + path);
                    }
                }
                parent = parent.getParent();
            }
        }

        try {
            PsiFile containingFile = element.getContainingFile(); // Fail in 2019.2... ?
            return ((FileBase) containingFile).getModuleName() + (path.isEmpty() ? "" : "." + path);
        } catch (PsiInvalidElementAccessException e) {
            return path;
        }
    }

    @NotNull
    public static String getQualifiedName(@NotNull PsiNameIdentifierOwner element) {
        String name = element.getName();
        String qualifiedPath = getQualifiedPath(element);
        return name == null ? qualifiedPath + ".UNKNOWN" : qualifiedPath + "." + name;
    }

    @NotNull
    public static ORTypes getTypes(@NotNull Language language) {
        return language == NsLanguage.INSTANCE ? NsTypes.INSTANCE : language == RmlLanguage.INSTANCE ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
    }

    @Nullable
    public static String computeAlias(@Nullable PsiElement rootElement, @NotNull Language language, boolean lowerAccepted) {
        boolean isALias = true;

        PsiElement currentElement = rootElement;
        ORTypes types = getTypes(language);
        StringBuilder aliasName = new StringBuilder();
        IElementType elementType = currentElement == null ? null : currentElement.getNode().getElementType();
        while (elementType != null && elementType != types.SEMI) {
            if (elementType != TokenType.WHITE_SPACE && elementType != types.C_UPPER_SYMBOL && elementType != types.DOT) {
                // if last term is lower symbol, and we accept lower symbol, then it's an alias
                if (elementType != types.C_LOWER_SYMBOL || currentElement.getNextSibling() != null || !lowerAccepted) {
                    isALias = false;
                    break;
                }
            }

            if (elementType != TokenType.WHITE_SPACE) {
                aliasName.append(currentElement.getText());
            }

            currentElement = currentElement.getNextSibling();
            elementType = currentElement == null ? null : currentElement.getNode().getElementType();
        }

        return isALias ? aliasName.toString() : null;
    }
}
