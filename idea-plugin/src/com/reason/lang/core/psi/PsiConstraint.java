package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiConstraint extends PsiToken<ORTypes> {

  public PsiConstraint(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @NotNull
  @Override
  public String toString() {
    return "Constraint";
  }
}
