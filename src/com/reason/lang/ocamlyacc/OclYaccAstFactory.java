package com.reason.lang.ocamlyacc;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.ocamlyacc.impl.*;

class OclYaccAstFactory {
    private OclYaccAstFactory() {
    }

    public static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == OclYaccTypes.INSTANCE.DECLARATION) {
            return new OclYaccDeclarationImpl(node);
        } else if (type == OclYaccTypes.INSTANCE.HEADER) {
            return new OclYaccHeaderImpl(node);
        } else if (type == OclYaccTypes.INSTANCE.RULE) {
            return new OclYaccRuleImpl(node);
        } else if (type == OclYaccTypes.INSTANCE.RULE_BODY) {
            return new OclYaccRuleBodyImpl(node);
        } else if (type == OclYaccTypes.INSTANCE.RULE_PATTERN) {
            return new OclYaccRulePatternImpl(node);
        } else if (type == OclYaccTypes.INSTANCE.TRAILER) {
            return new OclYaccTrailerImpl(node);
        }
        return new ASTWrapperPsiElement(node);
    }
}
