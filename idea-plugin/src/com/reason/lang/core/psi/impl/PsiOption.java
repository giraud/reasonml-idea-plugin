package com.reason.lang.core.psi.impl;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLanguageConverter;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class PsiOption extends CompositeTypePsiElement<ORTypes> implements PsiLanguageConverter {

  protected PsiOption(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @NotNull
  @Override
  public String asText(@NotNull Language language) {
    if (getLanguage() == language) {
      return getText();
    }

    String convertedText = null;

    if (language == OclLanguage.INSTANCE) {
      // Convert from Reason to OCaml
      PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
      if (scope != null) {
        String scopeText = scope.getText();
        convertedText = scopeText.substring(1, scopeText.length() - 1) + " option";
      }
    }

    // Convert from OCaml

    return convertedText == null ? getText() : convertedText;
  }
}
