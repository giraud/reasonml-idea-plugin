package com.reason.ide.handlers;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import com.reason.lang.ocaml.OclTypes;

public class OclQuoteHandler extends SimpleTokenSetQuoteHandler {

  public OclQuoteHandler() {
    super(OclTypes.INSTANCE.STRING_VALUE, OclTypes.INSTANCE.DOUBLE_QUOTE, TokenType.BAD_CHARACTER);
  }
}
