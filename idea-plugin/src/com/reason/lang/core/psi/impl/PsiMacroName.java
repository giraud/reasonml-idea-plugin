package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;

public class PsiMacroName extends CompositePsiElement {

  protected PsiMacroName(IElementType type) {
    super(type);
  }

  @Override
  public String toString() {
    return getText();
  }
}
