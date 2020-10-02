package com.reason.ide.handlers;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import com.reason.lang.napkin.NsTypes;

public class NsQuoteHandler extends SimpleTokenSetQuoteHandler {

  public NsQuoteHandler() {
    super(NsTypes.INSTANCE.STRING_VALUE, NsTypes.INSTANCE.DOUBLE_QUOTE, TokenType.BAD_CHARACTER);
  }
}
