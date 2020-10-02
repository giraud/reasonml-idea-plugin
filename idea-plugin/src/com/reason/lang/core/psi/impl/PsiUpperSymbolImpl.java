package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.reference.PsiUpperSymbolReference;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiUpperSymbolImpl extends PsiToken<ORTypes> implements PsiUpperSymbol {

  // region Constructors
  public PsiUpperSymbolImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }
  // endregion

  @Override
  public PsiReference getReference() {
    return new PsiUpperSymbolReference(this, m_types);
  }

  @NotNull
  @Override
  public String toString() {
    String name = getText();
    return "USymbol "
        + (name == null || name.isEmpty()
            ? "<" + ((FileBase) getContainingFile()).getModuleName() + ">"
            : name);
  }
}
