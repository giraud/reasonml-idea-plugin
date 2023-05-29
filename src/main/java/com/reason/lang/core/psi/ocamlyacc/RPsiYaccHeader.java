package com.reason.lang.core.psi.ocamlyacc;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiYaccHeader extends ASTWrapperPsiElement implements RPsiStructuredElement {
    public RPsiYaccHeader(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return "Header";
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.OCAML;
            }
        };
    }
}
