package com.reason.lang.core;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.openapi.util.io.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import com.reason.lang.rescript.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORUtil {
    private static final String[] EMPTY_PATH = new String[0];

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

    @Nullable
    public static PsiElement prevPrevSibling(@NotNull PsiElement element) {
        PsiElement prevElement = prevSibling(element);
        return prevElement == null ? null : prevSibling(prevElement);
    }

    @NotNull
    public static List<RPsiAnnotation> prevAnnotations(@NotNull PsiElement element) {
        List<RPsiAnnotation> annotations = new ArrayList<>();

        PsiElement prevSibling = prevSibling(element);
        while (prevSibling instanceof RPsiAnnotation) {
            annotations.add((RPsiAnnotation) prevSibling);
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

    /*
     x
     M1.M2.x
     */
    public static @NotNull String getLongIdent(@Nullable PsiElement root) {
        StringBuilder text = new StringBuilder(root == null ? "" : root.getText());
        ORLangTypes types = root == null ? null : ORUtil.getTypes(root.getLanguage());

        PsiElement sibling = root == null ? null : root.getNextSibling();
        while (sibling != null) {
            IElementType type = sibling.getNode().getElementType();
            if (type == types.DOT || type == types.UIDENT || type == types.LIDENT || type == types.A_UPPER_TAG_NAME || type == types.A_LOWER_TAG_NAME) {
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

    public static @Nullable PsiElement nextSibling(@Nullable PsiElement element) {
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

        return PsiTreeUtil.getStubChildrenOfTypeAsList(element, clazz);
    }

    @NotNull
    public static List<PsiElement> findImmediateChildrenOfType(@Nullable PsiElement element, @NotNull IElementType elementType) {
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
    public static List<PsiElement> findImmediateChildrenOfType(@Nullable PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateChildrenOfType(element, (IElementType) elementType);
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfType(@Nullable PsiElement element, @NotNull IElementType elementType) {
        Collection<PsiElement> children = findImmediateChildrenOfType(element, elementType);
        return children.isEmpty() ? null : children.iterator().next();
    }


    public static @Nullable PsiElement findImmediateLastChildOfType(@Nullable PsiElement element, @NotNull IElementType elementType) {
        Collection<PsiElement> children = findImmediateChildrenOfType(element, elementType);
        Iterator<PsiElement> it = children.iterator();

        PsiElement child = null;
        while (it.hasNext()) {
            child = it.next();
        }

        return child;
    }

    @Nullable
    public static PsiElement findImmediateFirstChildOfType(@Nullable PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateFirstChildOfType(element, (IElementType) elementType);
    }

    @Nullable
    public static PsiElement findImmediateLastChildOfType(@Nullable PsiElement element, @NotNull ORCompositeType elementType) {
        return findImmediateLastChildOfType(element, (IElementType) elementType);
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

    @Nullable
    public static <T extends PsiElement> T findImmediateLastChildOfClass(@Nullable PsiElement element, @NotNull Class<T> clazz) {
        PsiElement child = element == null ? null : element.getFirstChild();
        T found = null;

        while (child != null) {
            if (clazz.isInstance(child)) {
                //noinspection unchecked
                found = (T) child;
            }
            child = child.getNextSibling();
        }

        return found;
    }

    public static @Nullable PsiElement findImmediateFirstChildOfAnyClass(@NotNull PsiElement element, Class<?> @NotNull ... clazz) {
        PsiElement child = element.getFirstChild();

        while (child != null) {
            if (!(child instanceof PsiWhiteSpace)) {
                for (Class<?> aClazz : clazz) {
                    if (aClazz.isInstance(child)) {
                        return child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        return null;
    }

    public static String @NotNull [] getQualifiedPath(@NotNull PsiElement element) {
        String path = "";

        PsiElement parent = element.getParent();
        while (parent != null) {
            if (parent instanceof RPsiParameterReference) {
                PsiElement parameters = parent.getParent();
                PsiElement functionCall = parameters == null ? null : parameters.getParent();
                if (parameters instanceof RPsiParameters && (functionCall instanceof RPsiFunctionCall || functionCall instanceof RPsiFunctorCall)) {
                    int index = ((RPsiParameters) parameters).getParametersList().indexOf(parent);
                    if (index >= 0) {
                        path = ((NavigationItem) functionCall).getName() + "[" + index + "]" + (path.isEmpty() ? "" : "." + path);
                    }
                    parent = functionCall.getParent();
                } else {
                    parent = parameters;
                }
            } else if (parent instanceof RPsiQualifiedPathElement) {
                if (parent instanceof PsiNameIdentifierOwner && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
                    String[] parentPath = ((RPsiQualifiedPathElement) parent).getPath();
                    return parentPath == null ? EMPTY_PATH : parentPath;
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
            PsiFile containingFile = element.getContainingFile();
            String fileName = containingFile instanceof FileBase ? ((FileBase) containingFile).getModuleName() : containingFile.getName();
            return (fileName + (path.isEmpty() ? "" : "." + path)).split("\\.");
        } catch (PsiInvalidElementAccessException e) {
            return path.split("\\.");
        }
    }

    @NotNull
    public static String getQualifiedName(@NotNull PsiNamedElement element) {
        String qualifiedPath = Joiner.join(".", getQualifiedPath(element));
        String name = element.getName();
        if (name == null) {
            String nullifier = element instanceof RPsiModuleSignature ? "" : ".<UNKNOWN>";  // anonymous signature type is ok
            return qualifiedPath + nullifier;
        }
        return qualifiedPath + "." + name;
    }

    @NotNull
    public static ORLangTypes getTypes(@NotNull Language language) {
        return language == ResLanguage.INSTANCE
                ? ResTypes.INSTANCE
                : language == RmlLanguage.INSTANCE ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
    }

    @Nullable
    public static String computeAlias(@Nullable PsiElement rootElement, @NotNull Language language, boolean lowerAccepted) {
        boolean isALias = true;

        PsiElement currentElement = rootElement;
        ORLangTypes types = getTypes(language);
        StringBuilder aliasName = new StringBuilder();
        IElementType elementType = currentElement == null ? null : currentElement.getNode().getElementType();
        while (elementType != null && elementType != types.SEMI && elementType != types.EOL) {
            if (elementType != TokenType.WHITE_SPACE && elementType != types.A_MODULE_NAME && elementType != types.DOT) {
                // if last term is lower symbol, and we accept lower symbol, then it's an alias
                if ((elementType != types.LIDENT && elementType != types.A_VARIANT_NAME) || currentElement.getNextSibling() != null || !lowerAccepted) {
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

    public static @Nullable PsiElement resolveModuleSymbol(@Nullable RPsiUpperSymbol moduleSymbol) {
        ORPsiUpperSymbolReference reference = moduleSymbol == null ? null : moduleSymbol.getReference();
        PsiElement resolvedSymbol = reference == null ? null : reference.resolveInterface();
        return resolvedSymbol instanceof RPsiUpperSymbol ? resolvedSymbol.getParent() : resolvedSymbol;
    }

    public static <T> @NotNull List<T> findPreviousSiblingsOrParentOfClass(@NotNull PsiElement element, @NotNull Class<T> clazz) {
        List<T> result = new ArrayList<>();

        PsiElement previous = element.getPrevSibling();
        PsiElement prevSibling = previous == null ? element.getParent() : previous;
        while (prevSibling != null) {
            if (clazz.isInstance(prevSibling)) {
                //noinspection unchecked
                result.add((T) prevSibling);
            }
            previous = prevSibling.getPrevSibling();
            prevSibling = previous == null ? prevSibling.getParent() : previous;
        }

        return result;
    }

    public static @Nullable <T extends PsiNamedElement> T findImmediateNamedChildOfClass(@Nullable PsiElement element, @NotNull Class<T> clazz, @NotNull String name) {
        return ORUtil.findImmediateChildrenOfClass(element, clazz).stream().filter(item -> name.equals(item.getName())).findFirst().orElse(null);
    }

    public static boolean isPrevType(PsiElement root, ORTokenElementType elementType) {
        PsiElement prevSibling = ORUtil.prevSibling(root);
        IElementType prevType = prevSibling == null ? null : prevSibling.getNode().getElementType();
        return prevType != null && prevType == elementType;
    }

    public static boolean isInterfaceFile(@Nullable PsiElement element) {
        PsiFile file = element != null ? element.getContainingFile() : null;
        return file instanceof FileBase fileBase && fileBase.isInterface();
    }

    public static boolean inInterface(@Nullable PsiElement element) {
        PsiElement parent = PsiTreeUtil.getStubOrPsiParent(element);

        if (parent instanceof RPsiModuleSignature) {
            return true;
        } else if (parent instanceof RPsiModuleBinding moduleBinding) {
            boolean interfaceFile = isInterfaceFile(moduleBinding);
            if (interfaceFile) {
                return true;
            }

            PsiElement bindingParent = PsiTreeUtil.getStubOrPsiParent(moduleBinding);
            if (bindingParent instanceof RPsiInnerModule innerModule) {
                return innerModule.isModuleType();
            }
        }

        return isInterfaceFile(element);
    }
}
