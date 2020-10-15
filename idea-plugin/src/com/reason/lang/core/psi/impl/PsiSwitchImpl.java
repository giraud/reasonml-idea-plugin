package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.type.ORTypes;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiSwitchImpl extends CompositeTypePsiElement<ORTypes> implements PsiSwitch {

  protected PsiSwitchImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Nullable
  public PsiBinaryCondition getCondition() {
    return PsiTreeUtil.findChildOfType(this, PsiBinaryCondition.class);
  }

  @NotNull
  @Override
  public List<PsiPatternMatch> getPatterns() {
    PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
    return ORUtil.findImmediateChildrenOfClass(scope == null ? this : scope, PsiPatternMatch.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "Switch/function";
  }
}
