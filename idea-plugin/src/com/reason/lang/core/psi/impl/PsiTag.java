package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.core.type.ORTypes;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class PsiTag extends CompositeTypePsiElement<ORTypes> {
  protected PsiTag(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  public String getName() {
    PsiTagStart tagStart = ORUtil.findImmediateFirstChildOfClass(this, PsiTagStart.class);
    String text = ORUtil.getTextUntilWhitespace(tagStart.getFirstChild().getNextSibling());
    return text;
  }

  @NotNull
  public Collection<PsiTagProperty> getProperties() {
    return ORUtil.findImmediateChildrenOfClass(getFirstChild() /*tag start*/, PsiTagProperty.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "Tag";
  }
}
