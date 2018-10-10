package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionParameter;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiFunctionParameterImpl extends PsiToken<ORTypes> implements PsiNamedElement, PsiFunctionParameter {

    public PsiFunctionParameterImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiElement firstChild = getFirstChild();
        if (firstChild != null && firstChild.getNode().getElementType() == m_types.TILDE) {
            return firstChild.getNextSibling();
        }
        return firstChild;
    }

    @Override
    public String getName() {
        PsiElement identifier = getNameIdentifier();
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

    @Override
    @NotNull
    public PsiSignature getSignature() {
        PsiSignature signature = PsiTreeUtil.findChildOfType(this, PsiSignature.class);
        return signature == null ? PsiSignatureImpl.EMPTY : signature;
    }

    @Override
    public boolean hasDefaultValue() {
        return ORUtil.nextSiblingWithTokenType(getFirstChild(), m_types.EQ) != null;
    }
}
