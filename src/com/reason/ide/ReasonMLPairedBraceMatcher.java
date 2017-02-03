package com.reason.ide;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.reason.psi.ReasonMLTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReasonMLPairedBraceMatcher implements PairedBraceMatcher {
    private static BracePair[] PAIRS = new BracePair[]{
            new BracePair(ReasonMLTypes.LBRACE, ReasonMLTypes.RBRACE, true),
            new BracePair(ReasonMLTypes.LPAREN, ReasonMLTypes.RPAREN, true),
            new BracePair(ReasonMLTypes.LBRACKET, ReasonMLTypes.RBRACKET, false),
    };

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
