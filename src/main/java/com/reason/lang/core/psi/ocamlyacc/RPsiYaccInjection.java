package com.reason.lang.core.psi.ocamlyacc;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.extapi.psi.ASTWrapperPsiElement;

public class RPsiYaccInjection extends ASTWrapperPsiElement {
    public RPsiYaccInjection(@NotNull ASTNode node) {
        super(node);
    }
}
