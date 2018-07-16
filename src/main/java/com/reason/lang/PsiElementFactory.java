package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

public class PsiElementFactory {
    private PsiElementFactory() {
    }

    public static PsiElement createElement(MlTypes types, ASTNode node) {
        IElementType type = node.getElementType();

        if (type == types.EXTERNAL_EXPRESSION) {
            return new PsiExternalImpl(types, node);
        } else if (type == types.EXCEPTION_EXPRESSION) {
            return new PsiExceptionImpl(node);
        } else if (type == types.OPEN_EXPRESSION) {
            return new PsiOpenImpl(types, node);
        } else if (type == types.INCLUDE_EXPRESSION) {
            return new PsiIncludeImpl(types, node);
        } else if (type == types.TYPE_EXPRESSION) {
            return new PsiTypeImpl(node);
        } else if (type == types.ASSERT) {
            return new PsiAssert(node);
        } else if (type == types.IF_STATEMENT) {
            return new PsiIfStatement(node);
        } else if (type == types.BIN_CONDITION) {
            return new PsiBinaryCondition(node);
        } else if (type == types.TYPE_CONSTR_NAME) {
            return new PsiTypeConstrName(node);
        } else if (type == types.TYPE_BINDING) {
            return new PsiTypeBinding(node);
        } else if (type == types.MODULE_EXPRESSION) {
            return new PsiModuleImpl(node, types);
        } else if (type == types.MODULE_PATH) {
            return new PsiModulePath(node);
        } else if (type == types.LET_EXPRESSION) {
            return new PsiLetImpl(types, node);
        } else if (type == types.VAL_EXPRESSION) {
            return new PsiValImpl(types, node);
        } else if (type == types.ANNOTATION_EXPRESSION) {
            return new PsiAnnotation(node);
        } else if (type == types.LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == types.FUN_PARAMS) {
            return new PsiParametersImpl(types, node);
        } else if (type == types.MACRO_NAME) {
            return new PsiMacroName(node);
        } else if (type == types.SCOPED_EXPR) {
            return new PsiScopedExpr(node);
        } else if (type == types.LOCAL_OPEN) {
            return new PsiLocalOpen(node, types);
        } else if (type == types.PATTERN_MATCH_EXPR) {
            return new PsiPatternMatch(node);
        } else if (type == types.RECORD) {
            return new PsiRecord(node);
        } else if (type == types.RECORD_FIELD) {
            return new PsiRecordField(node);
        } else if (type == types.INTERPOLATION) {
            return new PsiInterpolation(node);
        } else if (type == types.SIG_SCOPE) {
            return new PsiSignatureImpl(node);
        } else if (type == types.TAG_START) {
            return new PsiTagStartImpl(node);
        } else if (type == types.TAG_PROPERTY) {
            return new PsiTagPropertyImpl(types, node);
        } else if (type == types.TAG_CLOSE) {
            return new PsiTagClose(node);
        } else if (type == types.UPPER_SYMBOL) {
            return new PsiUpperSymbolImpl(types, node);
        } else if (type == types.LOWER_SYMBOL) {
            return new PsiLowerSymbolImpl(types, node);
        } else if (type == types.NAMED_SYMBOL) {
            return new PsiNamedSymbol(node);
        } else if (type == types.SWITCH) {
            return new PsiSwitch(node);
        } else if (type == types.FUNCTION) {
            return new PsiFunction(node);
        } else if (type == types.FUN_BODY) {
            return new PsiFunctionBody(node);
        }

        return new PsiToken(node);
    }
}
