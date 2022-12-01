package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class RPsiDuneFields extends RPsiToken<DuneTypes> {
  public RPsiDuneFields(@NotNull DuneTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @Override
  public @NotNull String toString() {
    return "Fields";
  }
}
