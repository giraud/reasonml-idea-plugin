package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORElementFactory;
import com.reason.lang.core.psi.PsiLetName;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLetNameImpl extends PsiToken<ORTypes> implements PsiLetName {
    public PsiLetNameImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        return getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = ORElementFactory.createLetName(getProject(), newName);

        ASTNode newNameNode = newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();
        if (newNameNode != null) {
            PsiElement nameIdentifier = getNameIdentifier();
            if (nameIdentifier == null) {
                getNode().addChild(newNameNode);
            } else {
                ASTNode oldNameNode = nameIdentifier.getNode();
                getNode().replaceChild(oldNameNode, newNameNode);
            }
        }

        return this;
    }

    @NotNull
    @Override
    public String toString() {
        return "Name " + getText();
    }
}
