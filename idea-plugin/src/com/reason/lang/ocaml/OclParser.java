package com.reason.lang.ocaml;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;
import com.reason.lang.core.type.ORTokenElementType;
import org.jetbrains.annotations.NotNull;

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
      } else if (tokenType == m_types.ARROBASE) {
        parseArrobase(state);
      } else if (tokenType == m_types.AND) {
        parseAnd(state);
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
      // [> ... ]
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
    if (state.isCurrentResolution(external)) {
      state
          .remapCurrentToken(m_types.LIDENT)
          .wrapWith(m_types.C_LOWER_IDENTIFIER)
          .resolution(externalNamed);
    }
  }

  private void parseComma(@NotNull ParserState state) {
    if (state.isCurrentResolution(genericExpression) && state.isPreviousResolution(let)) {
      // It must be a deconstruction ::  let ( a |>,<| b ) = ...
      // We need to do it again because lower symbols must be wrapped with identifiers
      ParserScope scope = state.pop();
      if (scope != null) {
        scope.rollbackTo();
        state
            .markScope(m_types.C_DECONSTRUCTION, m_types.LPAREN)
            .resolution(deconstruction)
            .advance();
      }
    }
  }

  private void parseLt(@NotNull ParserState state) {
    if (!state.isCurrentResolution(binaryCondition)) {
      // |> < <| .. > ..
      state
          .markScope(m_types.C_OBJECT, m_types.LT)
          .resolution(object)
          .advance()
          .mark(m_types.C_OBJECT_FIELD)
          .resolution(objectField);
    }
  }

  private void parseGt(@NotNull ParserState state) {
    if (state.isPreviousResolution(object)) {
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
    state
        .mark(m_types.C_WHILE)
        .resolution(whileLoop)
        .advance()
        .mark(m_types.C_WHILE_CONDITION)
        .resolution(binaryCondition);
  }

  private void parseDo(@NotNull ParserState state) {
    if (state.isCurrentResolution(binaryCondition) && state.isPreviousResolution(whileLoop)) {
      state.popEnd().advance().markScope(m_types.C_SCOPED_EXPR, m_types.DO).resolution(doLoop);
    } else {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.DO).resolution(doLoop);
    }
  }

  private void parseDone(@NotNull ParserState state) {
    state.popEndUntilResolution(doLoop);
    if (state.isPreviousResolution(whileLoop)) {
      state.popEnd();
    }
    state.advance().popEnd();
  }

  private void parseRightArrow(@NotNull ParserState state) {
    if (state.isCurrentResolution(signatureItem)) {
      state.popEnd().advance().mark(m_types.C_SIG_ITEM).resolution(signatureItem);
    } else if (state.isCurrentResolution(patternMatch)) {
      state.advance().mark(m_types.C_PATTERN_MATCH_BODY).resolution(patternMatchBody);
    } else if (state.isCurrentResolution(maybeFunctionParameters)) {
      // fun ... |>-><| ...
      state
          .resolution(functionParameters)
          .complete()
          .popEnd()
          .advance()
          .mark(m_types.C_FUN_BODY)
          .resolution(functionBody);
    }
  }

  private void parseUnderscore(@NotNull ParserState state) {
    if (state.isCurrentResolution(let)) {
      state.resolution(letNamed);
    }
  }

  private void parseAssert(@NotNull ParserState state) {
    state.mark(m_types.C_ASSERT_STMT).resolution(assert_);
  }

  private void parseAnd(@NotNull ParserState state) {
    if (state.isCurrentResolution(
        constraint) /* || state.isCurrentResolution(includeConstraint)*/) {
      state.popEnd().advance().mark(m_types.C_CONSTRAINT).resolution(constraint);
    } else {
      // pop scopes until a known context is found
      endUntilStartExpression(state);
    }
  }

  private void endUntilStartExpression(@NotNull ParserState state) {
    // Remove intermediate constructions until a start expression
    state.popEndUntilStart();
    ParserScope latestScope = state.getLatestScope();
    state.popEnd();

    // Remove nested let
    while (state.isCurrentResolution(letBinding)) {
      state.popEnd();
      latestScope = state.getLatestScope();
      state.popEnd();
    }

    state.advance();

    if (latestScope != null) {
      if (latestScope.isCompositeType(m_types.C_MODULE_DECLARATION)) {
        state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
      } else if (latestScope.isCompositeType(m_types.C_LET_DECLARATION)) {
        state.mark(m_types.C_LET_DECLARATION).resolution(let).setStart();
      } else if (latestScope.isCompositeType(m_types.C_TYPE_DECLARATION)) {
        state.mark(m_types.C_TYPE_DECLARATION).resolution(type).setStart();
      }
    }
  }

  private void parsePipe(@NotNull ParserState state) {
    // Remove intermediate constructions
    if (state.isCurrentResolution(ifElseStatement)) {
      state.popEndUntilResolution(if_).popEnd();
    }
    if (state.isCurrentResolution(variantConstructorParameter)) {
      state.popEndUntilResolution(variantDeclaration);
    }
    if (state.isCurrentResolution(patternMatchBody)) {
      state.popEndUntilOneOfResolution(matchWith, functionMatch);
    }

    if (state.isCurrentResolution(typeBinding)) {
      state.advance().mark(m_types.C_VARIANT_DECL).resolution(variantDeclaration);
    } else if (state.isCurrentResolution(variantDeclaration)) {
      // type t = | V1 |>|<| ...
      state.popEnd().advance().mark(m_types.C_VARIANT_DECL).resolution(variantDeclaration);
    } else {
      // By default, a pattern match
      if (state.isCurrentResolution(maybeFunctionParameters)) {
        state.popEnd().resolution(matchWith);
      }
      state.advance().mark(m_types.C_PATTERN_MATCH_EXPR).resolution(patternMatch);
    }
  }

  private void parseMatch(@NotNull ParserState state) {
    state
        .mark(m_types.C_MATCH_EXPR)
        .resolution(match)
        .advance()
        .mark(m_types.C_BIN_CONDITION)
        .resolution(binaryCondition);
  }

  private void parseTry(@NotNull ParserState state) {
    state
        .mark(m_types.C_TRY_EXPR)
        .resolution(try_)
        .advance()
        .mark(m_types.C_TRY_BODY)
        .resolution(tryBody);
  }

  private void parseWith(@NotNull ParserState state) {
    if (state.isCurrentResolution(functorResult)) {
      // A functor with constraints
      //  module Make (M : Input) : S |>with<| ...
      state
          .popEnd()
          .advance()
          .mark(m_types.C_CONSTRAINTS)
          .resolution(constraints)
          .mark(m_types.C_CONSTRAINT)
          .resolution(constraint);
    } else if (state.in(m_types.C_MODULE_TYPE)) {
      // A module with a signature and constraints
      //  module G : sig ... end |>with<| ...
      //  module G : X |>with<| ...
      state
          .popEndUntil(m_types.C_MODULE_TYPE)
          .popEnd()
          .advance()
          .mark(m_types.C_CONSTRAINTS)
          .resolution(constraints)
          .mark(m_types.C_CONSTRAINT)
          .resolution(constraint);
    } else if (state.isPreviousResolution(include)) {
      // An include with constraints ::  include M |>with<| ...
      if (state.isCurrentResolution(maybeFunctorCall)) {
        state.popEnd();
      }
      state
          .mark(m_types.C_CONSTRAINTS)
          .resolution(constraints)
          .advance()
          .mark(m_types.C_CONSTRAINT)
          .resolution(constraint);
    } else if (state.isCurrentResolution(tryBody)) {
      // A try handler
      //   try ... |>with<| ...
      state
          .popEnd()
          .advance()
          .mark(m_types.C_TRY_HANDLERS)
          .resolution(tryBodyWith)
          .mark(m_types.C_TRY_HANDLER)
          .resolution(tryBodyWithHandler);
    } else if (state.isCurrentResolution(binaryCondition)) {
      if (state.isPreviousResolution(match)) {
        state.popEnd().resolution(matchWith);
      }
    }
  }

  private void parseIf(@NotNull ParserState state) {
    state
        .mark(m_types.C_IF_STMT)
        .resolution(if_)
        .advance()
        .mark(m_types.C_BIN_CONDITION)
        .resolution(binaryCondition);
  }

  private void parseThen(@NotNull ParserState state) {
    if (!state.isCurrentResolution(directive)) {
      state
          .popEndUntilResolution(if_)
          .advance()
          .mark(m_types.C_SCOPED_EXPR)
          .resolution(ifThenStatement);
    }
  }

  private void parseElse(@NotNull ParserState state) {
    state
        .popEndUntilResolution(if_)
        .advance()
        .mark(m_types.C_SCOPED_EXPR)
        .resolution(ifElseStatement);
  }

  private void parseStruct(@NotNull ParserState state) {
    if (state.isCurrentResolution(moduleBinding) || state.isPreviousResolution(module)) {
      // replace previous fake scope  ::  module X = |>struct<| ...
      state.popCancel().markScope(m_types.C_SCOPED_EXPR, m_types.STRUCT).resolution(moduleBinding);
    } else if (state.isCurrentResolution(functorNamedEq)) {
      // module X (...) = |>struct<| ...
      state.markScope(m_types.C_FUNCTOR_BINDING, m_types.STRUCT).resolution(functorBinding);
    } else {
      state.markScope(m_types.C_STRUCT_EXPR, m_types.STRUCT).resolution(scope);
    }
  }

  private void parseSig(@NotNull ParserState state) {
    if (state.isCurrentResolution(moduleBinding) && state.is(m_types.C_UNKNOWN_EXPR)) {
      // This is the body of a module type
      // module type X = |>sig<| ...
      state.popCancel().markScope(m_types.C_SCOPED_EXPR, m_types.SIG).resolution(moduleBinding);
    } else if (state.isCurrentResolution(moduleNamedColon)) {
      state
          .resolution(moduleNamedSignature)
          .markScope(m_types.C_SIG_EXPR, m_types.SIG)
          .resolution(signature);
    } else if (state.isCurrentResolution(functorParamColon)) {
      state
          .resolution(functorParamColonSignature)
          .markScope(m_types.C_SIG_EXPR, m_types.SIG)
          .resolution(functorParamColonSignature);
    }
  }

  private void parseSemi(@NotNull ParserState state) {
    if (state.isCurrentResolution(recordField)) {
      // SEMI ends the field, and starts a new one
      state.popEnd().advance();
      if (state.getTokenType() != m_types.RBRACE) {
        state.mark(m_types.C_RECORD_FIELD).resolution(recordField);
      }
    } else if (state.isPreviousResolution(object)) {
      // SEMI ends the field, and starts a new one
      state.popEnd().advance().mark(m_types.C_OBJECT_FIELD).resolution(objectField);
    } else {
      boolean isImplicitScope = state.isCurrentResolution(functionBody);

      // A SEMI operator ends the previous expression
      if (!isImplicitScope && !state.isInScopeExpression()) {
        state.popEnd();
        if (state.isCurrentResolution(object)) {
          state.advance().mark(m_types.C_OBJECT_FIELD).resolution(objectField);
        }
      }
    }
  }

  private void parseIn(@NotNull ParserState state) {
    if (!state.is(m_types.C_FUN_BODY)) {
      if (state.is(m_types.C_TRY_HANDLER)) {
        state.popEndUntilResolution(try_);
      } else if (state.in(m_types.C_LET_DECLARATION)) {
        state.popEndUntil(m_types.C_LET_DECLARATION);
      }

      state.popEnd();
    }
  }

  private void parseBegin(@NotNull ParserState state) {
    state.markScope(m_types.C_SCOPED_EXPR, m_types.BEGIN).resolution(beginScope);
  }

  private void parseObject(@NotNull ParserState state) {
    state.markScope(m_types.C_OBJECT, m_types.OBJECT).resolution(clazzBody);
  }

  private void parseEnd(@NotNull ParserState state) {
    ParserScope scope =
        state.popEndUntilOneOfElementType(
            m_types.BEGIN, m_types.SIG, m_types.STRUCT, m_types.OBJECT);

    state.advance().popEnd();

    if (scope != null && scope.isCompositeType(m_types.C_MODULE_TYPE)) {
      IElementType nextToken = state.getTokenType();
      if (nextToken == m_types.WITH) {
        state
            .advance()
            .mark(m_types.C_CONSTRAINTS)
            .resolution(constraints)
            .mark(m_types.C_CONSTRAINT)
            .resolution(constraint);
      }
    }
  }

  private void parseColon(@NotNull ParserState state) {
    if (state.isCurrentResolution(moduleNamed)) {
      // module M |> : <| ...
      state
          .resolution(moduleNamedColon)
          .advance()
          .mark(m_types.C_MODULE_TYPE)
          .resolution(moduleType);
      IElementType nextToken = state.getTokenType();
      if (nextToken == m_types.LPAREN || nextToken == m_types.SIG) {
        state.updateScopeToken((ORTokenElementType) nextToken);
      }
    } else if (state.isCurrentResolution(externalNamed)
        || state.isCurrentResolution(valNamed)
        || state.isCurrentResolution(letNamed)
        || state.is(m_types.C_CLASS_DECLARATION)) {
      // external x |> : <| ...
      // val x |> : <| ...
      // let x |> : <| ...
      // class x |> : <| ...
      state
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .resolution(signature)
          .mark(m_types.C_SIG_ITEM)
          .resolution(signatureItem);
    } else if (state.isCurrentResolution(objectField)) {
      // < x |> : <| ...
      state.resolution(objectFieldNamed);
    } else if (state.isCurrentResolution(functionParameter)) {
      state
          .resolution(functionParameterNamed)
          .advance()
          .mark(m_types.C_SIG_EXPR)
          .resolution(signature)
          .mark(m_types.C_SIG_ITEM)
          .resolution(signatureItem);
    } else if (state.isCurrentResolution(functorNamed)) {
      state
          .resolution(functorNamedColon)
          .advance()
          .mark(m_types.C_FUNCTOR_RESULT)
          .resolution(functorResult);
    } else if (state.isCurrentResolution(functorParam)) {
      state.resolution(functorParamColon);
    }
  }

  private void parseQuestionMark(@NotNull ParserState state) {
    if (state.is(m_types.C_FUN_PARAMS)) {
      state.mark(m_types.C_FUN_PARAM).resolution(functionParameter);
    } else if (state.isCurrentResolution(functionParameter) && !state.isInScopeExpression()) {
      // Start of a new optional parameter ::  |>?<| x ...
      state.complete().popEnd().mark(m_types.C_FUN_PARAM).resolution(functionParameter);
    }
  }

  private void parseFunction(@NotNull ParserState state) {
    state.mark(m_types.C_MATCH_EXPR).resolution(functionMatch).advance();
    if (state.getTokenType() != m_types.PIPE) {
      state.mark(m_types.C_PATTERN_MATCH_EXPR).resolution(patternMatch);
    }
  }

  private void parseFun(@NotNull ParserState state) {
    if (state.isCurrentResolution(letBinding)) {
      state
          .mark(m_types.C_FUN_EXPR)
          .resolution(function)
          .advance()
          .markOptional(m_types.C_FUN_PARAMS)
          .resolution(maybeFunctionParameters);
    }
  }

  private void parseEq(@NotNull ParserState state) {
    // Remove intermediate constructions
    if (state.isCurrentResolution(type)) {
      state.resolution(typeNamed);
    }
    if (state.isCurrentResolution(signatureItem) || state.isCurrentResolution(functionParameters)) {
      state.popEnd();
    }
    if (state.isCurrentResolution(functorResult)) {
      state.popEnd();
    }

    if (state.isCurrentResolution(signature)) {
      state.popEnd();
    } else if (state.isCurrentResolution(typeNamed)) {
      state.resolution(typeNamedEq).advance().mark(m_types.C_TYPE_BINDING).resolution(typeBinding);
    } else if (state.isCurrentResolution(letNamed)
        || state.isCurrentResolution(letNamedSignature)) {
      state.popEndUntilStart();
      state.resolution(letNamedEq).advance().mark(m_types.C_LET_BINDING).resolution(letBinding);
    } else if (state.is(m_types.C_MODULE_DECLARATION)) {
      // module M |> = <| ...
      state.advance().mark(m_types.C_UNKNOWN_EXPR /*C_DUMMY*/).resolution(moduleBinding).dummy();
    } else if (state.isCurrentResolution(clazzNamed)) {
      state.resolution(clazzNamedEq);
    } else if (state.isCurrentResolution(functorNamed)
        || state.isCurrentResolution(functorNamedColon)) {
      state.resolution(functorNamedEq);
    } else if (state.isCurrentResolution(constraint)
        && (state.isGrandPreviousResolution(functorNamedColon)
            || state.isGrandParent(m_types.C_MODULE_DECLARATION))) {
      IElementType nextElementType = state.lookAhead(1);
      if (nextElementType == m_types.STRUCT) {
        // Functor constraints ::  module M (...) : S with ... |> = <| struct ... end
        if (state.isGrandPreviousResolution(functorNamedColon)) {
          state.popEnd().popEnd().resolution(functorNamedEq);
        } else {
          state.popEndUntil(m_types.C_CONSTRAINTS).popEnd().resolution(moduleNamedEq);
        }
      }
    } else if (state.isCurrentResolution(functionParameter) /*&& !state.isInScopeExpression()*/) {
      state
          .popEndUntilResolution(function)
          .advance()
          .mark(m_types.C_FUN_BODY)
          .resolution(functionBody);
    }
  }

  private void parseOf(@NotNull ParserState state) {
    if (state.isCurrentResolution(variantDeclaration)) {
      // Variant params :: type t = | Variant «of» ..
      state
          .mark(m_types.C_FUN_PARAMS)
          .resolution(variantConstructorParameters)
          .advance()
          .mark(m_types.C_FUN_PARAM)
          .resolution(variantConstructorParameter);
    }
  }

  private void parseStar(@NotNull ParserState state) {
    if (state.isCurrentResolution(variantConstructorParameter)) {
      // type t = | Variant of x |>*<| y ..
      state.popEnd().advance().mark(m_types.C_FUN_PARAM).resolution(variantConstructorParameter);
    }
  }

  private void parseArrobase(@NotNull ParserState state) {}

  private void parseLParen(@NotNull ParserState state) {
    if (state.isCurrentResolution(external)) {
      // Overloading an operator
      //   external |>(<| ... ) = ...
      state
          .resolution(externalNamed)
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(genericExpression);
    } else if (state.isCurrentResolution(maybeFunctorCall)) {
      // Yes, it is a functor call
      //  module M = X |>(<| ... )
      state
          .resolution(functorCall)
          .complete()
          .markScope(m_types.C_FUN_PARAMS, m_types.LPAREN)
          .resolution(functionParameters)
          .advance()
          .mark(m_types.C_FUN_PARAM)
          .resolution(functionParameter);
    } else if (state.previousElementType2 == m_types.UIDENT
        && state.previousElementType1 == m_types.DOT) {
      // Detecting a local open
      //   M1.M2. |>(<| ... )
      state.markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN).resolution(localOpenScope);
    } else if (state.isCurrentResolution(clazzNamed)) {
      state.markScope(m_types.C_CLASS_CONSTR, m_types.LPAREN).resolution(clazzConstructor);
    } else if (state.isCurrentResolution(functionParameters)) {
      // Start of the first parameter
      //     let x |>(<| ...
      state.markScope(m_types.C_FUN_PARAM, m_types.LPAREN).resolution(functionParameter);
    } else if (state.isCurrentResolution(functionParameter)
        && !state.isInScopeExpression()
        && state.previousElementType1 != m_types.QUESTION_MARK) {
      // Start of a new parameter
      //    let f xxx |>(<| ..tuple ) = ..
      state
          .popEnd()
          .mark(m_types.C_FUN_PARAM)
          .resolution(functionParameter)
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(functionParameter);
    } else if (state.isCurrentResolution(moduleNamed)) {
      // This is a functor ::  module Make |>(<| ... )
      state
          .resolution(functorNamed)
          .updateCurrentCompositeElementType(m_types.C_FUNCTOR)
          .markScope(m_types.C_FUNCTOR_PARAMS, m_types.LPAREN)
          .resolution(functorParams)
          .advance()
          .mark(m_types.C_FUNCTOR_PARAM)
          .resolution(functorParam);
    } else if (state.is(m_types.C_MODULE_TYPE)) {
      // module M : |>(<| ... )
      state
          .popCancel()
          .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
          .resolution(genericExpression)
          .dummy()
          .advance()
          .mark(m_types.C_MODULE_TYPE)
          .resolution(moduleNamedWithType);
    } else {
      state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).resolution(genericExpression);
    }
  }

  private void parseRParen(@NotNull ParserState state) {
    ParserScope scope = state.popEndUntilScopeToken(m_types.LPAREN);
    state.advance();
    if (scope != null) {
      //    scope.complete();
      state.popEnd();
    }

    if (state.isCurrentResolution(let)) {
      // we are processing an infix operator or a deconstruction (tuple)
      //  let ( ... |>)<| = ...
      state.resolution(letNamed);
    }
  }

  private void parseLBrace(@NotNull ParserState state) {
    if (state.isCurrentResolution(functionParameters)) {
      // let fn |>{<| ... } = ...
      state.mark(m_types.C_FUN_PARAM).resolution(functionParameter);
    }

    state
        .markScope(m_types.C_RECORD_EXPR, m_types.LBRACE)
        .resolution(recordBinding)
        .advance()
        .mark(m_types.C_RECORD_FIELD)
        .resolution(recordField);
  }

  private void parseRBrace(@NotNull ParserState state) {
    ParserScope scope = state.popEndUntilScopeToken(m_types.LBRACE);
    state.advance();

    if (scope != null) {
      state.popEnd();
    }
  }

  private void parseLBracket(@NotNull ParserState state) {
    state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET).resolution(bracket);
  }

  private void parseBracketGt(@NotNull ParserState state) {}

  private void parseRBracket(@NotNull ParserState state) {
    state.popEndUntilScopeToken(m_types.LBRACKET);
    state.advance().popEnd();
  }

  private void parseLIdent(@NotNull ParserState state) {
    if (state.isCurrentResolution(functionParameters)) {
      state.mark(m_types.C_FUN_PARAM).resolution(functionParameter);
    } else if (state.isCurrentResolution(functionParameter) && !state.isInScopeExpression()) {
      // Start of a new parameter
      //    .. ( xxx |>yyy<| ) ..
      state.complete().popEnd().mark(m_types.C_FUN_PARAM).resolution(functionParameter);
    }

    if (state.isCurrentResolution(let)) {
      state.resolution(letNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
      IElementType nextToken = state.getTokenType();
      if (nextToken != m_types.EQ && nextToken != m_types.COLON) {
        // This is a function, we need to create the let binding now, to be in sync with reason
        //  let |>x<| y z = ...  vs    let x = y z => ...
        state
            .mark(m_types.C_LET_BINDING)
            .resolution(letBinding)
            .mark(m_types.C_FUN_EXPR)
            .resolution(function)
            .mark(m_types.C_FUN_PARAMS)
            .resolution(functionParameters);
      }
    } else if (state.isCurrentResolution(external)) {
      state.resolution(externalNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_TYPE_DECLARATION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.isCurrentResolution(val)) {
      state.resolution(valNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.isCurrentResolution(clazz)) {
      state.resolution(clazzNamed).wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else if (state.is(m_types.C_DECONSTRUCTION)) {
      state.wrapWith(m_types.C_LOWER_IDENTIFIER);
    } else {
      state.wrapWith(m_types.C_LOWER_SYMBOL);
    }
  }

  private void parseUIdent(@NotNull ParserState state) {
    if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
      return;
    }

    if (state.isCurrentResolution(module)) {
      // Module declaration  ::  module |>M<| ...
      state.resolution(moduleNamed).wrapWith(m_types.C_UPPER_IDENTIFIER);
    } else if (state.isCurrentResolution(open) || state.isCurrentResolution(include)) {
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
    } else if (state.isCurrentResolution(variantDeclaration)) {
      // Declaring a variant  ::  type t = | |>X<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
    } else if (state.isCurrentResolution(exception)) {
      // Declaring an exception  ::  exception |>X<| ...
      state.wrapWith(m_types.C_UPPER_IDENTIFIER);
    } else {
      if (state.isCurrentResolution(typeBinding)) {
        // Might be a variant declaration without a pipe
        IElementType nextToken = state.lookAhead(1);
        if (nextToken == m_types.OF || nextToken == m_types.PIPE) {
          // type t = |>X<| | ..   or   type t = |>X<| of ..
          if (state.previousElementType1 == m_types.PIPE) {
            state.advance();
          }
          state
              . // remapCurrentToken(m_types.VARIANT_NAME).
              mark(m_types.C_VARIANT_DECL)
              .resolution(variantDeclaration)
              .wrapWith(m_types.C_UPPER_IDENTIFIER);
          return;
        }
      } else if (state.isCurrentResolution(moduleBinding)) {
        // It might be a functor call, or just an alias ::  module M = |>X<| ( ... )
        state.markOptional(m_types.C_FUNCTOR_CALL).resolution(maybeFunctorCall);
      } else {
        IElementType nextToken = state.lookAhead(1);
        if (((state.isCurrentResolution(patternMatch) || state.isCurrentResolution(letBinding)))
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
    if (state.isCurrentResolution(let)) {
      // let open X (coq/indtypes.ml)
      state.resolution(open).updateCurrentCompositeElementType(m_types.C_OPEN);
    } else {
      // endLikeSemi(state);
      state.popEndUntilScope();
      state.mark(m_types.C_OPEN).resolution(open);
    }
  }

  private void parseInclude(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_INCLUDE).resolution(include).setStart();
  }

  private void parseExternal(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_EXTERNAL_DECLARATION).resolution(external).setStart();
  }

  private void parseType(@NotNull ParserState state) {
    if (state.isCurrentResolution(module)) {
      // module |>type<| M = ...
    } else if (state.isCurrentResolution(constraint)
        && (state.previousElementType1 == m_types.WITH
            || state.previousElementType1 == m_types.AND)) {
      // module M : X with |>type<| t ...
      // include X with |>type<| t ...
      // ?
    } else if (state.isCurrentResolution(clazz)) {
      // class |>type<| ...
    } else {
      state.popEndUntilScope();
      state.mark(m_types.C_TYPE_DECLARATION).resolution(type).setStart();
    }
  }

  private void parseException(@NotNull ParserState state) {
    if (state.previousElementType1 != m_types.PIPE) {
      state.popEndUntilScope();
      state.mark(m_types.C_EXCEPTION_DECLARATION).resolution(exception).setStart();
    }
  }

  private void parseDirectiveIf(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_DIRECTIVE).resolution(directive).setStart();
  }

  private void parseDirectiveElse(@NotNull ParserState state) {
    state.popEndUntilResolution(directive);
  }

  private void parseDirectiveElif(@NotNull ParserState state) {
    state.popEndUntilResolution(directive);
  }

  private void parseDirectiveEnd(@NotNull ParserState state) {
    state.popEndUntilResolution(directive);
    if (state.isCurrentResolution(directive)) {
      state.advance().popEnd();
    }
  }

  private void parseVal(@NotNull ParserState state) {
    endLikeSemi(state);
    state
        .mark(
            state.isCurrentResolution(clazzBody)
                ? m_types.C_CLASS_FIELD
                : m_types.C_VAL_DECLARATION)
        .resolution(val)
        .setStart();
  }

  private void parseMethod(@NotNull ParserState state) {
    state.popEndUntilScope();
    state.mark(m_types.C_CLASS_METHOD).resolution(val).setStart();
  }

  private void parseLet(@NotNull ParserState state) {
    endLikeSemi(state); // state.popEndUntilScope();
    state.mark(m_types.C_LET_DECLARATION).resolution(let).setStart();
  }

  private void parseModule(@NotNull ParserState state) {
    if (state.isCurrentResolution(let)) {
      state.resolution(module).updateCurrentCompositeElementType(m_types.C_MODULE_DECLARATION);
    } else if (!state.isCurrentResolution(annotationName)) {
      if (!state.is(m_types.C_MODULE_TYPE)) {
        state.popEndUntilScope();
      }
      state.mark(m_types.C_MODULE_DECLARATION).resolution(module).setStart();
    }
  }

  private void parseClass(@NotNull ParserState state) {
    endLikeSemi(state);
    state.mark(m_types.C_CLASS_DECLARATION).resolution(clazz).setStart();
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
      while (parserScope != null && !parserScope.isScope()) {
        state.popEnd();
        parserScope = state.getLatestScope();
      }
    }
  }
}
