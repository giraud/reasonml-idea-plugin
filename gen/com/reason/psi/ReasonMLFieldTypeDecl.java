// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLFieldTypeDecl extends PsiElement {

  @NotNull
  ReasonMLFieldName getFieldName();

  @NotNull
  List<ReasonMLFieldTypeDecl> getFieldTypeDeclList();

  @Nullable
  ReasonMLTypeConstr getTypeConstr();

  @Nullable
  ReasonMLValuePath getValuePath();

}
