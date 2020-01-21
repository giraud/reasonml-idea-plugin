package com.reason.ide.search;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTypeElementProvider {

    @Nullable
    public static String getType(@NotNull PsiElement element) {
        if (element instanceof PsiUpperSymbol)  {
            return ((PsiUpperSymbol) element).isVariant() ? "variant" : "module";
        }
        if (element instanceof PsiModule) {
            return "module";
        }
        if (element instanceof PsiLowerSymbol) {
            return "symbol";
        }
        if (element instanceof PsiLet) {
            return "let";
        }
        if (element instanceof PsiVal) {
            return "val";
        }
        if (element instanceof PsiExternal) {
            return "external";
        }
        if (element instanceof PsiType) {
            return "type";
        }
        if (element instanceof PsiVariantDeclaration) {
            return "variant";
        }

        return null;
    }

}
