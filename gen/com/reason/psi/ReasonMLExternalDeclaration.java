// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLExternalDeclaration extends PsiElement {

  @NotNull
  List<ReasonMLBsDirective> getBsDirectiveList();

  @NotNull
  ReasonMLExternalAlias getExternalAlias();

  @Nullable
  ReasonMLValueName getValueName();

}
