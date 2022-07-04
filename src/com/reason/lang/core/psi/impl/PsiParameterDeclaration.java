package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiParameterDeclaration extends PsiTokenStub<ORTypes, PsiParameter, PsiParameterStub> implements PsiParameter {
    // region Constructors
    public PsiParameterDeclaration(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiParameterDeclaration(@NotNull ORTypes types, @NotNull PsiParameterStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    //region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement parent = getParent();
        PsiElement grandParent = parent == null ? null : parent.getParent();
        if (grandParent instanceof PsiFunctionCall || grandParent instanceof PsiFunctorCall) {
            return null;
        }

        PsiElement identifier = getFirstChild();
        IElementType elementType = identifier == null ? null : identifier.getNode().getElementType();
        if (elementType == myTypes.TILDE || elementType == myTypes.LPAREN || elementType == myTypes.QUESTION_MARK) {
            PsiElement nextSibling = identifier.getNextSibling();
            IElementType nextElementType = nextSibling == null ? null : nextSibling.getNode().getElementType();
            identifier = nextElementType == myTypes.LPAREN ? nextSibling.getNextSibling() : nextSibling;
        }

        return identifier;
    }

    @Override
    public @Nullable String getName() {
        PsiParameterStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement identifier = getNameIdentifier();
        if (identifier != null) {
            return identifier.getText();
        }

        PsiElement parent = getParent();
        if (parent instanceof PsiParameters) {
            List<PsiElement> parameters = ((PsiParameters) parent).getParametersList();
            int i = 0;
            for (PsiElement parameter : parameters) {
                if (parameter == this) {
                    PsiElement prevSibling = ORUtil.prevSibling(parent);
                    return (prevSibling == null ? "" : prevSibling.getText()) + "[" + i + "]";
                }
                i++;
            }
        }

        return null;
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    //region PsiQualifiedName
    @Override
    public String @Nullable [] getPath() {
        PsiParameterStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        PsiQualifiedNamedElement qualifiedParent = PsiTreeUtil.getParentOfType(this, PsiQualifiedNamedElement.class);
        String qName = qualifiedParent == null ? null : qualifiedParent.getQualifiedName();
        return qName == null ? null : qName.split("\\.");
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiParameterStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        String name = getName();
        String[] path = getPath();
        return Joiner.join(".", path) + "[" + name + "]";
    }
    //endregion

    @Override
    public @Nullable PsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
    }

    @Override
    public @Nullable PsiDefaultValue getDefaultValue() {
        return null;
    }

    @Override
    public boolean isOptional() {
        return getDefaultValue() != null;
    }
}
