package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiRaw extends ASTWrapperPsiElement {

  public PsiRaw(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public String toString() {
    return "Raw";
  }
}
