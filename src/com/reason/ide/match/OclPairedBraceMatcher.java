package com.reason.ide.match;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OclPairedBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] PAIRS =
      new BracePair[] { //
        new BracePair(OclTypes.INSTANCE.LBRACE, OclTypes.INSTANCE.RBRACE, false), //
        new BracePair(OclTypes.INSTANCE.BEGIN, OclTypes.INSTANCE.END, false), //
        new BracePair(OclTypes.INSTANCE.STRUCT, OclTypes.INSTANCE.END, false), //
        new BracePair(OclTypes.INSTANCE.SIG, OclTypes.INSTANCE.END, false), //
        new BracePair(OclTypes.INSTANCE.OBJECT, OclTypes.INSTANCE.END, false), //
        new BracePair(OclTypes.INSTANCE.LPAREN, OclTypes.INSTANCE.RPAREN, false),
        new BracePair(OclTypes.INSTANCE.LBRACKET, OclTypes.INSTANCE.RBRACKET, false),
        new BracePair(OclTypes.INSTANCE.LARRAY, OclTypes.INSTANCE.RARRAY, false), //
        new BracePair(OclTypes.INSTANCE.LT, OclTypes.INSTANCE.GT, false), //
        new BracePair(OclTypes.INSTANCE.DO, OclTypes.INSTANCE.DONE, false), //
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
