package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

public class PsiElementFactory {
    public static PsiElement createElement(MlTypes types, ASTNode node) {
        IElementType type = node.getElementType();

        if (type == types.FILE_MODULE) {
            return new PsiFileModuleImpl(node);
        } else if (type == types.EXTERNAL_EXPRESSION) {
            return new PsiExternalImpl(types, node);
        } else if (type == types.EXCEPTION_EXPRESSION) {
            return new PsiExceptionImpl(node);
        } else if (type == types.EXCEPTION_NAME) {
            return new PsiExceptionNameImpl(node);
        } else if (type == types.OPEN_EXPRESSION) {
            return new PsiOpenImpl(types, node);
        } else if (type == types.INCLUDE_EXPRESSION) {
            return new PsiIncludeImpl(types, node);
        } else if (type == types.TYPE_EXPRESSION) {
            return new PsiTypeImpl(types, node);
        } else if (type == types.TYPE_CONSTR_NAME) {
            return new PsiTypeNameImpl(types, node);
        } else if (type == types.MODULE_EXPRESSION) {
            return new PsiModuleImpl(node);
        } else if (type == types.MODULE_PATH) {
            return new PsiModulePath(node);
        } else if (type == types.LET_EXPRESSION) {
            return new PsiLetImpl(node);
        } else if (type == types.VAL_EXPRESSION) {
            return new PsiValImpl(types, node);
        } else if (type == types.ANNOTATION_EXPRESSION) {
            return new PsiAnnotation(node);
        } else if (type == types.FUN_BODY) {
            return new PsiFunBody(node);
        } else if (type == types.LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == types.MACRO_NAME) {
            return new PsiMacroName(node);
        } else if (type == types.SCOPED_EXPR || type == types.OBJECT_EXPR || type == types.PATTERN_MATCH_EXPR) {
            return new PsiScopedExpr(node);
        } else if (type == types.INTERPOLATION) {
            return new PsiInterpolation(node);
        } else if (type == types.SIG_SCOPE) {
            return new PsiSignatureImpl(node);
        } else if (type == types.TAG_START) {
            return new PsiTagStart(node);
        } else if (type == types.TAG_PROPERTY) {
            return new PsiTagPropertyImpl(types, node);
        } else if (type == types.TAG_CLOSE) {
            return new PsiTagClose(node);
        } else if (type == types.UPPER_SYMBOL) {
            return new PsiUpperSymbolImpl(types, node);
        } else if (type == types.VAR_NAME) {
            return new PsiVarNameImpl(types, node);
        }

        return new PsiToken(node);
    }
}
