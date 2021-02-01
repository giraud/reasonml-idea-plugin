package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.reference.PsiLowerSymbolReference;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLowerSymbolImpl extends CompositeTypePsiElement<ORTypes> implements PsiLowerSymbol {

  // region Constructors
  protected PsiLowerSymbolImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
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
