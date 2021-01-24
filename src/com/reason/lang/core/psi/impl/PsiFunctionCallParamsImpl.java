package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiFunctionCallParamsImpl extends CompositeTypePsiElement<ORTypes> implements PsiFunctionCallParams {

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
