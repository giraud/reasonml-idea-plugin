package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.reference.PsiUpperSymbolReference;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiUpperSymbolImpl extends CompositeTypePsiElement<ORTypes> implements PsiUpperSymbol {

  // region Constructors
  protected PsiUpperSymbolImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
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
        + (name.isEmpty() ? "<" + ((FileBase) getContainingFile()).getModuleName() + ">" : name);
  }
}
