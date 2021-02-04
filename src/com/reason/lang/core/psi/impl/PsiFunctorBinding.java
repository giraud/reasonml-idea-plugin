package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiFunctorBinding extends CompositeTypePsiElement<ORTypes> {

  protected PsiFunctorBinding(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @NotNull
  @Override
  public String toString() {
    return "Functor binding";
  }
}
