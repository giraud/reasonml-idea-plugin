package com.reason.ide.match;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

public class DunePairedBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS =
            new BracePair[]{
                    new BracePair(DuneTypes.INSTANCE.LPAREN, DuneTypes.INSTANCE.RPAREN, true),
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
