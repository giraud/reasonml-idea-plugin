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
        if (element instanceof RPsiModule) {
            return "module";
        }
        if (element instanceof RPsiException) {
            return "exception";
        }
        if (element instanceof RPsiLet) {
            return "let";
        }
        if (element instanceof RPsiVal) {
            return "val";
        }
        if (element instanceof RPsiType) {
            return "type";
        }
        if (element instanceof RPsiExternal) {
            return "external";
        }
        if (element instanceof RPsiVariantDeclaration) {
            return "variant";
        }
        if (element instanceof RPsiParameterDeclaration) {
            return "parameter";
        }
        if (element instanceof RPsiRecordField) {
            return "record field";
        }
        if (element instanceof RPsiObjectField) {
            return "object field";
        }

        return null;
    }
}
