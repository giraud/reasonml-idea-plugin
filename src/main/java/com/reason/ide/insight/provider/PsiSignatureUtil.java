package com.reason.ide.insight.provider;

import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PsiSignatureUtil {
    @NotNull
    static String getProvidersType(@Nullable PsiNamedElement element) {
        if (element instanceof PsiExternal) {
            return ((PsiExternal) element).getSignature();
        } else if (element instanceof PsiLet) {
            return ((PsiLet) element).getSignature();
        }

        return "";
    }
}
