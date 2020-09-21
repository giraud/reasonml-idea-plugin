package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.reference.PsiLowerSymbolReference;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLowerSymbolImpl extends PsiToken<ORTypes> implements PsiLowerSymbol {

  // region Constructors
  public PsiLowerSymbolImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }
  // endregion

  @Override
  public PsiReference getReference() {
    return new PsiLowerSymbolReference(this, m_types);
  }

  @Nullable
  @Override
  public String toString() {
    return "LSymbol";
  }
}
