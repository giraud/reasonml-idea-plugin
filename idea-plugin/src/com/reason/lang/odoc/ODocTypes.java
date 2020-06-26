package com.reason.lang.odoc;

import com.intellij.psi.tree.IElementType;

public interface ODocTypes {

  IElementType OCL_START = new ODocTokenType("OCL_START");
  IElementType OCL_END = new ODocTokenType("OCL_END");
  IElementType RML_START = new ODocTokenType("RML_START");
  IElementType RML_END = new ODocTokenType("RML_END");
  IElementType NEW_LINE = new ODocTokenType("NEW_LINE");
  IElementType ATOM = new ODocTokenType("ATOM");
  IElementType CODE = new ODocTokenType("CODE");
  IElementType BOLD = new ODocTokenType("BOLD");
  IElementType ITALIC = new ODocTokenType("ITALIC");
  IElementType EMPHASIS = new ODocTokenType("EMPHASIS");
  IElementType CROSS_REF = new ODocTokenType("CROSS_REF");
  IElementType LINK = new ODocTokenType("LINK");
  IElementType O_LIST = new ODocTokenType("O_LIST");
  IElementType U_LIST = new ODocTokenType("U_LIST");
  IElementType LIST_ITEM = new ODocTokenType("LIST_ITEM");
  IElementType SECTION = new ODocTokenType("SECTION");
  IElementType PRE_START = new ODocTokenType("PRE_START");
  IElementType PRE_END = new ODocTokenType("PRE_END");
  IElementType RBRACE = new ODocTokenType("RBRACE");
  IElementType COLON = new ODocTokenType("COLON");

}
