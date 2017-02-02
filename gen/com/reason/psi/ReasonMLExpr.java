// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLExpr extends PsiElement {

  @NotNull
  List<ReasonMLArgument> getArgumentList();

  @Nullable
  ReasonMLConstant getConstant();

  @Nullable
  ReasonMLExpr getExpr();

  @Nullable
  ReasonMLJsx getJsx();

  @Nullable
  ReasonMLRecordBody getRecordBody();

  @Nullable
  ReasonMLValuePath getValuePath();

}
