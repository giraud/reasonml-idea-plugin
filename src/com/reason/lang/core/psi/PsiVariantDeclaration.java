package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiTokenStub;
import com.reason.lang.core.stub.PsiVariantDeclarationStub;
import com.reason.lang.core.type.ORTypes;

import static java.util.Collections.*;

public class PsiVariantDeclaration extends PsiTokenStub<ORTypes, PsiVariantDeclarationStub>
        implements PsiNameIdentifierOwner, PsiQualifiedElement, StubBasedPsiElement<PsiVariantDeclarationStub> {

    //region Constructors
    public PsiVariantDeclaration(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiVariantDeclaration(@NotNull ORTypes types, @NotNull PsiVariantDeclarationStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, PsiUpperSymbol.class);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @NotNull
    @Override
    public String getPath() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiVariantDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    @Nullable
    public PsiUpperSymbol getVariant() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
    }

    @NotNull
    public Collection<PsiParameter> getParameterList() {
        PsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class);
        return parameters == null ? emptyList() : ORUtil.findImmediateChildrenOfClass(parameters, PsiParameter.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "Variant declaration";
    }
}
