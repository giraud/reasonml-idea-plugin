package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.json.psi.impl.JSStringLiteralEscaper;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiRawBody extends ASTWrapperPsiElement implements PsiLanguageInjectionHost {

  private final ORTypes m_types;

  public PsiRawBody(ORTypes types, @NotNull ASTNode node) {
    super(node);
    m_types = types;
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @NotNull
  @Override
  public PsiLanguageInjectionHost updateText(@NotNull String text) {
    ASTNode valueNode = getNode().getFirstChildNode();
    assert valueNode instanceof LeafElement;

    ((LeafElement) valueNode).replaceWithText(text);
    return this;
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new JSStringLiteralEscaper<PsiLanguageInjectionHost>(this) {
      @Override
      protected boolean isRegExpLiteral() {
        return false;
      }
    };
  }

  @NotNull
  public TextRange getMacroTextRange() {
    IElementType elementType = getNode().getFirstChildNode().getElementType();
    if (elementType == m_types.STRING_VALUE) {
      int max = getTextLength() - 1;
      if (1 < max) {
        return new TextRange(1, max);
      }
    } else {
      int max = getTextLength() - 2;
      if (2 < max) {
        return new TextRange(2, max);
      }
    }

    return getTextRange();
  }
}
