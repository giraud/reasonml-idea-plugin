package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNamedElement;
import com.reason.lang.core.ExpressionFilter;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Common interface to file-based modules and inner modules */
public interface PsiModule
    extends PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement {

  boolean isInterface();

  boolean isComponent();

  @Nullable
  String getAlias();

  @Nullable
  String getModuleName();

  @Nullable
  PsiFunctorCall getFunctorCall();

  @NotNull
  Collection<PsiNamedElement> getExpressions(
      @NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter);

  @NotNull
  Collection<PsiModule> getModules();

  @Nullable
  PsiModule getModuleExpression(@Nullable String name);

  @Nullable
  PsiType getTypeExpression(@Nullable String name);

  @Nullable
  PsiLet getLetExpression(@Nullable String name);

  @Nullable
  PsiVal getValExpression(@Nullable String name);
}
