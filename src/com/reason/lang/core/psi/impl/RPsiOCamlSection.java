package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import org.jetbrains.annotations.*;

public class RPsiOCamlSection extends ASTWrapperPsiElement {
    public RPsiOCamlSection(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String toString() {
        return "OCaml lazy section";
    }
}
