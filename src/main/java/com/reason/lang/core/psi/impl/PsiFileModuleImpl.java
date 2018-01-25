package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.ModuleStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class PsiFileModuleImpl extends PsiModuleImpl {
    public PsiFileModuleImpl(@NotNull ModuleStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public PsiFileModuleImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public PsiSignature getSignature() {
        return null;
    }

    @Nullable
    @Override
    public PsiScopedExpr getBody() {
        return null;
    }

    @Override
    public Collection<PsiLet> getLetExpressions() {
        return Collections.emptyList();
    }

    @Override
    public Collection<PsiType> getTypeExpressions() {
        return Collections.emptyList();
    }

    @Override
    public Collection<PsiNamedElement> getExpressions() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public String getName() {
        return getQualifiedName();
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return RmlPsiUtil.fileNameToModuleName(getContainingFile());
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public String toString() {
        return "FileModule(" + getQualifiedName() + ")";
    }
}
