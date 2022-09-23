package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class RPsiSExpr extends RPsiToken<DuneTypes> {

  public RPsiSExpr(@NotNull DuneTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @NotNull
  @Override
  public String toString() {
    return "(";
  }
}
