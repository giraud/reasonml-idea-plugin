package com.reason.lang.core.psi;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.lang.core.psi.impl.PsiTokenStub;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlLanguage;

public class PsiFakeModule extends PsiTokenStub<ORTypes, PsiModuleStub> implements PsiModule, StubBasedPsiElement<PsiModuleStub> {

    //region Constructors
    public PsiFakeModule(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiFakeModule(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    @Override
    public String getName() {
        return ((FileBase) getContainingFile()).asModuleName();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;  // TODO implement method
    }

    @Override
    public boolean canBeDisplayed() {
        return false;
    }

    @Override
    public void navigate(boolean requestFocus) {
        getContainingFile().navigate(requestFocus);
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return getName();
    }

    @Nullable
    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return FileHelper.isInterface(getContainingFile().getFileType());
    }

    @NotNull
    @Override
    public Collection<PsiNameIdentifierOwner> getExpressions() {
        return ((FileBase) getContainingFile()).getExpressions();
    }

    @NotNull
    @Override
    public List<PsiLet> getLetExpressions() {
        return ((FileBase) getContainingFile()).getLetExpressions();
    }

    @Nullable
    @Override
    public PsiModule getModuleExpression(@Nullable String name) {
        return ((FileBase) getContainingFile()).getModuleExpression(name);
    }

    @Nullable
    @Override
    public PsiLet getLetExpression(@Nullable String name) {
        return ((FileBase) getContainingFile()).getLetExpression(name);
    }

    @Nullable
    @Override
    public PsiVal getValExpression(@Nullable String name) {
        return ((FileBase) getContainingFile()).getValExpression(name);
    }

    public boolean isComponent() {
        return ((FileBase) getContainingFile()).isComponent();
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @NotNull
            @Override
            public String getLocationString() {
                return getContainingFile().getVirtualFile().getPath();
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                boolean isInterface = isInterface();
                if (getLanguage() == RmlLanguage.INSTANCE) {
                    return isInterface ? Icons.RML_FILE_MODULE_INTERFACE : Icons.RML_FILE_MODULE;
                }
                return isInterface ? Icons.OCL_FILE_MODULE_INTERFACE : Icons.OCL_FILE_MODULE;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "Fake Module (" + getContainingFile().getName() + ")";
    }
}
