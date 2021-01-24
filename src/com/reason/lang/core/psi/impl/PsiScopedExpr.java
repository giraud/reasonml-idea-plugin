package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiScopedExpr extends CompositeTypePsiElement<ORTypes> {

  protected PsiScopedExpr(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  public boolean isEmpty() {
    PsiElement firstChild = getFirstChild();
    IElementType firstType = firstChild == null ? null : firstChild.getNode().getElementType();
    if (firstType == m_types.LPAREN) {
      assert firstChild != null;
      PsiElement secondChild = firstChild.getNextSibling();
      IElementType secondType = secondChild == null ? null : secondChild.getNode().getElementType();
      return secondType == m_types.RPAREN;
    }

    return false;
  }

  @NotNull
  @Override
  public String toString() {
    return "Scoped expression";
  }
}
