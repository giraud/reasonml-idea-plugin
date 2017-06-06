// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLLetBinding extends PsiElement {

  @NotNull
  List<ReasonMLJsx> getJsxList();

  @NotNull
  List<ReasonMLLabelName> getLabelNameList();

  @NotNull
  List<ReasonMLLetBinding> getLetBindingList();

  @NotNull
  ReasonMLLetName getLetName();

  @NotNull
  List<ReasonMLModulePath> getModulePathList();

  @NotNull
  List<ReasonMLParameter> getParameterList();

  @NotNull
  List<ReasonMLPatternMatching> getPatternMatchingList();

  @Nullable
  ReasonMLTypeExpr getTypeExpr();

  @NotNull
  List<ReasonMLUntypedObject> getUntypedObjectList();

  @NotNull
  List<ReasonMLValueExpr> getValueExprList();

  @NotNull
  List<ReasonMLValueName> getValueNameList();

  boolean isFunction();

}
