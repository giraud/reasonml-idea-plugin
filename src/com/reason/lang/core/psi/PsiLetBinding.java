package com.reason.lang.core.psi;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PsiLetBinding extends CompositePsiElement {
  public PsiLetBinding(IElementType type) {
    super(type);
  }

  @NotNull
  @Override
  public String toString() {
    return "Let binding";
  }
}
