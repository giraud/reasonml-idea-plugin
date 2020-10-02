package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiModulePath extends ASTWrapperPsiElement {
  public PsiModulePath(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public String toString() {
    return "ModulePath";
  }
}
