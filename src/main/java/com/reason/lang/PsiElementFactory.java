package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

class PsiElementFactory {
    static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();

        if (type == RmlTypes.EXTERNAL_EXPRESSION) {
            return new PsiExternal(node);
        } else if (type == RmlTypes.OPEN_EXPRESSION) {
            return new PsiOpen(node);
        } else if (type == RmlTypes.TYPE_EXPRESSION) {
            return new PsiTypeImpl(node);
        } else if (type == RmlTypes.TYPE_CONSTR_NAME) {
            return new PsiTypeNameImpl(node);
        } else if (type == RmlTypes.MODULE_EXPRESSION) {
            return new PsiModuleImpl(node);
        } else if (type == RmlTypes.MODULE_NAME) {
            return new PsiModuleNameImpl(node);
        } else if (type == RmlTypes.LET_EXPRESSION) {
            return new PsiLetImpl(node);
        } else if (type == RmlTypes.ANNOTATION_EXPRESSION) {
            return new PsiAnnotation(node);
        } else if (type == RmlTypes.FUN_BODY) {
            return new PsiFunBody(node);
        } else if (type == RmlTypes.LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == RmlTypes.MACRO_NAME) {
            return new PsiMacroName(node);
        } else if (type == RmlTypes.SCOPED_EXPR || type == RmlTypes.OBJECT_EXPR || type == RmlTypes.PATTERN_MATCH_EXPR) {
            return new PsiScopedExpr(node);
        } else if (type == RmlTypes.TAG_START) {
            return new PsiTagStart(node);
        } else if (type == RmlTypes.TAG_PROPERTY) {
            return new PsiTagProperty(node);
        } else if (type == RmlTypes.TAG_CLOSE) {
            return new PsiTagClose(node);
        }

        return new PsiToken(node);
    }
}
