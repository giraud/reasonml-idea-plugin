package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.ORUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiPatternMatch extends CompositePsiElement {

  protected PsiPatternMatch(IElementType type) {
    super(type);
  }

  @Nullable
  public PsiPatternMatchBody getBody() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiPatternMatchBody.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "Pattern match";
  }
}
