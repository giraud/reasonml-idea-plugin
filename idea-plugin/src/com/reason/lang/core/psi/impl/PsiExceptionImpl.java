package com.reason.lang.core.psi.impl;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.stub.PsiExceptionStub;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;

public class PsiExceptionImpl extends PsiTokenStub<ORTypes, PsiExceptionStub> implements PsiException {

    //region Constructors
    public PsiExceptionImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiExceptionImpl(@NotNull ORTypes types, @NotNull PsiExceptionStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    @Nullable
    @Override
    public String getName() {
        PsiElement nameIdentifier = ORUtil.findImmediateFirstChildOfClass(this, PsiUpperIdentifier.class);
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not implemented");
    }

    @NotNull
    @Override
    public String getPath() {
        PsiExceptionStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiExceptionStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    @Override
    public @Nullable String getAlias() {
        //PsiExceptionStub stub = getGreenStub(); zzz
        //if (stub != null) {
        //    return stub.getAlias();
        //}

        PsiElement eq = findChildByType(m_types.EQ);
        if (eq != null) {
            return ORUtil.computeAlias(eq.getNextSibling(), getLanguage(), false);
        }

        return null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.EXCEPTION;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "Exception " + getName();
    }
}
