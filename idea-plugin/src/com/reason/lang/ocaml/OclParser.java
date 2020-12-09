package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;

public class OclParser extends CommonParser<OclTypes> {

  public OclParser() {
    super(OclTypes.INSTANCE);
  }

  public static ASTNode parseOcamlNode(
      @NotNull ILazyParseableElementType root, @NotNull ASTNode chameleon) {
    PsiElement parentElement = chameleon.getTreeParent().getPsi();
    Project project = parentElement.getProject();

    PsiBuilder builder =
        PsiBuilderFactory.getInstance()
            .createBuilder(
                project, chameleon, new OclLexer(), root.getLanguage(), chameleon.getText());
    // builder.setDebugMode(true);
    OclParser parser = new OclParser();

    return parser.parse(root, builder).getFirstChildNode();
  }

  @Override
  protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
    IElementType tokenType = null;
    state.previousElementType1 = null;

    int c = current_position_(builder);
    while (true) {
      state.previousElementType2 = state.previousElementType1;
      state.previousElementType1 = tokenType;
      tokenType = builder.getTokenType();
      if (tokenType == null) {
        break;
      }

      if (tokenType == m_types.SEMI) {
        parseSemi(state);
      } else if (tokenType == m_types.IN) {
        parseIn(state);
      } else if (tokenType == m_types.END) { // end (like a })
        parseEnd(state);
      } else if (tokenType == m_types.UNDERSCORE) {
        parseUnderscore(state);
      } else if (tokenType == m_types.RIGHT_ARROW) {
        parseRightArrow(state);
      } else if (tokenType == m_types.PIPE) {
        parsePipe(state);
      } else if (tokenType == m_types.EQ) {
        parseEq(state);
      } else if (tokenType == m_types.OF) {
        parseOf(state);
      } else if (tokenType == m_types.STAR) {
        parseStar(state);
      } else if (tokenType == m_types.COLON) {
        parseColon(state);
      } else if (tokenType == m_types.QUESTION_MARK) {
        parseQuestionMark(state);
      } else if (tokenType == m_types.LIDENT) {
        parseLIdent(state);
      } else if (tokenType == m_types.UIDENT) {
        parseUIdent(state);
      } else if (tokenType == m_types.SIG) {
        parseSig(state);
      } else if (tokenType == m_types.STRUCT) {
        parseStruct(state);
      } else if (tokenType == m_types.BEGIN) {
        parseBegin(state);
      } else if (tokenType == m_types.OBJECT) {
        parseObject(state);
      } else if (tokenType == m_types.IF) {
        parseIf(state);
      } else if (tokenType == m_types.THEN) {
        parseThen(state);
      } else if (tokenType == m_types.ELSE) {
        parseElse(state);
      } else if (tokenType == m_types.MATCH) {
        parseMatch(state);
      } else if (tokenType == m_types.TRY) {
        parseTry(state);
      } else if (tokenType == m_types.WITH) {
        parseWith(state);
      } else if (tokenType == m_types.AND) {
        parseAnd(state);
      } else if (tokenType == m_types.DOT) {
        parseDot(state);
      } else if (tokenType == m_types.FUNCTION) {
        // function is a shortcut for a pattern match
        parseFunction(state);
      } else if (tokenType == m_types.FUN) {
        parseFun(state);
      } else if (tokenType == m_types.ASSERT) {
        parseAssert(state);
      } else if (tokenType == m_types.RAISE) {
        parseRaise(state);
      } else if (tokenType == m_types.COMMA) {
        parseComma(state);
        //      } else if (tokenType == m_types.ARROBASE) {
        //        parseArrobase(state);
      }
      // while ... do ... done
      else if (tokenType == m_types.WHILE) {
        parseWhile(state);
      }
      // do ... done
      else if (tokenType == m_types.DO) {
        parseDo(state);
      } else if (tokenType == m_types.DONE) {
        parseDone(state);
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
      else if (tokenType == m_types.LBRACKET) {
        parseLBracket(state);
      } else if (tokenType == m_types.RBRACKET) {
        parseRBracket(state);
      }
      // < ... >
      else if (tokenType == m_types.LT) {
        parseLt(state);
      } else if (tokenType == m_types.GT) {
        parseGt(state);
      }
      // Starts expression
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
      } else if (tokenType == m_types.CLASS) {
        parseClass(state);
      } else if (tokenType == m_types.LET) {
        parseLet(state);
      } else if (tokenType == m_types.VAL) {
        parseVal(state);
      } else if (tokenType == m_types.METHOD) {
        parseMethod(state);
      } else if (tokenType == m_types.EXCEPTION) {
        parseException(state);
      } else if (tokenType == m_types.DIRECTIVE_IF) {
        parseDirectiveIf(state);
      } else if (tokenType == m_types.DIRECTIVE_ELSE) {
        parseDirectiveElse(/*builder,*/ state);
      } else if (tokenType == m_types.DIRECTIVE_ELIF) {
        parseDirectiveElif(/*builder,*/ state);
      } else if (tokenType == m_types.DIRECTIVE_END || tokenType == m_types.DIRECTIVE_ENDIF) {
        parseDirectiveEnd(/*builder,*/ state);
      }

      if (state.dontMove) {
        state.dontMove = false;
        // revert
        tokenType = state.previousElementType1;
        state.previousElementType1 = state.previousElementType2;
      } else {
        builder.advanceLexer();
      }

      if (!empty_element_parsed_guard_(builder, "oclFile", c)) {
        break;
      }

      c = builder.rawTokenIndex();
    }
  }

  private void parseRaise(@NotNull ParserState state) {
    if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
      state.remapCurrentToken(m_types.LIDENT).wrapWith(m_types.C_LOWER_IDENTIFIER);
    }
  }

  private void parseComma(@NotNull ParserState state) {
    if (state.isPrevious(m_types.C_LET_DECLARATION)) {
      // It must be a deconstruction :: let (a |>,<| b) ...  OR  let a |>,<| b ...
      if (state.isCurrentResolution(genericExpression)) {
        // We need to do it again because lower symbols must be wrapped with identifiers
        ParserScope scope = state.pop();
        if (scope != null) {
          scope.rollbackTo();
          state
              .markScope(m_types.C_DECONSTRUCTION, m_types.LPAREN)
              .advance();
        }
      }
    }
  }

  private void parseLt(@NotNull ParserState state) {
    if (!state.is(m_types.C_BINARY_CONDITION)) {
      // |> < <| .. > ..
      state.markScope(m_types.C_OBJECT, m_types.LT)
          .advance()
          .mark(m_types.C_OBJECT_FIELD);
    }
  }

  private void parseGt(@NotNull ParserState state) {
    if (state.isPrevious(m_types.C_OBJECT)) {
      // < ... |> > <| ..
      if (state.isCurrentResolution(objectFieldNamed)) {
        state.popEnd();
      }
      state.advance();

      if ("Js".equals(state.getTokenText())) {
        // it might be a Js object (same with Js.t at the end)
        state.advance();
        if (state.getTokenType() == m_types.DOT) {
          state.advance();
          if ("t".equals(state.getTokenText())) {
            state.updateCurrentCompositeElementType(m_types.C_JS_OBJECT).advance().complete();
          }
        }
      }

      state.popEnd();
    }
  }

  private void parseWhile(@NotNull ParserState state) {
    state.mark(m_types.C_WHILE).advance().mark(m_types.C_BINARY_CONDITION);
  }

  private void parseDo(@NotNull ParserState state) {
    if (state.is(m_types.C_BINARY_CONDITION) && state.isPrevious(m_types.C_WHILE)) {
      state.popEnd().advance().markScope(m_types.C_DO_LOOP, m_types.DO);
    } else {
      state.markScope(m_types.C_DO_LOOP, m_types.DO);
    }
  }

  private void parseDone(@NotNull ParserState state) {
    state.popEndUntil(m_types.C_DO_LOOP).popEnd();
    if (state.is(m_types.C_WHILE)) {
      state.popEnd();
    }
  }

  private void parseRightArrow(@NotNull ParserState state) {
    // intermediate results
    if (state.is(m_types.C_FUN_PARAM)) {
      state.popEnd();
    }

    if (state.is(m_types.C_SIG_ITEM)) {
      state.popEnd().advance().mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_PATTERN_MATCH_EXPR)) {
      state.advance().mark(m_types.C_PATTERN_MATCH_BODY);
    } else if (state.isCurrentResolution(maybeFunctionParameters)) {
      // fun ... |>-><| ...
      state.complete().popEnd().advance().mark(m_types.C_FUN_BODY);
    }
  }

  private void parseUnderscore(@NotNull ParserState state) {
//    if (state.is(m_types.C_LET_DECLARATION)) {
//      state.resolution(letNamed);
//    }
  }

  private void parseAssert(@NotNull ParserState state) {
    state.mark(m_types.C_ASSERT_STMT);
  }

  private void parseAnd(@NotNull ParserState state) {
    if (state.is(m_types.C_CONSTRAINT)) {
      state.popEnd().advance().mark(m_types.C_CONSTRAINT);
    } else {
      // pop scopes until a known context is found
      ParserScope latestScope = endUntilStartExpression(state);

      if (latestScope != null) {
        if (latestScope.isCompositeType(m_types.C_MODULE_DECLARATION)) {
          state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
        } else if (latestScope.isCompositeType(m_types.C_LET_DECLARATION)) {
          state.mark(m_types.C_LET_DECLARATION).setStart();
        } else if (latestScope.isCompositeType(m_types.C_TYPE_DECLARATION)) {
          state.mark(m_types.C_TYPE_DECLARATION).setStart();
        }
      }
    }
  }

  private void parseDot(@NotNull ParserState state) {
    if (state.is(m_types.C_TYPE_VARIABLE)) {
      state
          .popEnd()
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .mark(m_types.C_SIG_ITEM);
    }
  }

  private @Nullable ParserScope endUntilStartExpression(@NotNull ParserState state) {
    // Remove intermediate constructions until a start expression
    state.popEndUntilStart();
    ParserScope latestScope = state.getLatestScope();
    state.popEnd();

    // Remove nested let
    while (state.is(m_types.C_LET_BINDING)
               || state.is(m_types.C_PATTERN_MATCH_BODY)
               || state.is(m_types.C_FUN_BODY)) {
      state.popEndUntilStart();
      latestScope = state.getLatestScope();
      state.popEnd();
    }

    state.advance();

    return latestScope;
  }

  private void parsePipe(@NotNull ParserState state) {
    // Remove intermediate constructions
    if (state.is(m_types.C_IF_THEN_SCOPE)) {
      state.popEndUntil(m_types.C_IF).popEnd();
    }
    if (state.is(m_types.C_FUN_PARAM) && state.in(m_types.C_VARIANT_CONSTRUCTOR)) {
      state.popEndUntil(m_types.C_VARIANT_DECLARATION);
    }
    if (state.is(m_types.C_PATTERN_MATCH_BODY)) {
      state.popEndUntilOneOfResolution(matchWith, functionMatch);
    }

    if (state.is(m_types.C_TYPE_BINDING)) {
      state.advance().mark(m_types.C_VARIANT_DECLARATION);
    } else if (state.is(m_types.C_VARIANT_DECLARATION)) {
      // type t = | V1 |>|<| ...
      state.popEnd().advance().mark(m_types.C_VARIANT_DECLARATION);
    } else {
      if (state.isCurrentResolution(maybeFunctionParameters)) {
        state.popEnd().resolution(matchWith);
      } else if (state.is(m_types.C_PATTERN_MATCH_EXPR)) {
        // pattern group ::  | X |>|<| Y ...
        state.popEnd();
      }

      // By default, a pattern match
      state.advance().mark(m_types.C_PATTERN_MATCH_EXPR);
    }
  }

  private void parseMatch(@NotNull ParserState state) {
    state.mark(m_types.C_MATCH_EXPR)
        .advance()
        .mark(m_types.C_BINARY_CONDITION);
  }

  private void parseTry(@NotNull ParserState state) {
    state
        .mark(m_types.C_TRY_EXPR)
        .advance()
        .mark(m_types.C_TRY_BODY);
  }

  private void parseWith(@NotNull ParserState state) {
    if (state.is(m_types.C_FUNCTOR_RESULT)) {
      // A functor with constraints
      //  module Make (M : Input) : S |>with<| ...
      state.popEnd().advance().mark(m_types.C_CONSTRAINTS).mark(m_types.C_CONSTRAINT);
    } else if (state.in(m_types.C_MODULE_TYPE)) {
      // A module with a signature and constraints
      //  module G : sig ... end |>with<| ...
      //  module G : X |>with<| ...
      state
          .popEndUntil(m_types.C_MODULE_TYPE)
          .popEnd()
          .advance()
          .mark(m_types.C_CONSTRAINTS)
          .mark(m_types.C_CONSTRAINT);
    } else if (state.isCurrentResolution(maybeFunctorCall)) {
      if (state.isCurrentResolution(maybeFunctorCall)) {
        state.popEnd();
      }
      if (state.is(m_types.C_INCLUDE)) {
        // An include with constraints ::  include M |>with<| ...
        state.mark(m_types.C_CONSTRAINTS).advance().mark(m_types.C_CONSTRAINT);
      }
    } else if (state.is(m_types.C_TRY_BODY)) {
      // A try handler ::  try ... |>with<| ...
      state
          .popEnd()
          .advance()
          .mark(m_types.C_TRY_HANDLERS)
          .mark(m_types.C_TRY_HANDLER);
    } else if (state.is(m_types.C_BINARY_CONDITION)) {
      if (state.isPrevious(m_types.C_MATCH_EXPR)) {
        state.popEnd().resolution(matchWith);
      }
    }
  }

  private void parseIf(@NotNull ParserState state) {
    // |>if<| ...
    state.mark(m_types.C_IF).advance().mark(m_types.C_BINARY_CONDITION);
  }

  private void parseThen(@NotNull ParserState state) {
    if (!state.is(m_types.C_DIRECTIVE)) {
      // if ... |>then<| ...
      state.popEndUntil(m_types.C_IF).advance().mark(m_types.C_IF_THEN_SCOPE);
    }
  }

  private void parseElse(@NotNull ParserState state) {
    // if ... then ... |>else<| ...
    state.popEndUntil(m_types.C_IF).advance().mark(m_types.C_IF_THEN_SCOPE);
  }

  private void parseStruct(@NotNull ParserState state) {
    if (state.isCurrentResolution(moduleBinding) || state.isPreviousResolution(module)) {
      // replace previous fake scope  ::  module X = |>struct<| ...
      state.popCancel().markScope(m_types.C_SCOPED_EXPR, m_types.STRUCT).resolution(moduleBinding);
    } else if (state.isCurrentResolution(functorNamedEq)) {
      // module X (...) = |>struct<| ...
      state.markScope(m_types.C_FUNCTOR_BINDING, m_types.STRUCT);
    } else {
      state.markScope(m_types.C_STRUCT_EXPR, m_types.STRUCT);
    }
  }

  private void parseSig(@NotNull ParserState state) {
    if (state.isCurrentResolution(moduleBinding) && state.is(m_types.C_UNKNOWN_EXPR)) {
      // This is the body of a module type
      // module type X = |>sig<| ...
      state.popCancel().markScope(m_types.C_SCOPED_EXPR, m_types.SIG).resolution(moduleBinding);
    } else if (state.isCurrentResolution(moduleNamedColon)) {
      state.markScope(m_types.C_SIG_EXPR, m_types.SIG);
    } else if (state.is(m_types.C_FUNCTOR_PARAM)) {
      state.markScope(m_types.C_SIG_EXPR, m_types.SIG);
    }
  }

  private void parseSemi(@NotNull ParserState state) {
    if (state.isPrevious(m_types.C_OBJECT)) {
      // SEMI ends the field, and starts a new one
      state.popEnd().advance().mark(m_types.C_OBJECT_FIELD);
    } else if (state.in(m_types.C_RECORD_FIELD)) {
      // SEMI ends the field, and starts a new one
      state.popEndUntil(m_types.C_RECORD_FIELD).popEnd().advance();
      if (state.getTokenType() != m_types.RBRACE) {
        state.mark(m_types.C_RECORD_FIELD);
      }
    } else {
      boolean isImplicitScope = state.isOneOf(m_types.C_FUN_BODY, m_types.C_LET_BINDING);

      // A SEMI operator ends the previous expression
      if (!isImplicitScope && !state.hasScopeToken()) {
        state.popEnd();
        if (state.is(m_types.C_OBJECT)) {
          state.advance().mark(m_types.C_OBJECT_FIELD);
        }
      }
    }
  }

  private void parseIn(@NotNull ParserState state) {
    if (!state.is(m_types.C_FUN_BODY)) {
      if (state.is(m_types.C_TRY_HANDLER)) {
        state.popEndUntil(m_types.C_TRY_EXPR);
      } else if (state.in(m_types.C_LET_DECLARATION)) {
        state.popEndUntil(m_types.C_LET_DECLARATION);
      }

      state.popEnd();
    }
  }

  private void parseBegin(@NotNull ParserState state) {
    state.markScope(m_types.C_SCOPED_EXPR, m_types.BEGIN);
  }

  private void parseObject(@NotNull ParserState state) {
    state.markScope(m_types.C_OBJECT, m_types.OBJECT);
  }

  private void parseEnd(@NotNull ParserState state) {
    ParserScope scope =
        state.popEndUntilOneOfElementType(
            m_types.BEGIN, m_types.SIG, m_types.STRUCT, m_types.OBJECT);

    state.advance().popEnd();

    if (scope != null && scope.isCompositeType(m_types.C_MODULE_TYPE)) {
      IElementType nextToken = state.getTokenType();
      if (nextToken == m_types.WITH) {
        state.advance().mark(m_types.C_CONSTRAINTS).mark(m_types.C_CONSTRAINT);
      }
    }
  }

  private void parseColon(@NotNull ParserState state) {
    if (state.is(m_types.C_MODULE_DECLARATION)) {
      // module M |> : <| ...
      state
          .resolution(moduleNamedColon)
          .advance()
          .mark(m_types.C_MODULE_TYPE);
      IElementType nextToken = state.getTokenType();
      if (nextToken == m_types.LPAREN || nextToken == m_types.SIG) {
        state.updateScopeToken((ORTokenElementType) nextToken);
      }
    } else if (state.is(m_types.C_EXTERNAL_DECLARATION)
                   || state.is(m_types.C_VAL_DECLARATION)
                   || state.is(m_types.C_LET_DECLARATION)) {
      // external x |> : <| ...  OR  val x |> : <| ...  OR  let x |> : <| ...
      state.advance();
      if (state.getTokenType() == m_types.TYPE) {
        // Local type
        state.mark(m_types.C_TYPE_VARIABLE);
      } else {
        state.mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
      }
    } else if (state.is(m_types.C_CLASS_DECLARATION) || state.is(m_types.C_CLASS_METHOD)) {
      // class x |> : <| ...  OR  method |> : <| ...
      state.advance().mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_OBJECT_FIELD)) {
      // < x |> : <| ...
      state.resolution(objectFieldNamed);
    } else if (state.is(m_types.C_RECORD_FIELD)) {
      state.advance()
          .mark(m_types.C_SIG_EXPR)
          .mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_FUN_PARAM)) {
      state.advance().mark(m_types.C_SIG_EXPR).mark(m_types.C_SIG_ITEM);
    } else if (state.is(m_types.C_FUNCTOR_DECLARATION)) {
      state.resolution(functorNamedColon)
          .advance()
          .mark(m_types.C_FUNCTOR_RESULT);
    }
  }

  private void parseQuestionMark(@NotNull ParserState state) {
    if (state.is(m_types.C_FUN_PARAMS)) {
      state.mark(m_types.C_FUN_PARAM);
    } else if (state.is(m_types.C_FUN_PARAM) && !state.hasScopeToken()) {
      // Start of a new optional parameter ::  |>?<| x ...
      state.complete().popEnd().mark(m_types.C_FUN_PARAM);
    } else if (state.is(m_types.C_BINARY_CONDITION)) {
      // ... |>?<| ... : ...
      state.popEnd();
    } else if (!state.in(m_types.C_TERNARY)) {
      // ... |>?<| ... : ...
      /* precedeMark is not working
      state
          .precedeMark(m_types.C_BINARY_CONDITION)
          .precedeScope(m_types.C_TERNARY).popEnd();
      */
    }
  }

  private void parseFunction(@NotNull ParserState state) {
    if (state.is(m_types.C_LET_BINDING)) {
      state.mark(m_types.C_FUN_EXPR)
          .advance()
          .mark(m_types.C_FUN_BODY);
    }

    state.mark(m_types.C_MATCH_EXPR).resolution(functionMatch).advance();
    if (state.getTokenType() != m_types.PIPE) {
      state.mark(m_types.C_PATTERN_MATCH_EXPR);
    }
  }

  private void parseFun(@NotNull ParserState state) {
    if (state.is(m_types.C_LET_BINDING)) {
      state
          .mark(m_types.C_FUN_EXPR)
          .advance()
          .markOptional(m_types.C_FUN_PARAMS)
          .resolution(maybeFunctionParameters);
    }
  }

  private void parseEq(@NotNull ParserState state) {
    // Remove intermediate constructions
    if (state.is(m_types.C_SIG_ITEM) || state.is(m_types.C_FUN_PARAMS)) {
      state.popEnd();
    }
    if (state.is(m_types.C_FUNCTOR_RESULT)) {
      state.popEnd();
    }
    if (state.is(m_types.C_SIG_EXPR)) {
      state.popEnd();
    }

    if (state.is(m_types.C_TYPE_DECLARATION)) {
      state.advance().mark(m_types.C_TYPE_BINDING);
    } else if (state.is(m_types.C_LET_DECLARATION)) {
      // let x |> = <| ...
      state.popEndUntilStart();
      state.advance().mark(m_types.C_LET_BINDING);
    } else if (state.is(m_types.C_MODULE_DECLARATION)) {
      // module M |> = <| ...
      state.advance().mark(m_types.C_UNKNOWN_EXPR /*C_DUMMY*/).resolution(moduleBinding).dummy();
    } else if (state.is(m_types.C_FUNCTOR_DECLARATION) || state.isCurrentResolution(functorNamedColon)) {
      state.resolution(functorNamedEq);
    } else if (state.is(m_types.C_CONSTRAINT) && (state.isGrandPreviousResolution(functorNamedColon) || state.isGrandParent(m_types.C_MODULE_DECLARATION))) {
      IElementType nextElementType = state.lookAhead(1);
      if (nextElementType == m_types.STRUCT) {
        // Functor constraints ::  module M (...) : S with ... |> = <| struct ... end
        if (state.isGrandPreviousResolution(functorNamedColon)) {
          state.popEnd().popEnd().resolution(functorNamedEq);
        } else {
          state.popEndUntil(m_types.C_CONSTRAINTS).popEnd();
        }
      }
    } else if (state.is(m_types.C_FUN_PARAM)) {
      state.popEndUntil(m_types.C_FUN_EXPR).advance().mark(m_types.C_FUN_BODY);
    }
  }

  private void parseOf(@NotNull ParserState state) {
    if (state.is(m_types.C_VARIANT_DECLARATION)) {
      // Variant params :: type t = | Variant «of» ..
      state.advance().mark(m_types.C_VARIANT_CONSTRUCTOR).mark(m_types.C_FUN_PARAM);
    } else if (state.is(m_types.C_MODULE_DECLARATION) && state.previousElementType1 == m_types.TYPE) {
      // extracting a module type ::  module type |>of<| X ...
      state.resolution(moduleTypeExtraction);
    }
  }

  private void parseStar(@NotNull ParserState state) {
    if (state.is(m_types.C_FUN_PARAM) && state.in(m_types.C_VARIANT_CONSTRUCTOR)) {
      // type t = | Variant of x |>*<| y ..
      state.popEnd().advance().mark(m_types.C_FUN_PARAM);
    }
  }

  private void parseLParen(@NotNull ParserState state) {
    if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
      // Overloading an operator ::  external |>(<| ... ) = ...
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(genericExpression);
    } else if (state.isCurrentResolution(maybeFunctorCall)) {
      // Yes, it is a functor call ::  module M = X |>(<| ... )
      state.complete()
          .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
          .advance()
          .mark(m_types.C_FUN_PARAM);
    } else if (state.previousElementType2 == m_types.UIDENT
                   && state.previousElementType1 == m_types.DOT) {
      // Detecting a local open ::  M1.M2. |>(<| ... )
      state.markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN);
    } else if (state.is(m_types.C_CLASS_DECLARATION)) {
      state.markScope(m_types.C_CLASS_CONSTR, m_types.LPAREN);
    } else if (state.is(m_types.C_FUN_PARAMS)) {
      // Start of the first parameter ::  let x |>(<| ...
      state.markScope(m_types.C_FUN_PARAM, m_types.LPAREN);
    } else if (state.is(m_types.C_FUN_PARAM)
                   && !state.hasScopeToken()
                   && state.previousElementType1 != m_types.QUESTION_MARK) {
      // Start of a new parameter
      //    let f xxx |>(<| ..tuple ) = ..
      state.popEnd().mark(m_types.C_FUN_PARAM).markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
    } else if (state.is(m_types.C_MODULE_DECLARATION)) {
      // This is a functor ::  module Make |>(<| ... )
      state.updateCurrentCompositeElementType(m_types.C_FUNCTOR_DECLARATION)
          .markScope(m_types.C_FUNCTOR_PARAMS, m_types.LPAREN)
          .advance()
          .mark(m_types.C_FUNCTOR_PARAM);
    } else if (state.is(m_types.C_MODULE_TYPE)) {
      // module M : |>(<| ... )
      state
          .popCancel()
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(genericExpression)
          .dummy()
          .advance()
          .mark(m_types.C_MODULE_TYPE);
    } else {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(genericExpression);
    }
  }

  private void parseRParen(@NotNull ParserState state) {
    ParserScope scope = state.popEndUntilScopeToken(m_types.LPAREN);
    if (scope == null) {
      return;
    }

    state.advance();

    int scopeLength = scope.getLength();
    if (scopeLength == 3 && state.isPrevious(m_types.C_LET_DECLARATION)) {
      // unit ::  let ()
      scope.updateCompositeElementType(m_types.C_UNIT);
    }

    IElementType nextToken = state.getTokenType();
    if (nextToken == m_types.LT || nextToken == m_types.LT_OR_EQUAL) {
      // are we in a binary op ?
      state.precedeScope(m_types.C_BINARY_CONDITION);
    } else if (nextToken == m_types.QUESTION_MARK && !state.isPrevious(m_types.C_TERNARY)) {
      // ( ... |>)<| ? ...
      state
          .updateCurrentCompositeElementType(m_types.C_BINARY_CONDITION)
          .precedeScope(m_types.C_TERNARY);
    }

    state.popEnd();

//    if (state.is(m_types.C_LET_DECLARATION)) {
    // we are processing an infix operator, unit or a deconstruction (tuple)
    //  let ( ... |>)<| = ...
//      state.resolution(letNamed);
//    }
  }

  private void parseLBrace(@NotNull ParserState state) {
    if (state.is(m_types.C_FUN_PARAMS)) {
      // let fn |>{<| ... } = ...
      state.mark(m_types.C_FUN_PARAM);
    }

    state
        .markScope(m_types.C_RECORD_EXPR, m_types.LBRACE)
        .advance()
        .mark(m_types.C_RECORD_FIELD);
  }

  private void parseRBrace(@NotNull ParserState state) {
    ParserScope scope = state.popEndUntilScopeToken(m_types.LBRACE);
    state.advance();

    if (scope != null) {
      state.popEnd();
    }
  }

  private void parseLBracket(@NotNull ParserState state) {
    IElementType nextElementType = state.rawLookup(1);
    if (nextElementType == m_types.ARROBASE
            || nextElementType == m_types.ARROBASE_2
            || nextElementType == m_types.ARROBASE_3) {
      // |>[ <|@?? ...
      if (nextElementType == m_types.ARROBASE_3) {
        // floating attribute
        endLikeSemi(state);
      } else if (state.isOneOf(m_types.C_FUN_BODY, m_types.C_FUN_EXPR)) {
        // block attribute inside a function
        state.popEndUntil(m_types.C_FUN_EXPR).popEnd();
      }
      state.markScope(m_types.C_ANNOTATION, m_types.LBRACKET);
    } else {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
    }
  }

  private void parseRBracket(@NotNull ParserState state) {
    state.popEndUntilScopeToken(m_types.LBRACKET);
    state.advance().popEnd();
  }

  private void parseLIdent(@NotNull ParserState state) {
    if (state.is(m_types.C_FUN_PARAMS)) {
      state.mark(m_types.C_FUN_PARAM);
    } else if (state.is(m_types.C_FUN_PARAM)
                   && !state.hasScopeToken()
                   && state.previousElementType1 != m_types.OF
                   && state.previousElementType1 != m_types.STAR) {
      // Start of a new parameter
      //    .. ( xxx |>yyy<| ) ..
      state.complete().popEnd().mark(m_types.C_FUN_PARAM);
    }

    if (state.is(m_types.C_LET_DECLARATION)) {
      // let |>x<| ...
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
      IElementType nextToken = state.getTokenType();
      if (nextToken == m_types.COMMA) {
        // A deconstruction without parenthesis
        // TODO
      } else if (nextToken != m_types.EQ && nextToken != m_types.COLON) {
        // This is a function, we need to create the let binding now, to be in sync with reason
        //  let |>x<| y z = ...  vs    let x = y z => ...
        state.mark(m_types.C_LET_BINDING).mark(m_types.C_FUN_EXPR).mark(m_types.C_FUN_PARAMS);
      }
    } else if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_TYPE_DECLARATION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_VAL_DECLARATION) || state.is(m_types.C_CLASS_METHOD)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_CLASS_DECLARATION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_DECONSTRUCTION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_ANNOTATION)) {
      // [@ |>x.y<| ... ]
      state.mark(m_types.C_MACRO_NAME);
      state.advance();
      while (state.getTokenType() == m_types.DOT) {
        state.advance();
        if (state.getTokenType() == m_types.LIDENT) {
          state.advance();
        }
      }
      state.popEnd();
    } else {
      if ((state.is(m_types.C_FUN_PARAM) && !state.isPrevious(m_types.C_FUN_CALL_PARAMS))) {
        state.wrapWith(m_types.C_LOWER_IDENTIFIER);
      } else {
        state.wrapWith(m_types.C_LOWER_SYMBOL);
      }
    }
  }

  private void parseUIdent(@NotNull ParserState state) {
    if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
      return;
    }

    if (state.isCurrentResolution(module)) {
      // Module declaration  ::  module |>M<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
    } else if (state.is(m_types.C_OPEN) || state.is(m_types.C_INCLUDE)) {
      // It is a module name/path, or might be a functor call  ::  open/include |>M<| ...
      state
          .markOptional(m_types.C_FUNCTOR_CALL)
          .resolution(maybeFunctorCall)
          .wrapWith(m_types.C_UPPER_SYMBOL);

      IElementType nextToken = state.getTokenType();
      if (nextToken != m_types.DOT && nextToken != m_types.LPAREN && nextToken != m_types.WITH) {
        // Not a path, nor a functor, must close that open
        state.popCancel();
        state.popEnd();
      }
    } else if (state.is(m_types.C_VARIANT_DECLARATION)) {
      // Declaring a variant  ::  type t = | |>X<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
    } else if (state.is(m_types.C_EXCEPTION_DECLARATION)) {
      // Declaring an exception  ::  exception |>X<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
    } else {
      if (state.is(m_types.C_TYPE_BINDING)) {
        // Might be a variant declaration without a pipe
        IElementType nextToken = state.lookAhead(1);
        if (nextToken == m_types.OF || nextToken == m_types.PIPE) {
          // type t = |>X<| | ..   or   type t = |>X<| of ..
          if (state.previousElementType1 == m_types.PIPE) {
            state.advance();
          }
          state.mark(m_types.C_VARIANT_DECLARATION).wrapWith(m_types.C_UPPER_IDENTIFIER);
          return;
        }
      } else if (state.isCurrentResolution(moduleBinding)) {
        // It might be a functor call, or just an alias ::  module M = |>X<| ( ... )
        state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
      } else {
        IElementType nextToken = state.lookAhead(1);
        if (((state.is(m_types.C_PATTERN_MATCH_EXPR) || state.is(m_types.C_LET_BINDING)))
                && nextToken != m_types.DOT) {
          // Pattern matching a variant or using it
          // match c with | |>X<| ... / let x = |>X<| ...
          state.remapCurrentToken(m_types.VARIANT_NAME).wrapWith(m_types.C_VARIANT);
          return;
        }
      }

      state.wrapWith(m_types.C_UPPER_SYMBOL);
    }
  }

  private void parseOpen(@NotNull ParserState state) {
    if (state.is(m_types.C_LET_DECLARATION)) {
      // let open X (coq/indtypes.ml)
      state.updateCurrentCompositeElementType(m_types.C_OPEN);
    } else {
      state.popEndUntilScope();
      state.mark(m_types.C_OPEN);
    }
  }

  private void parseInclude(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_INCLUDE).setStart();
  }

  private void parseExternal(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_EXTERNAL_DECLARATION).setStart();
  }

  private void parseType(@NotNull ParserState state) {
    if (state.isCurrentResolution(module)) {
      // module |>type<| M = ...
    } else if (state.is(m_types.C_TYPE_VARIABLE)) {
      // let x : |>type<| ...
    } else if (state.is(m_types.C_CLASS_DECLARATION)) {
      // class |>type<| ...
    } else if (state.is(m_types.C_CONSTRAINT)
                   && (state.previousElementType1 == m_types.WITH
                           || state.previousElementType1 == m_types.AND)) {
      // module M : X with |>type<| t ...
      // include X with |>type<| t ...
      // ?
    } else {
      state.popEndUntilScope();
      state.mark(m_types.C_TYPE_DECLARATION).setStart();
    }
  }

  private void parseException(@NotNull ParserState state) {
    if (state.previousElementType1 != m_types.PIPE) {
      state.popEndUntilScope();
      state.mark(m_types.C_EXCEPTION_DECLARATION);
    }
  }

  private void parseDirectiveIf(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_DIRECTIVE).setStart();
  }

  private void parseDirectiveElse(@NotNull ParserState state) {
    state.popEndUntil(m_types.C_DIRECTIVE);
  }

  private void parseDirectiveElif(@NotNull ParserState state) {
    state.popEndUntil(m_types.C_DIRECTIVE);
  }

  private void parseDirectiveEnd(@NotNull ParserState state) {
    state.popEndUntil(m_types.C_DIRECTIVE);
    if (state.is(m_types.C_DIRECTIVE)) {
      state.advance().popEnd();
    }
  }

  private void parseVal(@NotNull ParserState state) {
    endLikeSemi(state);
    state
        .mark(state.is(m_types.C_OBJECT) ? m_types.C_CLASS_FIELD : m_types.C_VAL_DECLARATION)
        .setStart();
  }

  private void parseMethod(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_CLASS_METHOD).setStart();
  }

  private void parseLet(@NotNull ParserState state) {
    endLikeSemi(state); // state.popEndUntilScope();
    state.mark(m_types.C_LET_DECLARATION).setStart();
  }

  private void parseModule(@NotNull ParserState state) {
    if (state.is(m_types.C_LET_DECLARATION)) {
      state.resolution(module).updateCurrentCompositeElementType(m_types.C_MODULE_DECLARATION);
    } else if (!state.is(m_types.C_MACRO_NAME)) {
      if (!state.is(m_types.C_MODULE_TYPE)) {
        state.popEndUntilScope();
      }
      state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
    }
  }

  private void parseClass(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_CLASS_DECLARATION).setStart();
  }

  private void endLikeSemi(@NotNull ParserState state) {
    if (state.previousElementType1 != m_types.EQ
            && state.previousElementType1 != m_types.RIGHT_ARROW
            && state.previousElementType1 != m_types.TRY
            && state.previousElementType1 != m_types.SEMI
            && state.previousElementType1 != m_types.THEN
            && state.previousElementType1 != m_types.ELSE
            && state.previousElementType1 != m_types.IN
            && state.previousElementType1 != m_types.LPAREN
            && state.previousElementType1 != m_types.DO
            && state.previousElementType1 != m_types.STRUCT
            && state.previousElementType1 != m_types.SIG
            && state.previousElementType1 != m_types.COLON) {
      ParserScope parserScope = state.getLatestScope();
      while (parserScope != null && !parserScope.hasScope()) {
        state.popEnd();
        parserScope = state.getLatestScope();
      }
    }
  }
}
