package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.psi.*;

class PsiElementFactory {
    static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (type == RmlTypes.EXTERNAL_EXPRESSION) {
            return new ReasonMLExternal(node);
        } else if (type == RmlTypes.FUN_BODY) {
            return new ReasonMLFunBody(node);
        } else if (type == RmlTypes.LET_BINDING) {
            return new ReasonMLLetBinding(node);
        } else if (type == RmlTypes.LET_EXPRESSION) {
            return new ReasonMLLet(node);
        } else if (type == RmlTypes.ANNOTATION_EXPRESSION) {
            return new PsiAnnotation(node);
        } else if (type == RmlTypes.ANNOTATION_NAME) {
            return new PsiAnnotationName(node);
        } else if (type == RmlTypes.MODULE_NAME) {
            return new ReasonMLModuleName(node);
        } else if (type == RmlTypes.MODULE_EXPRESSION) {
            return new ReasonMLModule(node);
        } else if (type == RmlTypes.TYPE_CONSTR_NAME) {
            return new ReasonMLTypeConstrName(node);
        } else if (type == RmlTypes.SCOPED_EXPR) {
            return new ReasonMLScopedExpr(node);
        } else if (type == RmlTypes.TYPE_EXPRESSION) {
            return new ReasonMLType(node);
        } else if (type == RmlTypes.VALUE_NAME) {
            return new ReasonMLValueName(node);
        }
        return new ReasonMLToken(node);
    }
}
