package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiFunctorCall extends CompositeTypePsiElement<ORTypes> {

  protected PsiFunctorCall(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @NotNull
  public String getFunctorName() {
    String text = getText();

    PsiParameters params = PsiTreeUtil.findChildOfType(this, PsiParameters.class);
    if (params == null) {
      return text;
    }

    return text.substring(0, params.getTextOffset() - getTextOffset());
  }

  @NotNull
  @Override
  public String toString() {
    return "Functor call";
  }
}
