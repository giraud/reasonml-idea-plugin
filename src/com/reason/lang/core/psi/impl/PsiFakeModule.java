package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiFakeModule extends PsiTokenStub<ORTypes, PsiModule, PsiModuleStub> implements PsiModule, StubBasedPsiElement<PsiModuleStub> {
    // region Constructors
    public PsiFakeModule(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiFakeModule(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    @Override
    public @NotNull String getModuleName() {
        PsiFile file = getContainingFile();
        assert file instanceof FileBase;
        return ((FileBase) file).getModuleName();
    }

    //region PsiNamedElement
    @Override
    public @Nullable String getName() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.getName();
        }

        return getModuleName();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new RuntimeException("Not implemented, use FileBase");
    }
    //endregion

    //region PsiQualifiedPath
    @Override
    public String @Nullable [] getPath() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.getPath();
        }

        return null;
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            String name = greenStub.getName();
            return name == null ? "" : name;
        }

        // ?? Namespace ??
        return getModuleName();
    }
    //endregion

    @Override public @Nullable String[] getQualifiedNameAsPath() {
        return ORUtil.getQualifiedNameAsPath(this);
    }

    @Override
    public void navigate(boolean requestFocus) {
        PsiFile file = getContainingFile();
        file.navigate(requestFocus);
    }

    @Override
    public @NotNull PsiElement getComponentNavigationElement() {
        return ((FileBase) getContainingFile()).getComponentNavigationElement();
    }

    @Override
    public boolean isInterface() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.isInterface();
        }
        return ((FileBase) getContainingFile()).isInterface();
    }

    @Override
    public @Nullable PsiFunctorCall getFunctorCall() {
        return null;
    }

    public boolean isComponent() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.isComponent();
        }
        return ((FileBase) getContainingFile()).isComponent();
    }

    @Override
    public @Nullable String getAlias() {
        return null;
    }

    @Override
    public @Nullable PsiUpperSymbol getAliasSymbol() {
        return null;
    }

    @Override
    public @Nullable PsiElement getModuleType() {
        return null;
    }

    @Override
    public @Nullable PsiElement getBody() {
        return null;
    }

    @Override
    public @NotNull Collection<PsiNamedElement> getExpressions(@NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter) {
        return PsiFileHelper.getExpressions(getContainingFile(), eScope, filter);
    }

    public @NotNull Collection<PsiModule> getModules() {
        return PsiFileHelper.getModuleExpressions(getContainingFile());
    }

    @Override
    public @Nullable PsiModule getModuleExpression(@Nullable String name) {
        if (name != null) {
            Collection<PsiInnerModule> modules = getExpressions(name, PsiInnerModule.class);
            for (PsiInnerModule module : modules) {
                if (name.equals(module.getName())) {
                    return module;
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable PsiLet getLetExpression(@Nullable String name) {
        Collection<PsiLet> expressions = getExpressions(name, PsiLet.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @Override
    public @Nullable PsiExternal getExternalExpression(@Nullable String name) {
        Collection<PsiExternal> expressions = getExpressions(name, PsiExternal.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @Override
    public @Nullable PsiVal getValExpression(@Nullable String name) {
        Collection<PsiVal> expressions = getExpressions(name, PsiVal.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @NotNull
    private <T extends PsiNamedElement> List<T> getExpressions(@Nullable String name, @NotNull Class<T> clazz) {
        List<T> result = new ArrayList<>();

        if (name != null) {
            Collection<T> children = PsiTreeUtil.findChildrenOfType(getContainingFile(), clazz);
            for (T child : children) {
                if (name.equals(child.getName())) {
                    result.add(child);
                }
            }
        }

        return result;
    }

    public @Nullable ItemPresentation getPresentation() {
        // FileBase presentation should be used
        return null;
    }

    @Override
    public @NotNull String toString() {
        return "PsiFakeModule";
    }
}
