package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class TagStart extends ASTWrapperPsiElement {
    public TagStart(@NotNull ASTNode node) {
        super(node);
    }
}
