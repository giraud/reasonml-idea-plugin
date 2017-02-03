// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLFieldExpr extends PsiElement {

  @Nullable
  ReasonMLConstant getConstant();

  @NotNull
  List<ReasonMLFieldExpr> getFieldExprList();

  @Nullable
  ReasonMLValueName getValueName();

  @Nullable
  ReasonMLValuePath getValuePath();

}
