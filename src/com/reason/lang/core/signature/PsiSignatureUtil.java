package com.reason.lang.core.signature;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiSignatureUtil {

    private PsiSignatureUtil() {
    }

    @NotNull
    public static String getSignature(@Nullable PsiElement element) {
        if (element instanceof PsiExternal) {
            return ((PsiExternal) element).getORSignature().asString(element.getLanguage());
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            ORSignature signature = let.hasInferredType() ? let.getInferredType() : let.getORSignature();
            return signature.asString(element.getLanguage());
        } else if (element instanceof PsiVal) {
            PsiVal val = (PsiVal) element;
            ORSignature signature = val.getORSignature();
            return signature.asString(element.getLanguage());
        } else if (element instanceof PsiInnerModule) {
            String qualifiedName = ((PsiInnerModule) element).getQualifiedName();
            return qualifiedName == null ? "" : qualifiedName;
        }
        return "";
    }

}
