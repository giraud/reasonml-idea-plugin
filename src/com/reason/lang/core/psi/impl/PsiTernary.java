package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiConditional;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTernary extends CompositeTypePsiElement<ORTypes> implements PsiConditional {

  protected PsiTernary(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
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
