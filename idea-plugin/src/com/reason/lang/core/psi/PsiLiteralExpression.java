package com.reason.lang.core.psi;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.impl.source.tree.injected.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class PsiLiteralExpression extends LeafPsiElement implements /*PsiLiteralValue,*/ PsiLanguageInjectionHost {
  public PsiLiteralExpression(@NotNull IElementType type, CharSequence text) {
    super(type, text);
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public PsiLiteralExpression updateText(@NotNull String text) {
    ASTNode valueNode = getNode().getFirstChildNode();
    assert valueNode instanceof LeafElement;
    ((LeafElement) valueNode).replaceWithText(text);
    return this;
  }

  @Override
  public @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new StringLiteralEscaper<>(this);
  }

  @Override
  public String toString() {
    return "PsiLiteralExpression:" + getText();
  }

}
