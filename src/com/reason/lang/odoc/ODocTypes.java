package com.reason.lang.odoc;

import com.intellij.psi.tree.IElementType;

public interface ODocTypes {

  IElementType START = new ODocTokenType("START");
  IElementType END = new ODocTokenType("END");
  IElementType NEW_LINE = new ODocTokenType("NEW_LINE");
  IElementType ATOM = new ODocTokenType("ATOM");
  IElementType CODE = new ODocTokenType("CODE");

}
