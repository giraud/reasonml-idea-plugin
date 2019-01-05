package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiSignatureUtil {

    private PsiSignatureUtil() {
    }

    @NotNull
    public static String getSignature(@Nullable PsiElement element) {
        if (element instanceof PsiExternal) {
            return ((PsiExternal) element).getHMSignature().toString();
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            ORSignature signature = let.hasInferredType() ? let.getInferredType() : let.getHMSignature();
            return signature.toString();
        } else if (element instanceof PsiVal) {
            PsiVal val = (PsiVal) element;
            ORSignature signature = val.getHMSignature();
            return signature.toString();
        } else if (element instanceof PsiModule) {
            return ((PsiModule) element).getQualifiedName();
        }
        return "";
    }

}
