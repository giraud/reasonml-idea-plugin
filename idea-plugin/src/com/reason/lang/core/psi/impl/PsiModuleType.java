package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiModuleType extends CompositeTypePsiElement<ORTypes> {

  protected PsiModuleType(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  public @NotNull String toString() {
    return "PsiModuleType";
  }
}
