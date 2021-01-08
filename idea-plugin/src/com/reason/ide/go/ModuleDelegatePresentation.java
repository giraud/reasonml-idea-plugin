package com.reason.ide.go;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

class ModuleDelegatePresentation extends ORDelegatePsiElement
        implements NavigationItem, PsiQualifiedElement {
    private final @NotNull ItemPresentation m_presentation;

    public ModuleDelegatePresentation(
            @NotNull PsiQualifiedElement source, @NotNull ItemPresentation presentation) {
        super(source);
        m_presentation = presentation;
    }

    @Override
    public @NotNull String getPath() {
        return ORUtil.getQualifiedPath(m_source);
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public @NotNull String getQualifiedName() {
        return m_source.getQualifiedName();
    }

    @Override
    public Icon getIcon(final int flags) {
        return m_presentation.getIcon(false);
    }

    @Override
    protected Icon getElementIcon(final int flags) {
        return m_presentation.getIcon(false);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return m_presentation;
    }
}
