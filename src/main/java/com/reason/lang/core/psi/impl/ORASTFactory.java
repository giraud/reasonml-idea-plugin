package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;

public class ORASTFactory<T extends ORLangTypes> extends ASTFactory {
    private final T myTypes;

    public ORASTFactory(T types) {
        myTypes = types;
    }

    @Override
    public @Nullable CompositeElement createComposite(@NotNull IElementType type) {
        if (type == myTypes.C_ASSERT_STMT) {
            return new RPsiAssert(myTypes, type);
        }
        if (type == myTypes.C_ARRAY) {
            return new RPsiArray(myTypes, type);
        }
        if (type == myTypes.C_LET_BINDING) {
            return new RPsiLetBinding(myTypes, type);
        }
        if (type == myTypes.C_DEFAULT_VALUE) {
            return new RPsiDefaultValue(myTypes, type);
        }
        if (type == myTypes.C_SIG_EXPR) {
            return new RPsiSignatureImpl(myTypes, type);
        }
        if (type == myTypes.C_SIG_ITEM) {
            return new RPsiSignatureItemImpl(myTypes, type);
        }
        if (type == myTypes.C_TAG) {
            return new RPsiTag(myTypes, type);
        }
        if (type == myTypes.C_TAG_BODY) {
            return new RPsiTagBody(myTypes, type);
        }
        if (type == myTypes.C_TAG_CLOSE) {
            return new RPsiTagClose(myTypes, type);
        }
        if (type == myTypes.C_TAG_PROPERTY) {
            return new RPsiTagProperty(myTypes, type);
        }
        if (type == myTypes.C_TAG_PROP_VALUE) {
            return new RPsiTagPropertyValue(myTypes, type);
        }
        if (type == myTypes.C_TAG_START) {
            return new RPsiTagStart(myTypes, type);
        }
        if (type == myTypes.C_FIELD_VALUE) {
            return new RPsiFieldValue(myTypes, type);
        }
        if (type == myTypes.C_FUN_EXPR) {
            return new RPsiFunSwitch(myTypes, type);
        }
        if (type == myTypes.C_FUNCTION_BODY) {
            return new RPsiFunctionBody(myTypes, type);
        }
        if (type == myTypes.C_FUNCTION_CALL) {
            return new RPsiFunctionCall(myTypes, type);
        }
        if (type == myTypes.C_FUNCTION_EXPR) {
            return new RPsiFunction(myTypes, type);
        }
        if (type == myTypes.C_CUSTOM_OPERATOR || type == myTypes.C_SCOPED_EXPR || type == myTypes.C_IF_THEN_ELSE || type == myTypes.C_DO_LOOP) {
            return new RPsiScopedExpr(myTypes, type);
        }
        if (type == myTypes.C_FIRST_CLASS) {
            return new RPsiFirstClass(myTypes, type);
        }
        if (type == myTypes.C_FOR_LOOP) {
            return new RPsiForLoop(myTypes, type);
        }
        if (type == myTypes.C_LOCAL_OPEN) {
            return new RPsiLocalOpen(myTypes, type);
        }
        if (type == myTypes.C_PARAM || type == myTypes.C_NAMED_PARAM) {
            return new RPsiParameterReference(myTypes, type);
        }
        if (type == myTypes.C_PARAMETERS || type == myTypes.C_VARIANT_CONSTRUCTOR) {
            return new RPsiParameters(myTypes, type);
        }
        if (type == myTypes.C_PATTERN_MATCH_BODY) {
            return new RPsiPatternMatchBody(myTypes, type);
        }
        if (type == myTypes.C_PATTERN_MATCH_EXPR) {
            return new RPsiPatternMatch(myTypes, type);
        }
        if (type == myTypes.C_JS_OBJECT) {
            return new RPsiJsObject(myTypes, type);
        }
        if (type == myTypes.C_DECONSTRUCTION) {
            return new RPsiDeconstruction(myTypes, type);
        }
        if (type == myTypes.C_TYPE_BINDING) {
            return new RPsiTypeBinding(myTypes, type);
        }
        if (type == myTypes.C_RECORD_EXPR) {
            return new RPsiRecord(myTypes, type);
        }
        if (type == myTypes.C_METHOD_CALL) {
            return new RPsiMethodCall(myTypes, type);
        }
        if (type == myTypes.C_MODULE_BINDING) {
            return new RPsiModuleBinding(myTypes, type);
        }
        if (type == myTypes.C_MODULE_SIGNATURE) {
            return new RPsiModuleSignature(myTypes, type);
        }
        if (type == myTypes.C_ANNOTATION) {
            return new RPsiAnnotation(myTypes, type);
        }
        if (type == myTypes.C_MACRO_BODY) {
            return new RPsiMacroBody(myTypes, type);
        }
        if (type == myTypes.C_FUNCTOR_CALL) {
            return new RPsiFunctorCall(myTypes, type);
        }
        if (type == myTypes.C_CONSTRAINTS) {
            return new RPsiConstraints(myTypes, type);
        }
        if (type == myTypes.C_TYPE_CONSTRAINT) {
            return new RPsiTypeConstraint(myTypes, type);
        }
        if (type == myTypes.C_GUARD) {
            return new RPsiGuard(myTypes, type);
        }
        if (type == myTypes.C_IF) {
            return new RPsiIfStatement(myTypes, type);
        }
        if (type == myTypes.C_OBJECT) {
            return new RPsiObject(myTypes, type);
        }
        if (type == myTypes.C_OPTION) {
            return new RPsiOption(myTypes, type);
        }
        if (type == myTypes.C_INHERIT) {
            return new RPsiInherit(myTypes, type);
        }
        if (type == myTypes.C_CLASS_CONSTR) {
            return new RPsiClassConstructor(myTypes, type);
        }
        if (type == myTypes.C_CLASS_FIELD) {
            return new RPsiClassField(myTypes, type);
        }
        if (type == myTypes.C_SWITCH_BODY) {
            return new RPsiSwitchBody(myTypes, type);
        }
        if (type == myTypes.C_SWITCH_EXPR || type == myTypes.C_MATCH_EXPR) {
            return new RPsiSwitch(myTypes, type);
        }
        if (type == myTypes.C_FUNCTOR_BINDING) {
            return new RPsiFunctorBinding(myTypes, type);
        }
        if (type == myTypes.C_FUNCTOR_RESULT) {
            return new RPsiFunctorResult(myTypes, type);
        }
        if (type == myTypes.C_BINARY_CONDITION) {
            return new RPsiBinaryCondition(myTypes, type);
        }
        if (type == myTypes.C_TERNARY) {
            return new RPsiTernary(myTypes, type);
        }
        if (type == myTypes.C_MIXIN_FIELD) {
            return new RPsiMixinField(myTypes, type);
        }
        if (type == myTypes.C_LET_ATTR) {
            return new RPsiLetAttribute(myTypes, type);
        }
        if (type == myTypes.C_CLOSED_VARIANT) {
            return new RPsiPolyVariantConstraint(myTypes, type);
        }
        if (type == myTypes.C_MACRO_EXPR) {
            return new RPsiMacro(myTypes, type);
        }
        if (type == myTypes.C_INTERPOLATION_EXPR) {
            return new RPsiInterpolation(myTypes, type);
        }
        if (type == myTypes.C_INTERPOLATION_REF) {
            return new RPsiInterpolationReference(myTypes, type);
        }
        if (type == myTypes.C_TRY_EXPR) {
            return new RPsiTry(myTypes, type);
        }
        if (type == myTypes.C_TRY_HANDLERS) {
            return new CompositePsiElement(type) {
            };
        }
        if (type == myTypes.C_TRY_HANDLER) {
            return new RPsiTryHandler(myTypes, type);
        }
        if (type == myTypes.C_TRY_HANDLER_BODY) {
            return new RPsiTryHandlerBody(myTypes, type);
        }
        if (type == myTypes.C_TRY_BODY) {
            return new RPsiTryBody(myTypes, type);
        }
        if (type == myTypes.C_TUPLE) {
            return new RPsiTuple(myTypes, type);
        }
        if (type == myTypes.C_ML_INTERPOLATOR) {
            return new RPsiMultiLineInterpolator(myTypes, type);
        }
        if (type == myTypes.C_DIRECTIVE) {
            return new RPsiDirective(myTypes, type);
        }
        if (type == myTypes.C_SOME) {
            return new RPsiOptionValue(myTypes, type);
        }
        if (type == myTypes.C_STRUCT_EXPR) {
            return new RPsiStruct(myTypes, type);
        }
        if (type == myTypes.C_UNIT) {
            return new RPsiUnit(myTypes, type);
        }
        if (type == myTypes.C_UNPACK) {
            return new RPsiUnpack(myTypes, type);
        }
        if (type == myTypes.C_WHILE) {
            return new RPsiWhile(myTypes, type);
        }
        // Generic
        if (type == myTypes.C_INTERPOLATION_PART || type == myTypes.C_TYPE_VARIABLE) {
            return new CompositePsiElement(type) {
            };
        }
        if (type == myTypes.C_OPEN_VARIANT) {
            return new RPsiPolyVariantConstraint(myTypes, type);
        }
        if (type == myTypes.C_MACRO_NAME) {
            return new RPsiMacroName(myTypes, type);
        }

        return null;
    }

    @Override
    public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
        if (type == myTypes.UIDENT || type == myTypes.A_VARIANT_NAME || type == myTypes.A_MODULE_NAME || type == myTypes.A_EXCEPTION_NAME) {
            if (!DUMMY_IDENTIFIER_TRIMMED.contentEquals(text)) {
                return new RPsiUpperSymbol(myTypes, type, text);
            }
        }
        if (type == myTypes.A_UPPER_TAG_NAME) {
            return new RPsiUpperTagName(myTypes, type, text);
        }
        if (type == myTypes.LIDENT) {
            return new RPsiLowerSymbol(myTypes, type, text);
        }
        if (type == myTypes.PROPERTY_NAME) {
            return new RPsiLeafPropertyName(type, text);
        }
        if (type == myTypes.STRING_VALUE) {
            return new RPsiLiteralString(myTypes, type, text);
        }

        return super.createLeaf(type, text);
    }
}
