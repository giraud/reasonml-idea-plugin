package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PsiStanza extends PsiToken<DuneTypes> implements PsiNameIdentifierOwner {
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

    @Override
    public String toString() {
        return "Stanza " + getName();
    }
}
