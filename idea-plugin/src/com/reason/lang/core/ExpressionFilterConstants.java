package com.reason.lang.core;

import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiType;
import org.jetbrains.annotations.Nullable;

public class ExpressionFilterConstants {

  public static final @Nullable ExpressionFilter NO_FILTER = null;
  public static final ExpressionFilter FILTER_LET = element -> element instanceof PsiLet;
  public static final ExpressionFilter FILTER_TYPE = element -> element instanceof PsiType;

  private ExpressionFilterConstants() {}
}
