package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiJsObject extends CompositePsiElement implements PsiLanguageConverter {

  protected PsiJsObject(IElementType type) {
    super(type);
  }

  public @NotNull Collection<PsiObjectField> getFields() {
    return ORUtil.findImmediateChildrenOfClass(this, PsiObjectField.class);
  }

  public @Nullable PsiObjectField getField(@NotNull String name) {
    for (PsiObjectField field : getFields()) {
      if (name.equals(field.getName())) {
        return field;
      }
    }
    return null;
  }

  @Override
  public @NotNull String asText(@NotNull Language language) {
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
