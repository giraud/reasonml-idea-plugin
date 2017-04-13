// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLPatternMatching extends PsiElement {

  @NotNull
  List<ReasonMLJsx> getJsxList();

  @NotNull
  List<ReasonMLLabelName> getLabelNameList();

  @NotNull
  List<ReasonMLLetBinding> getLetBindingList();

  @NotNull
  List<ReasonMLParameter> getParameterList();

  @NotNull
  ReasonMLPattern getPattern();

  @NotNull
  List<ReasonMLPatternMatching> getPatternMatchingList();

  @NotNull
  List<ReasonMLValueExpr> getValueExprList();

  @NotNull
  List<ReasonMLValueName> getValueNameList();

}
