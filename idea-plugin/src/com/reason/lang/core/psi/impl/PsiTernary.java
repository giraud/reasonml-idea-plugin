package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiBinaryCondition;
import com.reason.lang.core.psi.PsiConditional;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTernary extends PsiToken<ORTypes> implements PsiConditional {

  public PsiTernary(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @Override
  public @Nullable PsiBinaryCondition getCondition() {
    return findChildByClass(PsiBinaryCondition.class);
  }

  @Override
  public @Nullable PsiElement getThenExpression() {
    PsiElement element = ORUtil.findImmediateFirstChildOfType(this, m_types.QUESTION_MARK);
    return element == null ? null : ORUtil.nextSibling(element);
  }

  @Override
  public @Nullable PsiElement getElseExpression() {
    PsiElement element = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
    return element == null ? null : ORUtil.nextSibling(element);
  }
}
