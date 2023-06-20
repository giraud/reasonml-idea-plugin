package com.reason.lang.ocamlgrammar;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.ocamlgrammar.*;
import org.jetbrains.annotations.*;

class OclGrammarAstFactory extends ASTFactory {
    private OclGrammarAstFactory() {
    }

    public static @NotNull PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();

        if (type == OclGrammarTypes.INSTANCE.C_VERNAC) {
            return new RPsiGrammarVernac(node);
        }
        if (type == OclGrammarTypes.INSTANCE.C_TACTIC) {
            return new RPsiGrammarTactic(node);
        }
        if (type == OclGrammarTypes.INSTANCE.C_ARGUMENT) {
            return new RPsiGrammarArgument(node);
        }
        if (type == OclGrammarTypes.INSTANCE.C_GRAMMAR) {
            return new RPsiGrammarGrammar(node);
        }
        if (type == OclGrammarTypes.INSTANCE.C_INJECTION) {
            return new RPsiOCamlInjection(node);
        }

        return new ASTWrapperPsiElement(node);
    }
}
