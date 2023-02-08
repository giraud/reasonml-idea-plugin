package com.reason.lang;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public abstract class BaseQNameFinder implements QNameFinder {

    @NotNull
    protected String extractPathName(@NotNull PsiElement element, @NotNull ORLangTypes types) {
        String path = element instanceof RPsiUpperSymbol ? element.getText() : "";

        PsiElement prevSibling = PsiTreeUtil.prevVisibleLeaf(element);
        IElementType prevType = prevSibling == null ? null : prevSibling.getNode().getElementType();
        // Some operators can be ignored
        if (prevType == types.LPAREN || prevType == types.LBRACKET || prevType == types.LBRACE) {
            prevSibling = prevSibling.getPrevSibling();
        }

        if (prevSibling != null && (prevSibling.getNode().getElementType() == types.DOT || prevSibling instanceof RPsiUpperSymbol)) {
            // Extract the qualified name of current element
            if (prevSibling instanceof RPsiUpperSymbol) {
                String name = prevSibling.getText();
                path = name == null ? path : path.isEmpty() ? name : name + "." + path;
                prevSibling = prevSibling.getPrevSibling();
            }

            while (prevSibling != null && prevSibling.getNode().getElementType() == types.DOT) {
                prevSibling = prevSibling.getPrevSibling();
                if (prevSibling instanceof RPsiUpperSymbol) {
                    path = prevSibling.getText() + (path.isEmpty() ? "" : "." + path);
                    prevSibling = prevSibling.getPrevSibling();
                } else {
                    break;
                }
            }
        }

        return path;
    }

    @NotNull
    protected List<String> extendPathWith(@NotNull String filePath, @NotNull String openName, @NotNull Set<String> qualifiedNames) {
        return qualifiedNames.stream()
                .map(name -> {
                    String nameWithoutFile = name.startsWith(filePath) ? name.substring(filePath.length()) : name;
                    return openName + "." + nameWithoutFile;
                })
                .collect(Collectors.toList());
    }
}
