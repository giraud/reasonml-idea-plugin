package com.reason.lang.dune;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiToken;
import org.jetbrains.annotations.NotNull;

class DunePsiElementFactory {
  private DunePsiElementFactory() {}

  @NotNull
  static PsiElement createElement(@NotNull ASTNode node) {
    IElementType type = node.getElementType();

    if (type == DuneTypes.INSTANCE.C_FIELD) {
      return new PsiDuneField(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_FIELDS) {
      return new PsiDuneFields(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_STANZA) {
      return new PsiStanza(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_SEXPR) {
      return new PsiSExpr(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_VAR) {
      return new PsiDuneVar(DuneTypes.INSTANCE, node);
    }

    return new PsiToken<>(DuneTypes.INSTANCE, node);
  }
}
