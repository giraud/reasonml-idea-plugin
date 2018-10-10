package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureItem extends PsiToken<ORTypes> {
    public PsiSignatureItem(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
}
