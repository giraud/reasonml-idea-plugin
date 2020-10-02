package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiTagBody extends ASTWrapperPsiElement {
  public PsiTagBody(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public String toString() {
    return "Tag body";
  }
}
