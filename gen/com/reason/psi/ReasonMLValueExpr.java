// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLValueExpr extends PsiElement {

  @NotNull
  List<ReasonMLArgument> getArgumentList();

  @Nullable
  ReasonMLModulePath getModulePath();

  @Nullable
  ReasonMLSignedConstant getSignedConstant();

  @Nullable
  ReasonMLUntypedObject getUntypedObject();

  @NotNull
  List<ReasonMLValuePath> getValuePathList();

}
