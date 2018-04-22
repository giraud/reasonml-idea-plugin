package com.reason.lang.dune;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiDuneVersion;
import com.reason.lang.core.psi.PsiToken;

class DunePsiElementFactory {
    static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();

        if (type == DuneTypes.VERSION) {
            return new PsiDuneVersion(node);
        }

        return new PsiToken(node);
    }
}
