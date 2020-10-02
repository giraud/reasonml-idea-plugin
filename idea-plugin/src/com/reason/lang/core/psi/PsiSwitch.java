package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiSwitch extends PsiElement {
  @Nullable
  PsiBinaryCondition getCondition();

  @NotNull
  List<PsiPatternMatch> getPatterns();
}
