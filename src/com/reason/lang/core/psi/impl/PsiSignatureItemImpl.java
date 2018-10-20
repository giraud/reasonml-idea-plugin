package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureItemImpl extends PsiToken<ORTypes> implements PsiSignatureItem {
    public PsiSignatureItemImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public boolean isNamedItem() {
        return ORUtil.nextSiblingWithTokenType(getFirstChild(), m_types.COLON) != null;
    }

    @Override
    public String toString() {
        return "Signature item";
    }
}
