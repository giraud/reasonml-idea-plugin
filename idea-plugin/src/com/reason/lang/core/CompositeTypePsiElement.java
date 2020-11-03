package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CompositeTypePsiElement<T> extends CompositePsiElement {
  @NotNull protected final T m_types;

  protected CompositeTypePsiElement(@NotNull T types, @NotNull IElementType elementType) {
    super(elementType);
    m_types = types;
  }

  protected @Nullable <T extends PsiElement> T findChildByClass(@NotNull Class<T> clazz) {
    return ORUtil.findImmediateFirstChildOfClass(this, clazz);
  }
}
