package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class RPsiToken<T> extends ASTWrapperPsiElement {
  @NotNull protected final T m_types;

  public RPsiToken(@NotNull T types, @NotNull ASTNode node) {
    super(node);
    m_types = types;
  }
}
