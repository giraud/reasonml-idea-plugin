package com.reason.ide.highlight;

import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiInterpolationReference;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public abstract class ORSyntaxAnnotator implements Annotator {

  private final ORTypes m_types;

  ORSyntaxAnnotator(ORTypes types) {
    m_types = types;
  }

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    IElementType elementType = element.getNode().getElementType();

    if (elementType == m_types.C_UPPER_IDENTIFIER) {
      PsiElement parent = element.getParent();
      if (parent instanceof PsiModule) {
        color(holder, element.getNavigationElement(), MODULE_NAME_);
      }
    } else if (elementType == m_types.C_UPPER_SYMBOL) {
      PsiElement parent = element.getParent();
      PsiElement nextElement = element.getNextSibling();
      IElementType nextElementType =
          nextElement == null ? null : nextElement.getNode().getElementType();
      boolean mightBeVariant =
          (nextElementType != m_types.DOT)
              && !(parent instanceof PsiOpen)
              && !(parent instanceof PsiInclude)
              && !(parent instanceof PsiModule && ((PsiModule) parent).getAlias() != null);
      color(holder, element, mightBeVariant ? VARIANT_NAME_ : MODULE_NAME_);
    } else if (elementType == m_types.C_VARIANT_DECLARATION) {
      PsiElement identifier = element.getNavigationElement();
      color(holder, identifier, VARIANT_NAME_);
    } else if (elementType == m_types.C_MACRO_NAME) {
      color(holder, element, ANNOTATION_);
    } else if (elementType == m_types.TAG_NAME
        || elementType == m_types.TAG_LT
        || elementType == m_types.TAG_LT_SLASH
        || elementType == m_types.TAG_GT
        || elementType == m_types.TAG_AUTO_CLOSE) {
      color(holder, element, MARKUP_TAG_);
    } else if (elementType == m_types.OPTION) {
      color(holder, element, OPTION_);
    } else if (elementType == m_types.PROPERTY_NAME) {
      color(holder, element, MARKUP_ATTRIBUTE_);
    } else if (elementType == m_types.C_INTERPOLATION_PART) {
      color(holder, element, STRING_);
    } else if (element instanceof PsiInterpolationReference) {
      color(holder, element, DefaultLanguageHighlighterColors.IDENTIFIER);
    }
  }

  private void color(AnnotationHolder holder, PsiElement element, TextAttributesKey key) {
    EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
    TextAttributes scheme = globalScheme.getAttributes(key);

    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
  }
}
