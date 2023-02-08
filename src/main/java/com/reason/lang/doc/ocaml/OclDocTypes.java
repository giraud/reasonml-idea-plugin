package com.reason.lang.doc.ocaml;

import com.intellij.psi.tree.*;

public interface OclDocTypes {
    IElementType ATOM = new IElementType("ATOM", OclDocLanguage.INSTANCE);
    IElementType BOLD = new IElementType("BOLD", OclDocLanguage.INSTANCE);
    IElementType CODE = new IElementType("CODE", OclDocLanguage.INSTANCE);
    IElementType COLON = new IElementType("COLON", OclDocLanguage.INSTANCE);
    IElementType CROSS_REF = new IElementType("CROSS_REF", OclDocLanguage.INSTANCE);
    IElementType EMPHASIS = new IElementType("EMPHASIS", OclDocLanguage.INSTANCE);
    IElementType ITALIC = new IElementType("ITALIC", OclDocLanguage.INSTANCE);
    IElementType LINK_START = new IElementType("LINK_START", OclDocLanguage.INSTANCE);
    IElementType LIST_ITEM_START = new IElementType("LIST_ITEM_START", OclDocLanguage.INSTANCE);
    IElementType NEW_LINE = new IElementType("NEW_LINE", OclDocLanguage.INSTANCE);
    IElementType COMMENT_START = new IElementType("COMMENT_START", OclDocLanguage.INSTANCE);
    IElementType COMMENT_END = new IElementType("COMMENT_END", OclDocLanguage.INSTANCE);
    IElementType O_LIST = new IElementType("O_LIST", OclDocLanguage.INSTANCE);
    IElementType PRE = new IElementType("PRE", OclDocLanguage.INSTANCE);
    IElementType RBRACE = new IElementType("RBRACE", OclDocLanguage.INSTANCE);
    IElementType SECTION = new IElementType("SECTION", OclDocLanguage.INSTANCE);
    IElementType TAG = new IElementType("TAG", OclDocLanguage.INSTANCE);
    IElementType U_LIST = new IElementType("U_LIST", OclDocLanguage.INSTANCE);
}
