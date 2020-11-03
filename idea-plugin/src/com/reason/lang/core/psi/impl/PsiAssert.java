package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiAssert extends CompositePsiElement {

  protected PsiAssert(IElementType type) {
    super(type);
  }

  @NotNull
  @Override
  public String toString() {
    return "Assert";
  }

  public @Nullable PsiElement getAssertion() {
    return PsiTreeUtil.skipWhitespacesForward(getFirstChild());
  }
}
