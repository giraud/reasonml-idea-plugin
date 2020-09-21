package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiIfStatement extends ASTWrapperPsiElement {

  public PsiIfStatement(@NotNull ASTNode node) {
    super(node);
  }

  @Nullable
  public PsiBinaryCondition getBinaryCondition() {
    return findChildByClass(PsiBinaryCondition.class);
  }
}
