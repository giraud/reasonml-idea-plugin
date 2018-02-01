package com.reason.lang.core.psi;

public class PsiInferredTypeUtil {
    public static String getTypeInfo(PsiNamedElement expression) {
        if (expression instanceof PsiLet) {
            return ((PsiLet) expression).getInferredType();
        } else if (expression instanceof PsiType) {
            return ((PsiType) expression).getTypeInfo();
        } else if (expression instanceof PsiExternal) {
            return ((PsiExternal) expression).getSignature();
        } else if (expression instanceof PsiVal) {
            return ((PsiVal) expression).getSignature();
        }

        return "";
    }
}
