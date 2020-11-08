package com.reason.lang.napkin;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;
import static com.reason.lang.ParserScopeEnum.*;

public class NsParser extends CommonParser<NsTypes> {

  NsParser() {
    super(NsTypes.INSTANCE);
  }

  @Override
  protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
    IElementType tokenType = null;
    state.previousElementType1 = null;

    // long parseStart = System.currentTimeMillis();

    while (true) {
      // long parseTime = System.currentTimeMillis();
      // if (5 < parseTime - parseStart) {
      // Protection: abort the parsing if too much time spent
      // break;
      // }

      state.previousElementType2 = state.previousElementType1;
      state.previousElementType1 = tokenType;
      tokenType = builder.getTokenType();
      if (tokenType == null) {
        break;
      }

      // Special analyse when inside an interpolation string
      if (state.is(m_types.C_INTERPOLATION_EXPR)
              || state.is(m_types.C_INTERPOLATION_PART)
              || state.is(m_types.C_INTERPOLATION_REF)) {
        if (tokenType == m_types.ML_STRING_VALUE /*!*/) {
          state.popEndUntil(m_types.C_INTERPOLATION_EXPR).advance().popEnd();
        } else if (tokenType == m_types.DOLLAR && state.is(m_types.C_INTERPOLATION_PART)) {
          state.popEnd().advance();
          IElementType nextElement = state.getTokenType();
          if (nextElement == m_types.LBRACE) {
            state.advance().markScope(m_types.C_INTERPOLATION_REF, m_types.LBRACE);
          }
        } else if (state.is(m_types.C_INTERPOLATION_REF) && tokenType == m_types.RBRACE) {
          state.popEnd().advance().mark(m_types.C_INTERPOLATION_PART);
        }
      } else {
        if (tokenType == m_types.SEMI) {
          parseSemi(state);
        } else if (tokenType == m_types.EQ) {
          parseEq(state);
        } else if (tokenType == m_types.ARROW) {
          parseArrow(state);
        } else if (tokenType == m_types.REF) {
          parseRef(state);
        } else if (tokenType == m_types.OPTION) {
          parseOption(state);
        } else if (tokenType == m_types.SOME) {
          parseSome(state);
        } else if (tokenType == m_types.NONE) {
          parseNone(state);
        } else if (tokenType == m_types.TRY) {
          parseTry(state);
        } else if (tokenType == m_types.CATCH) {
          parseCatch(state);
        } else if (tokenType == m_types.SWITCH) {
          parseSwitch(state);
        } else if (tokenType == m_types.LIDENT) {
          parseLIdent(state);
        } else if (tokenType == m_types.UIDENT) {
          parseUIdent(state);
        } else if (tokenType == m_types.POLY_VARIANT) {
          parsePolyVariant(state);
        } else if (tokenType == m_types.ARROBASE) {
          parseArrobase(state);
        } else if (tokenType == m_types.PERCENT) {
          parsePercent(state);
        } else if (tokenType == m_types.COLON) {
          parseColon(state);
        } else if (tokenType == m_types.RAW) {
          parseRaw(state);
        } else if (tokenType == m_types.STRING_VALUE) {
          parseStringValue(state);
        } else if (tokenType == m_types.PIPE) {
          parsePipe(state);
        } else if (tokenType == m_types.COMMA) {
          parseComma(state);
        } else if (tokenType == m_types.AND) {
          parseAnd(state);
        } else if (tokenType == m_types.ASSERT) {
          parseAssert(state);
        } else if (tokenType == m_types.IF) {
          parseIf(state);
        } else if (tokenType == m_types.DOTDOTDOT) {
          parseDotDotDot(state);
        } else if (tokenType == m_types.WITH) {
          parseWith(state);
        } else if (tokenType == m_types.EQEQ) {
          parseEqEq(state);
        } else if (tokenType == m_types.QUESTION_MARK) {
          parseQuestionMark(state);
        } else if (tokenType == m_types.TILDE) {
          parseTilde(state);
        }
        // ( ... )
        else if (tokenType == m_types.LPAREN) {
          parseLParen(state);
        } else if (tokenType == m_types.RPAREN) {
          parseRParen(state);
        }
        // { ... }
        else if (tokenType == m_types.LBRACE) {
          parseLBrace(state);
        } else if (tokenType == m_types.RBRACE) {
          parseRBrace(state);
        }
        // [ ... ]
        //// [> ... ]
        else if (tokenType == m_types.LBRACKET) {
          parseLBracket(state);
        } else if (tokenType == m_types.BRACKET_GT) {
          parseBracketGt(state);
        } else if (tokenType == m_types.RBRACKET) {
          parseRBracket(state);
        }
        // < ... >
        else if (tokenType == m_types.LT) {
          parseLt(state);
        } else if (tokenType == m_types.TAG_LT_SLASH) {
          parseLtSlash(state);
        } else if (tokenType == m_types.GT) {
          parseGt(state);
        } else if (tokenType == m_types.TAG_AUTO_CLOSE) {
          parseGtAutoClose(state);
        }
        // j` ... `
        else if (tokenType == m_types.JS_STRING_OPEN) {
          parseTemplateStringOpen(state);
        }
        // Starts an expression
        else if (tokenType == m_types.OPEN) {
          parseOpen(state);
        } else if (tokenType == m_types.INCLUDE) {
          parseInclude(state);
        } else if (tokenType == m_types.EXTERNAL) {
          parseExternal(state);
        } else if (tokenType == m_types.TYPE) {
          parseType(state);
        } else if (tokenType == m_types.MODULE) {
          parseModule(state);
        } else if (tokenType == m_types.LET) {
          parseLet(state);
        } else if (tokenType == m_types.EXCEPTION) {
          parseException(state);
        }
      }

      if (state.dontMove) {
        state.dontMove = false;
        // revert
        tokenType = state.previousElementType1;
        state.previousElementType1 = state.previousElementType2;
      } else {
        builder.advanceLexer();
      }
    }

    endLikeSemi(state);
  }

  private void parseRef(@NotNull ParserState state) {
    if (state.is(m_types.C_TAG_START)) {
      state.remapCurrentToken(m_types.PROPERTY_NAME).mark(m_types.C_TAG_PROPERTY);
    }
  }

  private void parseOption(@NotNull ParserState state) {
    state.mark(m_types.C_OPTION);
  }

  private void parseSome(@NotNull ParserState state) {
    if (state.isCurrentResolution(patternMatch)) {
      // Defining a pattern match ::  switch (c) { | |>Some<| .. }
      state
          .remapCurrentToken(m_types.VARIANT_NAME)
          .wrapWith(m_types.C_VARIANT)
          .resolution(patternMatchVariant);
    }
  }

  private void parseNone(@NotNull ParserState state) {
    if (state.isCurrentResolution(patternMatch)) {
      // Defining a pattern match
      // switch (c) { | |>None<| .. }
      state
          .remapCurrentToken(m_types.VARIANT_NAME)
          .wrapWith(m_types.C_VARIANT)
          .resolution(patternMatchVariant);
    }
  }

  private void parseRaw(@NotNull ParserState state) {
    if (state.is(m_types.C_MACRO_EXPR)) {
      state.mark(m_types.C_MACRO_NAME);
    }
  }

  private void parseIf(@NotNull ParserState state) {
    state.mark(m_types.C_IF).advance().mark(m_types.C_BINARY_CONDITION);
  }

  private void parseDotDotDot(@NotNull ParserState state) {
    if (state.previousElementType1 == m_types.LBRACE) {
      // Mixin ::  ... { |>...<| x ...
      state
          .resolution(recordUsage)
          .updateCurrentCompositeElementType(m_types.C_RECORD_EXPR)
          .mark(m_types.C_MIXIN_FIELD);
    }
  }

  private void parseWith(@NotNull ParserState state) {
    if (state.isCurrentResolution(functorResult)) {
      // module M (X) : ( S |>with<| ... ) = ...
      state.popEnd().mark(m_types.C_CONSTRAINTS).resolution(functorConstraints);
    }
  }

  private void parseEqEq(@NotNull ParserState _state) {
    //if (!state.in(m_types.C_BINARY_CONDITION)) {
    //      state.precedeMark(m_types.C_BINARY_CONDITION);
    //}
  }

  private void parseQuestionMark(@NotNull ParserState state) {
    if (state.previousElementType1 == m_types.EQ) {
      // x=|>?<| ...
      return;
    }

    if (state.is(m_types.C_TAG_START)) {
      // <jsx |>?<|prop ...
      state
          .mark(m_types.C_TAG_PROPERTY)
          .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state))
          .advance()
          .remapCurrentToken(m_types.PROPERTY_NAME);
    } else if (state.is(m_types.C_BINARY_CONDITION)) {
      state.popEnd();
    }
  }

  private void parseTilde(@NotNull ParserState state) {
    if (state.is(m_types.C_SIG_ITEM) && state.isPrevious(m_types.C_SCOPED_EXPR)) {
      // must be the signature of a function definition
      state.updatePreviousComposite(m_types.C_FUN_EXPR);
    }

    state.advance().mark(m_types.C_NAMED_PARAM);
  }

  private void parseAssert(@NotNull ParserState state) {
    // |>assert<| ...
    state.mark(m_types.C_ASSERT_STMT);
  }

  private void parseAnd(@NotNull ParserState state) {
    if (state.isCurrentResolution(functorConstraint)) {
      // module M = (X) : ( S with ... |>and<| ... ) = ...
      state.popEnd();
    } else {
      state.popEndUntilStart();
      ParserScope latestScope = state.getLatestScope();
      state.popEnd().advance();

      if (latestScope != null) {
        if (isTypeResolution(latestScope)) {
          state.mark(m_types.C_TYPE_DECLARATION).resolution(type).setStart();
        } else if (isLetResolution(latestScope)) {
          state.mark(m_types.C_LET_DECLARATION).resolution(let).setStart();
        } else if (isModuleResolution(latestScope)) {
          state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
        }
      }
    }
  }

  private void parseComma(@NotNull ParserState state) {
    // Intermediate structures
    boolean isIntermediateState =
        state.is(m_types.C_FUN_BODY)
            || (state.is(m_types.C_BINARY_CONDITION) && !state.hasScopeToken())
            || state.is(m_types.C_TERNARY)
            || state.is(m_types.C_NAMED_PARAM)
            || state.is(m_types.C_SIG_ITEM);
    while (isIntermediateState) {
      if (state.is(m_types.C_FUN_BODY)) {
        // a function is part of something else, close it first
        state.popEnd().popEnd();
      } else if (state.is(m_types.C_BINARY_CONDITION) && !state.hasScopeToken()) {
        state.popEnd();
      } else if (state.is(m_types.C_TERNARY) || state.is(m_types.C_NAMED_PARAM)) {
        state.popEnd();
      } else if (state.is(m_types.C_SIG_ITEM)) {
        state.popEnd();
        if (!state.isCurrentResolution(signatureScope)) {
          state.popEnd();
        }
      }

      isIntermediateState =
          state.is(m_types.C_FUN_BODY)
              || (state.is(m_types.C_BINARY_CONDITION) && !state.hasScopeToken())
              || state.is(m_types.C_TERNARY)
              || state.is(m_types.C_NAMED_PARAM)
              || state.is(m_types.C_SIG_ITEM);
    }

    if (state.is(m_types.C_RECORD_FIELD) || state.is(m_types.C_MIXIN_FIELD)) {
      state.popEnd().advance();
      IElementType nextToken = state.getTokenType();
      if (nextToken != m_types.RBRACE) {
        state.mark(m_types.C_RECORD_FIELD);
      }
    } else if (state.is(m_types.C_OBJECT_FIELD) || state.isCurrentResolution(fieldNamed)) {
      boolean isJsObject = state.isCurrentCompositeElementType(m_types.C_OBJECT_FIELD);
      state.popEnd().advance();
      IElementType nextToken = state.getTokenType();
      if (nextToken != m_types.RBRACE) {
        state.mark(isJsObject ? m_types.C_OBJECT_FIELD : m_types.C_RECORD_FIELD).resolution(field);
      }
    } else if (state.isCurrentResolution(signatureScope)) {
      state.advance().mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_FUN_PARAM)) {
      state.popEnd();
      state.advance();
      IElementType nextTokenType = state.getTokenType();
      if (nextTokenType != m_types.RPAREN) {
        // not at the end of a list: ie not => (p1, p2<,> )
        state.mark(m_types.C_FUN_PARAM);
      }
    }
  }

  private void parsePipe(@NotNull ParserState state) {
    // Remove intermediate constructions
    if (state.is(m_types.C_FUN_BODY)) {
      // a function is part of something else, close it first
      state.popEnd().popEnd();
    }

    if (state.is(m_types.C_VARIANT_DECLARATION)) {
      state.popEnd();
    } else if (state.is(m_types.C_FUN_PARAM) && state.isPrevious(m_types.C_VARIANT_CONSTRUCTOR)) {
      state.popEndUntil(m_types.C_TYPE_BINDING);
    } else if (!state.isCurrentResolution(switchBody) /*nested switch*/
                   && state.in(m_types.C_PATTERN_MATCH_BODY)) {
      state.popEndUntil(m_types.C_PATTERN_MATCH_BODY);
      state.popEnd().popEnd();
    }

    if (state.is(m_types.C_TYPE_BINDING)) {
      // type x = |>|<| ...
      state.advance().mark(m_types.C_VARIANT_DECLARATION);
    } else if (state.isCurrentResolution(switchBody)) {
      // switch x { |>|<| ... }
      state.advance().mark(m_types.C_PATTERN_MATCH_EXPR).resolution(patternMatch);
    } else if (state.is(m_types.C_TRY_HANDLERS)) {
      // Start of a try handler ::  try (...) { |>|<| ... }
      state.advance().mark(m_types.C_TRY_HANDLER);
    } else {
      if (state.is(m_types.C_PATTERN_MATCH_EXPR)) {
        // pattern grouping ::  | X |>|<| Y => ...
        state.popEnd();
      } else if (state.in(m_types.C_PATTERN_MATCH_BODY)) {
        // can be a switchBody or a 'fun'
        state.popEndUntil(m_types.C_PATTERN_MATCH_BODY);
        state.popEnd().popEnd();
      }
      // By default, a pattern match
      state.advance().mark(m_types.C_PATTERN_MATCH_EXPR).resolution(patternMatch);
    }
  }

  private void parseStringValue(@NotNull ParserState state) {
    if (state.isCurrentResolution(maybeRecord)) {
      IElementType nextToken = state.lookAhead(1);
      if (nextToken == m_types.COLON) {
        state
            .resolution(jsObject)
            .updateCurrentCompositeElementType(m_types.C_JS_OBJECT)
            .mark(m_types.C_OBJECT_FIELD)
            .resolution(field);
      }
    }
  }

  private void parseTemplateStringOpen(@NotNull ParserState state) {
    // |>j`<| ...
    state
        .markScope(m_types.C_INTERPOLATION_EXPR, m_types.JS_STRING_OPEN)
        .advance()
        .mark(m_types.C_INTERPOLATION_PART);
  }

  private void parseLet(@NotNull ParserState state) {
    if (!state.is(m_types.C_PATTERN_MATCH_BODY)) {
      endLikeSemi(state);
    }
    state.mark(m_types.C_LET_DECLARATION).resolution(let).setStart();
  }

  private void parseModule(@NotNull ParserState state) {
    if (!state.is(m_types.C_MACRO_NAME)) {
      endLikeSemi(state);
      state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
    }
  }

  private void parseException(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_EXCEPTION_DECLARATION).setStart();
  }

  private void parseType(@NotNull ParserState state) {
    if (state.isCurrentResolution(functorConstraints)) {
      // module M = (X) : ( S with |>type<| ... ) = ...
      state.mark(m_types.C_CONSTRAINT).resolution(functorConstraint);
    } else if (!state.isCurrentResolution(module)) {
      endLikeSemi(state);
      state.mark(m_types.C_TYPE_DECLARATION).resolution(type).setStart();
    }
  }

  private void parseExternal(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_EXTERNAL_DECLARATION).setStart();
  }

  private void parseOpen(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_OPEN);
  }

  private void parseInclude(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_INCLUDE);
  }

  private void parsePercent(@NotNull ParserState state) {
    state.mark(m_types.C_MACRO_EXPR);
  }

  private void parseColon(@NotNull ParserState state) {
    if (state.isCurrentResolution(maybeRecord)) {
      // yes it is a record, rollback and remove the maybe
      ParserScope startScope = state.getLatestScope();
      if (startScope != null) {
        startScope.rollbackTo();
        state.pop();
        state
            .mark(m_types.C_RECORD_EXPR)
            .resolution(recordUsage)
            .advance()
            .mark(m_types.C_RECORD_FIELD);
      }
      return;
    }

    if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
      // external x |> :<| ...
      state.advance().mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
    } else if (state.isCurrentResolution(letNamed)) {
      // let x |> :<| ...
      state.advance().mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_MODULE_DECLARATION)) {
      // module M |> :<| ...
      state
          .resolution(moduleNamedSignature)
          .advance()
          .mark(m_types.C_MODULE_TYPE)
          .resolution(moduleType);
    } else if (state.isCurrentResolution(functorNamedEq)) {
      // module M = (X:Y) |> :<| ...
      state.resolution(functorNamedEqColon).advance();
      IElementType tokenType = state.getTokenType();
      if (tokenType == m_types.LPAREN) {
        // module M = (X:Y) : |>(<| S ... ) = ...
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(scope).dummy().advance();
      }
      state.mark(m_types.C_FUNCTOR_RESULT).resolution(functorResult);
    } else if (state.is(m_types.C_RECORD_FIELD) || state.is(m_types.C_OBJECT_FIELD)) {
      state.complete().advance();
      if (state.in(m_types.C_TYPE_BINDING)) {
        state.mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
      }
    } else if (state.isCurrentResolution(field)) {
      state.resolution(fieldNamed);
    } else if (state.is(m_types.C_FUN_PARAM)) {
      state.advance().mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_NAMED_PARAM)) {
      state
          .popEnd()
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .mark(m_types.C_SIG_ITEM);
    }
  }

  private void parseArrobase(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_ANNOTATION).mark(m_types.C_MACRO_NAME);
  }

  private void parseLt(@NotNull ParserState state) {
    if (state.is(m_types.C_OPTION)) {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LT);
    } else if (state.isCurrentResolution(typeNamed)) {
      // type parameters ::  type t |> < <| 'a >
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LT).resolution(typeNamedParameters);
    } else if (!(state.is(m_types.C_SIG_ITEM) || state.is(m_types.C_TYPE_BINDING))) {
      // Can be a symbol or a JSX tag
      IElementType nextTokenType = state.rawLookup(1);
      if (nextTokenType == m_types.LIDENT
              || nextTokenType == m_types.UIDENT
              || nextTokenType == m_types.OPTION) {
        // Note that option is a ReasonML keyword but also a JSX keyword !
        // Surely a tag
        state
            .remapCurrentToken(m_types.TAG_LT)
            .mark(m_types.C_TAG)
            .markScope(m_types.C_TAG_START, m_types.TAG_LT)
            .advance()
            .remapCurrentToken(m_types.TAG_NAME)
            .wrapWith(
                nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
      } else if (nextTokenType == m_types.GT) {
        // a React fragment start
        state
            .remapCurrentToken(m_types.TAG_LT)
            .mark(m_types.C_TAG)
            .mark(m_types.C_TAG_START)
            .advance()
            .remapCurrentToken(m_types.TAG_GT)
            .advance()
            .popEnd();
      }
    }
  }

  private void parseGt(@NotNull ParserState state) {
    // ?prop=value |> > <| ...
    if (state.is(m_types.C_TAG_PROP_VALUE)) {
      state.popEnd().popEnd();
    }
    // ?prop |> > <| ...
    else if (state.is(m_types.C_TAG_PROPERTY)) {
      state.popEnd();
    }

    if (state.is(m_types.C_TAG_START)) {
      state.remapCurrentToken(m_types.TAG_GT).advance().popEnd().mark(m_types.C_TAG_BODY);
    } else if (state.is(m_types.C_TAG_CLOSE)) {
      state.remapCurrentToken(m_types.TAG_GT).advance().popEnd().popEnd();
    } else if (state.isCurrentResolution(typeNamedParameters)) {
      state.advance().popEnd();
    }
    // option < ... |> > <| ...
    else if (state.is(m_types.C_SCOPED_EXPR) && state.isPrevious(m_types.C_OPTION)) {
      state.advance().popEnd().popEnd();
    }
  }

  private void parseGtAutoClose(@NotNull ParserState state) {
    // ?prop=value |> /> <| ...
    if (state.is(m_types.C_TAG_PROP_VALUE)) {
      state.popEnd().popEnd();
    }
    // ?prop |> /> <| ...
    else if (state.is(m_types.C_TAG_PROPERTY)) {
      state.popEnd();
    }

    state.advance().popEnd().popEnd();
  }

  private void parseLtSlash(@NotNull ParserState state) {
    IElementType nextTokenType = state.rawLookup(1);
    if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
      // A closing tag
      if (state.is(m_types.C_TAG_BODY)) {
        state.popEnd();
      }

      state
          .remapCurrentToken(m_types.TAG_LT)
          .mark(m_types.C_TAG_CLOSE)
          .advance()
          .remapCurrentToken(m_types.TAG_NAME)
          .wrapWith(
              nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
    } else if (nextTokenType == m_types.GT) {
      // a React fragment end
      state
          .remapCurrentToken(m_types.TAG_LT_SLASH)
          .mark(m_types.C_TAG_CLOSE)
          .advance()
          .remapCurrentToken(m_types.TAG_GT)
          .advance()
          .popEnd();
    }
  }

  private void parseLIdent(@NotNull ParserState state) {
    if (state.is(m_types.C_MACRO_NAME)) {
      // Must stop annotation if no dot/@ before
      if (state.previousElementType1 != m_types.DOT
              && state.previousElementType1 != m_types.ARROBASE) {
        state.popEnd().popEnd();
      }
    }

    // external |>x<| ...
    if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    }
    // let |>x<| ...
    else if (state.isCurrentResolution(let)) {
      state.resolution(letNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    }
    // type |>x<| ...
    else if (state.isCurrentResolution(type)) {
      state.resolution(typeNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    }
    // not an identifier
    else {
      // This is a property
      if (state.is(m_types.C_TAG_START)) {
        state
            .remapCurrentToken(m_types.PROPERTY_NAME)
            .mark(m_types.C_TAG_PROPERTY)
            .setWhitespaceSkippedCallback(
                (type, start, end) -> {
                  if (state.is(m_types.C_TAG_PROPERTY)
                          || (state.is(m_types.C_TAG_PROP_VALUE) && !state.hasScopeToken())) {
                    if (state.is(m_types.C_TAG_PROP_VALUE)) {
                      state.popEnd();
                    }
                    state.popEnd();
                    state.setWhitespaceSkippedCallback(null);
                  }
                });
      } else if (state.isCurrentResolution(recordBinding)) {
        state.mark(m_types.C_RECORD_FIELD);
      } else {
        IElementType nextElementType = state.lookAhead(1);

        // Single (paren less) function parameters ::  |>x<| => ...
        if (!state.is(m_types.C_SIG_ITEM) && nextElementType == m_types.ARROW) {
          state.mark(m_types.C_FUN_EXPR).mark(m_types.C_FUN_PARAMS).mark(m_types.C_FUN_PARAM);
        }
        // a ternary ::  |>x<| ? ...
        else if (nextElementType == m_types.QUESTION_MARK && !state.in(m_types.C_TAG_START)) {
          state.mark(m_types.C_TERNARY).mark(m_types.C_BINARY_CONDITION);
        }
      }

      if (state.is(m_types.C_DECONSTRUCTION)
              || (state.is(m_types.C_FUN_PARAM) && !state.isPrevious(m_types.C_FUN_CALL_PARAMS))) {
        state.wrapWith(m_types.C_LOWER_IDENTIFIER);
      } else if (!state.is(m_types.C_TAG_PROPERTY)) {
        state.wrapWith(m_types.C_LOWER_SYMBOL);
      }
    }
  }

  private void parseLBracket(@NotNull ParserState state) {
    if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
      // Local open
      // M.|>[<| ... ]
      state.markScope(m_types.C_LOCAL_OPEN, m_types.LBRACKET);
    } else {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
    }
  }

  private void parseBracketGt(@NotNull ParserState state) {
    state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
  }

  private void parseRBracket(@NotNull ParserState state) {
    ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACKET);
    state.advance();

    if (scope != null) {
      state.popEnd();
    }
  }

  private void parseLBrace(@NotNull ParserState state) {
    if (state.previousElementType1 == m_types.DOT && state.previousElementType2 == m_types.UIDENT) {
      // Local open a js object ::  Xxx.|>{<| "y" : ... }
      state.mark(m_types.C_LOCAL_OPEN);
      IElementType nextElementType = state.lookAhead(1);
      if (nextElementType == m_types.LIDENT) {
        state
            .markScope(m_types.C_RECORD_EXPR, m_types.LBRACE)
            .resolution(record)
            .advance()
            .mark(m_types.C_RECORD_FIELD)
            .resolution(field);
      } else {
        state
            .markScope(m_types.C_JS_OBJECT, m_types.LBRACE)
            .resolution(jsObject)
            .advance()
            .mark(m_types.C_OBJECT_FIELD)
            .resolution(field);
      }
    } else if (state.is(m_types.C_TYPE_BINDING)) {
      boolean isJsObject = state.lookAhead(1) == m_types.DOT;
      state
          .markScope(isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE)
          .resolution(isJsObject ? jsObject : recordBinding);
      if (isJsObject) {
        state.advance().advance().mark(m_types.C_OBJECT_FIELD).resolution(field);
      }
    } else if (state.is(m_types.C_MODULE_TYPE)) {
      // module M : |>{<| ...
      state.updateScopeToken(m_types.LBRACE);
    } else if (state.is(m_types.C_TRY_BODY) || state.is(m_types.C_TRY_EXPR)) {
      // A try expression ::  try ... |>{<| ... }
      state.popEndUntil(m_types.C_TRY_EXPR)
          .markScope(m_types.C_TRY_HANDLERS, m_types.LBRACE);
    } else if (state.isCurrentResolution(moduleNamedEq)) {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(moduleBinding);
    } else if (state.is(m_types.C_LET_BINDING)) {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(maybeRecord);
    } else if (state.is(m_types.C_BINARY_CONDITION)) {
      state.popEnd();
      if (state.is(m_types.C_IF)) {
        // if x |>{<| ... }
        state.markScope(m_types.C_IF_THEN_SCOPE, m_types.LBRACE);
      } else if (state.is(m_types.C_SWITCH_EXPR)) {
        // switch x |>{<| ... }
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(switchBody);
      }
    } else if (state.is(m_types.C_SWITCH_EXPR)) {
      // switch (x) |>{<| ... }
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(switchBody);
    } else if (state.is(m_types.C_TAG_PROP_VALUE)) {
      // A scoped property
      state.updateScopeToken(m_types.LBRACE);
    } else {
      // it might be a js object
      IElementType nextElement = state.lookAhead(1);
      if (nextElement == m_types.STRING_VALUE || nextElement == m_types.DOT) {
        // js object detected
        // |>{<| ./"x" ___ }
        state
            .markScope(m_types.C_JS_OBJECT, m_types.LBRACE)
            .resolution(jsObject)
            .advance()
            .advance()
            .mark(m_types.C_OBJECT_FIELD)
            .resolution(field);
      } else if (nextElement == m_types.DOTDOTDOT) {
        // record usage ::  x  => |>{<| ...
        state
            .markScope(m_types.C_RECORD_EXPR, m_types.LBRACE)
            .resolution(recordUsage)
            .advance()
            .mark(m_types.C_MIXIN_FIELD);
      } else if (state.is(m_types.C_FUN_BODY) && !state.hasScopeToken()) {
        state.updateScopeToken(m_types.LBRACE);
      } else {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(scope);
      }
    }
  }

  private void parseRBrace(@NotNull ParserState state) {
    ParserScope scope =
        state.popEndUntilOneOfElementType(m_types.LBRACE, m_types.RECORD, m_types.SWITCH);
    state.advance();
    if (scope != null) {
      state.popEnd();
    }

    if (state.is(m_types.C_LOCAL_OPEN)) {
      state.popEnd();
    } else if (state.is(m_types.C_TAG_PROPERTY)) {
      state.popEnd();
    }
  }

  private void parseLParen(@NotNull ParserState state) {
    if (state.is(m_types.C_MACRO_NAME) && state.isPrevious(m_types.C_ANNOTATION)) {
      // @ann |>(<| ... )
      state
          .popEnd()
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
    } else if (state.is(m_types.C_SIG_ITEM) && state.previousElementType1 != m_types.LIDENT) {
      if (state.isPrevious(m_types.C_SIG_EXPR)) {
        state
            .resolution(signatureScope)
            .updateCurrentCompositeElementType(m_types.C_SCOPED_EXPR)
            .updateScopeToken(m_types.LPAREN)
            .advance()
            .mark(m_types.C_SIG_ITEM);
      } else {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(signatureScope);
      }
    } else if (state.is(m_types.C_MACRO_NAME)) {
      // %raw |>(<| ...
      state.popEnd().markScope(m_types.C_MACRO_RAW_BODY, m_types.LPAREN);
    } else if (state.isCurrentResolution(moduleBinding)
                   && state.previousElementType1 != m_types.UIDENT) {
      // This is a functor ::  module M = |>(<| ... )
      state
          .popCancel()
          . // remove previous module binding
                resolution(functorNamedEq)
          .updateCurrentCompositeElementType(m_types.C_FUNCTOR_DECLARATION)
          .markScope(m_types.C_FUNCTOR_PARAMS, m_types.LPAREN)
          .resolution(functorParams)
          .advance()
          .mark(m_types.C_FUNCTOR_PARAM)
          .resolution(functorParam);
    } else if (state.isCurrentResolution(maybeFunctorCall)) {
      // We know now that it is really a functor call
      //  module M = X |>(<| ... )
      //  open X |>(<| ... )
      state
          .resolution(functorCall)
          .complete()
          .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
          .advance()
          .mark(m_types.C_FUN_PARAM);
    } else if (state.is(m_types.C_VARIANT_DECLARATION)) {
      // Variant constructor ::  type t = | Variant |>(<| .. )
      state
          .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN) // C_PARAMETERS
          .advance()
          .mark(m_types.C_FUN_PARAM);
    } else if (state.isCurrentResolution(patternMatchVariant)) {
      // It's a constructor in a pattern match ::  switch x { | Variant |>(<| ... ) => ... }
      state.markScope(m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN);
    } else if (state.is(m_types.C_PATTERN_MATCH_EXPR)) {
      // A tuple in a pattern match ::  | |>(<| .. ) => ..
      state
          .resolution(patternMatchValue)
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(patternMatchValue);
    } else if (state.previousElementType2 == m_types.UIDENT
                   && state.previousElementType1 == m_types.DOT) {
      // Local open ::  M. |>(<| ... )
      state.markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN);
    } else if (state.isCurrentResolution(let)) {
      // Deconstructing a term ::  let |>(<| a, b ) =
      state
          .resolution(letNamed)
          .markScope(m_types.C_DECONSTRUCTION, m_types.LPAREN)
          .resolution(deconstruction);
    } else if (state.previousElementType1 == m_types.LIDENT) {
      // Calling a function
      state
          .markScope(m_types.C_FUN_CALL_PARAMS, m_types.LPAREN)
          .resolution(functionCallParams)
          .advance();
      IElementType nextTokenType = state.getTokenType();
      if (nextTokenType != m_types.RPAREN) {
        state.mark(m_types.C_FUN_PARAM);
      }
    } else if (state.is(m_types.C_BINARY_CONDITION)) {
      state.updateScopeToken(m_types.LPAREN);
    } else if (state.is(m_types.C_TAG_PROP_VALUE) && !state.hasScopeToken()) {
      // <div prop=|>(<| ...
      state.updateScopeToken(m_types.LPAREN);
    } else {
      IElementType nextTokenType = state.lookAhead(1);

      if (nextTokenType == m_types.DOT || nextTokenType == m_types.TILDE) {
        // A function
        // |>(<| .  OR  |>(<| ~
        state.mark(m_types.C_FUN_EXPR).markScope(m_types.C_FUN_PARAMS, m_types.LPAREN).advance();
        if (nextTokenType == m_types.DOT) {
          state.advance();
        }
        state.mark(m_types.C_FUN_PARAM);
      } else {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(genericExpression);
      }
    }
  }

  private void parseRParen(@NotNull ParserState state) {
    ParserScope startScope = state.popEndUntilOneOfElementType(m_types.LPAREN);
    if (startScope == null) {
      return;
    }

    if (startScope.isResolution(genericExpression)) {
      IElementType aheadType = state.lookAhead(1);
      if (aheadType == m_types.ARROW) {
        // if current resolution is UNKNOWN and next item is an arrow, it means we are processing a
        // function definition,
        // we must rollback to the start of the scope and start the parsing again, but this time
        // with exact information!
        startScope.rollbackTo();
        state.pop();
        state
            .mark(m_types.C_FUN_EXPR)
            .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
            .advance()
            .mark(m_types.C_FUN_PARAM);
        return;
      }
    }

    state.advance();
    IElementType nextTokenType = state.getTokenType();

    if (nextTokenType == m_types.QUESTION_MARK && !state.isPrevious(m_types.C_TERNARY)) {
      // ( ... |>)<| ? ...
      state
          .precedeScope(m_types.C_TERNARY)
          .updateCurrentCompositeElementType(m_types.C_BINARY_CONDITION);
    }

    state.popEnd();

    if (state.is(m_types.C_VARIANT_DECLARATION)) {
      state.popEndUntil(m_types.C_TYPE_BINDING);
    } else if (state.is(m_types.C_ANNOTATION)) {
      state.popEnd();
    } else if (state.is(m_types.C_TAG_PROP_VALUE) && !state.hasScopeToken()) {
      state.popEnd().popEnd();
    } else if (state.is(m_types.C_TAG_PROPERTY)) {
      state.popEnd();
    }
  }

  private void parseEq(@NotNull ParserState state) {
    // Intermediate constructions
    if (state.is(m_types.C_SIG_ITEM)) {
      state.popEndUntil(m_types.C_SIG_EXPR).popEnd();
    }

    if (state.isCurrentResolution(typeNamed)) {
      // type t |> = <| ...
      state.resolution(typeNamedEq).advance().mark(m_types.C_TYPE_BINDING);
    } else if (state.isCurrentResolution(let)
                   || state.isCurrentResolution(letNamed) /* || state.isCurrentResolution(letNamedAttribute)*/
                   || state.isCurrentResolution(letNamedSignature)) {
      state.resolution(letNamedEq).advance().mark(m_types.C_LET_BINDING);
    } else if (state.is(m_types.C_MODULE_DECLARATION)) {
      // module M |> = <| ...
      state.advance().mark(m_types.C_UNKNOWN_EXPR /*C_DUMMY*/).dummy().resolution(moduleBinding);
    } else if (state.is(m_types.C_TAG_PROPERTY)) {
      state.advance().mark(m_types.C_TAG_PROP_VALUE);
    }
  }

  private void parseSemi(@NotNull ParserState state) {
    if (state.in(m_types.C_PATTERN_MATCH_BODY)) {
      state.popEndUntil(m_types.C_PATTERN_MATCH_BODY);
    }

    if (!state.isCurrentResolution(patternMatchBody)) {
      // Don't pop the scopes
      state.popEndUntilScope();
    }
  }

  private void parseUIdent(@NotNull ParserState state) {
    if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
      return;
    }
    if (state.is(m_types.C_MODULE_DECLARATION)) {
      // module |>M<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
      return;
    }
    if (state.is(m_types.C_EXCEPTION_DECLARATION)) {
      // exception |>E<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
      return;
    }

    if (state.is(m_types.C_OPEN)) {
      // It is a module name/path, or maybe a functor call ::  open |>M<| ...
      state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
    } else if (state.is(m_types.C_INCLUDE)) {
      // It is a module name/path, or maybe a functor call ::  include |>M<| ...
      state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
    } else if (state.isCurrentResolution(moduleBinding)) {
      // it might be a module functor call ::  module M = |>X<| ( ... )
      state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
    } else if ((state.is(m_types.C_TAG_START) || state.is(m_types.C_TAG_CLOSE))
                   && state.previousElementType1 == m_types.DOT) {
      // a namespaced custom component
      state.remapCurrentToken(m_types.TAG_NAME);
    } else if (state.is(m_types.C_VARIANT_DECLARATION)) {
      // Declaring a variant ::  type t = | |>X<| ..
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
      return;
    } else if (state.isCurrentResolution(patternMatch)) {
      IElementType nextElementType = state.lookAhead(1);
      if (nextElementType != m_types.DOT) {
        // Defining a pattern match ::  switch (c) { | |>X<| .. }
        state
            .remapCurrentToken(m_types.VARIANT_NAME)
            .wrapWith(m_types.C_VARIANT)
            .resolution(patternMatchVariant);

        return;
      }
    } else {
      IElementType nextElementType = state.lookAhead(1);

      if (state.is(m_types.C_TYPE_BINDING)
              && (nextElementType == m_types.PIPE || nextElementType == m_types.LPAREN)) {
        // We are declaring a variant without a pipe before
        // type t = |>X<| | ...
        // type t = |>X<| (...) | ...
        state
            .remapCurrentToken(m_types.VARIANT_NAME)
            .mark(m_types.C_VARIANT_DECLARATION)
            .wrapWith(m_types.C_UPPER_IDENTIFIER);
        return;
      } else if (!state.isCurrentResolution(moduleNamedEq)
                     && !state.isCurrentResolution(maybeFunctorCall)) {
        if (nextElementType == m_types.LPAREN) {
          state.remapCurrentToken(m_types.VARIANT_NAME);
          // A variant with a constructor
          if (state.isCurrentResolution(typeNamedEq)) {
            state.mark(m_types.C_VARIANT_DECLARATION).resolution(typeNamedEqVariant);
          }
          state.wrapWith(m_types.C_VARIANT);
          return;
        } else if (nextElementType != m_types.DOT) {
          // Must be a variant call
          state.remapCurrentToken(m_types.VARIANT_NAME).wrapWith(m_types.C_VARIANT);
          return;
        }
      }
    }

    state.wrapWith(m_types.C_UPPER_SYMBOL);
  }

  private void parsePolyVariant(@NotNull ParserState state) {
    if (state.isCurrentResolution(patternMatch)) {
      IElementType nextElementType = state.lookAhead(1);
      if (nextElementType == m_types.LPAREN) {
        state.wrapWith(m_types.C_VARIANT);
        state.resolution(patternMatchVariant);
      }
    }
  }

  private void parseSwitch(@NotNull ParserState state) {
    boolean inScope = state.isScopeTokenElementType(m_types.LBRACE);
    state
        .mark(m_types.C_SWITCH_EXPR)
        .setStart(inScope)
        .advance()
        .mark(m_types.C_BINARY_CONDITION);
  }

  private void parseTry(@NotNull ParserState state) {
    endLikeSemi(state);
    state
        .mark(m_types.C_TRY_EXPR)
        .advance()
        .mark(m_types.C_TRY_BODY);
  }

  private void parseCatch(@NotNull ParserState state) {
    if (state.is(m_types.C_TRY_BODY)) {
      state.popEnd();
    }
  }

  private void parseArrow(@NotNull ParserState state) {
    if (state.is(m_types.C_FUN_EXPR) || state.is(m_types.C_FUN_PARAM)) {
      // param(s) |>=><| body
      state.popEndUntilOneOf(m_types.C_FUN_EXPR, m_types.C_FUN_CALL_PARAMS);
      state.advance().mark(m_types.C_FUN_BODY);
    } else if (state.is(m_types.C_SIG_EXPR)) {
      state.advance().mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_SIG_ITEM)) {
      state.popEnd();
      if (!state.isCurrentResolution(signatureScope)) {
        state.popEndUntil(m_types.C_SIG_EXPR);
      }
      state.advance().mark(m_types.C_SIG_ITEM);
    } else if (state.isCurrentResolution(functorNamedEq)
                   || state.isCurrentResolution(functorNamedEqColon)
                   || state.isCurrentResolution(functorResult)) {
      // module Make = (M) : R |>=><| ...
      if (state.isCurrentResolution(functorResult)) {
        state.popEnd();
      }
      state.advance().mark(m_types.C_FUNCTOR_BINDING).resolution(functorBinding);
    } else if (state.isCurrentResolution(patternMatchVariant)
                   || state.is(m_types.C_VARIANT_CONSTRUCTOR)
                   || state.isCurrentResolution(patternMatchValue)) {
      // switch ( ... ) { | ... |>=><| ... }
      state.advance().mark(m_types.C_PATTERN_MATCH_BODY).resolution(patternMatchBody).setStart();
    }
  }

  private void endLikeSemi(@NotNull ParserState state) {
    state.popEndUntilScope();
  }
}
