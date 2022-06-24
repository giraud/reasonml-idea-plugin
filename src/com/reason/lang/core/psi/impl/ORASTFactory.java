package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;

public class ORASTFactory<T extends ORTypes> extends ASTFactory {
    private final T myTypes;

    public ORASTFactory(T types) {
        myTypes = types;
    }

    @Override
    public @Nullable CompositeElement createComposite(@NotNull IElementType type) {
        if (type == myTypes.C_LET_BINDING) {
            return new PsiLetBinding(myTypes, type);
        }
        if (type == myTypes.C_DEFAULT_VALUE) {
            return new PsiDefaultValue(myTypes, type);
        }
        if (type == myTypes.C_SIG_EXPR) {
            return new PsiSignatureImpl(myTypes, type);
        }
        if (type == myTypes.C_TAG) {
            return new PsiTag(myTypes, type);
        }
        if (type == myTypes.C_TAG_BODY) {
            return new PsiTagBody(myTypes, type);
        }
        if (type == myTypes.C_TAG_CLOSE) {
            return new PsiTagClose(myTypes, type);
        }
        if (type == myTypes.C_TAG_PROPERTY) {
            return new PsiTagProperty(myTypes, type);
        }
        if (type == myTypes.C_TAG_PROP_VALUE) {
            return new PsiTagPropertyValue(myTypes, type);
        }
        if (type == myTypes.C_TAG_START) {
            return new PsiTagStart(myTypes, type);
        }
        if (type == myTypes.C_FIELD_VALUE) {
            return new PsiFieldValue(myTypes, type);
        }
        if (type == myTypes.C_FUN_CALL) {
            return new PsiFunctionCall(myTypes, type);
        }
        if (type == myTypes.C_FUN_BODY) {
            return new PsiFunctionBody(myTypes, type);
        }
        if (type == myTypes.C_SIG_ITEM) {
            return new PsiSignatureItemImpl(myTypes, type);
        }
        if (type == myTypes.C_CUSTOM_OPERATOR || type == myTypes.C_SCOPED_EXPR || type == myTypes.C_IF_THEN_SCOPE || type == myTypes.C_DO_LOOP) {
            return new PsiScopedExpr(myTypes, type);
        }
        if (type == myTypes.C_FOR_LOOP) {
            return new PsiForLoop(myTypes, type);
        }
        if (type == myTypes.C_LOCAL_OPEN) {
            return new PsiLocalOpen(myTypes, type);
        }
        if (type == myTypes.C_PARAM || type == myTypes.C_NAMED_PARAM) {
            return new PsiParameterReference(myTypes, type);
        }
        if (type == myTypes.C_PARAMETERS || type == myTypes.C_VARIANT_CONSTRUCTOR) {
            return new PsiParameters(myTypes, type);
        }
        if (type == myTypes.C_PATTERN_MATCH_BODY) {
            return new PsiPatternMatchBody(myTypes, type);
        }
        if (type == myTypes.C_PATTERN_MATCH_EXPR) {
            return new PsiPatternMatch(myTypes, type);
        }
        if (type == myTypes.C_JS_OBJECT) {
            return new PsiJsObject(myTypes, type);
        }
        if (type == myTypes.C_FUN_EXPR) {
            return new PsiFunction(myTypes, type);
        }
        if (type == myTypes.C_DECONSTRUCTION) {
            return new PsiDeconstruction(myTypes, type);
        }
        if (type == myTypes.C_TYPE_BINDING) {
            return new PsiTypeBinding(myTypes, type);
        }
        if (type == myTypes.C_RECORD_EXPR) {
            return new PsiRecord(myTypes, type);
        }
        if (type == myTypes.C_MODULE_TYPE) {
            return new PsiModuleType(myTypes, type);
        }
        if (type == myTypes.C_MODULE_BINDING) {
            return new PsiModuleBinding(myTypes, type);
        }
        if (type == myTypes.C_ANNOTATION) {
            return new PsiAnnotation(myTypes, type);
        }
        if (type == myTypes.C_MACRO_BODY) {
            return new PsiMacroBody(myTypes, type);
        }
        if (type == myTypes.C_FUNCTOR_CALL) {
            return new PsiFunctorCall(myTypes, type);
        }
        if (type == myTypes.C_CONSTRAINTS) {
            return new PsiConstraints(myTypes, type);
        }
        if (type == myTypes.C_CONSTRAINT) {
            return new PsiConstraint(myTypes, type);
        }
        if (type == myTypes.C_ASSERT_STMT) {
            return new PsiAssert(myTypes, type);
        }
        if (type == myTypes.C_IF) {
            return new PsiIfStatement(myTypes, type);
        }
        if (type == myTypes.C_OBJECT) {
            return new PsiObject(myTypes, type);
        }
        if (type == myTypes.C_OPTION) {
            return new PsiOption(myTypes, type);
        }
        if (type == myTypes.C_CLASS_CONSTR) {
            return new PsiClassConstructor(myTypes, type);
        }
        if (type == myTypes.C_CLASS_FIELD) {
            return new PsiClassField(myTypes, type);
        }
        if (type == myTypes.C_CLASS_METHOD) {
            return new PsiClassMethod(myTypes, type);
        }
        if (type == myTypes.C_SWITCH_BODY) {
            return new PsiSwitchBody(myTypes, type);
        }
        if (type == myTypes.C_SWITCH_EXPR || type == myTypes.C_MATCH_EXPR) {
            return new PsiSwitch(myTypes, type);
        }
        if (type == myTypes.C_FUNCTOR_BINDING) {
            return new PsiFunctorBinding(myTypes, type);
        }
        if (type == myTypes.C_FUNCTOR_RESULT) {
            return new PsiFunctorResult(myTypes, type);
        }
        if (type == myTypes.C_BINARY_CONDITION) {
            return new PsiBinaryCondition(myTypes, type);
        }
        if (type == myTypes.C_TERNARY) {
            return new PsiTernary(myTypes, type);
        }
        if (type == myTypes.C_MIXIN_FIELD) {
            return new PsiMixinField(type);
        }
        if (type == myTypes.C_LET_ATTR) {
            return new PsiLetAttribute(type);
        }
        if (type == myTypes.C_CLOSED_VARIANT) {
            return new PsiPolyVariantConstraint(myTypes, type);
        }
        if (type == myTypes.C_MACRO_EXPR) {
            return new PsiMacro(myTypes, type);
        }
        if (type == myTypes.C_INTERPOLATION_EXPR) {
            return new PsiInterpolation(myTypes, type);
        }
        if (type == myTypes.C_INTERPOLATION_REF) {
            return new PsiInterpolationReference(myTypes, type);
        }
        if (type == myTypes.C_TRY_EXPR) {
            return new PsiTry(myTypes, type);
        }
        if (type == myTypes.C_TUPLE) {
            return new PsiTuple(myTypes, type);
        }
        if (type == myTypes.C_ML_INTERPOLATOR) {
            return new PsiMultiLineInterpolator(myTypes, type);
        }
        if (type == myTypes.C_DIRECTIVE) {
            return new PsiDirective(myTypes, type);
        }
        if (type == myTypes.C_STRUCT_EXPR) {
            return new PsiStruct(myTypes, type);
        }
        if (type == myTypes.C_UNIT) {
            return new PsiUnit(type);
        }
        if (type == myTypes.C_WHILE) {
            return new PsiWhile(myTypes, type);
        }
        // Generic
        if (type == myTypes.C_TRY_HANDLERS
                || type == myTypes.C_TRY_HANDLER
                || type == myTypes.C_TRY_BODY
                || type == myTypes.C_INTERPOLATION_PART
                || type == myTypes.C_TYPE_VARIABLE) {
            return new CompositePsiElement(type) {
            };
        }
        if (type == myTypes.C_OPEN_VARIANT) {
            return new PsiPolyVariantConstraint(myTypes, type);
        }
        if (type == myTypes.C_MACRO_NAME) {
            return new PsiMacroName(myTypes, type);
        }

        return null;
    }

    @Override
    public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
        if (type == myTypes.UIDENT || type == myTypes.A_VARIANT_NAME || type == myTypes.A_MODULE_NAME) {
            if (!DUMMY_IDENTIFIER_TRIMMED.contentEquals(text)) {
                return new PsiUpperSymbol(myTypes, type, text);
            }
        }
        if (type == myTypes.A_UPPER_TAG_NAME) {
            return new PsiUpperTagName(myTypes, type, text);
        }
        if (type == myTypes.LIDENT) {
            return new PsiLowerSymbol(myTypes, type, text);
        }
        if (type == myTypes.PROPERTY_NAME) {
            return new PsiLeafPropertyName(type, text);
        }
        if (type == myTypes.STRING_VALUE) {
            return new PsiLiteralExpression(type, text);
        }

        return super.createLeaf(type, text);
    }
}
