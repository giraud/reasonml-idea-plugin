package com.reason.ide.go;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiElementBase;
import com.reason.lang.core.psi.PsiQualifiedElement;

import javax.swing.*;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ORDelegatePsiElement extends PsiElementBase {
    protected final @NotNull PsiQualifiedElement m_source;

    protected ORDelegatePsiElement(@NotNull PsiQualifiedElement source) {
        m_source = source;
    }

    @Override
    public @NotNull PsiElement getOriginalElement() {
        return m_source;
    }

    @Override
    public @Nullable PsiElement getParent() {
        return m_source.getParent();
    }

    @Override
    public @NotNull PsiElement getNavigationElement() {
        return m_source.getNavigationElement();
    }

    @Override
    public @NotNull Language getLanguage() {
        return m_source.getLanguage();
    }

    @Override
    public @NotNull PsiElement[] getChildren() {
        return m_source.getChildren();
    }

    @Override
    public @Nullable PsiElement getFirstChild() {
        return m_source.getFirstChild();
    }

    @Override
    public @Nullable PsiElement getLastChild() {
        return m_source.getLastChild();
    }

    @Override
    public @Nullable PsiElement getNextSibling() {
        return m_source.getNextSibling();
    }

    @Override
    public @Nullable PsiElement getPrevSibling() {
        return m_source.getPrevSibling();
    }

    @Override
    public @Nullable TextRange getTextRange() {
        return m_source.getTextRange();
    }

    @Override
    public int getStartOffsetInParent() {
        return m_source.getStartOffsetInParent();
    }

    @Override
    public int getTextLength() {
        return m_source.getTextLength();
    }

    @Override
    public @Nullable PsiElement findElementAt(int offset) {
        return m_source.findElementAt(offset);
    }

    @Override
    public int getTextOffset() {
        return m_source.getTextOffset();
    }

    @Override
    @NonNls
    public @Nullable String getText() {
        return m_source.getText();
    }

    @Override
    public @NotNull TextRange getTextRangeInParent() {
        return m_source.getTextRangeInParent();
    }

    @Override
    public @NotNull char[] textToCharArray() {
        return m_source.textToCharArray();
    }

    @Override
    public boolean textContains(char c) {
        return m_source.textContains(c);
    }

    @Override
    public @Nullable ASTNode getNode() {
        return m_source.getNode();
    }

    @Override
    public @Nullable Icon getIcon(final int flags) {
        return m_source.getIcon(flags);
    }

    @Override
    public PsiManager getManager() {
        return m_source.getManager();
    }

    @Override
    public boolean isPhysical() {
        return false;
    }
}
