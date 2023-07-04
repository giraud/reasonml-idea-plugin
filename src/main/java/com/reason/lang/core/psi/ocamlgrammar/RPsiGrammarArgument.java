package com.reason.lang.core.psi.ocamlgrammar;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.ocamlgrammar.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RPsiGrammarArgument extends ASTWrapperPsiElement implements RPsiStructuredElement {
    public RPsiGrammarArgument(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                PsiElement ident = ORUtil.findImmediateFirstChildOfType(RPsiGrammarArgument.this, OclGrammarTypes.INSTANCE.IDENT);
                return "Argument " + (ident == null ?  "" : ident.getText());
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.OBJECT;
            }
        };
    }
}
