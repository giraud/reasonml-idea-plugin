package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTagPropertyImpl extends PsiToken<ORTypes> implements PsiTagProperty {

  // region Constructors
  public PsiTagPropertyImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }
  // endregion

  @Nullable
  private PsiElement getNameElement() {
    return getFirstChild();
  }

  @NotNull
  @Override
  public String getName() {
    PsiElement nameElement = getNameElement();
    return nameElement == null ? "" : nameElement.getText();
  }

  @Nullable
  @Override
  public PsiElement getValue() {
    PsiElement eq = ORUtil.nextSiblingWithTokenType(getFirstChild(), m_types.EQ);
    return eq == null ? null : eq.getNextSibling();
  }

  @NotNull
  @Override
  public String toString() {
    return "TagProperty " + getName();
  }
}
