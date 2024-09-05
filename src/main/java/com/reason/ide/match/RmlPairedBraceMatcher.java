package com.reason.ide.match;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlPairedBraceMatcher implements PairedBraceMatcher {
    @NotNull
    private static final BracePair[] PAIRS =
            new BracePair[]{ //
                    new BracePair(RmlTypes.INSTANCE.LBRACE, RmlTypes.INSTANCE.RBRACE, true), //
                    new BracePair(RmlTypes.INSTANCE.LPAREN, RmlTypes.INSTANCE.RPAREN, true),
                    new BracePair(RmlTypes.INSTANCE.ML_STRING_OPEN, RmlTypes.INSTANCE.ML_STRING_CLOSE, true),
                    new BracePair(RmlTypes.INSTANCE.LBRACKET, RmlTypes.INSTANCE.RBRACKET, true),
                    new BracePair(RmlTypes.INSTANCE.LARRAY, RmlTypes.INSTANCE.RARRAY, true), //
            };

    @Override
    public @NotNull BracePair[] getPairs() {
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
