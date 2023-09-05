package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class RPsiVariantDeclaration extends RPsiTokenStub<ORLangTypes, RPsiVariantDeclaration, PsiVariantDeclarationStub> implements PsiNameIdentifierOwner, RPsiQualifiedPathElement, StubBasedPsiElement<PsiVariantDeclarationStub> {
    // region Constructors
    public RPsiVariantDeclaration(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiVariantDeclaration(@NotNull ORLangTypes types, @NotNull PsiVariantDeclarationStub stub, @NotNull IStubElementType nodeType) {
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
    public @Nullable String getName() {
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

        String[] path = ORUtil.getQualifiedPath(this);
        // We do not include type in the variant path
        String[] result = new String[path.length - 1];
        System.arraycopy(path, 0, result, 0, path.length - 1);
        return result;
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        String[] path = getPath();
        return Joiner.join(".", path) + "." + getName();
    }
    //endregion

    @Nullable
    public PsiElement getVariant() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
    }

    @NotNull
    public List<RPsiParameterDeclaration> getParameterList() {
        RPsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, RPsiParameters.class);
        return parameters == null
                ? emptyList()
                : ORUtil.findImmediateChildrenOfClass(parameters, RPsiParameterDeclaration.class);
    }
}
