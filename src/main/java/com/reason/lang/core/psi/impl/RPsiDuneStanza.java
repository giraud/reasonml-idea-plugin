package com.reason.lang.core.psi.impl;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class RPsiDuneStanza extends ORCompositePsiElement<DuneTypes> implements PsiNameIdentifierOwner, RPsiStructuredElement {
    public RPsiDuneStanza(@NotNull DuneTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement firstChild = getFirstChild();
        PsiElement nextSibling = firstChild.getNextSibling();
        return nextSibling != null && nextSibling.getNode().getElementType() == myTypes.ATOM
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
}
