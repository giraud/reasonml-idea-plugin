package com.reason.lang.core.psi.impl;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiClassField extends ORCompositePsiElement<ORLangTypes> implements NavigatablePsiElement, PsiNameIdentifierOwner, RPsiStructuredElement {
    protected RPsiClassField(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, RPsiLowerSymbol.class);
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
                return ORIcons.VAL;
            }
        };
    }
}
