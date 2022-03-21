package com.reason.ide.go;

import com.intellij.codeInsight.navigation.actions.*;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
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
            if (resolveResults.length == 1) {
                return resolveResults[0].getElement();
            } else if (resolveResults.length > 1) {
                // Look into other resolved elements to find an equivalent interface if one exist
                for (ResolveResult resolved : resolveResults) {
                    PsiElement element = resolved.getElement();
                    FileBase file = element == null ? null : (FileBase) element.getContainingFile();
                    if (file != null && file.isInterface()) {
                        return element;
                    }
                }

                return resolveResults[0].getElement();
            }
        }

        return reference == null ? null : reference.resolve();
    }
}
