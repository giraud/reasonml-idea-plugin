package com.reason.ide.match;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DunePairedBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] PAIRS =
      new BracePair[] {
        new BracePair(DuneTypes.INSTANCE.LPAREN, DuneTypes.INSTANCE.RPAREN, true),
      };

  @NotNull
  @Override
  public BracePair @NotNull [] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(
      @NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return true;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
