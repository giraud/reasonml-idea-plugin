package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.ReasonMLExternal;
import com.reason.psi.ReasonMLValueName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLExternalImpl extends ASTWrapperPsiElement implements ReasonMLExternal {

    public ReasonMLExternalImpl(ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public ReasonMLValueName getValueName() {
        return findNotNullChildByClass(ReasonMLValueName.class);
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
//                ReasonMLExternalAlias externalAlias = external.getExternalAlias();
                String externalName = getValueName().getText();
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
                return ReasonMLIcons.EXTERNAL;
            }
        };
    }

}
