// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface ReasonMLExternalStatement extends PsiElement {

  @NotNull
  List<ReasonMLBsDirective> getBsDirectiveList();

  @NotNull
  ReasonMLExternalAlias getExternalAlias();

  @NotNull
  ReasonMLTypeExpr getTypeExpr();

  @NotNull
  ReasonMLValueName getValueName();

  ItemPresentation getPresentation();

}
