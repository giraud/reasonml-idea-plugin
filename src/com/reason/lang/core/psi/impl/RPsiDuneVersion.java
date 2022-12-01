package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

public class RPsiDuneVersion extends RPsiToken<DuneTypes> {
  public RPsiDuneVersion(@NotNull DuneTypes types, @NotNull ASTNode node) {
    super(types, node);
  }
}
