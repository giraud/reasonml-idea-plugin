package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.psi.impl.PsiSignatureImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiFunctionParameter extends ASTWrapperPsiElement implements PsiNamedElement {

    public PsiFunctionParameter(ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement identifier = getNameIdentifier();
        if (identifier instanceof PsiNamedSymbol) {
            return ((PsiNamedSymbol) identifier).getName();
        }

        return identifier == null ? "" : identifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @Override
    public String toString() {
        return "Function parameter " + getName();
    }

    @NotNull
    public PsiSignature getSignature() {
        PsiSignature signature = PsiTreeUtil.findChildOfType(this, PsiSignature.class);
        return signature == null ? PsiSignatureImpl.EMPTY : signature;
    }
}
