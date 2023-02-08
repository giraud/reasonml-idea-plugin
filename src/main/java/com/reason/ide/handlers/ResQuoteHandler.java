package com.reason.ide.handlers;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.TokenType;
import com.reason.lang.rescript.ResTypes;

public class ResQuoteHandler extends SimpleTokenSetQuoteHandler {
    public ResQuoteHandler() {
        super(ResTypes.INSTANCE.STRING_VALUE, ResTypes.INSTANCE.DOUBLE_QUOTE, TokenType.BAD_CHARACTER);
    }
}
