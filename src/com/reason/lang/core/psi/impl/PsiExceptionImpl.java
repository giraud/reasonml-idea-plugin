package com.reason.lang.core.psi.impl;

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

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiExceptionImpl extends PsiTokenStub<ORTypes, PsiException, PsiExceptionStub> implements PsiException {
    // region Constructors
    public PsiExceptionImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiExceptionImpl(@NotNull ORTypes types, @NotNull PsiExceptionStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = ORUtil.findImmediateFirstChildOfClass(this, PsiUpperIdentifier.class);
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not implemented");
    }

    @Override
    public @NotNull String getPath() {
        PsiExceptionStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiExceptionStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    @Override
    public @Nullable String getAlias() {
        PsiElement eq = findChildByType(m_types.EQ);
        if (eq != null) {
            return ORUtil.computeAlias(eq.getNextSibling(), getLanguage(), false);
        }

        return null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                return getName();
            }

            @Override
            public @Nullable String getLocationString() {
                return null;
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.EXCEPTION;
            }
        };
    }

    @Override
    public @Nullable String toString() {
        return "Exception " + getName();
    }
}
