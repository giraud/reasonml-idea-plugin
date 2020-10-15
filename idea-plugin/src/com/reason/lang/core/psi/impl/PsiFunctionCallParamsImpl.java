package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.type.ORTypes;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PsiFunctionCallParamsImpl extends CompositeTypePsiElement<ORTypes>
    implements PsiFunctionCallParams {

  protected PsiFunctionCallParamsImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  @NotNull
  public List<PsiParameter> getParametersList() {
    return ORUtil.findImmediateChildrenOfClass(this, PsiParameter.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "Function call parameters";
  }
}
