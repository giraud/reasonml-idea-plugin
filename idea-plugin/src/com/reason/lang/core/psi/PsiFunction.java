package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiFunction extends PsiElement {
  @NotNull
  List<PsiParameter> getParameters();

  @Nullable
  PsiFunctionBody getBody();
}
