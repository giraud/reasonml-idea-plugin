// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLExpr extends PsiElement {

  @Nullable
  ReasonMLConstant getConstant();

  @Nullable
  ReasonMLExpr getExpr();

  @NotNull
  List<ReasonMLFieldDecl> getFieldDeclList();

  @Nullable
  ReasonMLFunDecl getFunDecl();

  @Nullable
  ReasonMLJsx getJsx();

  @Nullable
  ReasonMLLetBinding getLetBinding();

  @NotNull
  List<ReasonMLValueExpr> getValueExprList();

}
