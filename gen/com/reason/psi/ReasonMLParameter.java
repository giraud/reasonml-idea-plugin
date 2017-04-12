// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLParameter extends PsiElement {

  @Nullable
  ReasonMLConstant getConstant();

  @NotNull
  List<ReasonMLField> getFieldList();

  @Nullable
  ReasonMLLabelName getLabelName();

  @Nullable
  ReasonMLParameterExpr getParameterExpr();

  @Nullable
  ReasonMLTypeExpr getTypeExpr();

  @Nullable
  ReasonMLValueName getValueName();

}
