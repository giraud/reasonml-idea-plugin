package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.reference.PsiPropertyNameReference;
import org.jetbrains.annotations.NotNull;

public class PsiLeafPropertyName extends LeafPsiElement {
  public PsiLeafPropertyName(@NotNull IElementType type, CharSequence text) {
    super(type, text);
  }

  @Override
  public PsiReference getReference() {
    return new PsiPropertyNameReference(this);
  }

  @Override
  public String toString() {
    return "PropertyName:" + getText();
  }
}
