package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.type.ORTypes;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class PsiFunctionCallParamsImpl extends PsiToken<ORTypes> implements PsiFunctionCallParams {

  public PsiFunctionCallParamsImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
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
