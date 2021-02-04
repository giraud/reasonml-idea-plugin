package com.reason.lang.core.psi.impl;

import com.intellij.lang.Language;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiLanguageConverter;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class PsiObject extends CompositePsiElement implements PsiLanguageConverter {

  protected PsiObject(IElementType type) {
    super(type);
  }

  @NotNull
  @Override
  public String asText(@NotNull Language language) {
    String text = getText();
    if (getLanguage() == language) {
      return text;
    }

    String convertedText;

    if (language == OclLanguage.INSTANCE) {
      // Convert from Reason to OCaml
      convertedText = text;
    } else {
      // Convert from OCaml to Reason
      convertedText = "{. " + text.substring(1, getTextLength() - 1) + " }";
    }

    return convertedText;
  }

  @NotNull
  @Override
  public String toString() {
    return "Object";
  }
}
