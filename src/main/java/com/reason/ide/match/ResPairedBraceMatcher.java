package com.reason.ide.match;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

public class ResPairedBraceMatcher implements PairedBraceMatcher {
    private static final @NotNull BracePair[] PAIRS =
            new BracePair[]{ //
                    new BracePair(ResTypes.INSTANCE.LBRACE, ResTypes.INSTANCE.RBRACE, true), //
                    new BracePair(ResTypes.INSTANCE.LPAREN, ResTypes.INSTANCE.RPAREN, true),
                    new BracePair(ResTypes.INSTANCE.ML_STRING_OPEN, ResTypes.INSTANCE.ML_STRING_CLOSE, true),
                    new BracePair(ResTypes.INSTANCE.LBRACKET, ResTypes.INSTANCE.RBRACKET, true),
                    new BracePair(ResTypes.INSTANCE.LARRAY, ResTypes.INSTANCE.RARRAY, true), //
            };

    @Override
    public BracePair @NotNull [] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
