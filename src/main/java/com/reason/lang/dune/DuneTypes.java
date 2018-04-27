package com.reason.lang.dune;

import com.intellij.psi.tree.IElementType;

public class DuneTypes {

    public static IElementType LPAREN = new DuneElementType("LPAREN");
    public static IElementType RPAREN = new DuneElementType("RPAREN");
    public static IElementType STRING = new DuneElementType("String");

    public static final IElementType SEXPR = new DuneElementType("s-expr");

    public static IElementType VERSION = new DuneElementType("VERSION");
    public static final IElementType LIBRARY = new DuneElementType("LIBRARY");
    public static final IElementType NAME = new DuneElementType("NAME");
    public static final IElementType PUBLIC_NAME = new DuneElementType("PUBLIC_NAME");
    public static final IElementType SYNOPSIS = new DuneElementType("SYNOPSIS");
    public static final IElementType EXECUTABLE = new DuneElementType("EXECUTABLE");
}
