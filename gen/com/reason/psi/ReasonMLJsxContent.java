// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLJsxContent extends PsiElement {

  @Nullable
  ReasonMLJsx getJsx();

  @NotNull
  List<ReasonMLLabelName> getLabelNameList();

  @NotNull
  List<ReasonMLLetBinding> getLetBindingList();

  @NotNull
  List<ReasonMLModulePath> getModulePathList();

  @NotNull
  List<ReasonMLParameter> getParameterList();

  @NotNull
  List<ReasonMLPatternMatching> getPatternMatchingList();

  @NotNull
  List<ReasonMLUntypedObject> getUntypedObjectList();

  @NotNull
  List<ReasonMLValueExpr> getValueExprList();

  @NotNull
  List<ReasonMLValueName> getValueNameList();

}
