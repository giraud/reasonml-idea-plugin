package com.reason.lang.rescript;

import com.intellij.lexer.FlexAdapter;

public class ResLexer extends FlexAdapter {
  public ResLexer() {
    super(new ResFlexLexer(ResTypes.INSTANCE));
  }
}
