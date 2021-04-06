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
import com.reason.lang.core.psi.PsiType;
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
    public @NotNull String getPath() {
        PsiFile file = getContainingFile();
        return file instanceof FileBase ? ((FileBase) file).getModuleName() : "";
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            String name = greenStub.getName();
            return name == null ? "" : name;
        }

        // ?? Namespace ??
        return getModuleName();
    }

    @Override
    public void navigate(boolean requestFocus) {
        PsiFile file = getContainingFile();
        file.navigate(requestFocus);
    }

    @Override
    public boolean isInterface() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.isInterface();
        }
        return ((FileBase) getContainingFile()).isInterface();
    }

    @Nullable
    @Override
    public PsiFunctorCall getFunctorCall() {
        return null;
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
        return getModuleName();
    }

    @NotNull
    @Override
    public String getModuleName() {
        PsiFile file = getContainingFile();
        assert file instanceof FileBase;
        return ((FileBase) file).getModuleName();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new RuntimeException("Not implemented, use FileBase");
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

    @Nullable
    @Override
    public PsiLet getLetExpression(@Nullable String name) {
        Collection<PsiLet> expressions = getExpressions(name, PsiLet.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @Nullable
    @Override
    public PsiVal getValExpression(@Nullable String name) {
        Collection<PsiVal> expressions = getExpressions(name, PsiVal.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @Nullable
    @Override
    public PsiType getTypeExpression(@Nullable String name) {
        List<PsiType> expressions = getExpressions(name, PsiType.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @NotNull
    private <T extends PsiNamedElement> List<T> getExpressions(
            @Nullable String name, @NotNull Class<T> clazz) {
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

    @Nullable
    public ItemPresentation getPresentation() {
        // FileBase presentation should be used
        return null;
    }

    public boolean hasNamespace() {
        return false;
    }

    @Override
    public @NotNull String toString() {
        return "PsiFakeModule";
    }
}
