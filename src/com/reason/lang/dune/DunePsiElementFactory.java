package com.reason.lang.dune;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.NotNull;

class DunePsiElementFactory {
  private DunePsiElementFactory() {}

  @NotNull
  static PsiElement createElement(@NotNull ASTNode node) {
    IElementType type = node.getElementType();

    if (type == DuneTypes.INSTANCE.C_FIELD) {
      return new RPsiDuneField(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_FIELDS) {
      return new RPsiDuneFields(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_STANZA) {
      return new RPsiStanza(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_SEXPR) {
      return new RPsiSExpr(DuneTypes.INSTANCE, node);
    } else if (type == DuneTypes.INSTANCE.C_VAR) {
      return new RPsiDuneVar(DuneTypes.INSTANCE, node);
    }

    return new RPsiToken<>(DuneTypes.INSTANCE, node);
  }
}
