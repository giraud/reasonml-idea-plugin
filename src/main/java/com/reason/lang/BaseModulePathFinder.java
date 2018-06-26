package com.reason.lang;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseModulePathFinder implements ModulePathFinder {

    @NotNull
    protected String extractPathName(@NotNull PsiElement element, @NotNull MlTypes types) {
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

    @Nullable
    protected String findModuleAlias(@NotNull Project project, @Nullable String qname) {
        // qname might be also an alias !
        if (qname != null) {
            PsiModule moduleAlias1 = PsiFinder.getInstance().findModuleAlias(project, qname);
            if (moduleAlias1 != null) {
                return moduleAlias1.getQualifiedName();
            }
        }
        return qname;
    }
}
