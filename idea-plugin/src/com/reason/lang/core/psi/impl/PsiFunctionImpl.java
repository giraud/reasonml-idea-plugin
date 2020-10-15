package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.type.ORTypes;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiFunctionImpl extends CompositeTypePsiElement<ORTypes> implements PsiFunction {

  protected PsiFunctionImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  @NotNull
  public List<PsiParameter> getParameters() {
    return ORUtil.findImmediateChildrenOfClass(
        ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class), PsiParameter.class);
  }

  @Override
  @Nullable
  public PsiFunctionBody getBody() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctionBody.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "Function";
  }
}
