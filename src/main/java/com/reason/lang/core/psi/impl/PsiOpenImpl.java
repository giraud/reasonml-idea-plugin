package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.reason.icons.Icons;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiModuleName;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiStructuredElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiOpenImpl extends MlAstWrapperPsiElement implements PsiOpen, PsiStructuredElement {

    //region Constructors
    public PsiOpenImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    @Nullable
    public PsiElement getNameIdentifier() {
        return findChildByType(m_types.MODULE_PATH);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public PsiReference getReference() {
        PsiElement nameIdentifier = this.getNameIdentifier();
        if (nameIdentifier != null) {
            PsiElement firstChild = nameIdentifier.getFirstChild(); // only first module name, not path yet
            if (firstChild instanceof PsiModuleName) {
                return new PsiModuleReference((PsiModuleName) firstChild);
            }
        }
        return null;
    }

    @Override
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

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.OPEN;
            }
        };
    }

    @Override
    public String toString() {
        return "Open(" + getName() + ")";
    }
}
