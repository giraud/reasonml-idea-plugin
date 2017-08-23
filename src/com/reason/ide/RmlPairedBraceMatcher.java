package com.reason.ide;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlPairedBraceMatcher implements PairedBraceMatcher {
    private static BracePair[] PAIRS = new BracePair[]{
            new BracePair(RmlTypes.LBRACE, RmlTypes.RBRACE, true),
            new BracePair(RmlTypes.LPAREN, RmlTypes.RPAREN, true),
            new BracePair(RmlTypes.LBRACKET, RmlTypes.RBRACKET, false),
            new BracePair(RmlTypes.LARRAY, RmlTypes.RARRAY, false),
    };

    @NotNull
    @Override
    public BracePair[] getPairs() {
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
