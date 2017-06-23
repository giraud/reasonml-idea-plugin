// This is a generated file. Not intended for manual editing.
package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.ReasonMLModule;
import com.reason.psi.ReasonMLModuleName;
import com.reason.psi.ReasonMLScopedExpr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLModuleImpl extends ASTWrapperPsiElement implements ReasonMLModule {

    public ReasonMLModuleImpl(ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public ReasonMLScopedExpr getModuleBody() {
        return findChildByClass(ReasonMLScopedExpr.class);
    }

    @Override
    @NotNull
    public ReasonMLModuleName getModuleName() {
        return findNotNullChildByClass(ReasonMLModuleName.class);
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getModuleName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
//                PsiFile containingFile = module.getContainingFile();
//                return containingFile == null ? null : containingFile.getName();
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.MODULE;
            }
        };

    }

}
