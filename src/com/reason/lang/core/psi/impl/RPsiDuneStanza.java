package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.dune.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class RPsiDuneStanza extends RPsiToken<DuneTypes> implements PsiNameIdentifierOwner, RPsiStructuredElement {
    public RPsiDuneStanza(@NotNull DuneTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement firstChild = getFirstChild();
        PsiElement nextSibling = firstChild.getNextSibling();
        return nextSibling != null && nextSibling.getNode().getElementType() == m_types.ATOM
                ? nextSibling
                : null;
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

    public @NotNull Collection<RPsiDuneField> getFields() {
        RPsiDuneFields fields = ORUtil.findImmediateFirstChildOfClass(this, RPsiDuneFields.class);
        return ORUtil.findImmediateChildrenOfClass(fields, RPsiDuneField.class);
    }

    public @Nullable RPsiDuneField getField(@NotNull String name) {
        for (RPsiDuneField field : getFields()) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                String name = getName();
                return name == null ? "unknown" : name;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.OBJECT;
            }
        };
    }

    @Override
    public @Nullable String toString() {
        return "Stanza " + getName();
    }
}
