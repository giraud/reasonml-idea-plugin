package com.reason.lang.dune;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class DuneParser extends CommonParser<DuneTypes> {
  DuneParser() {
    super(DuneTypes.INSTANCE);
  }

  @Override
  protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
    IElementType tokenType = null;

    // long parseStart = System.currentTimeMillis();

    while (true) {
      // long parseTime = System.currentTimeMillis();
      // if (5 < parseTime - parseStart) {
      // Protection: abort the parsing if too much time spent
      // break;
      // }

      state.previousElementType1 = tokenType;
      tokenType = builder.getTokenType();
      if (tokenType == null) {
        break;
      }

      if (tokenType == m_types.ATOM) {
        parseAtom(state);
      }
      // ( ... )
      else if (tokenType == m_types.LPAREN) {
        parseLParen(state);
      } else if (tokenType == m_types.RPAREN) {
        parseRParen(state);
      }
      // %{ ... }
      else if (tokenType == m_types.VAR_START) {
        parseVarStart(state);
      } else if (tokenType == m_types.VAR_END) {
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
    if (state.is(m_types.C_STANZA)) {
      state.advance().mark(m_types.C_FIELDS);
    }
  }

  private void parseLParen(@NotNull ParserState state) {
    if (state.isRoot()) {
      state.markScope(m_types.C_STANZA, m_types.LPAREN);
    } else if (state.is(m_types.C_FIELDS)) {
      state.markScope(m_types.C_FIELD, m_types.LPAREN);
    } else {
      state.markScope(m_types.C_SEXPR, m_types.LPAREN);
    }
  }

  private void parseRParen(@NotNull ParserState state) {
    if (state.is(m_types.C_FIELDS)) {
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
    state.markScope(m_types.C_VAR, m_types.VAR_START);
  }

  private void parseVarEnd(@NotNull ParserState state) {
    if (state.is(m_types.C_VAR)) {
      // %{ ... |>}<|
      state.advance().popEnd();
    }
  }
}
