package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class PsiDuneVar extends PsiToken<DuneTypes> {
  public PsiDuneVar(@NotNull DuneTypes types, @NotNull ASTNode node) {
    super(types, node);
  }
}
