package com.reason.lang.core.psi.impl;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
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
        PsiElement identifier = null;
        PsiElement parent = getParent();
        if (!(parent instanceof PsiFunctionCallParams)) {
            identifier = getFirstChild();
            if (identifier != null && identifier.getNode().getElementType() == m_types.TILDE) {
                return identifier.getNextSibling();
            }
        }
        return identifier;
    }

    @Override
    public @Nullable String getName() {
        PsiElement identifier = getNameIdentifier();
        if (identifier != null) {
            return identifier.getText();
        }

        PsiElement parent = getParent();
        if (parent instanceof PsiFunctionCallParams) {
            List<PsiParameter> parameters = ((PsiFunctionCallParams) parent).getParametersList();
            int i = 0;
            for (PsiParameter parameter : parameters) {
                if (parameter == this) {
                    PsiElement prevSibling = parent.getPrevSibling();
                    return (prevSibling == null ? "" : prevSibling.getText()) + "[" + i + "]";
                }
                i++;
            }
        }

        return null;
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
        PsiElement parent = getParent();
        return getPath() + (parent instanceof PsiFunctionCallParams ? "." + getName() : "[" + getName() + "]");
    }

    @Nullable
    @Override
    public String toString() {
        return "Parameter " + getQualifiedName();
    }
}
