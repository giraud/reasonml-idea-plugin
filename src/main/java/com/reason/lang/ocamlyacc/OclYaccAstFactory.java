package com.reason.lang.ocamlyacc;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.ocamlyacc.*;
import org.jetbrains.annotations.*;

class OclYaccAstFactory extends ASTFactory {
    private OclYaccAstFactory() {
    }

    public static @NotNull PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();

        if (type == OclYaccTypes.INSTANCE.C_HEADER) {
            return new RPsiYaccHeader(node);
        }
        if (type == OclYaccTypes.INSTANCE.C_DECLARATION) {
            return new RPisYaccDeclaration(node);
        }
        if (type == OclYaccTypes.INSTANCE.C_RULE) {
            return new RPsiYaccRule(node);
        }
        if (type == OclYaccTypes.INSTANCE.C_RULE_BODY) {
            return new RPsiYaccRuleBody(node);
        }
        if (type == OclYaccTypes.INSTANCE.C_INJECTION) {
            return new RPsiYaccInjection(node);
        }

        return new ASTWrapperPsiElement(node);
    }
}
