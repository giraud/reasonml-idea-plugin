package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class TagClose extends ASTWrapperPsiElement {
    public TagClose(@NotNull ASTNode node) {
        super(node);
    }
}
