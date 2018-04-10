package com.reason.lang;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BaseModulePathFinder implements ModulePathFinder {

    protected void extractQualifiedName(@NotNull PsiElement element, List<String> qualifiedNames) {
        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        if (prevLeaf != null && prevLeaf.getNode().getElementType() == RmlTypes.INSTANCE.DOT) {
            // Extract the qualified name of current element
            String path = "";
            PsiElement prevSibling = prevLeaf.getPrevSibling();

            if (prevSibling instanceof PsiNamedElement) {
                path = ((PsiNamedElement) prevSibling).getName();
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

            qualifiedNames.add(path);
        }
    }

}
