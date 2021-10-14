package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class ORASTFactory<T extends ORTypes> extends ASTFactory {
    private final T m_types;

    public ORASTFactory(T types) {
        m_types = types;
    }

    @Override
    public @Nullable CompositeElement createComposite(@NotNull IElementType type) {
        if (type == m_types.C_LET_BINDING) {
            return new PsiLetBinding(type);
        }
        if (type == m_types.C_DEFAULT_VALUE) {
            return new PsiDefaultValue(type);
        }
        if (type == m_types.C_SIG_EXPR) {
            return new PsiSignatureImpl(m_types, type);
        }
        if (type == m_types.C_TAG) {
            return new PsiTag(m_types, type);
        }
        if (type == m_types.C_TAG_BODY) {
            return new PsiTagBody(type);
        }
        if (type == m_types.C_TAG_CLOSE) {
            return new PsiTagClose(type);
        }
        if (type == m_types.C_TAG_PROPERTY) {
            return new PsiTagPropertyImpl(m_types, type);
        }
        if (type == m_types.C_TAG_PROP_VALUE) {
            return new PsiTagPropertyValueImpl(m_types, type);
        }
        if (type == m_types.C_TAG_START) {
            return new PsiTagStartImpl(m_types, type);
        }
        if (type == m_types.C_FUN_CALL_PARAMS) {
            return new PsiFunctionCallParamsImpl(m_types, type);
        }
        if (type == m_types.C_FUN_BODY) {
            return new PsiFunctionBody(type);
        }
        if (type == m_types.C_SIG_ITEM) {
            return new PsiSignatureItemImpl(m_types, type);
        }
        if (type == m_types.C_CUSTOM_OPERATOR || type == m_types.C_SCOPED_EXPR || type == m_types.C_IF_THEN_SCOPE || type == m_types.C_DO_LOOP) {
            return new PsiScopedExpr(m_types, type);
        }
        if (type == m_types.C_LOCAL_OPEN) {
            return new PsiLocalOpen(type);
        }
        if (type == m_types.C_NAMED_PARAM) {
            return new PsiNamedParam(m_types, type);
        }
        if (type == m_types.C_PARAMETERS) {
            return new PsiParametersImpl(m_types, type);
        }
        if (type == m_types.C_PATTERN_MATCH_BODY) {
            return new PsiPatternMatchBody(type);
        }
        if (type == m_types.C_PATTERN_MATCH_EXPR) {
            return new PsiPatternMatch(type);
        }
        if (type == m_types.C_JS_OBJECT) {
            return new PsiJsObject(type);
        }
        if (type == m_types.C_FUN_EXPR) {
            return new PsiFunctionImpl(m_types, type);
        }
        if (type == m_types.C_FUN_PARAMS
                || type == m_types.C_FUNCTOR_PARAMS
                || type == m_types.C_VARIANT_CONSTRUCTOR) {
            return new PsiParametersImpl(m_types, type);
        }
        if (type == m_types.C_DECONSTRUCTION) {
            return new PsiDeconstruction(m_types, type);
        }
        if (type == m_types.C_TYPE_BINDING) {
            return new PsiTypeBinding(type);
        }
        if (type == m_types.C_RECORD_EXPR) {
            return new PsiRecord(type);
        }
        if (type == m_types.C_MODULE_TYPE) {
            return new PsiModuleType(m_types, type);
        }
        if (type == m_types.C_ANNOTATION) {
            return new PsiAnnotationImpl(m_types, type);
        }
        if (type == m_types.C_MACRO_RAW_BODY) {
            return new PsiMacroBody(m_types, type);
        }
        if (type == m_types.C_FUNCTOR_CALL) {
            return new PsiFunctorCall(m_types, type);
        }
        if (type == m_types.C_CONSTRAINTS) {
            return new PsiConstraints(m_types, type);
        }
        if (type == m_types.C_CONSTRAINT) {
            return new PsiConstraint(m_types, type);
        }
        if (type == m_types.C_ASSERT_STMT) {
            return new PsiAssert(type);
        }
        if (type == m_types.C_IF) {
            return new PsiIfStatement(m_types, type);
        }
        if (type == m_types.C_OBJECT) {
            return new PsiObject(type);
        }
        if (type == m_types.C_OPTION) {
            return new PsiOption(m_types, type);
        }
        if (type == m_types.C_CLASS_PARAMS) {
            return new PsiClassParameters(type);
        }
        if (type == m_types.C_CLASS_CONSTR) {
            return new PsiClassConstructor(type);
        }
        if (type == m_types.C_CLASS_FIELD) {
            return new PsiClassField(type);
        }
        if (type == m_types.C_CLASS_METHOD) {
            return new PsiClassMethod(type);
        }
        if (type == m_types.C_SWITCH_EXPR || type == m_types.C_MATCH_EXPR) {
            return new PsiSwitchImpl(m_types, type);
        }
        if (type == m_types.C_FUNCTOR_BINDING) {
            return new PsiFunctorBinding(m_types, type);
        }
        if (type == m_types.C_FUNCTOR_RESULT) {
            return new PsiFunctorResult(m_types, type);
        }
        if (type == m_types.C_BINARY_CONDITION) {
            return new PsiBinaryCondition(type);
        }
        if (type == m_types.C_TERNARY) {
            return new PsiTernary(m_types, type);
        }
        if (type == m_types.C_MIXIN_FIELD) {
            return new PsiMixinField(type);
        }
        if (type == m_types.C_LET_ATTR) {
            return new PsiLetAttribute(type);
        }
        if (type == m_types.C_LOWER_BOUND_CONSTRAINT) {
            return new PsiTypeConstraint(m_types, type);
        }
        if (type == m_types.C_MACRO_EXPR) {
            return new PsiMacro(type);
        }
        if (type == m_types.C_INTERPOLATION_EXPR) {
            return new PsiInterpolation(type);
        }
        if (type == m_types.C_INTERPOLATION_REF) {
            return new PsiInterpolationReference(type);
        }
        if (type == m_types.C_TRY_EXPR) {
            return new PsiTry(m_types, type);
        }
        if (type == m_types.C_ML_INTERPOLATOR) {
            return new PsiMultiLineInterpolator(type);
        }
        if (type == m_types.C_DIRECTIVE) {
            return new PsiDirective(type);
        }
        if (type == m_types.C_STRUCT_EXPR) {
            return new PsiStruct(type);
        }
        if (type == m_types.C_UNIT) {
            return new PsiUnit(type);
        }
        if (type == m_types.C_WHILE) {
            return new PsiWhile(m_types, type);
        }
        // Generic
        if (type == m_types.C_TRY_HANDLERS
                || type == m_types.C_TRY_HANDLER
                || type == m_types.C_TRY_BODY
                || type == m_types.C_INTERPOLATION_PART
                || type == m_types.C_TYPE_VARIABLE) {
            return new CompositePsiElement(type) {
            };
        }
        // Leaf !?
        if (type == m_types.C_LOWER_IDENTIFIER) {
            return new PsiLowerIdentifier(m_types, type);
        }
        if (type == m_types.C_LOWER_SYMBOL) {
            return new PsiLowerSymbolImpl(m_types, type);
        }
        if (type == m_types.C_UPPER_IDENTIFIER) {
            return new PsiUpperIdentifier(m_types, type);
        }
        if (type == m_types.C_UPPER_BOUND_CONSTRAINT) {
            return new PsiTypeConstraint(m_types, type);
        }
        if (type == m_types.C_UPPER_SYMBOL) {
            return new PsiUpperSymbolImpl(m_types, type);
        }
        if (type == m_types.C_VARIANT) {
            return new PsiUpperSymbolImpl(m_types, type);
        }
        if (type == m_types.C_MACRO_NAME) {
            return new PsiMacroName(type);
        }

        return null;
    }

    @Override
    public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
        if (type == m_types.PROPERTY_NAME) {
            return new PsiLeafPropertyName(type, text);
        }
        if (type == m_types.TAG_NAME) {
            return new PsiLeafTagName(type, text);
        }
        if (type == m_types.STRING_VALUE) {
            return new PsiLiteralExpression(type, text);
        }

        return super.createLeaf(type, text);
    }
}
