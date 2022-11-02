package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiExceptionImpl extends RPsiTokenStub<ORLangTypes, RPsiException, PsiExceptionStub> implements RPsiException {
    // region Constructors
    public RPsiExceptionImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiExceptionImpl(@NotNull ORLangTypes types, @NotNull PsiExceptionStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    //region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public @Nullable String getName() {
        PsiExceptionStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    //region PsiQualifiedPathName
    @Override
    public @Nullable String[] getPath() {
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
    //endregion


    @Override
    public @NotNull PsiElement getNavigationElement() {
        PsiElement id = getNameIdentifier();
        return id == null ? this : id;
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @Nullable String getAlias() {
        PsiExceptionStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        PsiElement eq = findChildByType(myTypes.EQ);
        return eq == null ? null : ORUtil.computeAlias(eq.getNextSibling(), getLanguage(), false);
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
    public @NotNull String toString() {
        return "RPsiException:" + getName();
    }
}
