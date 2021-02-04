package com.reason.lang.core.signature;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PsiSignatureUtil {
    private PsiSignatureUtil() {
    }

    public static @NotNull String getSignature(@Nullable PsiElement element, @NotNull Language targetLanguage) {
        if (element instanceof PsiExternal) {
            PsiSignature signature = ((PsiExternal) element).getSignature();
            return signature == null ? "" : signature.asText(targetLanguage);
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            PsiSignature signature = let.hasInferredType() ? let.getInferredType() : let.getSignature();
            return signature == null ? "" : signature.asText(targetLanguage);
        } else if (element instanceof PsiVal) {
            PsiSignature signature = ((PsiVal) element).getSignature();
            return signature == null ? "" : signature.asText(targetLanguage);
        } else if (element instanceof PsiInnerModule) {
            return ((PsiInnerModule) element).getQualifiedName();
        }
        return "";
    }
}
