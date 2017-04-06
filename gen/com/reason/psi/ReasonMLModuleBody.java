// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLModuleBody extends PsiElement {

  @NotNull
  List<ReasonMLExternalStatement> getExternalStatementList();

  @NotNull
  List<ReasonMLIncludeStatement> getIncludeStatementList();

  @NotNull
  List<ReasonMLLetStatement> getLetStatementList();

  @NotNull
  List<ReasonMLModuleStatement> getModuleStatementList();

  @NotNull
  List<ReasonMLTypeStatement> getTypeStatementList();

}
