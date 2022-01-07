package com.reason.lang;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public abstract class BaseQNameFinder implements QNameFinder {

    @NotNull
    protected String extractPathName(@NotNull PsiElement element, @NotNull ORTypes types) {
        String path = "";

        boolean isTagName = element instanceof PsiUpperSymbol && (element.getFirstChild() instanceof PsiLeafTagName);
        PsiElement prevLeaf = isTagName ? element.getFirstChild() : PsiTreeUtil.prevVisibleLeaf(element);
        if (prevLeaf != null && (prevLeaf.getNode().getElementType() == types.DOT || prevLeaf instanceof PsiLeafTagName)) {
            // Extract the qualified name of current element
            PsiElement prevSibling = prevLeaf.getPrevSibling();

            if (prevLeaf instanceof PsiLeafTagName) {
                String name = prevLeaf.getText();
                path = name == null ? "" : name;
                prevSibling = prevLeaf.getParent().getPrevSibling();
            } else if (prevSibling instanceof PsiUpperSymbol) {
                String name = prevSibling.getText();
                path = name == null ? "" : name;
                prevSibling = prevSibling.getPrevSibling();
            }

            while (prevSibling != null && prevSibling.getNode().getElementType() == types.DOT) {
                prevSibling = prevSibling.getPrevSibling();
                if (prevSibling instanceof PsiUpperSymbol) {
                    path = prevSibling.getText() + "." + path;
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
