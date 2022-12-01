package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public final class RPsiSignatureUtil {
    private RPsiSignatureUtil() {
    }

    public static @NotNull String getSignature(@Nullable PsiElement element, @Nullable ORLanguageProperties toLang) {
        if (element instanceof RPsiExternal) {
            RPsiSignature signature = ((RPsiExternal) element).getSignature();
            return signature == null ? "" : signature.asText(toLang);
        } else if (element instanceof RPsiLet) {
            RPsiLet let = (RPsiLet) element;
            RPsiSignature signature = let.hasInferredType() ? let.getInferredType() : let.getSignature();
            return signature == null ? "" : signature.asText(toLang);
        } else if (element instanceof RPsiVal) {
            RPsiSignature signature = ((RPsiVal) element).getSignature();
            return signature == null ? "" : signature.asText(toLang);
        } else if (element instanceof RPsiInnerModule) {
            String qName = ((RPsiInnerModule) element).getQualifiedName();
            return qName == null ? "" : qName;
        }
        return "";
    }
}
