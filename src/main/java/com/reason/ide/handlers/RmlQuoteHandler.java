package com.reason.ide.handlers;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import com.reason.lang.reason.RmlTypes;

public class RmlQuoteHandler extends SimpleTokenSetQuoteHandler {

  public RmlQuoteHandler() {
    super(RmlTypes.INSTANCE.STRING_VALUE, RmlTypes.INSTANCE.DOUBLE_QUOTE, TokenType.BAD_CHARACTER);
  }
}
