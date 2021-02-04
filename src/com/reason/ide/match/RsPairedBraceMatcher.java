package com.reason.ide.match;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.napkin.*;
import org.jetbrains.annotations.*;

public class RsPairedBraceMatcher implements PairedBraceMatcher {
  private static final @NotNull BracePair[] PAIRS =
      new BracePair[]{ //
          new BracePair(NsTypes.INSTANCE.LBRACE, NsTypes.INSTANCE.RBRACE, true), //
          new BracePair(NsTypes.INSTANCE.LPAREN, NsTypes.INSTANCE.RPAREN, true),
          new BracePair(NsTypes.INSTANCE.ML_STRING_OPEN, NsTypes.INSTANCE.ML_STRING_CLOSE, true),
          new BracePair(NsTypes.INSTANCE.LBRACKET, NsTypes.INSTANCE.RBRACKET, true),
          new BracePair(NsTypes.INSTANCE.LARRAY, NsTypes.INSTANCE.RARRAY, true), //
      };

  @Override
  public @NotNull BracePair @NotNull [] getPairs() {
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
