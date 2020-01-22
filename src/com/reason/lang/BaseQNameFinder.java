package com.reason.lang;

import java.util.*;
import java.util.stream.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.type.ORTypes;

public abstract class BaseQNameFinder implements QNameFinder {

    @NotNull
    protected String extractPathName(@NotNull PsiElement element, @NotNull ORTypes types) {
        String path = "";

        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        if (prevLeaf != null && prevLeaf.getNode().getElementType() == types.DOT) {
            // Extract the qualified name of current element
            PsiElement prevSibling = prevLeaf.getPrevSibling();

            if (prevSibling instanceof PsiNamedElement) {
                String name = ((PsiNamedElement) prevSibling).getName();
                path = name == null ? "" : name;
                prevSibling = prevSibling.getPrevSibling();
            }

            while (prevSibling != null && prevSibling.getNode().getElementType() == types.DOT) {
                prevSibling = prevSibling.getPrevSibling();
                if (prevSibling instanceof PsiNamedElement) {
                    path = ((PsiNamedElement) prevSibling).getName() + "." + path;
                    prevSibling = prevSibling.getPrevSibling();
                } else {
                    break;
                }
            }
        }
        return path;
    }

    @NotNull
    protected List<String> extendPathWith(String openName, Set<String> qualifiedNames, String pathExtension) {
        return qualifiedNames.stream().map(name -> openName + pathExtension).collect(Collectors.toList());
    }
}
