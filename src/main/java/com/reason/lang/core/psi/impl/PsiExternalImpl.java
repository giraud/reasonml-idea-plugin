package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiExternal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiExternalImpl extends MlASTWrapperPsiElement implements PsiExternal {

    //region Constructors
    public PsiExternalImpl(MlTypes types, ASTNode node) {
        super(types, node);
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findNotNullChildByType(m_types.VALUE_NAME);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                //                ReasonMLExternalAlias externalAlias = external.getExternalAlias();
                String externalName = getName();
                //                if (externalAlias.getTextLength() == 2) {
                //                    return externalName;
                //                }

                //                String externalAliasText = externalAlias.getText();
                //                String externalAliasName = externalAliasText.substring(1, externalAliasText.length() - 1);
                //                return externalName + (externalAliasName.equals(externalName) ? "" : " ⇐ " + externalAliasName);
                return externalName;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.EXTERNAL;
            }
        };
    }
}
