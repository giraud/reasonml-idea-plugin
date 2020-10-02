package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.lang.core.ORUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiPatternMatch extends ASTWrapperPsiElement {

  public PsiPatternMatch(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean canNavigate() {
    return false;
  }

  @NotNull
  @Override
  public String toString() {
    return "Pattern match";
  }

  @Nullable
  public PsiPatternMatchBody getBody() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiPatternMatchBody.class);
  }
}
