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
import java.util.*;

public class RPsiValImpl extends RPsiTokenStub<ORLangTypes, RPsiVal, PsiValStub> implements RPsiVal {
    // region Constructors
    public RPsiValImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiValImpl(@NotNull ORLangTypes types, @NotNull PsiValStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, RPsiLowerSymbol.class, RPsiScopedExpr.class);
    }

    @Override
    public @Nullable String getName() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;
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
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public boolean isFunction() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        RPsiSignature signature = getSignature();
        return signature != null && signature.isFunction();
    }

    @Override
    public @NotNull Collection<RPsiObjectField> getJsObjectFields() {
        PsiElement firstChild = getFirstChild();
        RPsiJsObject jsObject = firstChild instanceof RPsiJsObject ? ((RPsiJsObject) firstChild) : null;
        return ORUtil.findImmediateChildrenOfClass(jsObject, RPsiObjectField.class);
    }

    @Override
    public @NotNull Collection<RPsiRecordField> getRecordFields() {
        return PsiTreeUtil.findChildrenOfType(this, RPsiRecordField.class);
    }

    @Override
    public @Nullable RPsiSignature getSignature() {
        return findChildByClass(RPsiSignature.class);
    }

    @Override
    public boolean isAnonymous() {
        return getName() == null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                String name = getName();
                return name == null ? "" : name;
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
