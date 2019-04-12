package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.ORTypes;

public class PsiElementFactory {
    private PsiElementFactory() {
    }

    public static PsiElement createElement(ORTypes types, ASTNode node) {
        IElementType type = node.getElementType();

        if (type == types.C_EXTERNAL_STMT) {
            return new PsiExternalImpl(types, node);
        } else if (type == types.C_EXCEPTION_EXPR) {
            return new PsiExceptionImpl(types, node);
        } else if (type == types.C_OPEN) {
            return new PsiOpenImpl(types, node);
        } else if (type == types.C_INCLUDE) {
            return new PsiIncludeImpl(types, node);
        } else if (type == types.C_EXP_TYPE) {
            return new PsiTypeImpl(types, node);
        } else if (type == types.C_ASSERT_STMT) {
            return new PsiAssert(node);
        } else if (type == types.C_IF_STMT) {
            return new PsiIfStatement(node);
        } else if (type == types.C_BIN_CONDITION) {
            return new PsiBinaryCondition(node);
        } else if (type == types.C_TYPE_CONSTR_NAME) {
            return new PsiTypeConstrName(node);
        } else if (type == types.C_TYPE_BINDING) {
            return new PsiTypeBinding(node);
        } else if (type == types.C_MODULE_STMT) {
            return new PsiInnerModuleImpl(types, node);
        } else if (type == types.C_CLASS_STMT) {
            return new PsiClassImpl(types, node);
        } else if (type == types.C_CLASS_PARAMS) {
            return new PsiClassParameters(node);
        } else if (type == types.C_CLASS_CONSTR) {
            return new PsiClassConstructor(node);
        } else if (type == types.C_CLASS_FIELD) {
            return new PsiClassField(node);
        } else if (type == types.C_CLASS_METHOD) {
            return new PsiClassMethod(node);
        } else if (type == types.C_MODULE_PATH) {
            return new PsiModulePath(node);
        } else if (type == types.C_LET_STMT) {
            return new PsiLetImpl(types, node);
        } else if (type == types.C_VAL_EXPR) {
            return new PsiValImpl(types, node);
        } else if (type == types.C_ANNOTATION_EXPR) {
            return new PsiAnnotationImpl(types, node);
        } else if (type == types.C_LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == types.C_FUN_CALL_PARAMS) {
            return new PsiFunctionCallParamsImpl(types, node);
        } else if (type == types.C_OPTION) {
            return new PsiOption(types, node);
        } else if (type == types.C_MACRO_EXPR) {
            return new PsiMacro(node);
        } else if (type == types.C_MACRO_NAME) {
            return new PsiMacroName(node);
        } else if (type == types.C_MACRO_RAW_BODY) {
            return new PsiRawBody(types, node);
        } else if (type == types.C_SCOPED_EXPR) {
            return new PsiScopedExpr(types, node);
        } else if (type == types.C_LOCAL_OPEN) {
            return new PsiLocalOpen(node);
        } else if (type == types.C_PATTERN_MATCH_BODY) {
            return new PsiPatternMatchBody(node);
        } else if (type == types.C_PATTERN_MATCH_EXPR) {
            return new PsiPatternMatch(node);
        } else if (type == types.C_RECORD_EXPR) {
            return new PsiRecord(node);
        } else if (type == types.C_MIXIN_FIELD) {
            return new PsiMixinField(node);
        } else if (type == types.C_RECORD_FIELD) {
            return new PsiRecordField(types, node);
        } else if (type == types.C_INTERPOLATION_EXPR) {
            return new PsiInterpolation(node);
        } else if (type == types.C_SIG_EXPR) {
            return new PsiSignatureImpl(types, node);
        } else if (type == types.C_TAG) {
            return new PsiTag(node);
        } else if (type == types.C_TAG_START) {
            return new PsiTagStartImpl(node);
        } else if (type == types.C_TAG_PROPERTY) {
            return new PsiTagPropertyImpl(types, node);
        } else if (type == types.C_TAG_PROP_VALUE) {
            return new PsiTagPropertyValueImpl(types, node);
        } else if (type == types.C_TAG_BODY) {
            return new PsiTagBody(node);
        } else if (type == types.C_TAG_CLOSE) {
            return new PsiTagClose(node);
        } else if (type == types.C_UPPER_SYMBOL) {
            return new PsiUpperSymbolImpl(types, node);
        } else if (type == types.C_LOWER_SYMBOL) {
            return new PsiLowerSymbolImpl(types, node);
        } else if (type == types.C_TRY_EXPR) {
            return new PsiTry(types, node);
        } else if (type == types.C_SWITCH_EXPR || type == types.C_MATCH_EXPR) {
            return new PsiSwitchImpl(types, node);
        } else if (type == types.C_FUNCTOR) {
            return new PsiFunctorImpl(types, node);
        } else if (type == types.C_FUNCTOR_PARAMS) {
            return new PsiParametersImpl(types, node);
        } else if (type == types.C_FUNCTOR_PARAM) {
            return new PsiParameterImpl(types, node);
        } else if (type == types.C_FUNCTOR_BINDING) {
            return new PsiFunctorBinding(types, node);
        } else if (type == types.C_FUN_EXPR) {
            return new PsiFunctionImpl(types, node);
        } else if (type == types.C_FUN_PARAMS) {
            return new PsiParametersImpl(types, node);
        } else if (type == types.C_FUN_PARAM) {
            return new PsiParameterImpl(types, node);
        } else if (type == types.C_FUN_BODY) {
            return new PsiFunctionBody(node);
        } else if (type == types.C_STRUCT_EXPR) {
            return new PsiStruct(node);
        } else if (type == types.C_ML_INTERPOLATOR) {
            return new PsiMultiLineInterpolator(node);
        } else if (type == types.C_JS_OBJECT) {
            return new PsiJsObject(node);
        } else if (type == types.C_JS_OBJECT_FIELD) {
            return new PsiJsObjectField(node);
        } else if (type == types.C_VARIANT_DECL) {
            return new PsiVariantDeclaration(types, node);
        } else if (type == types.C_VARIANT) {
            return new PsiUpperSymbolImpl(types, node);
        } else if (type == types.C_RAW) {
            return new PsiRaw(node);
        } else if (type == types.C_SIG_ITEM) {
            return new PsiSignatureItemImpl(types, node);
        } else if (type == types.C_UNKNOWN_EXPR) {
            // Try to resolve something from the parent context
            ASTNode parentNode = node.getTreeParent();
            if (parentNode != null && parentNode.getElementType() == types.C_FUN_PARAMS) {
                return new PsiParameterImpl(types, node);
            }
            // Remove the unknown node by its children
        }

        return new PsiToken<>(types, node);
    }
}
