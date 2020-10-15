package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORASTFactory<T extends ORTypes> extends ASTFactory {
  private final T m_types;

  public ORASTFactory(T types) {
    m_types = types;
  }

  @Override
  public @Nullable CompositeElement createComposite(@NotNull IElementType type) {
    T types = m_types;
    if (type == this.m_types.C_LET_BINDING) {
      return new PsiLetBinding(type);
    }
    if (type == this.m_types.C_SIG_EXPR) {
      return new PsiSignatureImpl(this.m_types, type);
    }
    if (type == this.m_types.C_TAG) {
      return new PsiTag(this.m_types, type);
    }
    if (type == this.m_types.C_TAG_BODY) {
      return new PsiTagBody(type);
    }
    if (type == this.m_types.C_TAG_CLOSE) {
      return new PsiTagClose(type);
    }
    if (type == types.C_TAG_PROPERTY) {
      return new PsiTagPropertyImpl(types, type);
    }
    if (type == types.C_TAG_PROP_VALUE) {
      return new PsiTagPropertyValueImpl(types, type);
    }
    if (type == this.m_types.C_TAG_START) {
      return new PsiTagStartImpl(this.m_types, type);
    }
    if (type == types.C_FUN_CALL_PARAMS) {
      return new PsiFunctionCallParamsImpl(types, type);
    }
    if (type == types.C_FUN_BODY) {
      return new PsiFunctionBody(type);
    }
    if (type == types.C_SIG_ITEM) {
      return new PsiSignatureItemImpl(types, type);
    }
    if (type == types.C_SCOPED_EXPR || type == types.C_IF_THEN_SCOPE || type == types.C_DO_LOOP) {
      return new PsiScopedExpr(types, type);
    }
    if (type == types.C_LOCAL_OPEN) {
      return new PsiLocalOpen(type);
    }
    if (type == types.C_PATTERN_MATCH_BODY) {
      return new PsiPatternMatchBody(type);
    }
    if (type == types.C_PATTERN_MATCH_EXPR) {
      return new PsiPatternMatch(type);
    }
    if (type == types.C_JS_OBJECT) {
      return new PsiJsObject(type);
    }
    if (type == types.C_OBJECT_FIELD) {
      return new PsiObjectField(types, type);
    }
    if (type == types.C_FUN_EXPR) {
      return new PsiFunctionImpl(types, type);
    }
    if (type == types.C_FUN_PARAMS
        || type == types.C_FUNCTOR_PARAMS
        || type == types.C_VARIANT_CONSTRUCTOR) {
      return new PsiParametersImpl(types, type);
    }
    if (type == types.C_DECONSTRUCTION) {
      return new PsiDeconstruction(types, type);
    }
    if (type == types.C_TYPE_BINDING) {
      return new PsiTypeBinding(type);
    }
    if (type == types.C_RECORD_EXPR) {
      return new PsiRecord(type);
    }
    if (type == types.C_MODULE_TYPE) {
      return new PsiModuleType(types, type);
    }
    if (type == types.C_ANNOTATION) {
      return new PsiAnnotationImpl(types, type);
    }
    if (type == types.C_MACRO_RAW_BODY) {
      return new PsiRawBody(types, type);
    }
    if (type == types.C_FUNCTOR_CALL) {
      return new PsiFunctorCall(types, type);
    }
    if (type == types.C_CONSTRAINTS) {
      return new PsiConstraints(types, type);
    }
    if (type == types.C_CONSTRAINT) {
      return new PsiConstraint(types, type);
    }
    if (type == types.C_ASSERT_STMT) {
      return new PsiAssert(type);
    }
    if (type == types.C_CLASS_DECLARATION) {
      return new PsiClassImpl(types, type);
    }
    if (type == types.C_IF) {
      return new PsiIfStatement(types, type);
    }
    if (type == types.C_OPEN) {
      return new PsiOpenImpl(types, type);
    }
    if (type == types.C_INCLUDE) {
      return new PsiIncludeImpl(types, type);
    }
    if (type == types.C_OBJECT) {
      return new PsiObject(type);
    }
    if (type == types.C_OPTION) {
      return new PsiOption(types, type);
    }
    if (type == types.C_CLASS_PARAMS) {
      return new PsiClassParameters(type);
    }
    if (type == types.C_CLASS_CONSTR) {
      return new PsiClassConstructor(type);
    }
    if (type == types.C_CLASS_FIELD) {
      return new PsiClassField(type);
    }
    if (type == types.C_CLASS_METHOD) {
      return new PsiClassMethod(type);
    }
    if (type == types.C_SWITCH_EXPR || type == types.C_MATCH_EXPR) {
      return new PsiSwitchImpl(types, type);
    }
    if (type == types.C_FUNCTOR_BINDING) {
      return new PsiFunctorBinding(types, type);
    }
    if (type == types.C_FUNCTOR_RESULT) {
      return new PsiFunctorResult(types, type);
    }
    if (type == types.C_BINARY_CONDITION) {
      return new PsiBinaryCondition(type);
    }
    if (type == types.C_TERNARY) {
      return new PsiTernary(types, type);
    }
    if (type == types.C_MIXIN_FIELD) {
      return new PsiMixinField(type);
    }
    if (type == types.C_LET_ATTR) {
      return new PsiLetAttribute(type);
    }
    if (type == types.C_MACRO_EXPR) {
      return new PsiMacro(type);
    }
    if (type == types.C_INTERPOLATION_EXPR) {
      return new PsiInterpolation(type);
    }
    if (type == types.C_INTERPOLATION_REF) {
      return new PsiInterpolationReference(type);
    }
    if (type == types.C_TRY_EXPR) {
      return new PsiTry(types, type);
    }
    if (type == types.C_ML_INTERPOLATOR) {
      return new PsiMultiLineInterpolator(type);
    }
    if (type == types.C_RAW) {
      return new PsiRaw(type);
    }
    if (type == types.C_DIRECTIVE) {
      return new PsiDirective(type);
    }
    if (type == types.C_STRUCT_EXPR) {
      return new PsiStruct(type);
    }
    if (type == types.C_UNIT) {
      return new PsiUnit(type);
    }
    if (type == types.C_WHILE) {
      return new PsiWhile(types, type);
    }
    // Generic
    if (type == types.C_TRY_HANDLERS
        || type == types.C_TRY_HANDLER
        || type == types.C_TRY_BODY
        || type == types.C_NAMED_PARAM
        || type == types.C_FUN_PARAM_BINDING
        || type == types.C_INTERPOLATION_PART
        || type == types.C_TYPE_VARIABLE) {
      return new CompositePsiElement(type) {};
    }
    // Leaf !?
    if (type == this.m_types.C_LOWER_IDENTIFIER) {
      return new PsiLowerIdentifier(this.m_types, type);
    }
    if (type == this.m_types.C_LOWER_SYMBOL) {
      return new PsiLowerSymbolImpl(this.m_types, type);
    }
    if (type == types.C_UPPER_IDENTIFIER) {
      return new PsiUpperIdentifier(types, type);
    }
    if (type == types.C_UPPER_SYMBOL) {
      return new PsiUpperSymbolImpl(types, type);
    }
    if (type == types.C_VARIANT) {
      return new PsiUpperSymbolImpl(types, type);
    }
    if (type == types.C_MACRO_NAME) {
      return new PsiMacroName(type);
    }

    return null;
  }

  @Override
  public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
    if (type == m_types.PROPERTY_NAME) {
      return new PsiPropertyName(type, text);
    }
    if (type == m_types.TAG_NAME) {
      return new LeafPsiElement(type, text) {
        @Override
        public String toString() {
          return "PsiTagName:" + getText();
        }
      };
    }

    return null;
  }
}
