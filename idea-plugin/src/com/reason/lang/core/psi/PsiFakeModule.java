package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.psi.impl.PsiTokenStub;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        PsiFile file = getContainingFile();
        return file instanceof FileBase ? ((FileBase) file).getModuleName() : "";
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiModuleStub greenStub = getGreenStub();
        if (greenStub != null) {
            return greenStub.getName();
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
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new RuntimeException("Not implemented, use FileBase");
    }

    @NotNull
    @Override
    public Collection<PsiNameIdentifierOwner> getExpressions(@NotNull ExpressionScope eScope) {
        return PsiFileHelper.getExpressions(getContainingFile(), eScope);
    }

    @NotNull
    public Collection<PsiModule> getModules() {
        return PsiFileHelper.getModuleExpressions(getContainingFile());
    }

    @Nullable
    @Override
    public PsiModule getModuleExpression(@Nullable String name) {
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

    @NotNull
    @Override
    public List<PsiLet> getLetExpressions() {
        return PsiFileHelper.getLetExpressions(getContainingFile());
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
    private <T extends PsiNameIdentifierOwner> List<T> getExpressions(@Nullable String name, @NotNull Class<T> clazz) {
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
    public String toString() {
        return "PsiFakeModule:" + getModuleName();
    }
}
