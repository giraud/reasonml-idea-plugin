package com.reason.lang.ocaml;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.ReasonMLLexer;

public class OclLexer extends FlexAdapter {
  public OclLexer() {
    super(new ReasonMLLexer(OclTypes.INSTANCE));
  }
}
