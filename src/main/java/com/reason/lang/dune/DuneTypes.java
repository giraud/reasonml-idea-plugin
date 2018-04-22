package com.reason.lang.dune;

import com.intellij.psi.tree.IElementType;

public class DuneTypes {

    public static IElementType VERSION = new DuneElementType("version");

    public static IElementType LPAREN = new DuneElementType("LPAREN");
    public static IElementType RPAREN = new DuneElementType("RPAREN");
    public static IElementType STRING = new DuneElementType("String");
}
