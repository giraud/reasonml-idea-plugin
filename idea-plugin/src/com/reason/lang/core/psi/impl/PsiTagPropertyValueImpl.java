package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.psi.PsiTagPropertyValue;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiTagPropertyValueImpl extends CompositeTypePsiElement<ORTypes>
    implements PsiTagPropertyValue {

  // region Constructors
  protected PsiTagPropertyValueImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }
  // endregion

  @NotNull
  @Override
  public String toString() {
    return "TagPropertyValue";
  }
}
