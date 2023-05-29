package com.reason.lang.core.psi.ocamllex;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiLexInjection extends ASTWrapperPsiElement implements RPsiStructuredElement {
    public RPsiLexInjection(@NotNull ASTNode node) {
        super(node);
    }

    // region RPsiStructuredElement
    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                return "OCaml";
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.OCAML;
            }
        };
    }
    // endregion
}
