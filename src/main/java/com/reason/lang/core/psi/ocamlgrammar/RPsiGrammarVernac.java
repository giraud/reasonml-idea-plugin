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

public class RPsiGrammarVernac extends ASTWrapperPsiElement implements RPsiStructuredElement {
    public RPsiGrammarVernac(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                PsiElement ident = ORUtil.findImmediateFirstChildOfType(RPsiGrammarVernac.this, OclGrammarTypes.INSTANCE.IDENT);
                return "Vernac " + (ident == null ? "" : ident.getText());
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.OBJECT;
            }
        };
    }
}
