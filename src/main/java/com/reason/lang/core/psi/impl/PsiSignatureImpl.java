package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureImpl extends PsiToken<ORTypes> implements PsiSignature {

    public static final PsiSignature EMPTY = new PsiSignatureImpl(RmlTypes.INSTANCE, new LeafPsiElement(PlainTextTokenTypes.PLAIN_TEXT, ""));

    public PsiSignatureImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @NotNull
    @Override
    public HMSignature asHMSignature() {
        return new HMSignature(getText());
    }

    @NotNull
    @Override
    public String asString() {
        return getText();
    }

    @Override
    public String toString() {
        return "Signature";
    }
}
