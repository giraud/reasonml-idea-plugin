package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiClass extends PsiQualifiedElement, NavigatablePsiElement, PsiStructuredElement {
  @Nullable
  PsiElement getClassBody();

  @NotNull
  Collection<PsiClassField> getFields();

  @NotNull
  Collection<PsiClassMethod> getMethods();

  @NotNull
  Collection<PsiClassParameters> getParameters();

  @Nullable
  PsiClassConstructor getConstructor();
}
