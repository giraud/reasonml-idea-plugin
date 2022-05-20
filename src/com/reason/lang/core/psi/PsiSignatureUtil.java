package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public final class PsiSignatureUtil {
    private PsiSignatureUtil() {
    }

    public static @NotNull String getSignature(@Nullable PsiElement element, @Nullable ORLanguageProperties toLang) {
        if (element instanceof PsiExternal) {
            PsiSignature signature = ((PsiExternal) element).getSignature();
            return signature == null ? "" : signature.asText(toLang);
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            PsiSignature signature = let.hasInferredType() ? let.getInferredType() : let.getSignature();
            return signature == null ? "" : signature.asText(toLang);
        } else if (element instanceof PsiVal) {
            PsiSignature signature = ((PsiVal) element).getSignature();
            return signature == null ? "" : signature.asText(toLang);
        } else if (element instanceof PsiInnerModule) {
            String qName = ((PsiInnerModule) element).getQualifiedName();
            return qName == null ? "" : qName;
        }
        return "";
    }
}
