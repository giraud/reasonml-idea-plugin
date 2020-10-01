package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface PsiConditional {
  @Nullable
  PsiBinaryCondition getCondition();

  @Nullable
  PsiElement getThenExpression();

  @Nullable
  PsiElement getElseExpression();
}
