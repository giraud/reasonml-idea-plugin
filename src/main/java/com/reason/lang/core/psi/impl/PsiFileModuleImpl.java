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

import java.util.ArrayList;
import java.util.Collection;

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

    @NotNull
    @Override
    public Collection<PsiModule> getModules() {
        Collection<PsiModule> result = new ArrayList<>();

        PsiElement element = getFirstChild();
        while (element != null) {
            if (element instanceof PsiModule) {
                result.add((PsiModule) element);
            }
            element = element.getNextSibling();
        }

        return result;
    }

    @NotNull
    @Override
    public Collection<PsiNamedElement> getExpressions() {
        Collection<PsiNamedElement> result = new ArrayList<>();

        PsiElement element = getFirstChild();
        while (element != null) {
            if (element instanceof PsiType || element instanceof PsiModule || element instanceof PsiLet || element instanceof PsiExternal || element instanceof PsiVal) {
                result.add((PsiNamedElement) element);
            }
            element = element.getNextSibling();
        }

        return result;
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
        ModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return RmlPsiUtil.fileNameToModuleName(getContainingFile());
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    public String toString() {
        return "FileModule(" + getQualifiedName() + ")";
    }
}
