package com.reason.ide.go;

import com.intellij.codeInsight.navigation.actions.*;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ORGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Override
    public @Nullable PsiElement getGotoDeclarationTarget(@Nullable PsiElement sourceElement, Editor editor) {
        PsiElement parent = sourceElement == null ? null : sourceElement.getParent();
        if (parent instanceof PsiUpperSymbol || parent instanceof PsiLowerSymbol) {
            return resolveInterface(parent.getReference());
        }
        return null;
    }

    public @Nullable static PsiElement resolveInterface(@Nullable PsiReference reference) {
        if (reference instanceof PsiPolyVariantReference) {
            ResolveResult[] resolveResults = ((PsiPolyVariantReference) reference).multiResolve(false);
            if (resolveResults.length > 0) {
                PsiElement resolvedElement = resolveResults[0].getElement();
                FileBase resolvedFile = resolvedElement == null ? null : (FileBase) resolvedElement.getContainingFile();

                if (resolvedFile != null && !resolvedFile.isInterface()) {
                    PsiQualifiedElement qualifiedResolvedElement = PsiTreeUtil.getParentOfType(resolvedElement, PsiQualifiedElement.class);
                    if (qualifiedResolvedElement != null) {
                        // Look into other resolved elements to find an equivalent interface if one exist
                        for (ResolveResult resolved : resolveResults) {
                            PsiElement element = resolved.getElement();
                            if (element == resolvedElement) {
                                continue;
                            }

                            PsiQualifiedElement qualifiedElement = PsiTreeUtil.getParentOfType(element, PsiQualifiedElement.class);
                            if (qualifiedElement != null && qualifiedElement.getQualifiedName().equals(qualifiedResolvedElement.getQualifiedName())) {
                                FileBase file = (FileBase) qualifiedElement.getContainingFile();
                                if (file.isInterface()) {
                                    return element;
                                }
                            }
                        }
                    }
                }

                return resolvedElement;
            }
        }

        return reference == null ? null : reference.resolve();
    }
}
