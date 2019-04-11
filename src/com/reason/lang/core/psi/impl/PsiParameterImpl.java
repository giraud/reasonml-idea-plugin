package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiParameterImpl extends PsiToken<ORTypes> implements PsiNamedElement, PsiParameter {

    public PsiParameterImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
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

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @Nullable
    @Override
    public String toString() {
        return "Parameter " + getName();
    }

    @Override
    @Nullable
    public PsiSignature getPsiSignature() {
        return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
    }

    @NotNull
    @Override
    public ORSignature getORSignature() {
        PsiSignature signature = getPsiSignature();
        return signature == null ? ORSignature.EMPTY : signature.asHMSignature();
    }

    @Override
    public boolean hasDefaultValue() {
        return ORUtil.nextSiblingWithTokenType(getFirstChild(), m_types.EQ) != null;
    }
}
