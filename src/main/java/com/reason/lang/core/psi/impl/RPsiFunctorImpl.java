package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class RPsiFunctorImpl extends RPsiTokenStub<ORLangTypes, RPsiModule, PsiModuleStub> implements RPsiFunctor {
    // region Constructors
    public RPsiFunctorImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiFunctorImpl(@NotNull ORLangTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @Nullable String getName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement id = getNameIdentifier();
        PsiElement newId = ORCodeFactory.createModuleName(getProject(), newName);
        if (id != null && newId != null) {
            id.replace(newId);
        }

        return this;
    }
    // endregion

    //region PsiQualifiedName
    @Override
    public @Nullable String[] getPath() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    public @Nullable PsiElement getMakeFunction() {
        return null;
    }

    @Override
    public @Nullable PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorBinding.class);
    }

    @Override
    public @NotNull String getModuleName() {
        String name = getName();
        return name == null ? "" : name;
    }

    @Override
    public @NotNull List<RPsiParameterDeclaration> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(
                ORUtil.findImmediateFirstChildOfClass(this, RPsiParameters.class), RPsiParameterDeclaration.class);
    }

    @Override
    public @Nullable RPsiFunctorResult getReturnType() {
        RPsiFunctorResult result = ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorResult.class);
        if (result == null) {
            // maybe it is a signature only
            RPsiModuleSignature signature = ORUtil.findImmediateFirstChildOfClass(this, RPsiModuleSignature.class);
            result = signature == null ? null : ORUtil.findImmediateFirstChildOfClass(signature, RPsiFunctorResult.class);
        }
        return result;
    }

    @Override
    public @NotNull List<RPsiTypeConstraint> getConstraints() {
        RPsiConstraints constraints = ORUtil.findImmediateFirstChildOfClass(this, RPsiConstraints.class);
        if (constraints == null) {
            PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, myTypes.COLON);
            PsiElement element = ORUtil.nextSibling(colon);
            constraints = element instanceof RPsiConstraints ? (RPsiConstraints) element : ORUtil.findImmediateFirstChildOfClass(element, RPsiConstraints.class);
        }

        return constraints == null ? Collections.emptyList() : ORUtil.findImmediateChildrenOfClass(constraints, RPsiTypeConstraint.class);
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                return getName();
            }

            @Override
            public @Nullable String getLocationString() {
                return null;
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.FUNCTOR;
            }
        };
    }

    @Override
    public String toString() {
        return super.toString() + ":" + getModuleName();
    }
}
