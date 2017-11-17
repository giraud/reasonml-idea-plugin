package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import static com.reason.lang.RmlTypes.MODULE_PATH;

public class PsiOpen extends ASTWrapperPsiElement {
    PsiOpen(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        PsiElement name = findChildByType(TokenSet.create(MODULE_PATH));
        return name == null ? "" : name.getText();
    }

    @Override
    public String toString() {
        return "Open " + getName();
    }
}
