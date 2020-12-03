package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class PsiObjectField extends CompositeTypePsiElement<ORTypes> implements PsiLanguageConverter {

  protected PsiObjectField(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return getFirstChild();
  }

  @Override
  public String getName() {
    PsiElement nameElement = getNameIdentifier();
    return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
  }

  @Nullable
  public PsiSignature getSignature() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiSignature.class);
  }

  @NotNull
  @Override
  public String asText(@NotNull Language language) {
    if (getLanguage() == language) {
      return getText();
    }

    String convertedText;

    if (language == OclLanguage.INSTANCE) {
      // Convert from Reason to OCaml
      convertedText = getText();
    } else {
      // Convert from OCaml to Reason
      PsiElement nameIdentifier = getNameIdentifier();
      if (nameIdentifier == null) {
        convertedText = getText();
      } else {
        String valueAsText = "";
        PsiElement value = getValue();
        if (value instanceof PsiLanguageConverter) {
          valueAsText = ((PsiLanguageConverter) value).asText(language);
        } else if (value != null) {
          valueAsText = value.getText();
        }

        convertedText = "" + nameIdentifier.getText() + ":" + valueAsText;
      }
    }

    return convertedText;
  }

  @Nullable
  public PsiElement getValue() {
    PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
    return colon == null ? null : ORUtil.nextSiblingNode(colon.getNode()).getPsi();
  }

  @NotNull
  @Override
  public String toString() {
    return "ObjectField";
  }
}
