package com.reason.lang.core;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiAssert;
import com.reason.lang.core.psi.PsiBinaryCondition;
import com.reason.lang.core.psi.PsiClassConstructor;
import com.reason.lang.core.psi.PsiClassField;
import com.reason.lang.core.psi.PsiClassMethod;
import com.reason.lang.core.psi.PsiClassParameters;
import com.reason.lang.core.psi.PsiConstraint;
import com.reason.lang.core.psi.PsiConstraints;
import com.reason.lang.core.psi.PsiDeconstruction;
import com.reason.lang.core.psi.PsiDirective;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiFunctionBody;
import com.reason.lang.core.psi.PsiFunctorBinding;
import com.reason.lang.core.psi.PsiFunctorCall;
import com.reason.lang.core.psi.PsiFunctorResult;
import com.reason.lang.core.psi.PsiIfStatement;
import com.reason.lang.core.psi.PsiInterpolation;
import com.reason.lang.core.psi.PsiInterpolationReference;
import com.reason.lang.core.psi.PsiJsObject;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiLocalOpen;
import com.reason.lang.core.psi.PsiMacro;
import com.reason.lang.core.psi.PsiMacroName;
import com.reason.lang.core.psi.PsiMixinField;
import com.reason.lang.core.psi.PsiModulePath;
import com.reason.lang.core.psi.PsiMultiLineInterpolator;
import com.reason.lang.core.psi.PsiObject;
import com.reason.lang.core.psi.PsiObjectField;
import com.reason.lang.core.psi.PsiOption;
import com.reason.lang.core.psi.PsiPatternMatch;
import com.reason.lang.core.psi.PsiPatternMatchBody;
import com.reason.lang.core.psi.PsiRaw;
import com.reason.lang.core.psi.PsiRawBody;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiStruct;
import com.reason.lang.core.psi.PsiTag;
import com.reason.lang.core.psi.PsiTagBody;
import com.reason.lang.core.psi.PsiTagClose;
import com.reason.lang.core.psi.PsiTry;
import com.reason.lang.core.psi.PsiTypeBinding;
import com.reason.lang.core.psi.PsiUnit;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.PsiWhile;
import com.reason.lang.core.psi.impl.PsiAnnotationImpl;
import com.reason.lang.core.psi.impl.PsiClassImpl;
import com.reason.lang.core.psi.impl.PsiExceptionImpl;
import com.reason.lang.core.psi.impl.PsiExternalImpl;
import com.reason.lang.core.psi.impl.PsiFunctionCallParamsImpl;
import com.reason.lang.core.psi.impl.PsiFunctionImpl;
import com.reason.lang.core.psi.impl.PsiFunctorImpl;
import com.reason.lang.core.psi.impl.PsiIncludeImpl;
import com.reason.lang.core.psi.impl.PsiInnerModuleImpl;
import com.reason.lang.core.psi.impl.PsiLetAttribute;
import com.reason.lang.core.psi.impl.PsiLetImpl;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiLowerSymbolImpl;
import com.reason.lang.core.psi.impl.PsiOpenImpl;
import com.reason.lang.core.psi.impl.PsiParameterImpl;
import com.reason.lang.core.psi.impl.PsiParametersImpl;
import com.reason.lang.core.psi.impl.PsiRecordFieldImpl;
import com.reason.lang.core.psi.impl.PsiSignatureImpl;
import com.reason.lang.core.psi.impl.PsiSignatureItemImpl;
import com.reason.lang.core.psi.impl.PsiSwitchImpl;
import com.reason.lang.core.psi.impl.PsiTagPropertyImpl;
import com.reason.lang.core.psi.impl.PsiTagPropertyValueImpl;
import com.reason.lang.core.psi.impl.PsiTagStartImpl;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.psi.impl.PsiTypeImpl;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperSymbolImpl;
import com.reason.lang.core.psi.impl.PsiValImpl;
import com.reason.lang.core.type.ORTypes;

public class PsiElementFactory {
    private PsiElementFactory() {
    }

    @NotNull
    public static PsiElement createElement(@NotNull ORTypes types, @NotNull ASTNode node) {
        IElementType type = node.getElementType();

        if (type == types.C_EXTERNAL_DECLARATION) {
            return new PsiExternalImpl(types, node);
        } else if (type == types.C_EXCEPTION_DECLARATION) {
            return new PsiExceptionImpl(types, node);
        } else if (type == types.C_OPEN) {
            return new PsiOpenImpl(types, node);
        } else if (type == types.C_INCLUDE) {
            return new PsiIncludeImpl(types, node);
        } else if (type == types.C_TYPE_DECLARATION) {
            return new PsiTypeImpl(types, node);
        } else if (type == types.C_ASSERT_STMT) {
            return new PsiAssert(node);
        } else if (type == types.C_IF_STMT) {
            return new PsiIfStatement(node);
        } else if (type == types.C_BIN_CONDITION || type == types.C_WHILE_CONDITION) {
            return new PsiBinaryCondition(node);
        } else if (type == types.C_TYPE_BINDING) {
            return new PsiTypeBinding(node);
        } else if (type == types.C_MODULE_DECLARATION) {
            return new PsiInnerModuleImpl(types, node);
        } else if (type == types.C_UPPER_IDENTIFIER) {
            return new PsiUpperIdentifier(types, node);
        } else if (type == types.C_CLASS_DECLARATION) {
            return new PsiClassImpl(types, node);
        } else if (type == types.C_CLASS_PARAMS) {
            return new PsiClassParameters(node);
        } else if (type == types.C_CLASS_CONSTR) {
            return new PsiClassConstructor(node);
        } else if (type == types.C_CLASS_FIELD) {
            return new PsiClassField(node);
        } else if (type == types.C_CLASS_METHOD) {
            return new PsiClassMethod(node);
        } else if (type == types.C_DECONSTRUCTION) {
            return new PsiDeconstruction(types, node);
        } else if (type == types.C_DIRECTIVE) {
            return new PsiDirective(node);
        } else if (type == types.C_MODULE_PATH) {
            return new PsiModulePath(node);
        } else if (type == types.C_LET_DECLARATION) {
            return new PsiLetImpl(types, node);
        } else if (type == types.C_LET_ATTR) {
            return new PsiLetAttribute(node);
        } else if (type == types.C_LET_BINDING) {
            return new PsiLetBinding(node);
        } else if (type == types.C_VAL_DECLARATION) {
            return new PsiValImpl(types, node);
        } else if (type == types.C_ANNOTATION) {
            return new PsiAnnotationImpl(types, node);
        } else if (type == types.C_FAKE_MODULE) {
            return new PsiFakeModule(types, node);
        } else if (type == types.C_FUN_CALL_PARAMS) {
            return new PsiFunctionCallParamsImpl(types, node);
        } else if (type == types.C_FUN_EXPR) {
            return new PsiFunctionImpl(types, node);
        } else if (type == types.C_FUN_PARAMS) {
            return new PsiParametersImpl(types, node);
        } else if (type == types.C_FUN_PARAM) {
            return new PsiParameterImpl(types, node);
        } else if (type == types.C_FUN_BODY) {
            return new PsiFunctionBody(node);
        } else if (type == types.C_LOWER_IDENTIFIER) {
            return new PsiLowerIdentifier(types, node);
        } else if (type == types.C_OBJECT) {
            return new PsiObject(node);
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
            return new PsiRecordFieldImpl(types, node);
        } else if (type == types.C_INTERPOLATION_EXPR) {
            return new PsiInterpolation(node);
        } else if (type == types.C_INTERPOLATION_REF) {
            return new PsiInterpolationReference(node);
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
        } else if (type == types.C_UNIT) {
            return new PsiUnit(node);
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
        } else if (type == types.C_FUNCTOR_RESULT) {
            return new PsiFunctorResult(types, node);
        } else if (type == types.C_FUNCTOR_CALL) {
            return new PsiFunctorCall(types, node);
        } else if (type == types.C_CONSTRAINTS) {
            return new PsiConstraints(types, node);
        } else if (type == types.C_CONSTRAINT) {
            return new PsiConstraint(types, node);
        } else if (type == types.C_STRUCT_EXPR) {
            return new PsiStruct(node);
        } else if (type == types.C_ML_INTERPOLATOR) {
            return new PsiMultiLineInterpolator(node);
        } else if (type == types.C_JS_OBJECT) {
            return new PsiJsObject(node);
        } else if (type == types.C_OBJECT_FIELD) {
            return new PsiObjectField(types, node);
        } else if (type == types.C_VARIANT_DECL) {
            return new PsiVariantDeclaration(types, node);
        } else if (type == types.C_VARIANT) {
            return new PsiUpperSymbolImpl(types, node);
        } else if (type == types.C_RAW) {
            return new PsiRaw(node);
        } else if (type == types.C_SIG_ITEM) {
            return new PsiSignatureItemImpl(types, node);
        } else if (type == types.C_WHILE) {
            return new PsiWhile(types, node);
        }

        return new PsiToken<>(types, node);
    }
}
