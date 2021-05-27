package com.reason.lang.core;

import com.intellij.lang.*;
import com.intellij.openapi.util.io.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.napkin.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORUtil {

    private ORUtil() {
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
        return nameWithoutExtension.substring(0, 1).toUpperCase(Locale.getDefault())
                + nameWithoutExtension.substring(1);
    }

    @Nullable
    public static PsiElement prevSibling(@NotNull PsiElement element) {
        // previous sibling without considering whitespace
        PsiElement prevSibling = element.getPrevSibling();
        while (prevSibling != null) {
            ASTNode prevNode = prevSibling.getNode();
            if (prevNode == null || prevNode.getElementType() != TokenType.WHITE_SPACE) {
                break;
            }
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

    public static @Nullable PsiElement nextSiblingWithTokenType(@NotNull PsiElement root, @NotNull IElementType elementType) {
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

    public static @Nullable PsiElement nextSiblingWithTokenType(@NotNull PsiElement root, @NotNull ORCompositeType elementType) {
        return nextSiblingWithTokenType(root, (IElementType) elementType);
    }

    public static @NotNull String getTextUntilTokenType(@NotNull PsiElement root, @Nullable IElementType elementType) {
        StringBuilder text = new StringBuilder(root.getText());

        PsiElement sibling = root.getNextSibling();
        while (sibling != null) {
            if (sibling.getNode().getElementType() == elementType) {
                sibling = null;
            } else {
                text.append(sibling.getText());
                sibling = sibling.getNextSibling();
            }
        }

        return text.toString().trim();
    }

    public static @NotNull String getTextUntilClass(@NotNull PsiElement root, @Nullable Class<?> clazz) {
        StringBuilder text = new StringBuilder(root.getText());

        PsiElement sibling = root.getNextSibling();
        while (sibling != null) {
            if (clazz != null && sibling.getClass().isAssignableFrom(clazz)) {
                sibling = null;
            } else {
                text.append(sibling.getText());
                sibling = sibling.getNextSibling();
            }
        }

        return text.toString().trim();
    }

    public static @NotNull String getTextUntilWhitespace(@NotNull PsiElement root) {
        StringBuilder text = new StringBuilder(root.getText());

        PsiElement sibling = root.getNextSibling();
        while (sibling != null) {
            if (sibling instanceof PsiWhiteSpace) {
                sibling = null;
            } else {
                text.append(sibling.getText());
                sibling = sibling.getNextSibling();
            }
        }

        return text.toString().trim();
    }

    /*
     x
     M1.M2.x
     */
    public static @NotNull String getLongIdent(@Nullable PsiElement root) {
        StringBuilder text = new StringBuilder(root == null ? "" : root.getText());
        ORTypes types = root == null ? null : ORUtil.getTypes(root.getLanguage());

        PsiElement sibling = root == null ? null : root.getNextSibling();
        while (sibling != null) {
            IElementType type = sibling.getNode().getElementType();
            if (type == types.DOT || type == types.UIDENT || type == types.LIDENT) {
                text.append(sibling.getText());
                sibling = PsiTreeUtil.nextLeaf(sibling);
            } else {
                sibling = null;
            }
        }

        return text.toString();
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
        while ((nextSibling instanceof PsiWhiteSpace)) {
            nextSibling = nextSibling.getNextSibling();
        }

        return nextSibling;
    }

    public static @NotNull <T extends PsiElement> List<T> findImmediateChildrenOfClass(@Nullable PsiElement element, @NotNull Class<T> clazz) {
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
                result.add(clazz.cast(child));
            }
            child = child.getNextSibling();
        }

        return result;
    }

    @NotNull
    public static List<PsiElement> findImmediateChildrenOfType(
            @Nullable PsiElement element, @NotNull IElementType elementType) {
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
    public static Collection<PsiElement> findImmediateChildrenOfType(
            @Nullable PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateChildrenOfType(element, (IElementType) elementType);
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfType(
            @NotNull PsiElement element, @NotNull IElementType elementType) {
        Collection<PsiElement> children = findImmediateChildrenOfType(element, elementType);
        return children.isEmpty() ? null : children.iterator().next();
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfType(
            @NotNull PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateFirstChildOfType(element, (IElementType) elementType);
    }

    @Nullable
    public static <T extends PsiElement> T findImmediateFirstChildOfClass(@Nullable PsiElement element, @NotNull Class<T> clazz) {
        PsiElement child = element == null ? null : element.getFirstChild();

        while (child != null) {
            if (clazz.isInstance(child)) {
                return clazz.cast(child);
            }
            child = child.getNextSibling();
        }

        return null;
    }

    public static @Nullable PsiElement findImmediateFirstChildOfAnyClass(@NotNull PsiElement element, @NotNull Class<?>... clazz) {
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

    public static @Nullable PsiElement findImmediateFirstChildWithoutClass(@NotNull PsiElement element, @NotNull Class<?> clazz) {
        PsiElement child = element.getFirstChild();

        while (child != null) {
            if (!clazz.isInstance(child) && !(child instanceof PsiWhiteSpace)) {
                return child;
            }
            child = child.getNextSibling();
        }

        return null;
    }

    public static @Nullable String[] getQualifiedPath(@NotNull PsiElement element) {
        String path = "";

        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof PsiQualifiedPathElement) {
                if (parent instanceof PsiNameIdentifierOwner && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
                    return ((PsiQualifiedPathElement) parent).getPath();
                }
                return (((PsiQualifiedNamedElement) parent).getQualifiedName() + (path.isEmpty() ? "" : "." + path)).split("\\.");
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
            return (((FileBase) containingFile).getModuleName() + (path.isEmpty() ? "" : "." + path)).split("\\.");
        } catch (PsiInvalidElementAccessException e) {
            return path.split("\\.");
        }
    }

    @NotNull
    public static String getQualifiedName(@NotNull PsiNamedElement element) {
        String name = element.getName();
        String qualifiedPath = Joiner.join(".", getQualifiedPath(element));
        return name == null ? qualifiedPath + ".UNKNOWN" : qualifiedPath + "." + name;
    }

    @NotNull
    public static ORTypes getTypes(@NotNull Language language) {
        return language == ResLanguage.INSTANCE
                ? ResTypes.INSTANCE
                : language == RmlLanguage.INSTANCE ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
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

    public static String[] getQualifiedNameAsPath(PsiQualifiedPathElement element) {
        String[] path = element.getPath();
        if (path == null) {
            return null;
        }

        String name = element.getName();
        if (name == null) {
            return path;
        }

        String[] qpath = new String[path.length + 1];
        System.arraycopy(path, 0, qpath, 0, path.length);
        qpath[qpath.length - 1] = name;
        return qpath;
    }
}
