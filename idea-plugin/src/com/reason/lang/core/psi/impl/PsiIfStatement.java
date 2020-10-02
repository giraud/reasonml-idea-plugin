package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiBinaryCondition;
import com.reason.lang.core.psi.PsiConditional;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiIfStatement extends PsiToken<ORTypes> implements PsiConditional {

  public PsiIfStatement(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @Nullable
  public PsiBinaryCondition getCondition() {
    return findChildByClass(PsiBinaryCondition.class);
  }

  @Override
  public @Nullable PsiElement getThenExpression() {
    PsiBinaryCondition condition = getCondition();
    return condition == null ? null : ORUtil.nextSibling(condition);
  }

  @Override
  public @Nullable PsiElement getElseExpression() {
    PsiElement else_ = ORUtil.findImmediateFirstChildOfType(this, m_types.ELSE);
    return else_ == null ? null : ORUtil.nextSibling(else_);
  }
}
