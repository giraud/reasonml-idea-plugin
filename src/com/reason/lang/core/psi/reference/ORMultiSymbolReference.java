package com.reason.lang.core.psi.reference;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public abstract class ORMultiSymbolReference<T extends PsiElement> extends PsiPolyVariantReferenceBase<T> {
    protected final @Nullable String myReferenceName;
    protected final @NotNull ORTypes myTypes;

    public ORMultiSymbolReference(@NotNull T element, @NotNull ORTypes types) {
        super(element, TextRange.create(0, element.getTextLength()));
        myReferenceName = element.getText();
        myTypes = types;
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return 0 < resolveResults.length ? resolveResults[0].getElement() : null;
    }

    public @Nullable PsiElement resolveInterface() {
        ResolveResult[] resolveResults = multiResolve(false);

        if (resolveResults.length < 1) {
            return null;
        }

        if (resolveResults.length == 1) {
            return resolveResults[0].getElement();
        }

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
