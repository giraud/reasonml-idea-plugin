package com.reason.ide.search.reference;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class ORMultiSymbolReference<T extends PsiElement> extends PsiPolyVariantReferenceBase<T> {
    protected final @Nullable String myReferenceName;
    protected final @NotNull ORLangTypes myTypes;

    protected ORMultiSymbolReference(@NotNull T element, @NotNull ORLangTypes types) {
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

        // Move inner modules first (if we found inner module and file it means that there is alternative names)
        Arrays.sort(resolveResults, (m1, m2) -> m1.getElement() instanceof RPsiInnerModule ? -1 : (m2.getElement() instanceof RPsiInnerModule ? 1 : 0));

        // Look into other resolved elements to find an equivalent interface if one exist
        for (ResolveResult resolved : resolveResults) {
            PsiElement element = resolved.getElement();
            if (ORUtil.inInterface(element)) {
                return element;
            }
        }

        return resolveResults[0].getElement();
    }
}
