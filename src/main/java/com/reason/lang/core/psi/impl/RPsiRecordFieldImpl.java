package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiRecordFieldImpl extends RPsiTokenStub<ORLangTypes, RPsiRecordField, RsiRecordFieldStub> implements RPsiRecordField {
    // region Constructors
    public RPsiRecordFieldImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiRecordFieldImpl(@NotNull ORLangTypes types, @NotNull RsiRecordFieldStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiLowerSymbol.class);
    }

    @Override
    public @Nullable String getName() {
        RsiRecordFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedName
    @Override
    public @NotNull String[] getPath() {
        RsiRecordFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        RsiRecordFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public @Nullable RPsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, RPsiSignature.class);
    }

    @Override
    public @Nullable RPsiFieldValue getValue() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiFieldValue.class);
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
                RPsiSignature signature = getSignature();
                return signature == null ? null : signature.asText(ORLanguageProperties.cast(getLanguage()));
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.VAL;
            }
        };
    }
}
