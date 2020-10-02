package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiFunctorBinding extends PsiToken<ORTypes> {

  public PsiFunctorBinding(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @NotNull
  @Override
  public String toString() {
    return "Functor binding";
  }
}
