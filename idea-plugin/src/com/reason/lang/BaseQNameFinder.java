package com.reason.lang;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseQNameFinder implements QNameFinder {

    @NotNull
    protected String extractPathName(@NotNull PsiElement element, @NotNull ORTypes types) {
        String path = "";

        PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        if (prevLeaf != null && prevLeaf.getNode().getElementType() == types.DOT) {
            // Extract the qualified name of current element
            PsiElement prevSibling = prevLeaf.getPrevSibling();

            if (prevSibling instanceof PsiUpperSymbol) {
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
        return qualifiedNames.stream().
                map(name -> {
                    String nameWithoutFile = name.startsWith(filePath) ? name.substring(filePath.length()) : name;
                    return openName + "." + nameWithoutFile;
                }).
                collect(Collectors.toList());
    }
}
