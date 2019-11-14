package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiStanza extends PsiToken<DuneTypes> implements PsiNameIdentifierOwner, PsiStructuredElement {
    public PsiStanza(@NotNull DuneTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiElement firstChild = getFirstChild();
        PsiElement nextSibling = firstChild.getNextSibling();
        return nextSibling != null && nextSibling.getNode().getElementType() == m_types.ATOM ? nextSibling : null;
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement identifier = getNameIdentifier();
        return identifier == null ? null : identifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    public Collection<PsiDuneField> getFields() {
        PsiDuneFields fields = ORUtil.findImmediateFirstChildOfClass(this, PsiDuneFields.class);
        return ORUtil.findImmediateChildrenOfClass(fields, PsiDuneField.class);
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
                return Icons.OBJECT;
            }
        };
    }

    @Override
    public String toString() {
        return "Stanza " + getName();
    }
}
