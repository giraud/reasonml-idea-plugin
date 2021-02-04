package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.type.ORTypes;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTry extends CompositeTypePsiElement<ORTypes> {

  protected PsiTry(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Nullable
  public PsiElement getBody() {
    return ORUtil.findImmediateFirstChildOfType(this, (IElementType) m_types.C_TRY_BODY);
  }

  @Nullable
  public List<PsiElement> getHandlers() {
    PsiElement scopedElement =
        ORUtil.findImmediateFirstChildOfType(this, (IElementType) m_types.C_TRY_HANDLERS);
    return ORUtil.findImmediateChildrenOfType(scopedElement, (IElementType) m_types.C_TRY_HANDLER);
  }

  @NotNull
  @Override
  public String toString() {
    return "Try";
  }
}
