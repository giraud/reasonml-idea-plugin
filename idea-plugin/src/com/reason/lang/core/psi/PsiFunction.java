package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiFunction extends PsiElement {
  @NotNull
  Collection<PsiParameter> getParameters();

  @Nullable
  PsiFunctionBody getBody();
}
