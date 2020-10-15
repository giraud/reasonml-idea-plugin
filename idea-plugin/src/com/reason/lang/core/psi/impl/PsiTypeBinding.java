package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PsiTypeBinding extends CompositePsiElement {

  protected PsiTypeBinding(IElementType type) {
    super(type);
  }

  @NotNull
  @Override
  public String toString() {
    return "Type binding";
  }
}
