package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PsiPropertyName extends LeafPsiElement {
  public PsiPropertyName(@NotNull IElementType type, CharSequence text) {
    super(type, text);
  }

  @Override
  public String toString() {
    return "PsiPropertyName:" + getText();
  }
}
