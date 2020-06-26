package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PsiSignatureImpl extends PsiToken<ORTypes> implements PsiSignature {

    public PsiSignatureImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @NotNull
    @Override
    public ORSignature asHMSignature() {
        Collection<PsiSignatureItem> items = PsiTreeUtil.findChildrenOfType(this, PsiSignatureItemImpl.class);
        return new ORSignature(getContainingFile().getLanguage(), items);
    }

    @NotNull
    @Override
    public String asString(@NotNull Language lang) {
        return asHMSignature().asString(lang);
    }

    @NotNull
    @Override
    public String toString() {
        return "Signature";
    }
}
