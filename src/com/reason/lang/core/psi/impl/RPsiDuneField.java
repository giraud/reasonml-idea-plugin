package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.dune.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiDuneField extends RPsiToken<DuneTypes> implements PsiNameIdentifierOwner, RPsiStructuredElement {
    public RPsiDuneField(@NotNull DuneTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement firstChild = getFirstChild();
        PsiElement nextSibling = firstChild.getNextSibling();
        return nextSibling != null && nextSibling.getNode().getElementType() == m_types.ATOM ? nextSibling : null;
    }

    @Override
    public @Nullable String getName() {
        PsiElement identifier = getNameIdentifier();
        return identifier == null ? null : identifier.getText();
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    public @NotNull String getValue() {
        PsiElement name = ORUtil.findImmediateFirstChildOfType(this, DuneTypes.INSTANCE.ATOM);
        PsiElement nextLeaf = name == null ? null : PsiTreeUtil.nextVisibleLeaf(name);
        return nextLeaf == null ? "" : nextLeaf.getText();  // might be not enough
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                String name = getName();
                return name == null ? "unknown" : name;
            }

            @Override
            public @Nullable String getLocationString() {
                return null;
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.OBJECT;
            }
        };
    }

    @Override
    public @Nullable String toString() {
        return "Field " + getName();
    }
}
