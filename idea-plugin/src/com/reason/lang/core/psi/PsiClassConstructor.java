package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiClassConstructor extends ASTWrapperPsiElement {

  public PsiClassConstructor(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean canNavigate() {
    return false;
  }
}
