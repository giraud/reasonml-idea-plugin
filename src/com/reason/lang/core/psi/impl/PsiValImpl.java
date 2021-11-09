package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class PsiValImpl extends PsiTokenStub<ORTypes, PsiVal, PsiValStub> implements PsiVal {
    // region Constructors
    public PsiValImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiValImpl(@NotNull ORTypes types, @NotNull PsiValStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, PsiLowerIdentifier.class, PsiScopedExpr.class);
    }

    @Override
    public @Nullable String getName() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedName
    @Override
    public @NotNull String[] getPath() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public boolean isFunction() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        PsiSignature signature = getSignature();
        return signature != null && signature.isFunction();
    }

    @Override
    public @NotNull Collection<PsiRecordField> getRecordFields() {
        return PsiTreeUtil.findChildrenOfType(this, PsiRecordField.class);
    }

    @Override
    public @Nullable PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                return getName();
            }

            @Override
            public @Nullable String getLocationString() {
                PsiSignature signature = getSignature();
                return signature == null ? null : signature.asText(ORLanguageProperties.cast(getLanguage()));
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.VAL;
            }
        };
    }

    @Override
    public @Nullable String toString() {
        return "Val " + getQualifiedName();
    }
}
