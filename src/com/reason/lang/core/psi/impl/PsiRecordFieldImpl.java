package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class PsiRecordFieldImpl extends PsiTokenStub<ORTypes, PsiRecordFieldStub> implements PsiRecordField {

    // region Constructors
    public PsiRecordFieldImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiRecordFieldImpl(@NotNull ORTypes types, @NotNull PsiRecordFieldStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public @NotNull String getPath() {
        PsiRecordFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        PsiType parent = PsiTreeUtil.getParentOfType(this, PsiType.class);
        String name = getName();
        return (parent == null) ? name : (ORUtil.getQualifiedPath(parent) + "." + name);
    }


    @Override
    public @NotNull String getQualifiedName() {
        PsiRecordFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    @Override
    public @NotNull String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    public @Nullable PsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
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
                return signature == null ? null : signature.asText(getLanguage());
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.VAL;
            }
        };
    }

    @Override
    public @Nullable String toString() {
        return "Record field " + getQualifiedName();
    }
}
