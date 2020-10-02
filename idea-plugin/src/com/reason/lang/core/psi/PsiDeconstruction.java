package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PsiDeconstruction extends PsiToken<ORTypes> {

  public PsiDeconstruction(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  public List<PsiElement> getDeconstructedElements() {
    List<PsiElement> result = new ArrayList<>();
    for (PsiElement child : getChildren()) {
      IElementType elementType = child.getNode().getElementType();
      if (elementType != m_types.LPAREN
          && elementType != m_types.COMMA
          && elementType != m_types.RPAREN) {
        result.add(child);
      }
    }
    return result;
  }
}
