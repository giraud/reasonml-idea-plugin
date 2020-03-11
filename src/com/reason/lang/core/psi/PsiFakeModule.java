package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.impl.PsiTokenStub;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;

public class PsiFakeModule extends PsiTokenStub<ORTypes, PsiModuleStub> implements PsiModule, StubBasedPsiElement<PsiModuleStub> {

    //region Constructors
    public PsiFakeModule(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiFakeModule(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    @NotNull
    @Override
    public String getPath() {
        return ((FileBase) getContainingFile()).getPath();
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.getQualifiedName();
        }
        return ((FileBase) getContainingFile()).getQualifiedName();
    }

    @Override
    public boolean isInterface() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.isInterface();
        }
        return ((FileBase) getContainingFile()).isInterface();
    }

    public boolean isComponent() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.isComponent();
        }
        return ((FileBase) getContainingFile()).isComponent();
    }

    @Nullable
    @Override
    public String getAlias() {
        return null;
    }

    @Override
    @Nullable
    public String getName() {
        return getContainingFile().getName();
    }

    @Nullable
    @Override
    public String getModuleName() {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Override
    public boolean canBeDisplayed() {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Override
    public void navigate(boolean requestFocus) {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @NotNull
    @Override
    public Collection<PsiNameIdentifierOwner> getExpressions(@NotNull ExpressionScope eScope) {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @NotNull
    @Override
    public List<PsiLet> getLetExpressions() {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Nullable
    @Override
    public PsiModule getModuleExpression(@Nullable String name) {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Nullable
    @Override
    public PsiLet getLetExpression(@Nullable String name) {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Nullable
    @Override
    public PsiVal getValExpression(@Nullable String name) {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @Nullable
    @Override
    public PsiType getTypeExpression(@NotNull String name) {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    public ItemPresentation getPresentation() {
        throw new RuntimeException("Not implemented, use FileBase");
    }
}
