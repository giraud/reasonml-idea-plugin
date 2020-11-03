package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiParametersImpl extends CompositeTypePsiElement<ORTypes> implements PsiParameters {

  protected PsiParametersImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  public int getSize() {
    return ORUtil.findImmediateChildrenOfClass(this, PsiParameter.class).size();
  }

  @NotNull
  @Override
  public String toString() {
    return "Parameters";
  }
}
