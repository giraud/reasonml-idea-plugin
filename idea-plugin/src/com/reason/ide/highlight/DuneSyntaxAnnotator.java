package com.reason.ide.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiDuneField;
import com.reason.lang.core.psi.PsiStanza;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class DuneSyntaxAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    IElementType elementType = element.getNode().getElementType();

    if (element instanceof PsiStanza) {
      PsiElement identifier = ((PsiStanza) element).getNameIdentifier();
      if (identifier != null) {
        color(holder, identifier, DuneSyntaxHighlighter.STANZAS_);
      }
    }
    if (element instanceof PsiDuneField) {
      PsiElement identifier = ((PsiDuneField) element).getNameIdentifier();
      if (identifier != null) {
        color(holder, identifier, DuneSyntaxHighlighter.FIELDS_);
      }
    } else if (elementType == DuneTypes.INSTANCE.C_VAR) {
      color(holder, element, DuneSyntaxHighlighter.VAR_);
    }
  }

  private void color(AnnotationHolder holder, PsiElement element, TextAttributesKey key) {
    EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
    TextAttributes scheme = globalScheme.getAttributes(key);

    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
  }
}
