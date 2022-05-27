package com.reason.ide.search;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTypeElementProvider {
    private PsiTypeElementProvider() {
    }

    @Nullable
    public static String getType(@NotNull PsiElement element) {
        if (element instanceof PsiModule) {
            return "module";
        }
        if (element instanceof PsiException) {
            return "exception";
        }
        if (element instanceof PsiLet) {
            return "let";
        }
        if (element instanceof PsiVal) {
            return "val";
        }
        if (element instanceof PsiType) {
            return "type";
        }
        if (element instanceof PsiExternal) {
            return "external";
        }

        //if (element instanceof PsiUpperSymbol) { // zzz missing
        //    if (parent instanceof PsiVariantDeclaration) {
        //        return "variant";
        //    }
        //} else if (element instanceof PsiLowerSymbol) {
        //    if (parent instanceof PsiParameter) {
        //        return "parameter";
        //    }
        //}

        return null;
    }
}
