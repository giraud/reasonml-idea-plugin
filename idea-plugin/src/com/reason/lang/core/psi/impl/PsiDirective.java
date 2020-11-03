package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;

public class PsiDirective extends CompositePsiElement {

  protected PsiDirective(IElementType type) {
    super(type);
  }
}
