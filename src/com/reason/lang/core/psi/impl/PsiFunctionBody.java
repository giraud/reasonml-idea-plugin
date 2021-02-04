package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class PsiFunctionBody extends CompositePsiElement {

  protected PsiFunctionBody(IElementType type) {
    super(type);
  }

  @NotNull
  @Override
  public String toString() {
    return "Function body";
  }
}
