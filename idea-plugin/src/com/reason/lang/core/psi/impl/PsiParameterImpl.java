package com.reason.lang.core.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.stub.PsiParameterStub;
import com.reason.lang.core.type.ORTypes;

public class PsiParameterImpl extends PsiTokenStub<ORTypes, PsiParameterStub> implements PsiParameter {

    //region Constructors
    public PsiParameterImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiParameterImpl(@NotNull ORTypes types, @NotNull PsiParameterStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    @Nullable
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

    @NotNull
    @Override
    public String getPath() {
        PsiQualifiedElement qualifiedParent = PsiTreeUtil.getParentOfType(this, PsiQualifiedElement.class);
        String parentQName = qualifiedParent == null ? null : qualifiedParent.getQualifiedName();
        return parentQName == null ? "" : parentQName;
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiParameterStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }
        return getPath() + "[" + getName() + "]";
    }

    @Nullable
    @Override
    public String toString() {
        return "Parameter " + getQualifiedName();
    }
}
