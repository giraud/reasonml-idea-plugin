package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiDirective extends ASTWrapperPsiElement {

  public PsiDirective(@NotNull ASTNode node) {
    super(node);
  }
}
