// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLPattern extends PsiElement {

  @NotNull
  List<ReasonMLConstant> getConstantList();

  @Nullable
  ReasonMLModulePath getModulePath();

  @NotNull
  List<ReasonMLPattern> getPatternList();

  @NotNull
  List<ReasonMLValueName> getValueNameList();

  @Nullable
  ReasonMLValuePath getValuePath();

}
