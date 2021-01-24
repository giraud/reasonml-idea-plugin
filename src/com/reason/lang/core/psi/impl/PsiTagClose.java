package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PsiTagClose extends CompositePsiElement {

  protected PsiTagClose(IElementType type) {
    super(type);
  }

  @NotNull
  @Override
  public String toString() {
    return "Tag close";
  }
}
