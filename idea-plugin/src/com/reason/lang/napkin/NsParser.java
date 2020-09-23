package com.reason.lang.napkin;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.reason.lang.ParserScopeEnum.*;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;
import org.jetbrains.annotations.NotNull;

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
      if (state.isCurrentResolution(interpolationString)
          || state.isCurrentResolution(interpolationPart)
          || state.isCurrentResolution(interpolationReference)) {
        if (tokenType == m_types.ML_STRING_VALUE /*!*/) {
          state.popEndUntilResolution(interpolationString).advance().popEnd();
        } else if (tokenType == m_types.DOLLAR && state.isCurrentResolution(interpolationPart)) {
          state.popEnd().advance();
          IElementType nextElement = state.getTokenType();
          if (nextElement == m_types.LBRACE) {
            state
                .advance()
                .markScope(m_types.C_INTERPOLATION_REF, m_types.LBRACE)
                .resolution(interpolationReference);
          }
        } else if (state.isCurrentResolution(interpolationReference)
            && tokenType == m_types.RBRACE) {
          state.popEnd().advance().mark(m_types.C_INTERPOLATION_PART).resolution(interpolationPart);
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
    state.mark(m_types.C_OPTION).resolution(option);
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
    endLikeSemi(state);
    state
        .mark(m_types.C_IF_STMT)
        .resolution(if_)
        .advance()
        .mark(m_types.C_BIN_CONDITION)
        .resolution(binaryCondition);
  }

  private void parseDotDotDot(@NotNull ParserState state) {
    if (state.previousElementType1 == m_types.LBRACE) {
      // Mixin
      // ... { |>...<| x ...
      state
          .resolution(recordUsage)
          .updateCurrentCompositeElementType(m_types.C_RECORD_EXPR)
          .mark(m_types.C_MIXIN_FIELD)
          .resolution(mixin);
    }
  }

  private void parseWith(@NotNull ParserState state) {
    if (state.isCurrentResolution(functorResult)) {
      // module M (X) : ( S |>with<| ... ) = ...
      state.popEnd().mark(m_types.C_CONSTRAINTS).resolution(functorConstraints);
    }
  }

  private void parseAssert(@NotNull ParserState state) {
    // |>assert<| ...
    state.mark(m_types.C_ASSERT_STMT).resolution(assert_);
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
    if (state.is(m_types.C_FUN_BODY)) {
      // a function is part of something else, close it first
      state.popEnd().popEnd();
    }
    if (state.is(m_types.C_SIG_ITEM)) {
      state.popEnd();
      if (!state.isCurrentResolution(signatureScope)) {
        state.popEnd();
      }
    }

    if (state.isCurrentResolution(recordField) || state.isCurrentResolution(mixin)) {
      state.popEnd().advance();
      IElementType nextToken = state.getTokenType();
      if (nextToken != m_types.RBRACE) {
        state.mark(m_types.C_RECORD_FIELD).resolution(recordField);
      }
    } else if (state.is(m_types.C_OBJECT_FIELD) || state.isCurrentResolution(fieldNamed)) {
      boolean isJsObject = state.isCurrentCompositeElementType(m_types.C_OBJECT_FIELD);
      state.popEnd().advance();
      IElementType nextToken = state.getTokenType();
      if (nextToken != m_types.RBRACE) {
        state.mark(isJsObject ? m_types.C_OBJECT_FIELD : m_types.C_RECORD_FIELD).resolution(field);
      }
    } else if (state.isCurrentResolution(signatureScope)) {
      state.advance().mark(m_types.C_SIG_ITEM).resolution(signatureItem);
    } else if (state.isCurrentResolution(functionParameter)) {
      state.popEnd();
      state.advance();
      IElementType nextTokenType = state.getTokenType();
      if (nextTokenType != m_types.RPAREN) {
        // not at the end of a list: ie not => (p1, p2<,> )
        state.mark(m_types.C_FUN_PARAM).resolution(functionParameter);
      }
    }
  }

  private void parsePipe(@NotNull ParserState state) {
    // Remove intermediate constructions
    if (state.isCurrentResolution(functionBody)) {
      // a function is part of something else, close it first
      state.popEnd().popEnd();
    }

    if (state.isCurrentResolution(variantDeclaration)) {
      state.popEnd();
    } else if (state.isCurrentResolution(functionParameter)
        && state.isPreviousResolution(variantConstructor)) {
      state.popEndUntilResolution(typeBinding);
    } else if (!state.isCurrentResolution(switchBody) /*nested switch*/
        && state.in(m_types.C_PATTERN_MATCH_BODY)) {
      state.popEndUntil(m_types.C_PATTERN_MATCH_BODY);
      state.popEnd().popEnd();
    }

    if (state.isCurrentResolution(typeBinding)) {
      // type x = |>|<| ...
      state.advance().mark(m_types.C_VARIANT_DECL).resolution(variantDeclaration);
    } else if (state.isCurrentResolution(switchBody)) {
      // switch x { |>|<| ... }
      state.advance().mark(m_types.C_PATTERN_MATCH_EXPR).resolution(patternMatch);
    } else if (state.isCurrentResolution(tryBodyWith)) {
      // Start of a try handler
      //   try (...) { |>|<| ... }
      state.advance().mark(m_types.C_TRY_HANDLER).resolution(tryBodyWithHandler);
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
        .resolution(interpolationString)
        .advance()
        .mark(m_types.C_INTERPOLATION_PART)
        .resolution(interpolationPart);
  }

  private void parseLet(@NotNull ParserState state) {
    if (!state.is(m_types.C_PATTERN_MATCH_BODY)) {
      endLikeSemi(state);
    }
    state.mark(m_types.C_LET_DECLARATION).resolution(let).setStart();
  }

  private void parseModule(@NotNull ParserState state) {
    if (!state.isCurrentResolution(annotationName)) {
      endLikeSemi(state);
      state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
    }
  }

  private void parseException(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_EXCEPTION_DECLARATION).resolution(exception).setStart();
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
    state.mark(m_types.C_EXTERNAL_DECLARATION).resolution(external).setStart();
  }

  private void parseOpen(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_OPEN).resolution(open).setStart();
  }

  private void parseInclude(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_INCLUDE).resolution(include).setStart();
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
            .mark(m_types.C_RECORD_FIELD)
            .resolution(recordField);
      }
      return;
    }

    if (state.isCurrentResolution(externalNamed)) {
      // external x |> :<| ...
      state
          .resolution(externalNamedSignature)
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .resolution(signature)
          .mark(m_types.C_SIG_ITEM)
          .resolution(signatureItem);
    } else if (state.isCurrentResolution(letNamed)) {
      // let x |> :<| ...
      state
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .resolution(signature)
          .mark(m_types.C_SIG_ITEM)
          .resolution(signatureItem);
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
    } else if (state.isCurrentResolution(recordField) || state.is(m_types.C_OBJECT_FIELD)) {
      state.complete().advance();
      if (state.isInContext(typeBinding)) {
        state
            .mark(m_types.C_SIG_EXPR)
            .resolution(signature)
            .mark(m_types.C_SIG_ITEM)
            .resolution(signatureItem);
      }
    } else if (state.isCurrentResolution(field)) {
      state.resolution(fieldNamed);
    } else if (state.isCurrentResolution(functionParameter)) {
      state
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .resolution(signature)
          .mark(m_types.C_SIG_ITEM)
          .resolution(signatureItem);
    }
  }

  private void parseArrobase(@NotNull ParserState state) {
    endLikeSemi(state);
    state
        .mark(m_types.C_ANNOTATION)
        .resolution(annotation)
        .mark(m_types.C_MACRO_NAME)
        .resolution(annotationName);
  }

  private void parseLt(@NotNull ParserState state) {
    if (state.isCurrentResolution(option)) {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LT).resolution(optionParameter);
    } else if (state.isCurrentResolution(typeNamed)) {
      // type parameters ::  type t |> < <| 'a >
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LT).resolution(typeNamedParameters);
    } else if (!(state.isCurrentResolution(signatureItem) || state.is(m_types.C_TYPE_BINDING))) {
      // Can be a symbol or a JSX tag
      IElementType nextTokenType = state.rawLookup(1);
      if (nextTokenType == m_types.LIDENT
          || nextTokenType == m_types.UIDENT
          || nextTokenType == m_types.OPTION) {
        // Note that option is a ReasonML keyword but also a JSX keyword !
        // Surely a tag
        state
            .remapCurrentToken(m_types.TAG_LT)
            .mark(m_types.C_TAG)
            .resolution(jsxTag)
            .markScope(m_types.C_TAG_START, m_types.TAG_LT)
            .resolution(jsxStartTag)
            .advance()
            .remapCurrentToken(m_types.TAG_NAME)
            .wrapWith(
                nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
      } else if (nextTokenType == m_types.GT) {
        // a React fragment start
        state
            .remapCurrentToken(m_types.TAG_LT)
            .mark(m_types.C_TAG)
            .resolution(jsxTag)
            .mark(m_types.C_TAG_START)
            .resolution(jsxStartTag)
            .advance()
            .remapCurrentToken(m_types.TAG_GT)
            .advance()
            .popEnd();
      }
    }
  }

  private void parseGt(@NotNull ParserState state) {
    if (state.isCurrentResolution(jsxTagPropertyValue)) {
      state.popEnd().popEnd();
    }

    if (state.isCurrentResolution(jsxStartTag)) {
      state
          .remapCurrentToken(m_types.TAG_GT)
          .advance()
          .popEnd()
          .mark(m_types.C_TAG_BODY)
          .resolution(jsxTagBody);
    } else if (state.isCurrentResolution(jsxTagClose)) {
      state.remapCurrentToken(m_types.TAG_GT).advance().popEnd().popEnd();
    } else if (state.isCurrentResolution(optionParameter)) {
      state.advance().popEnd().popEnd();
    } else if (state.isCurrentResolution(typeNamedParameters)) {
      state.advance().popEnd();
    }
  }

  private void parseLtSlash(@NotNull ParserState state) {
    IElementType nextTokenType = state.rawLookup(1);
    if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
      // A closing tag
      if (state.isCurrentResolution(jsxTagBody)) {
        state.popEnd();
      }

      state
          .remapCurrentToken(m_types.TAG_LT)
          .mark(m_types.C_TAG_CLOSE)
          .resolution(jsxTagClose)
          .advance()
          .remapCurrentToken(m_types.TAG_NAME)
          .wrapWith(
              nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
    } else if (nextTokenType == m_types.GT) {
      // a React fragment end
      state
          .remapCurrentToken(m_types.TAG_LT_SLASH)
          .mark(m_types.C_TAG_CLOSE)
          .resolution(jsxTagClose)
          .advance()
          .remapCurrentToken(m_types.TAG_GT)
          .advance()
          .popEnd();
    }
  }

  private void parseGtAutoClose(@NotNull ParserState state) {
    if (state.isCurrentResolution(jsxTagPropertyValue)) {
      state.popEnd().popEnd();
    }

    state.advance().popEnd().popEnd();
  }

  private void parseLIdent(@NotNull ParserState state) {
    if (state.isCurrentResolution(annotationName)) {
      // Must stop annotation if no dot/@ before
      if (state.previousElementType1 != m_types.DOT
          && state.previousElementType1 != m_types.ARROBASE) {
        state.popEnd().popEnd();
      }
    }

    if (state.isCurrentResolution(external)) {
      // external |>x<| ...
      state.resolution(externalNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.isCurrentResolution(let)) {
      // let |>x<| ...
      state.resolution(letNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.isCurrentResolution(type)) {
      // type |>x<| ...
      state.resolution(typeNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else {
      if (state.isCurrentResolution(jsxStartTag)) {
        // This is a property
        state
            .remapCurrentToken(m_types.PROPERTY_NAME)
            .mark(m_types.C_TAG_PROPERTY)
            .setWhitespaceSkippedCallback(
                (type, start, end) -> {
                  if (state.is(m_types.C_TAG_PROPERTY)
                      || (state.isCurrentResolution(jsxTagPropertyValue)
                          && state.notInScopeExpression())) {
                    if (state.isCurrentResolution(jsxTagPropertyValue)) {
                      state.popEnd();
                    }
                    state.popEnd();
                    state.setWhitespaceSkippedCallback(null);
                  }
                });
      } else if (state.isCurrentResolution(recordBinding)) {
        state.mark(m_types.C_RECORD_FIELD).resolution(recordField);
      } else {
        IElementType nextElementType = state.lookAhead(1);
        if (!state.isCurrentResolution(signatureItem) && nextElementType == m_types.ARROW) {
          // Single (paren less) function parameters
          // |>x<| => ...
          state
              .mark(m_types.C_FUN_EXPR)
              .resolution(function)
              .mark(m_types.C_FUN_PARAMS)
              .resolution(functionParameters)
              .mark(m_types.C_FUN_PARAM)
              .resolution(functionParameter);
        }
      }

      if (state.is(m_types.C_DECONSTRUCTION)) {
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
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET).resolution(bracket);
    }
  }

  private void parseBracketGt(@NotNull ParserState state) {
    state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET).resolution(bracketGt);
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
      // Local open a js object
      // Xxx.|>{<| "y" : ... }
      state.mark(m_types.C_LOCAL_OPEN).resolution(localObjectOpen);
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
    } else if (state.isCurrentResolution(typeBinding)) {
      boolean isJsObject = state.lookAhead(1) == m_types.DOT;
      state
          .markScope(isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE)
          .resolution(isJsObject ? jsObject : recordBinding);
      if (isJsObject) {
        state.advance().advance().mark(m_types.C_OBJECT_FIELD).resolution(field);
      }
    } else if (state.isCurrentResolution(tryBodyWith)) {
      // A try expression ::  try ... |>{<| ... }
      state.markScope(m_types.C_TRY_HANDLERS, m_types.LBRACE).resolution(tryBodyWith);
    } else if (state.isCurrentResolution(moduleNamedEq)) {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(moduleBinding);
    } else if (state.isCurrentResolution(letBinding)) {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(maybeRecord);
    } else if (state.isCurrentResolution(binaryCondition)) {
      state.popEnd();
      if (state.isCurrentResolution(if_)) {
        // if x |>{<| ... }
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(ifThenStatement);
      } else if (state.isCurrentResolution(switch_)) {
        // switch x |>{<| ... }
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(switchBody);
      }
    } else if (state.isCurrentResolution(jsxTagPropertyValue)) {
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
            .mark(m_types.C_MIXIN_FIELD)
            .resolution(mixin);
      } else if (state.is(m_types.C_FUN_BODY)) {
        state.updateScopeToken(m_types.LBRACE);
      } else {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE).resolution(scope);
      }
    }
  }

  private void parseRBrace(@NotNull ParserState state) {
    ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACE);
    state.advance();
    if (scope != null) {
      state.popEnd();
    }

    if (state.isCurrentResolution(jsxTagPropertyEq) || state.isCurrentResolution(localObjectOpen)) {
      state.popEnd();
    }
  }

  private void parseLParen(@NotNull ParserState state) {
    if (state.isCurrentResolution(annotationName)) {
      // @ann |>(<| ... )
      state
          .popEnd()
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(annotationParameter);
    } else if (state.isCurrentResolution(signatureItem)
        && state.previousElementType1 != m_types.LIDENT) {
      if (state.isPreviousResolution(signature)) {
        state
            .resolution(signatureScope)
            .updateCurrentCompositeElementType(m_types.C_SCOPED_EXPR)
            .updateScopeToken(m_types.LPAREN)
            .advance()
            .mark(m_types.C_SIG_ITEM)
            .resolution(signatureItem);
      } else {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(signatureScope);
      }
    } else if (state.is(m_types.C_MACRO_NAME)) {
      state.popEnd().markScope(m_types.C_MACRO_RAW_BODY, m_types.LPAREN);
    } else if (state.isCurrentResolution(moduleBinding)
        && state.previousElementType1 != m_types.UIDENT) {
      // This is a functor ::  module M = |>(<| ... )
      state
          .popCancel()
          . // remove previous module binding
          resolution(functorNamedEq)
          .updateCurrentCompositeElementType(m_types.C_FUNCTOR)
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
          .resolution(functionParameters)
          .advance()
          .mark(m_types.C_FUN_PARAM)
          .resolution(functionParameter);
    } else if (state.isCurrentResolution(variantDeclaration)) {
      // Variant constructor ::  type t = | Variant |>(<| .. )
      state
          .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
          .resolution(variantConstructor)
          .advance()
          .mark(m_types.C_FUN_PARAM)
          .resolution(functionParameter);
    } else if (state.isCurrentResolution(patternMatchVariant)) {
      // It's a constructor in a pattern match ::  switch x { | Variant |>(<| ... ) => ... }
      state
          .markScope(m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN)
          .resolution(patternMatchVariantConstructor);
    } else if (state.is(m_types.C_PATTERN_MATCH_EXPR)) {
      // A tuple in a pattern match ::  | |>(<| .. ) => ..
      state
          .resolution(patternMatchValue)
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(patternMatchValue);
    } else if (state.previousElementType2 == m_types.UIDENT
        && state.previousElementType1 == m_types.DOT) {
      // Local open
      // M. |>(<| ... )
      state.markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN).resolution(localOpen);
    } else if (state.isCurrentResolution(let)) {
      // Deconstructing a term
      //  let |>(<| a, b ) =
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
        state.mark(m_types.C_FUN_PARAM).resolution(functionParameter);
      }
    } else {
      IElementType nextTokenType = state.lookAhead(1);

      if (nextTokenType == m_types.DOT || nextTokenType == m_types.TILDE) {
        // A function
        // |>(<| .  OR  |>(<| ~
        state
            .mark(m_types.C_FUN_EXPR)
            .resolution(function)
            .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
            .resolution(functionParameters)
            .advance();
        if (nextTokenType == m_types.DOT) {
          state.advance();
        }
        state.mark(m_types.C_FUN_PARAM).resolution(functionParameter);
      } else {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(genericExpression);
      }
    }
  }

  private void parseRParen(@NotNull ParserState state) {
    ParserScope startScope = state.popEndUntilOneOfElementType(m_types.LPAREN);
    if (startScope != null && startScope.isResolution(genericExpression)) {
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
            .resolution(function)
            .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
            .resolution(functionParameters)
            .advance()
            .mark(m_types.C_FUN_PARAM)
            .resolution(functionParameter);
        return;
      }
    }

    state.advance().popEnd();

    if (state.isCurrentResolution(variantDeclaration)) {
      state.popEndUntilResolution(typeBinding);
    } else if (state.isCurrentResolution(annotation)) {
      state.popEnd();
    }
  }

  private void parseEq(@NotNull ParserState state) {
    // Intermediate constructions
    if (state.isCurrentResolution(signatureItem)) {
      state.popEndUntilResolution(signature).popEnd();
    }

    if (state.isCurrentResolution(typeNamed)) {
      // type t |> = <| ...
      state.resolution(typeNamedEq).advance().mark(m_types.C_TYPE_BINDING).resolution(typeBinding);
    } else if (state.isCurrentResolution(let)
        || state.isCurrentResolution(letNamed) /* || state.isCurrentResolution(letNamedAttribute)*/
        || state.isCurrentResolution(letNamedSignature)) {
      state.resolution(letNamedEq).advance().mark(m_types.C_LET_BINDING).resolution(letBinding);
    } else if (state.isCurrentResolution(module)) {
      // module M |> = <| ...
      state.advance().mark(m_types.C_UNKNOWN_EXPR /*C_DUMMY*/).dummy().resolution(moduleBinding);
    } else if (state.is(m_types.C_TAG_PROPERTY)) {
      state
          .resolution(jsxTagPropertyEq)
          .advance()
          .mark(m_types.C_TAG_PROP_VALUE)
          .resolution(jsxTagPropertyValue);
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

    if (state.isCurrentResolution(open)) {
      // It is a module name/path, or maybe a functor call
      // open |>M<| ...
      state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
    } else if (state.isCurrentResolution(include)) {
      // It is a module name/path, or maybe a functor call
      // include |>M<| ...
      state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
    } else if (state.isCurrentResolution(moduleBinding)) {
      // it might be a module functor call
      // module M = |>X<| ( ... )
      state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
    } else if ((state.isCurrentResolution(jsxStartTag) || state.isCurrentResolution(jsxTagClose))
        && state.previousElementType1 == m_types.DOT) {
      // a namespaced custom component
      state.remapCurrentToken(m_types.TAG_NAME);
    } else if (state.isCurrentResolution(variantDeclaration)) {
      // Declaring a variant
      // type t = | |>X<| ..
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
      return;
    } else if (state.isCurrentResolution(patternMatch)) {
      IElementType nextElementType = state.lookAhead(1);
      if (nextElementType != m_types.DOT) {
        // Defining a pattern match
        // switch (c) { | |>X<| .. }
        state
            .remapCurrentToken(m_types.VARIANT_NAME)
            .wrapWith(m_types.C_VARIANT)
            .resolution(patternMatchVariant);

        return;
      }
    } else {
      IElementType nextElementType = state.lookAhead(1);

      if (state.isCurrentResolution(typeBinding)
          && (nextElementType == m_types.PIPE || nextElementType == m_types.LPAREN)) {
        // We are declaring a variant without a pipe before
        // type t = |>X<| | ...
        // type t = |>X<| (...) | ...
        state
            .remapCurrentToken(m_types.VARIANT_NAME)
            .mark(m_types.C_VARIANT_DECL)
            .resolution(variantDeclaration)
            .wrapWith(m_types.C_UPPER_IDENTIFIER);
        return;
      } else if (!state.isCurrentResolution(moduleNamedEq)
          && !state.isCurrentResolution(maybeFunctorCall)) {
        if (nextElementType == m_types.LPAREN) {
          state.remapCurrentToken(m_types.VARIANT_NAME);
          // A variant with a constructor
          if (state.isCurrentResolution(typeNamedEq)) {
            state.mark(m_types.C_VARIANT_DECL).resolution(typeNamedEqVariant);
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
        .resolution(switch_)
        .setStart(inScope)
        .advance()
        .mark(m_types.C_BIN_CONDITION)
        .resolution(binaryCondition);
  }

  private void parseTry(@NotNull ParserState state) {
    endLikeSemi(state);
    state
        .mark(m_types.C_TRY_EXPR)
        .resolution(try_)
        .advance()
        .mark(m_types.C_TRY_BODY)
        .resolution(tryBody);
  }

  private void parseCatch(@NotNull ParserState state) {
    if (state.isCurrentResolution(tryBody)) {
      state.popEnd().resolution(tryBodyWith);
    }
  }

  private void parseArrow(@NotNull ParserState state) {
    if (state.isCurrentResolution(function) || state.isCurrentResolution(functionParameter)) {
      // param(s) |>=><| body
      state.popEndUntilOneOfResolution(function, functionCallParams);
      state.advance().mark(m_types.C_FUN_BODY).resolution(functionBody);
    } else if (state.isCurrentResolution(signature)) {
      state.advance().mark(m_types.C_SIG_ITEM).resolution(signatureItem);
    } else if (state.isCurrentResolution(signatureItem)) {
      state.popEnd();
      if (!state.isCurrentResolution(signatureScope)) {
        state.popEndUntilResolution(signature);
      }
      state.advance().mark(m_types.C_SIG_ITEM).resolution(signatureItem);
    } else if (state.isCurrentResolution(functorNamedEq)
        || state.isCurrentResolution(functorNamedEqColon)
        || state.isCurrentResolution(functorResult)) {
      // module Make = (M) : R |>=><| ...
      if (state.isCurrentResolution(functorResult)) {
        state.popEnd();
      }
      state.advance().mark(m_types.C_FUNCTOR_BINDING).resolution(functorBinding);
    } else if (state.isCurrentResolution(patternMatchVariant)
        || state.isCurrentResolution(patternMatchVariantConstructor)
        || state.isCurrentResolution(patternMatchValue)) {
      // switch ( ... ) { | ... |>=><| ... }
      state.advance().mark(m_types.C_PATTERN_MATCH_BODY).resolution(patternMatchBody).setStart();
    }
  }

  private void endLikeSemi(@NotNull ParserState state) {
    state.popEndUntilScope();
  }
}
