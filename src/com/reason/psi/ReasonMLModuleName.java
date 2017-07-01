package com.reason.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.psi.impl.RmlModuleReference;
import com.reason.psi.impl.RmlNamedElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReasonMLModuleName extends RmlNamedElementImpl/*TODO: needed?*/ {

    public ReasonMLModuleName(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RmlModuleReference(this, getText());
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        // TODO
        return null;
    }

    @Override
    public String toString() {
        return "ModuleName '" + getText() + "'";
    }
}
