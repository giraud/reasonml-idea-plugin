package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.*;

class PsiElementFactory {
    static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();

        if (type == RmlTypes.FILE_MODULE) {
            return new PsiModuleFile(node);
        } else if (type == RmlTypes.EXTERNAL_EXPRESSION) {
            return new PsiExternal(node);
        } else if (type == RmlTypes.OPEN_EXPRESSION) {
            return new PsiOpen(node);
        } else if (type == RmlTypes.MODULE_EXPRESSION) {
            return new PsiModule(node);
        } else if (type == RmlTypes.LET_EXPRESSION) {
            return new PsiLet(node);
        } else if (type == RmlTypes.ANNOTATION_EXPRESSION) {
            return new PsiAnnotation(node);
        } else if (type == RmlTypes.FUN_BODY) {
            return new PsiFunBody(node);
        } else if (type == RmlTypes.LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == RmlTypes.ANNOTATION_NAME) {
            return new PsiAnnotationName(node);
        } else if (type == RmlTypes.MODULE) {
            return new PsiModule(node);
        } else if (type == RmlTypes.SCOPED_EXPR) {
            return new PsiScopedExpr(node);
        } else if (type == RmlTypes.TYPE_EXPRESSION) {
            return new PsiType(node);
        } else if (type == RmlTypes.TAG_START) {
            return new TagStart(node);
        } else if (type == RmlTypes.TAG_CLOSE) {
            return new TagClose(node);
        }

        return new PsiToken(node);
    }
}
