package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RPsiLetAttribute extends CompositePsiElement {

  protected RPsiLetAttribute(IElementType type) {
    super(type);
  }

  @Nullable
  String getValue() {
    PsiElement nextSibling = getFirstChild().getNextSibling();
    return nextSibling == null ? null : nextSibling.getText();
  }

  @NotNull
  @Override
  public String toString() {
    return "Let attribute";
  }
}
