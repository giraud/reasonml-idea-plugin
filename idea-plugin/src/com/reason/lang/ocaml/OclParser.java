package com.reason.lang.ocaml;

import org.jetbrains.annotations.NotNull;
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

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;

public class OclParser extends CommonParser<OclTypes> {

    public OclParser() {
        super(OclTypes.INSTANCE);
    }

    public static ASTNode parseOcamlNode(@NotNull ILazyParseableElementType root, @NotNull ASTNode chameleon) {
        PsiElement parentElement = chameleon.getTreeParent().getPsi();
        Project project = parentElement.getProject();

        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new OclLexer(), root.getLanguage(), chameleon.getText());
        //builder.setDebugMode(true);
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
            // [| ... |]
            else if (tokenType == m_types.LARRAY) {
                parseLArray(state);
            } else if (tokenType == m_types.RARRAY) {
                parseRArray(state);
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
            state.remapCurrentToken(m_types.LIDENT).
                    wrapWith(m_types.C_LOWER_SYMBOL).
                    updateCurrentResolution(externalNamed);
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.isCurrentResolution(genericExpression) && state.isPreviousResolution(let)) {
            // It must be a deconstruction
            // let ( a |>,<| b ) = ..
            state.updateCurrentResolution(deconstruction).
                    updateCurrentCompositeElementType(m_types.C_DECONSTRUCTION);
        }
    }

    private void parseLArray(@NotNull ParserState state) {
    }

    private void parseRArray(@NotNull ParserState state) {
    }

    private void parseLt(@NotNull ParserState state) {
        if (!state.isCurrentResolution(binaryCondition)) {
            // |> < <| .. > ..
            state.markScope(object, m_types.C_OBJECT, m_types.LT).
                    advance().
                    mark(objectField, m_types.C_OBJECT_FIELD);
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
        state.mark(whileLoop, m_types.C_WHILE).
                advance().
                mark(binaryCondition, m_types.C_WHILE_CONDITION);
    }

    private void parseDo(@NotNull ParserState state) {
        if (state.isCurrentResolution(binaryCondition) && state.isPreviousResolution(whileLoop)) {
            state.popEnd().
                    advance().
                    markScope(doLoop, m_types.C_SCOPED_EXPR, m_types.DO);
        } else {
            state.markScope(doLoop, m_types.C_SCOPED_EXPR, m_types.DO);
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
            state.popEnd().
                    advance().
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(patternMatch)) {
            state.advance().
                    mark(patternMatchBody, m_types.C_PATTERN_MATCH_BODY);
        } else if (state.isCurrentResolution(maybeFunctionParameters)) {
            // fun ... |>-><| ...
            state.updateCurrentResolution(functionParameters).complete().
                    popEnd().
                    advance().
                    mark(functionBody, m_types.C_FUN_BODY);
        }
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(assert_, m_types.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.isCurrentResolution(constraint)/* || state.isCurrentResolution(includeConstraint)*/) {
            state.popEnd().
                    advance().
                    mark(constraint, m_types.C_CONSTRAINT);
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
            if (latestScope.isCompositeType(m_types.C_EXPR_MODULE)) {
                state.markStart(module, m_types.C_EXPR_MODULE);
            } else if (latestScope.isCompositeType(m_types.C_EXPR_LET)) {
                state.markStart(let, m_types.C_EXPR_LET);
            } else if (latestScope.isCompositeType(m_types.C_EXPR_TYPE)) {
                state.markStart(type, m_types.C_EXPR_TYPE).
                        mark(typeConstrName, m_types.C_TYPE_CONSTR_NAME);
            }
        }
    }

    private void parsePipe(@NotNull ParserState state) {
        // Remove intermediate constructions
        if (state.isCurrentResolution(variantConstructorParameter)) {
            state.popEndUntilResolution(variantDeclaration);
        }
        if (state.isCurrentResolution(patternMatchBody)) {
            state.popEndUntilResolution(matchWith);
        }

        if (state.isCurrentResolution(typeBinding)) {
            state.mark(variantDeclaration, m_types.C_VARIANT_DECL);
        } else if (state.isCurrentResolution(variantDeclaration)) {
            // type t = | V1 |>|<| ...
            state.popEnd().
                    mark(variantDeclaration, m_types.C_VARIANT_DECL);
        } else {
            // By default, a pattern match
            if (state.isCurrentResolution(maybeFunctionParameters)) {
                state.popEnd().
                        updateCurrentResolution(matchWith);
            }
            state.mark(patternMatch, m_types.C_PATTERN_MATCH_EXPR);
        }
    }

    private void parseMatch(@NotNull ParserState state) {
        state.mark(match, m_types.C_MATCH_EXPR).
                advance().
                mark(binaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        state.mark(try_, m_types.C_TRY_EXPR).
                advance().
                mark(tryBody, m_types.C_TRY_BODY);
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorResult)) {
            // A functor with constraints
            //  module Make (M : Input) : S |>with<| ...
            state.popEnd().
                    mark(constraints, m_types.C_CONSTRAINTS).
                    advance().
                    mark(constraint, m_types.C_CONSTRAINT);
        } else if (state.isCurrentResolution(moduleNamedSignature) || state.isCurrentResolution(moduleNamedColon)) {
            // A module with a signature and constraints
            //  module G : sig ... end |>with<| ...
            //  module G : X |>with<| ...
            state.mark(constraints, m_types.C_CONSTRAINTS).
                    mark(constraint, m_types.C_CONSTRAINT);
        } else if (state.isPreviousResolution(include)) {
            // An include with constraints
            //   include M |>with<| ...
            if (state.isCurrentResolution(maybeFunctorCall)) {
                state.popEnd();
            }
            state.mark(constraints, m_types.C_CONSTRAINTS).
                    advance().
                    mark(constraint, m_types.C_CONSTRAINT);
        } else if (state.isCurrentResolution(tryBody)) {
            // A try handler
            //   try ... |>with<| ...
            state.popEnd().
                    advance().
                    mark(tryBodyWith, m_types.C_TRY_HANDLERS).
                    mark(tryBodyWithHandler, m_types.C_TRY_HANDLER);
        } else if (state.isCurrentResolution(binaryCondition)) {
            if (state.isPreviousResolution(match)) {
                state.popEnd().
                        updateCurrentResolution(matchWith);
            }
        }
    }

    private void parseIf(@NotNull ParserState state) {
        state.mark(if_, m_types.C_IF_STMT).
                advance().
                mark(binaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseThen(@NotNull ParserState state) {
        if (!state.isCurrentResolution(directive)) {
            state.popEndUntilResolution(if_).
                    advance().
                    mark(ifThenStatement, m_types.C_SCOPED_EXPR);
        }
    }

    private void parseElse(@NotNull ParserState state) {
        state.popEndUntilResolution(if_).
                advance().
                mark(ifElseStatement, m_types.C_SCOPED_EXPR);
    }

    private void parseStruct(@NotNull ParserState state) {
        if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignatureEq)) {
            // module X = |>struct<| ...
            state.markScope(moduleBinding, m_types.C_SCOPED_EXPR, m_types.STRUCT);
        } else if (state.isCurrentResolution(functorNamedEq)) {
            // module X (...) = |>struct<| ...
            state.markScope(functorBinding, m_types.C_FUNCTOR_BINDING, m_types.STRUCT);
        } else {
            state.markScope(scope, m_types.C_STRUCT_EXPR, m_types.STRUCT);
        }
    }

    private void parseSig(@NotNull ParserState state) {
        if (state.isCurrentResolution(moduleNamedEq)) {
            // This is the body of a module type
            // module type X = |>sig<| ...
            state.markScope(moduleBinding, m_types.C_SCOPED_EXPR, m_types.SIG);
        } else if (state.isCurrentResolution(moduleNamedColon)) {
            state.updateCurrentResolution(moduleNamedSignature).
                    markScope(signature, m_types.C_SIG_EXPR, m_types.SIG);
        } else if (state.isCurrentResolution(functorParamColon)) {
            state.updateCurrentResolution(functorParamColonSignature).
                    markScope(functorParamColonSignature, m_types.C_SIG_EXPR, m_types.SIG);
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        if (state.isCurrentResolution(recordField)) {
            // SEMI ends the field, and starts a new one
            state.popEnd().
                    advance();
            if (state.getTokenType() != m_types.RBRACE) {
                state.mark(recordField, m_types.C_RECORD_FIELD);
            }
        } else if (state.isPreviousResolution(object)) {
            // SEMI ends the field, and starts a new one
            state.popEnd().
                    advance().
                    mark(objectField, m_types.C_OBJECT_FIELD);
        } else {
            boolean isImplicitScope = state.isCurrentResolution(functionBody);

            // A SEMI operator ends the previous expression
            if (!isImplicitScope && !state.isInScopeExpression()) {
                state.popEnd();
                if (state.isCurrentResolution(object)) {
                    state.advance().mark(objectField, m_types.C_OBJECT_FIELD);
                }
            }
        }
    }

    private void parseIn(@NotNull ParserState state) {
        state.popEnd();
    }

    private void parseBegin(@NotNull ParserState state) {
        state.markScope(beginScope, m_types.C_SCOPED_EXPR, m_types.BEGIN);
    }

    private void parseObject(@NotNull ParserState state) {
        if (state.isCurrentResolution(clazzNamedEq)) {
            state.markScope(clazzBody, m_types.C_SCOPED_EXPR, m_types.OBJECT);
        }
    }

    private void parseEnd(@NotNull ParserState state) {
        ParserScope scope = state.popEndUntilOneOfElementType(m_types.BEGIN, m_types.SIG, m_types.STRUCT, m_types.OBJECT);
        state.advance();

        //if (scope != null && scope.isStart()) {
        state.popEnd();
        //}
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedColon);
        } else if (state.isCurrentResolution(externalNamed) || state.isCurrentResolution(valNamed) || state.isCurrentResolution(letNamed)) {
            // external x |> : <| ...
            // val x |> : <| ...
            // let x |> : <|
            state.advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(objectField)) {
            // < x |> : <| ...
            state.updateCurrentResolution(objectFieldNamed);
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateCurrentResolution(functionParameterNamed).
                    advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(functorNamed)) {
            state.updateCurrentResolution(functorNamedColon).
                    advance().
                    mark(functorResult, m_types.C_FUNCTOR_RESULT);
        } else if (state.isCurrentResolution(functorParam)) {
            state.updateCurrentResolution(functorParamColon);
        }
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        if (state.isCurrentResolution(functionParameter) && !state.isInScopeExpression()) {
            // Start of a new optional parameter
            //    .. ( xxx |>?<|yyy ) ..
            state.complete().
                    popEnd().
                    mark(functionParameter, m_types.C_FUN_PARAM);
        }
    }

    private void parseFunction(@NotNull ParserState state) {
        state.advance();
        if (state.getTokenType() != m_types.PIPE) {
            state.mark(patternMatch, m_types.C_PATTERN_MATCH_EXPR);
        }
    }

    private void parseFun(@NotNull ParserState state) {
        if (state.isCurrentResolution(letBinding)) {
            state.mark(function, m_types.C_FUN_EXPR).
                    advance().
                    markOptional(maybeFunctionParameters, m_types.C_FUN_PARAMS);
        }
    }

    private void parseEq(@NotNull ParserState state) {
        // Remove intermediate constructions
        if (state.isCurrentResolution(typeConstrName)) {
            state.popEnd().updateCurrentResolution(typeNamed);
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
            state.
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    mark(typeBinding, m_types.C_TYPE_BINDING);
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedSignature)) {
            state.popEndUntilStart();
            state.updateCurrentResolution(letNamedEq).
                    advance().
                    mark(letBinding, m_types.C_LET_BINDING);
        } else if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedEq);
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.updateCurrentResolution(clazzNamedEq);
        } else if (state.isCurrentResolution(functorNamed) || state.isCurrentResolution(functorNamedColon)) {
            state.updateCurrentResolution(functorNamedEq);
        } else if (state.isCurrentResolution(constraint) && state.isGrandPreviousResolution(functorNamedColon)) {
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == m_types.STRUCT) {
                // Functor constraints
                // module M (...) : S with ... |> =<| struct ... end
                state.popEnd().
                        popEnd().
                        updateCurrentResolution(functorNamedEq);
            }
        } else if (state.isCurrentResolution(functionParameter) /*&& !state.isInScopeExpression()*/) {
            state.popEndUntilResolution(function).
                    advance().
                    mark(functionBody, m_types.C_FUN_BODY);
        }
    }

    private void parseOf(@NotNull ParserState state) {
        if (state.isCurrentResolution(variantDeclaration)) {
            // Variant params :: type t = | Variant «of» ..
            state.mark(variantConstructorParameters, m_types.C_FUN_PARAMS).
                    advance().
                    mark(variantConstructorParameter, m_types.C_FUN_PARAM);
        }
    }

    private void parseStar(@NotNull ParserState state) {
        if (state.isCurrentResolution(variantConstructorParameter)) {
            // type t = | Variant of x |>*<| y ..
            state.popEnd().
                    advance().
                    mark(variantConstructorParameter, m_types.C_FUN_PARAM);
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.isCurrentResolution(external)) {
            // Overloading an operator
            //   external |>(<| ... ) = ...
            state.updateCurrentResolution(externalNamed).
                    markScope(genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.isCurrentResolution(maybeFunctorCall)) {
            // Yes, it is a functor call
            //  module M = X |>(<| ... )
            state.updateCurrentResolution(functorCall).complete().
                    markScope(functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                    advance().
                    mark(functionParameter, m_types.C_FUN_PARAM);
        } else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Detecting a local open
            //   M1.M2. |>(<| ... )
            state.markScope(localOpenScope, m_types.C_LOCAL_OPEN, m_types.LPAREN);
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.markScope(clazzConstructor, m_types.C_CLASS_CONSTR, m_types.LPAREN);
        } else if (state.isCurrentResolution(functionParameters)) {
            // Start of the first parameter
            //     let x |>(<| ...
            state.markScope(functionParameter, m_types.C_FUN_PARAM, m_types.LPAREN);
        } else if (state.isCurrentResolution(functionParameter) && !state.isInScopeExpression() && state.previousElementType1 != m_types.QUESTION_MARK) {
            // Start of a new parameter
            //    let f xxx |>(<| ..tuple ) = ..
            state.popEnd().
                    mark(functionParameter, m_types.C_FUN_PARAM).
                    markScope(functionParameter, m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.isCurrentResolution(moduleNamed)) {
            // This is a functor
            //   module Make |>(<| ... )
            state.updateCurrentResolution(functorNamed).
                    updateCurrentCompositeElementType(m_types.C_FUNCTOR).
                    markScope(functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN).
                    advance().
                    mark(functorParam, m_types.C_FUNCTOR_PARAM);
        } else {
            state.markScope(genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN);
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LPAREN);
        state.advance();
        if (scope != null) {
            //    scope.complete();
            state.popEnd();
        }

        if (state.isCurrentResolution(let)) {
            // we are processing an infix operator or a deconstruction (tuple)
            //  let ( ... |>)<| = ...
            state.updateCurrentResolution(letNamed);
        }
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.isCurrentResolution(functionParameters)) {
            // let fn |>{<| ... } = ...
            state.mark(functionParameter, m_types.C_FUN_PARAM);
        }

        state.markScope(recordBinding, m_types.C_RECORD_EXPR, m_types.LBRACE).
                advance().
                mark(recordField, m_types.C_RECORD_FIELD);
    }

    private void parseRBrace(@NotNull ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LBRACE);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
    }

    private void parseBracketGt(@NotNull ParserState state) {
    }

    private void parseRBracket(@NotNull ParserState state) {
    }

    private void parseLIdent(@NotNull ParserState state) {
        if (state.isCurrentResolution(functionParameters)) {
            state.mark(functionParameter, m_types.C_FUN_PARAM);
        } else if (state.isCurrentResolution(functionParameter) && !state.isInScopeExpression()) {
            // Start of a new parameter
            //    .. ( xxx |>yyy<| ) ..
            state.complete().popEnd().
                    mark(functionParameter, m_types.C_FUN_PARAM);
        }

        state.wrapWith(m_types.C_LOWER_SYMBOL);

        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
            IElementType nextToken = state.getTokenType();
            if (nextToken != m_types.EQ && nextToken != m_types.COLON) {
                // This is a function, we need to create the let binding now, to be in sync with reason
                //  let |>x<| y z = ...  vs    let x = y z => ...
                state.mark(letBinding, m_types.C_LET_BINDING).
                        mark(function, m_types.C_FUN_EXPR).
                        mark(functionParameters, m_types.C_FUN_PARAMS);
            }
        } else if (state.isCurrentResolution(external)) {
            state.updateCurrentResolution(externalNamed);
        } else if (state.isCurrentResolution(val)) {
            state.updateCurrentResolution(valNamed);
        } else if (state.isCurrentResolution(clazz)) {
            state.updateCurrentResolution(clazzNamed);
        }
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.isCurrentResolution(open) || state.isCurrentResolution(include)) {
            // It is a module name/path, or might be a functor call
            //  open/include |>M<| ...
            state.markOptional(maybeFunctorCall, m_types.C_FUNCTOR_CALL).
                    wrapWith(m_types.C_UPPER_SYMBOL);

            IElementType nextToken = state.getTokenType();
            if (nextToken != m_types.DOT && nextToken != m_types.LPAREN && nextToken != m_types.WITH) {
                // Not a path, nor a functor, must close that open
                state.popCancel();
                state.popEnd();
            }
        } else if (state.isCurrentResolution(variantDeclaration)) {
            // Declaring a variant
            // type t = | |>X<| ..
            state.remapCurrentToken(m_types.VARIANT_NAME).
                    wrapWith(m_types.C_VARIANT);
        } else {
            if (state.isCurrentResolution(module)) {
                // Module declaration
                // module |>M<| ...
                state.updateCurrentResolution(moduleNamed);
            } else if (state.isCurrentResolution(typeBinding)) {
                // Might be a variant declaration without a pipe
                IElementType nextToken = state.lookAhead(1);
                if (nextToken == m_types.OF || nextToken == m_types.PIPE) {
                    // type t = |>X<| | ..   or   type t = |>X<| of ..
                    state.remapCurrentToken(m_types.VARIANT_NAME).
                            mark(variantDeclaration, m_types.C_VARIANT_DECL).
                            wrapWith(m_types.C_VARIANT);
                    return;
                }
            } else if (state.isCurrentResolution(moduleNamedEq)) {
                // It might be a functor call, or just an alias
                //   module M = |>X<| ( ... )
                state.markOptional(maybeFunctorCall, m_types.C_FUNCTOR_CALL);
            } else {
                IElementType nextToken = state.lookAhead(1);
                if (((state.isCurrentResolution(patternMatch) || state.isCurrentResolution(letBinding))) && nextToken != m_types.DOT) {
                    // Pattern matching a variant or using it
                    // match c with | |>X<| ... / let x = |>X<| ...
                    state.remapCurrentToken(m_types.VARIANT_NAME).
                            wrapWith(m_types.C_VARIANT);
                    return;
                }
            }

            state.wrapWith(m_types.C_UPPER_SYMBOL);
        }
    }

    private void parseOpen(@NotNull ParserState state) {
        if (state.isCurrentResolution(let)) {
            // let open X (coq/indtypes.ml)
            state.updateCurrentResolution(open).
                    updateCurrentCompositeElementType(m_types.C_OPEN);
        } else {
            //endLikeSemi(state);
            state.popEndUntilScope();
            state.mark(open, m_types.C_OPEN);
        }
    }

    private void parseInclude(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.markStart(include, m_types.C_INCLUDE);
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.markStart(external, m_types.C_EXPR_EXTERNAL);
    }

    private void parseType(@NotNull ParserState state) {
        if (state.isCurrentResolution(module)) {
            // module |>type<| M = ...
            // state.updateCurrentResolution(moduleType);
        } else if (state.isCurrentResolution(constraint) && (state.previousElementType1 == m_types.WITH || state.previousElementType1 == m_types.AND)) {
            // module M : X with |>type<| t ...
            // include X with |>type<| t ...
            // ?
        } else if (state.isCurrentResolution(clazz)) {
            // class |>type<| ...
        } else {
            state.popEndUntilScope();
            state.markStart(type, m_types.C_EXPR_TYPE).
                    advance().
                    mark(typeConstrName, m_types.C_TYPE_CONSTR_NAME);
        }
    }

    private void parseException(@NotNull ParserState state) {
        if (state.previousElementType1 != m_types.PIPE) {
            state.popEndUntilScope();
            state.markStart(exception, m_types.C_EXPR_EXCEPTION);
        }
    }

    private void parseDirectiveIf(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.markStart(directive, m_types.C_DIRECTIVE);
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
        state.markStart(val, state.isCurrentResolution(clazzBody) ? m_types.C_CLASS_FIELD : m_types.C_EXPR_VAL);
    }

    private void parseMethod(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.markStart(val, m_types.C_CLASS_METHOD);
    }

    private void parseLet(@NotNull ParserState state) {
        endLikeSemi(state);//state.popEndUntilScope();
        state.markStart(let, m_types.C_EXPR_LET);
    }

    private void parseModule(@NotNull ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(module).
                    updateCurrentCompositeElementType(m_types.C_EXPR_MODULE);
        } else if (!state.isCurrentResolution(annotationName)) {
            state.popEndUntilScope();
            state.markStart(module, m_types.C_EXPR_MODULE);
        }
    }

    private void parseClass(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(clazz, m_types.C_CLASS_STMT);
    }

    private void endLikeSemi(@NotNull ParserState state) {
        if (state.previousElementType1 != m_types.EQ && state.previousElementType1 != m_types.RIGHT_ARROW && state.previousElementType1 != m_types.TRY
                && state.previousElementType1 != m_types.SEMI && state.previousElementType1 != m_types.THEN && state.previousElementType1 != m_types.ELSE
                && state.previousElementType1 != m_types.IN && state.previousElementType1 != m_types.LPAREN && state.previousElementType1 != m_types.DO
                && state.previousElementType1 != m_types.STRUCT && state.previousElementType1 != m_types.SIG && state.previousElementType1 != m_types.COLON) {
            ParserScope parserScope = state.getLatestScope();
            while (parserScope != null && !parserScope.isScope()) {
                state.popEnd();
                parserScope = state.getLatestScope();
            }
        }
    }
}
