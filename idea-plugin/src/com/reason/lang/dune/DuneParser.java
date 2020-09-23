package com.reason.lang.dune;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserState;
import org.jetbrains.annotations.NotNull;

import static com.reason.lang.ParserScopeEnum.*;

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
    if (state.isCurrentResolution(stanza)) {
      state.advance().mark(m_types.C_FIELDS).resolution(stanzaNamedFields);
    }
  }

  private void parseLParen(@NotNull ParserState state) {
    if (state.isCurrentResolution(file)) {
      state.markScope(m_types.C_STANZA, m_types.LPAREN).resolution(stanza);
    } else if (state.isCurrentResolution(stanzaNamedFields)) {
      state.markScope(m_types.C_FIELD, m_types.LPAREN).resolution(field);
    } else {
      state.markScope(m_types.C_SEXPR, m_types.LPAREN).resolution(sexpr);
    }
  }

  private void parseRParen(ParserState state) {
    if (state.isCurrentResolution(stanzaNamedFields)) {
      state.popEnd();
    }

    if (state.isInScopeExpression()) {
      state.advance().popEnd();
    } else {
      state.error("Unbalanced parenthesis");
    }
  }

  private void parseVarStart(@NotNull ParserState state) {
    // |>%{<| ... }
    state.markScope(m_types.C_VAR, m_types.VAR_START).resolution(duneVariable);
  }

  private void parseVarEnd(@NotNull ParserState state) {
    if (state.isCurrentResolution(duneVariable)) {
      // %{ ... |>}<|
      state.advance().popEnd();
    }
  }
}
