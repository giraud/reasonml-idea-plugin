package com.reason.lang.dune;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class DuneParser extends CommonParser<DuneTypes> {
  DuneParser() {
    super(true, DuneTypes.INSTANCE);
  }

  @Override
  protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
    IElementType tokenType;

    while (true) {
      tokenType = builder.getTokenType();
      if (tokenType == null) {
        break;
      }

      if (tokenType == myTypes.ATOM) {
        parseAtom(state);
      }
      // ( ... )
      else if (tokenType == myTypes.LPAREN) {
        parseLParen(state);
      } else if (tokenType == myTypes.RPAREN) {
        parseRParen(state);
      }
      // %{ ... }
      else if (tokenType == myTypes.VAR_START) {
        parseVarStart(state);
      } else if (tokenType == myTypes.VAR_END) {
        parseVarEnd(state);
      }

      if (state.dontMove) {
        state.dontMove = false;
      } else {
        builder.advanceLexer();
      }
    }
  }

  private void parseAtom(@NotNull ParserState state) {
    if (state.is(myTypes.C_STANZA)) {
      state.advance().mark(myTypes.C_FIELDS);
    }
  }

  private void parseLParen(@NotNull ParserState state) {
    if (state.isRoot()) {
      state.markScope(myTypes.C_STANZA, myTypes.LPAREN);
    } else if (state.is(myTypes.C_FIELDS)) {
      state.markScope(myTypes.C_FIELD, myTypes.LPAREN);
    } else {
      state.markScope(myTypes.C_SEXPR, myTypes.LPAREN);
    }
  }

  private void parseRParen(@NotNull ParserState state) {
    if (state.is(myTypes.C_FIELDS)) {
      state.popEnd();
    }

    if (state.hasScopeToken()) {
      state.advance().popEnd();
    } else {
      state.error("Unbalanced parenthesis");
    }
  }

  private void parseVarStart(@NotNull ParserState state) {
    // |>%{<| ... }
    state.markScope(myTypes.C_VAR, myTypes.VAR_START);
  }

  private void parseVarEnd(@NotNull ParserState state) {
    if (state.is(myTypes.C_VAR)) {
      // %{ ... |>}<|
      state.advance().popEnd();
    }
  }
}
