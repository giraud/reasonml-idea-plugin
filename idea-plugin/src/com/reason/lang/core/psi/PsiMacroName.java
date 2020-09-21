package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiMacroName extends ASTWrapperPsiElement {

  public PsiMacroName(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return getText();
  }
}
