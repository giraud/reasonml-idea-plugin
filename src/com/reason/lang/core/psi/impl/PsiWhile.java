package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiWhile extends CompositeTypePsiElement<ORTypes> {

  protected PsiWhile(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Nullable
  public PsiBinaryCondition getCondition() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiBinaryCondition.class);
  }

  @Nullable
  public PsiScopedExpr getBody() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "While";
  }
}
