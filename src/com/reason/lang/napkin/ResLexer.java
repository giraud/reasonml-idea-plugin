package com.reason.lang.napkin;

import com.intellij.lexer.FlexAdapter;

public class ResLexer extends FlexAdapter {
  public ResLexer() {
    super(new ResFlexLexer(NsTypes.INSTANCE));
  }
}
