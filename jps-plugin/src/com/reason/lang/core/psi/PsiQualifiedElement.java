package com.reason.lang.core.psi;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

public interface PsiQualifiedElement extends PsiNamedElement {
  @NotNull
  String getPath();

  @NotNull
  String getQualifiedName();
}
