// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLExpr extends PsiElement {

  @Nullable
  ReasonMLBooleanExpr getBooleanExpr();

  @Nullable
  ReasonMLExpr getExpr();

  @Nullable
  ReasonMLFunDecl getFunDecl();

  @Nullable
  ReasonMLJsx getJsx();

  @Nullable
  ReasonMLLetBinding getLetBinding();

  @Nullable
  ReasonMLPatternMatching getPatternMatching();

  @Nullable
  ReasonMLValueExpr getValueExpr();

  @Nullable
  ReasonMLValueName getValueName();

}
