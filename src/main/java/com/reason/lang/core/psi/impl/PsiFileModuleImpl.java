package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.ModuleStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PsiFileModuleImpl extends PsiModuleImpl {
    public PsiFileModuleImpl(@NotNull ModuleStub stub, @NotNull IStubElementType nodeType, MlTypes types) {
        super(stub, nodeType, types);
    }

    public PsiFileModuleImpl(@NotNull ASTNode node, MlTypes types) {
        super(node, types);
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

    @NotNull
    @Override
    public Collection<PsiLet> getLetExpressions() {
        Collection<PsiLet> result = new ArrayList<>();

        PsiElement element = getFirstChild();
        while (element != null) {
            if (element instanceof PsiLet) {
                result.add((PsiLet) element);
            }
            element = element.getNextSibling();
        }

        return result;
    }

    @NotNull
    @Override
    public Collection<PsiType> getTypeExpressions() {
        Collection<PsiType> result = new ArrayList<>();

        PsiElement element = getFirstChild();
        while (element != null) {
            if (element instanceof PsiType) {
                result.add((PsiType) element);
            }
            element = element.getNextSibling();
        }

        return result;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiElement firstChild = getFirstChild();
        return firstChild instanceof PsiUpperSymbol ? firstChild : null;
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

        return PsiUtil.fileNameToModuleName(getContainingFile());
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isComponent() {
        ModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isComponent();
        }

        // naive detection

        List<PsiLet> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(this, PsiLet.class);
        PsiLet componentDef = null;
        PsiLet makeDef = null;
        for (PsiLet let : expressions) {
            if (componentDef == null && "component".equals(let.getName())) {
                componentDef = let;
            } else if (makeDef == null && "make".equals(let.getName())) {
                makeDef = let;
            } else if (componentDef != null && makeDef != null) {
                break;
            }
        }

        return componentDef != null && makeDef != null;
    }

    @Override
    public String toString() {
        return "FModule " + getQualifiedName() + " (" + getContainingFile().getVirtualFile().getCanonicalPath() + ")";
    }
}
