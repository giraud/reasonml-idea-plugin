package com.reason.lang;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

public abstract class BaseModulePathFinder implements ModulePathFinder {

    @NotNull
    protected String extractPathName(@NotNull PsiElement element) {
        String path = "";

        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        if (prevLeaf != null && prevLeaf.getNode().getElementType() == RmlTypes.INSTANCE.DOT) {
            // Extract the qualified name of current element
            PsiElement prevSibling = prevLeaf.getPrevSibling();

            if (prevSibling instanceof PsiNamedElement) {
                String name = ((PsiNamedElement) prevSibling).getName();
                path = name == null ? "" : name;
                prevSibling = prevSibling.getPrevSibling();
            }

            while (prevSibling != null && prevSibling.getNode().getElementType() == RmlTypes.INSTANCE.DOT) {
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

}
