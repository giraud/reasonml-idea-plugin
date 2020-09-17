package com.reason.ide.search;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;

public class PsiTypeElementProvider {

    @Nullable
    public static String getType(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (element instanceof PsiUpperIdentifier) {
            if (parent instanceof PsiModule) {
                return "module";
            }
            if (parent instanceof PsiVariantDeclaration) {
                return "variant";
            }
        } else if (element instanceof PsiLowerIdentifier) {
            if (parent instanceof PsiLet) {
                return "let";
            }
            if (parent instanceof PsiVal) {
                return "val";
            }
            if (parent instanceof PsiExternal) {
                return "external";
            }
            if (parent instanceof PsiType) {
                return "type";
            }
        }

        return null;
    }
}
