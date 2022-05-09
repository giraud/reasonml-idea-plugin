package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.ORCompositeTypePsiElement;
import com.reason.lang.core.type.ORTypes;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PsiDeconstruction extends ORCompositeTypePsiElement<ORTypes> {

  protected PsiDeconstruction(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  public @NotNull List<PsiElement> getDeconstructedElements() {
    List<PsiElement> result = new ArrayList<>();
    for (PsiElement child : getChildren()) {
      IElementType elementType = child.getNode().getElementType();
      if (elementType != m_types.LPAREN
          && elementType != m_types.COMMA
          && elementType != m_types.RPAREN
          && elementType != m_types.UNDERSCORE
          && !(child instanceof PsiWhiteSpace)) {
        result.add(child);
      }
    }
    return result;
  }
}
