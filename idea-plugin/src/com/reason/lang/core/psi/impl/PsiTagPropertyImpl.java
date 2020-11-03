package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTagPropertyImpl extends CompositeTypePsiElement<ORTypes> implements PsiTagProperty {

  // region Constructors
  protected PsiTagPropertyImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }
  // endregion

  @Nullable
  private PsiElement getNameElement() {
    return ORUtil.findImmediateFirstChildOfType(this, m_types.PROPERTY_NAME);
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
