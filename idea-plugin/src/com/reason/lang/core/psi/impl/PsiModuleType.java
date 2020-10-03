package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiModuleType extends PsiToken<ORTypes> {
  public PsiModuleType(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @Override
  public @NotNull String toString() {
    return "PsiModuleType";
  }
}
