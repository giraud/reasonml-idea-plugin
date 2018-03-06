package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiNamedSymbol extends ASTWrapperPsiElement {
    public PsiNamedSymbol(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        PsiUpperSymbol upperSymbol = findChildByClass(PsiUpperSymbol.class);
        if (upperSymbol != null) {
            return upperSymbol.getName();
        }

        PsiLowerSymbol lowerSymbol = findChildByClass(PsiLowerSymbol.class);
        if (lowerSymbol != null) {
            return lowerSymbol.getName();
        }

        return super.getName();
    }
}
