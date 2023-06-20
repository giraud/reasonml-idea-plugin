package com.reason.lang.ocamllex;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.ocamllex.*;
import org.jetbrains.annotations.*;

class OclLexAstFactory extends ASTFactory {
    private OclLexAstFactory() {
    }

    public static @NotNull PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();

        if (type == OclLexTypes.INSTANCE.C_LET) {
            return new RPsiLexLet(node);
        }
        if (type == OclLexTypes.INSTANCE.C_RULE) {
            return new RPsiLexRule(node);
        }
        if (type == OclLexTypes.INSTANCE.C_PATTERN) {
            return new RPsiLexPattern(node);
        }
        if (type == OclLexTypes.INSTANCE.C_INJECTION) {
            return new RPsiOCamlInjection(node);
        }

        return new ASTWrapperPsiElement(node);
    }
}
