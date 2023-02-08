package com.reason.lang.doc.reason;

import com.intellij.psi.tree.*;

public interface RmlDocTypes {
    IElementType ATOM = new IElementType("ATOM", RmlDocLanguage.INSTANCE);
    IElementType NEW_LINE = new IElementType("NEW_LINE", RmlDocLanguage.INSTANCE);
    IElementType COMMENT_START = new IElementType("COMMENT_START", RmlDocLanguage.INSTANCE);
    IElementType COMMENT_END = new IElementType("COMMENT_END", RmlDocLanguage.INSTANCE);
    IElementType TAG = new IElementType("TAG", RmlDocLanguage.INSTANCE);
}
