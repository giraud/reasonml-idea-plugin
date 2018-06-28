package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiSignatureUtil {

    private PsiSignatureUtil() {
    }

    @NotNull
    public static String getProvidersType(@Nullable PsiElement element) {
        if (element instanceof PsiExternal) {
            return ((PsiExternal) element).getSignature().toString();
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            HMSignature signature = let.hasInferredType() ? let.getInferredType() : let.getSignature();
            return signature.toString();
        } else if (element instanceof PsiVal) {
            PsiVal val = (PsiVal) element;
            HMSignature signature = val.getSignature();
            return signature.toString();
        }
        return "";
    }

}
