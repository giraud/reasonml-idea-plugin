package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiSignature;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureImpl extends ASTWrapperPsiElement implements PsiSignature {

    public static final PsiSignature EMPTY = new PsiSignatureImpl(new LeafPsiElement(PlainTextTokenTypes.PLAIN_TEXT, ""));

    public PsiSignatureImpl(ASTNode node) {
        super(node);
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
