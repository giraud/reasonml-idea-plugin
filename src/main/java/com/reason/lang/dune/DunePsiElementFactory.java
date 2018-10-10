package com.reason.lang.dune;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiDuneVersion;
import com.reason.lang.core.psi.PsiSExpr;
import com.reason.lang.core.psi.impl.PsiToken;
import org.jetbrains.annotations.NotNull;

class DunePsiElementFactory {
    private DunePsiElementFactory() {
    }

    static PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();

        if (type == DuneTypes.INSTANCE.SEXPR) {
            return new PsiSExpr(DuneTypes.INSTANCE, node);
        } else if (type == DuneTypes.INSTANCE.VERSION) {
            return new PsiDuneVersion(DuneTypes.INSTANCE, node);
        }

        return new PsiToken<>(DuneTypes.INSTANCE, node);
    }
}
