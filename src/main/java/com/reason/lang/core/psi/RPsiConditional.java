package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.impl.RPsiBinaryCondition;
import org.jetbrains.annotations.Nullable;

public interface RPsiConditional {
  @Nullable
  RPsiBinaryCondition getCondition();

  @Nullable
  PsiElement getThenExpression();

  @Nullable
  PsiElement getElseExpression();
}
