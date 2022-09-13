package com.reason.lang.core.psi.impl;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class PsiClassMethod extends ORCompositePsiElement<ORTypes> implements NavigatablePsiElement, PsiNameIdentifierOwner, PsiStructuredElement {
    protected PsiClassMethod(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiLowerSymbol.class);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    public @Nullable PsiSignature getSignature() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiSignature.class);
    }

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
                PsiSignature signature = getSignature();
                return signature == null ? null : signature.getText();
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.METHOD;
            }
        };
    }
}
