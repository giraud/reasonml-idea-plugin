package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;

public class RPsiMixinField extends CompositePsiElement {

  protected RPsiMixinField(IElementType type) {
    super(type);
  }

  @Override
  public boolean canNavigate() {
    return false;
  }
}
