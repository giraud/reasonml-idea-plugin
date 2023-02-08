package com.reason.lang.core;

import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.Nullable;

public class ExpressionFilterConstants {

  public static final @Nullable ExpressionFilter NO_FILTER = null;
  public static final ExpressionFilter FILTER_LET = element -> element instanceof RPsiLet;
  public static final ExpressionFilter FILTER_LET_OR_EXTERNAL = element -> element instanceof RPsiLet || element instanceof RPsiExternal;
  public static final ExpressionFilter FILTER_TYPE = element -> element instanceof RPsiType;

  private ExpressionFilterConstants() {}
}
