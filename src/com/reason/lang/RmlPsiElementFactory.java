package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.psi.ReasonMLModule;
import com.reason.psi.ReasonMLModuleName;
import com.reason.psi.impl.*;

public class RmlPsiElementFactory {
    static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == ReasonMLTypes.EXTERNAL_EXPRESSION) {
            return new ReasonMLExternalImpl(node);
        } else if (type == ReasonMLTypes.FUN_BODY) {
            return new ReasonMLFunBodyImpl(node);
        } else if (type == ReasonMLTypes.LET_BINDING) {
            return new ReasonMLLetBindingImpl(node);
        } else if (type == ReasonMLTypes.LET_EXPRESSION) {
            return new ReasonMLLetImpl(node);
        } else if (type == ReasonMLTypes.MODULE_NAME) {
            return new ReasonMLModuleName(node);
        } else if (type == ReasonMLTypes.MODULE_EXPRESSION) {
            return new ReasonMLModule(node);
        } else if (type == ReasonMLTypes.TYPE_CONSTR_NAME) {
            return new ReasonMLTypeConstrNameImpl(node);
        } else if (type == ReasonMLTypes.SCOPED_EXPR) {
            return new ReasonMLScopedExprImpl(node);
        } else if (type == ReasonMLTypes.TYPE_EXPRESSION) {
            return new ReasonMLTypeImpl(node);
        } else if (type == ReasonMLTypes.VALUE_NAME) {
            return new ReasonMLValueNameImpl(node);
        }
        return new ReasonMLTokenImpl(node);
    }
}
