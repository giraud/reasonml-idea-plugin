package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;

public class PsiMacro extends CompositePsiElement {

  protected PsiMacro(IElementType type) {
    super(type);
  }
}
