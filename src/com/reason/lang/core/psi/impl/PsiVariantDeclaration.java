package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class PsiVariantDeclaration extends PsiTokenStub<ORTypes, PsiVariantDeclaration, PsiVariantDeclarationStub> implements PsiNameIdentifierOwner, PsiQualifiedPathElement, StubBasedPsiElement<PsiVariantDeclarationStub> {
    // region Constructors
    public PsiVariantDeclaration(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiVariantDeclaration(@NotNull ORTypes types, @NotNull PsiVariantDeclarationStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    //region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public String getName() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement id = getNameIdentifier();
        return id == null ? "" : id.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    //region PsiQualifiedName
    @Override
    public String @NotNull [] getPath() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public @NotNull PsiElement getNavigationElement() {
        PsiUpperSymbol id = ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
        return id == null ? this : id;
    }

    @Nullable
    public PsiElement getVariant() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
    }

    @NotNull
    public Collection<com.reason.lang.core.psi.PsiParameter> getParameterList() {
        PsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class);
        return parameters == null
                ? emptyList()
                : ORUtil.findImmediateChildrenOfClass(parameters, PsiParameter.class);
    }
}
