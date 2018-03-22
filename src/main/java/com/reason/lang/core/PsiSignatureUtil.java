package com.reason.lang.core;

import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiSignatureUtil {
    @NotNull
    public static String getProvidersType(@Nullable PsiNamedElement element) {
        if (element instanceof PsiExternal) {
            return ((PsiExternal) element).getSignature().toString();
        } else if (element instanceof PsiLet) {
            return ((PsiLet) element).getSignature().toString();
        }

        return "";
    }

    public static String getTypeInfo(PsiNamedElement expression) {
        if (expression instanceof PsiLet) {
            return ((PsiLet) expression).getInferredType();
        } else if (expression instanceof PsiType) {
            return ((PsiType) expression).getTypeInfo();
        } else if (expression instanceof PsiExternal) {
            return ((PsiExternal) expression).getSignature().toString();
        } else if (expression instanceof PsiVal) {
            return ((PsiVal) expression).getSignature();
        }

        return "";
    }

}
