package com.reason.ide.highlight;

import static com.intellij.lang.annotation.HighlightSeverity.INFORMATION;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.impl.RPsiDuneField;
import com.reason.lang.core.psi.impl.RPsiDuneStanza;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class DuneSyntaxAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    IElementType elementType = element.getNode().getElementType();

    if (element instanceof RPsiDuneStanza) {
      PsiElement identifier = ((RPsiDuneStanza) element).getNameIdentifier();
      if (identifier != null) {
        color(holder, identifier, DuneSyntaxHighlighter.STANZAS_);
      }
    }
    if (element instanceof RPsiDuneField) {
      PsiElement identifier = ((RPsiDuneField) element).getNameIdentifier();
      if (identifier != null) {
        color(holder, identifier, DuneSyntaxHighlighter.FIELDS_);
      }
    } else if (elementType == DuneTypes.INSTANCE.C_VAR) {
      color(holder, element, DuneSyntaxHighlighter.VAR_);
    }
  }

  private void color(
      @NotNull AnnotationHolder holder,
      @NotNull PsiElement element,
      @NotNull TextAttributesKey key) {
    holder.newSilentAnnotation(INFORMATION).range(element).textAttributes(key).create();
  }
}
