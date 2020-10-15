package com.reason.lang.core.psi.impl;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiLanguageConverter;
import com.reason.lang.ocaml.OclLanguage;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class PsiJsObject extends CompositePsiElement implements PsiLanguageConverter {

  protected PsiJsObject(IElementType type) {
    super(type);
  }

  @NotNull
  public Collection<PsiObjectField> getFields() {
    return PsiTreeUtil.findChildrenOfType(this, PsiObjectField.class);
  }

  @NotNull
  @Override
  public String asText(@NotNull Language language) {
    if (getLanguage() == language) {
      return getText();
    }

    StringBuilder convertedText = new StringBuilder();
    boolean firstField = true;
    if (language == OclLanguage.INSTANCE) {
      // Convert from Reason to OCaml
      for (PsiElement element : getChildren()) {
        if (element instanceof PsiObjectField) {
          if (firstField) {
            firstField = false;
          } else {
            convertedText.append("; ");
          }
          convertedText.append(((PsiObjectField) element).asText(language));
        }
      }
      return "<" + convertedText + "> Js.t";
    }

    // Convert from OCaml to Reason
    for (PsiElement element : getChildren()) {
      if (element instanceof PsiObjectField) {
        if (firstField) {
          firstField = false;
        } else {
          convertedText.append(", ");
        }
        convertedText.append(((PsiObjectField) element).asText(language));
      }
    }
    return "{. " + convertedText + " }";
  }

  @NotNull
  @Override
  public String toString() {
    return "JsObject";
  }
}
