package com.reason.lang.core.psi;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class PsiVariantDeclaration extends PsiTokenStub<ORTypes, PsiVariantDeclaration, PsiVariantDeclarationStub> implements PsiQualifiedPathElement, StubBasedPsiElement<PsiVariantDeclarationStub> {
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
    public String getName() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getFirstChild();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    //region PsiQualifiedName
    @Override
    public @NotNull String[] getPath() {
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
        PsiUpperIdentifier id = ORUtil.findImmediateFirstChildOfClass(this, PsiUpperIdentifier.class);
        return id == null ? this : id;
    }

    @Nullable
    public PsiNameIdentifierOwner getVariant() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperIdentifier.class);
    }

    @NotNull
    public Collection<PsiParameter> getParameterList() {
        PsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class);
        return parameters == null
                ? emptyList()
                : ORUtil.findImmediateChildrenOfClass(parameters, PsiParameter.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "Variant declaration";
    }
}
