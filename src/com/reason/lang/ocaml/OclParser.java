package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;

public class OclParser extends CommonParser<OclTypes> {
    public OclParser() {
        super(OclTypes.INSTANCE);
    }

    public static ASTNode parseOcamlNode(@NotNull ILazyParseableElementType root, @NotNull ASTNode chameleon) {
        PsiElement parentElement = chameleon.getTreeParent().getPsi();
        Project project = parentElement.getProject();

        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new OclLexer(), root.getLanguage(), chameleon.getText());
        OclParser parser = new OclParser();

        return parser.parse(root, builder).getFirstChildNode();
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;
        state.previousElementType1 = null;

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
            } else if (tokenType == m_types.INT_VALUE) {
                parseNumber(state);
            } else if (tokenType == m_types.FLOAT_VALUE) {
                parseNumber(state);
            } else if (tokenType == m_types.LIDENT) {
                parseLIdent(state);
            } else if (tokenType == m_types.UIDENT) {
                parseUIdent(state);
            } else if (tokenType == m_types.SIG) {
                parseSig(state);
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
            }
            // else if (tokenType == m_types.DOTDOT) {
            //    parseDotDot(state);
            //}
            else if (tokenType == m_types.FUNCTION) {
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
            } else if (tokenType == m_types.ARROBASE) {
                parseArrobase(state);
            } else if (tokenType == m_types.ARROBASE_2) {
                parseArrobase2(state);
            } else if (tokenType == m_types.ARROBASE_3) {
                parseArrobase3(state);
            } else if (tokenType == m_types.OPTION) {
                parseOption(state);
            }
            // while ... do ... done
            else if (tokenType == m_types.WHILE) {
                parseWhile(state);
            }
            // for ... to ... do ... done
            else if (tokenType == m_types.FOR) {
                parseFor(state);
            }
            // do ... done
            else if (tokenType == m_types.DO) {
                parseDo(state);
            } else if (tokenType == m_types.DONE) {
                parseDone(state);
            }
            // begin/struct ... end
            else if (tokenType == m_types.BEGIN) {
                parseBegin(state);
            } else if (tokenType == m_types.STRUCT) {
                parseStruct(state);
            } else if (tokenType == m_types.END) {
                parseEnd(state);
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
        }
    }

    private void parseOption(@NotNull ParserState state) {
        //if (state.isDummy() && (state.isParent(m_types.C_SIG_ITEM) || state.isParent(m_types.C_TYPE_BINDING))) {
        // in type      :  type t = xxx |>option<|
        // or signature :  ... -> xxx |>option<| ...
        //state.updateCurrentCompositeElementType(m_types.C_OPTION).complete();
        //}
        if (state.inAny(m_types.C_TYPE_BINDING)) {
            int pos = state.getIndex();
            if (pos > 0) {
                // in type      :  type t = xxx |>option<|
                // or signature :  ... -> xxx |>option<| ...
                state.markBefore(pos - 1, m_types.C_OPTION);
            }
        }
    }

    private void parseRaise(@NotNull ParserState state) {
        if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
            // external |>raise<| ...
            state.remapCurrentToken(m_types.LIDENT).wrapWith(m_types.C_LOWER_IDENTIFIER);
        }
    }

    private void parseComma(@NotNull ParserState state) {
        //if (state.isParentComplete(m_types.C_LET_DECLARATION)) {
        //    state.dropOptionals();
        //    // It must be a deconstruction ::  let (a |>,<| b) = ..
        //    // We need to do it again because lower symbols must be wrapped with identifiers
        //    if (state.isCurrentResolution(genericExpression)) {
        //        ParserScope scope = state.pop();
        //        if (scope != null) {
        //            scope.rollbackTo();
        //            state
        //                    .markScope(m_types.C_DECONSTRUCTION, m_types.LPAREN)
        //                    .advance();
        //        }
        //    }
        //} else
        if (state.in(m_types.C_TUPLE)) {
            // a tuple
            state.popEndUntil(m_types.C_TUPLE);
        }
        // else if (state.in(m_types.C_FUN_PARAM)) {
        //state.popEndUntil(m_types.C_FUN_PARAM).popEnd().advance()
        //      .mark(m_types.C_FUN_PARAM);
        //}
        else if (state.inAny(m_types.C_LET_DECLARATION, m_types.C_SIG_ITEM, m_types.C_MATCH_EXPR, m_types.C_FUN_PARAM, m_types.C_SCOPED_EXPR)) {
            // all same priority

            if (state.isFound(m_types.C_LET_DECLARATION)) {
                if (state.in(m_types.C_DECONSTRUCTION)) {
                    state.popEndUntil(m_types.C_DECONSTRUCTION);
                } else {
                    // It must be a deconstruction ::  let a |>,<| b ...
                    // We need to do it again because lower symbols must be wrapped with identifiers
                    int letPos = state.indexOfComposite(m_types.C_LET_DECLARATION);
                    state.rollbackTo(letPos - 1);
                    state.mark(m_types.C_DECONSTRUCTION);
                }
            } else if (state.isFound(m_types.C_SCOPED_EXPR)) {
                MarkerScope blockScope = state.find(state.getIndex());
                MarkerScope parentScope = state.find(state.getIndex() + 1);
                if (blockScope != null && parentScope != null) {
                    if (parentScope.isCompositeType(m_types.C_LET_DECLARATION)) {
                        // let (x |>,<| ... )
                        // We need to do it again because lower symbols must be wrapped with identifiers
                        int letPos = state.indexOfComposite(m_types.C_LET_DECLARATION);
                        state.rollbackTo(letPos - 1);
                        state.mark(m_types.C_DECONSTRUCTION);
                        if (state.getTokenType() == m_types.LPAREN) {
                            state.updateScopeToken(m_types.LPAREN).advance();
                        }
                    } else if (parentScope.isCompositeType(m_types.C_FUN_PARAM)) {
                        // a tuple ::  let fn (x |>,<| ... ) ...
                        blockScope.updateCompositeElementType(m_types.C_TUPLE);
                        state.popEndUntil(m_types.C_TUPLE);
                    }
                }
            }
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        if (state.is(m_types.C_ANNOTATION)) {
            state.mark(m_types.C_MACRO_NAME);
        }
    }

    private void parseArrobase2(@NotNull ParserState state) {
        if (state.is(m_types.C_ANNOTATION)) {
            state.mark(m_types.C_MACRO_NAME);
        }
    }

    private void parseArrobase3(@NotNull ParserState state) {
        if (state.is(m_types.C_ANNOTATION)) {
            state.mark(m_types.C_MACRO_NAME);
        }
    }

    private void parseLt(@NotNull ParserState state) {
        if (state.is/*parent?*/(m_types.C_SIG_ITEM) || state.in(m_types.C_TYPE_BINDING) || state.is(m_types.C_OBJECT_FIELD)) {
            // |> < <| .. > ..
            state.markScope(m_types.C_OBJECT, m_types.LT).advance()
                    .mark(m_types.C_OBJECT_FIELD);
        }
    }

    private void parseGt(@NotNull ParserState state) {
        // intermediate structures
        //if (state.isComplete(m_types.C_SIG_ITEM)) {
        //    state.popEndComplete() // item
        //            .pop(); // expr
        //}
        //if (state.isComplete(m_types.C_OBJECT_FIELD)) {
        //    state.popEndComplete();
        //}

        //if (state.is(m_types.C_OBJECT)) {
        //    state.advance();
        //    if (state.getTokenType() == m_types.AS) {
        //        // type t = < .. > |>as<| ..
        //        state.advance().advance().popEnd();
        //    }
        // } else
        if (state.in(m_types.C_OBJECT)) {
            // < ... |> > <| ..
            //    if (state.isCurrentResolution(objectFieldNamed)) {
            //        state.popEnd();
            //    }
            state.popEndUntil(m_types.C_OBJECT);
            state.advance();

            if ("Js".equals(state.getTokenText())) {
                // it might be a Js object (same with Js.t at the end)
                state.advance();
                if (state.getTokenType() == m_types.DOT) {
                    state.advance();
                    //            if ("t".equals(state.getTokenText())) {
                    //                state.updateCurrentCompositeElementType(m_types.C_JS_OBJECT).advance().complete();
                    //            }
                }
            }

            state.popEnd();
        }
    }

    private void parseWhile(@NotNull ParserState state) {
        state.mark(m_types.C_WHILE).advance()
                .mark(m_types.C_BINARY_CONDITION);
    }

    private void parseFor(@NotNull ParserState state) {
        state.mark(m_types.C_FOR_LOOP);
    }

    private void parseDo(@NotNull ParserState state) {
        if (state.in(m_types.C_BINARY_CONDITION)) {
            state.popEndUntil(m_types.C_BINARY_CONDITION).popEnd();
        }

        if (state.is(m_types.C_WHILE)) {
            state.advance()
                    .markScope(m_types.C_DO_LOOP, m_types.DO);
        } else if (!state.in(m_types.C_FOR_LOOP)) {
            state.markScope(m_types.C_DO_LOOP, m_types.DO);
        }
    }

    private void parseDone(@NotNull ParserState state) {
        if (state.inAny(m_types.C_DO_LOOP, m_types.C_FOR_LOOP)) {
            state.popEndUntilIndex(state.getIndex()).popEnd();
        }
        if (state.is(m_types.C_WHILE)) {
            state.popEnd();
        }
    }

    private void parseRightArrow(@NotNull ParserState state) {
        // intermediate results
        //if (state.is(m_types.C_FUN_PARAM)) {
        //    state.popEnd();
        //}
        if (state.in(m_types.C_SIG_ITEM, /*not*/m_types.C_SCOPED_EXPR) && !state.hasScopeToken()) {
            state.popEndUntil(m_types.C_SIG_ITEM).popEnd();
            if (state.in(m_types.C_NAMED_PARAM)) {
                // can't have arrow in a named param signature ::  let fn x:int |>-><| y:int
                state.popEnd().popEndUntil(m_types.C_SIG_EXPR);
            }
            state.advance()
                    .mark(m_types.C_SIG_ITEM);
        } else if (state.inAny(m_types.C_PATTERN_MATCH_EXPR, m_types.C_FUN_EXPR)) { // no priority, first win
            MarkerScope found = state.find(state.getIndex());
            if (found != null && found.isCompositeType(m_types.C_PATTERN_MATCH_EXPR)) {
                // | ... |>-><|
                state.popEndUntil(m_types.C_PATTERN_MATCH_EXPR).advance()
                        .mark(m_types.C_PATTERN_MATCH_BODY);
            } else if (found != null && found.isCompositeType(m_types.C_FUN_EXPR)) {
                // fun ... |>-><| ...
                state.popEndUntil(m_types.C_FUN_EXPR).advance()
                        .mark(m_types.C_FUN_BODY);
            }

        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(m_types.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.in(m_types.C_CONSTRAINT)) {
            state.popEndUntil(m_types.C_CONSTRAINT).popEnd().advance()
                    .mark(m_types.C_CONSTRAINT);
        } else if (state.inAny(m_types.C_LET_DECLARATION, m_types.C_TYPE_DECLARATION)) {
            // pop scopes until a chainable expression is found
            state.popEndUntilIndex(state.getIndex());
            MarkerScope latestScope = state.getLatestScope();

            state.popEnd().advance();

            if (latestScope.isCompositeType(m_types.C_LET_DECLARATION)) {
                state.mark(m_types.C_LET_DECLARATION).setStart();
            } else if (latestScope.isCompositeType(m_types.C_TYPE_DECLARATION)) {
                state.mark(m_types.C_TYPE_DECLARATION).setStart();
            }
        }
    }

    private void parseDot(@NotNull ParserState state) {
        if (state.in(m_types.C_TYPE_VARIABLE)) {
            state.popEndUntil(m_types.C_TYPE_VARIABLE).popEnd().advance()
                    .mark(m_types.C_SIG_EXPR)
                    .mark(m_types.C_SIG_ITEM);
        }
    }

    private void parseDotDot(@NotNull ParserState state) {
        if (state.is(m_types.C_OBJECT_FIELD)) {
            state.advance().popEnd();
        }
    }

    private void parsePipe(@NotNull ParserState state) {
        if (state.is(m_types.C_SCOPED_EXPR) && state.isParent(m_types.C_LET_DECLARATION)) {
            // let ( |>|<| ...
            return;
        }

        // Remove intermediate constructions
        //if (state.isComplete(m_types.C_IF_THEN_SCOPE)) {
        //    state.popEndUntil(m_types.C_IF).popEnd();
        //}
        //if (state.is(m_types.C_FUN_PARAM) && state.in(m_types.C_VARIANT_CONSTRUCTOR)) {
        //    state.popEndUntil(m_types.C_VARIANT_DECLARATION);
        //}
        if (state.in(m_types.C_PATTERN_MATCH_BODY)) {
            state.popEndUntil(m_types.C_PATTERN_MATCH_EXPR).popEnd().advance()
                    .mark(m_types.C_PATTERN_MATCH_EXPR);
        } else if (state.in(m_types.C_TYPE_BINDING)) {
            // type t = (|) V1 |>|<| ...
            // remap an upper symbol to a variant if first element is missing pipe
            //if (!state.is(m_types.C_TYPE_BINDING) && !state.is(m_types.C_VARIANT_DECLARATION)) {
            //    int bindingPos = state.indexOfComposite(m_types.C_TYPE_BINDING);
            //    ParserScope firstToken = state.findNext(bindingPos);
            //    if (firstToken != null && firstToken.isCompositeType(m_types.C_UPPER_SYMBOL)) {
            //        state.rollbackTo(bindingPos - 1);
            //        state.mark(m_types.C_VARIANT_DECLARATION).mark(m_types.C_UPPER_IDENTIFIER);
            //        return;
            //    }
            //}
            state.popEndUntil(m_types.C_TYPE_BINDING).advance()
                    .mark(m_types.C_VARIANT_DECLARATION);
        } else {
            //    if (state.isCurrentResolution(maybeFunctionParameters)) {
            //        state.popEnd().resolution(matchWith);
            //    } else
            if (state.in(m_types.C_PATTERN_MATCH_EXPR)) {
                // pattern group ::  | X |>|<| Y ...
                state.popEndUntil(m_types.C_PATTERN_MATCH_EXPR).popEnd();
            }

            // By default, a pattern match
            state.advance().mark(m_types.C_PATTERN_MATCH_EXPR);
        }
    }

    private void parseMatch(@NotNull ParserState state) {
        state.mark(m_types.C_MATCH_EXPR).advance()
                .mark(m_types.C_BINARY_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        state.mark(m_types.C_TRY_EXPR).advance()
                .mark(m_types.C_TRY_BODY);
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.in(m_types.C_FUNCTOR_RESULT)) {
            // A functor with constraints
            //  module Make (M : Input) : S |>with<| ...
            state.popEndUntil(m_types.C_FUNCTOR_RESULT).popEnd().advance()
                    .mark(m_types.C_CONSTRAINTS)
                    .mark(m_types.C_CONSTRAINT);
        } else if (state.in(m_types.C_MODULE_TYPE)) {
            // A module with a signature and constraints
            //  module G : sig ... end |>with<| ...
            //  module G : X |>with<| ...
            state.popEndUntil(m_types.C_MODULE_TYPE).popEnd().advance()
                    .mark(m_types.C_CONSTRAINTS)
                    .mark(m_types.C_CONSTRAINT);
        } else if (state.in(m_types.C_INCLUDE)) {
            // include with constraints ::  include M |>with<| ...
            state.mark(m_types.C_CONSTRAINTS).advance()
                    .mark(m_types.C_CONSTRAINT);
        } else if (state.in(m_types.C_TRY_BODY)) {
            // A try handler ::  try ... |>with<| ...
            state.popEndUntil(m_types.C_TRY_EXPR).advance()
                    .mark(m_types.C_TRY_HANDLERS)
                    .mark(m_types.C_TRY_HANDLER);
        } else if (state.in(m_types.C_BINARY_CONDITION)) {
            if (state.isPrevious(m_types.C_MATCH_EXPR, state.getIndex())) {
                // match ... |>with<| ...
                state.popEndUntil(m_types.C_MATCH_EXPR);
            }
        }
    }

    private void parseIf(@NotNull ParserState state) {
        // |>if<| ...
        state.mark(m_types.C_IF).advance()
                .mark(m_types.C_BINARY_CONDITION);
    }

    private void parseThen(@NotNull ParserState state) {
        if (!state.in(m_types.C_DIRECTIVE)) {
            // if ... |>then<| ...
            state.popEndUntil(m_types.C_IF).advance()
                    .mark(m_types.C_IF_THEN_SCOPE);
        }
    }

    private void parseElse(@NotNull ParserState state) {
        // if ... then ... |>else<| ...
        state.popEndUntil(m_types.C_IF).advance()
                .mark(m_types.C_IF_THEN_SCOPE);
    }

    private void parseStruct(@NotNull ParserState state) {
        if (state.is(m_types.C_FUNCTOR_DECLARATION)) {
            // module X (...) = |>struct<| ...
            state.markScope(m_types.C_FUNCTOR_BINDING, m_types.STRUCT);
        } else if (state.is(m_types.C_MODULE_BINDING)) {
            // module X = |>struct<| ...
            state.updateScopeToken(m_types.STRUCT);
        } else {
            state.markScope(m_types.C_STRUCT_EXPR, m_types.STRUCT);
        }
    }

    private void parseSig(@NotNull ParserState state) {
        //if (state.in(m_types.C_MODULE_DECLARATION)) {
        // This is the body of a module type
        // module type X = |>sig<| ...
        state.markScope(m_types.C_SIG_EXPR, m_types.SIG);
        //} else if (state.isCurrentResolution(moduleNamedColon)) {
        //    state.markScope(m_types.C_SIG_EXPR, m_types.SIG);
        //} else if (state.is(m_types.C_FUNCTOR_PARAM)) {
        //    state.markScope(m_types.C_SIG_EXPR, m_types.SIG);
        //}
    }

    private void parseSemi(@NotNull ParserState state) {
        if (state.isParent(m_types.C_OBJECT)) {
            // SEMI ends the field, and starts a new one
            state.popEnd().advance().mark(m_types.C_OBJECT_FIELD);
        } else if (state.in(m_types.C_RECORD_FIELD)) {
            // SEMI ends the field, and starts a new one
            state.popEndUntil(m_types.C_RECORD_FIELD).popEnd().advance();
            if (state.getTokenType() != m_types.RBRACE) {
                state.mark(m_types.C_RECORD_FIELD);
            }
        } else if (state.in(m_types.C_OBJECT_FIELD)) {
            // SEMI ends the field, and starts a new one
            state.popEndUntil(m_types.C_OBJECT_FIELD).popEnd().advance();
            if (state.getTokenType() != m_types.RBRACE) {
                state.mark(m_types.C_OBJECT_FIELD);
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
        if (state.in(m_types.C_TRY_HANDLER)) {
            state.popEndUntil(m_types.C_TRY_EXPR);
        } else if (state.inAny(m_types.C_LET_DECLARATION, m_types.C_PATTERN_MATCH_BODY)) {
            boolean isStart = state.isFound(m_types.C_LET_DECLARATION);
            state.popEndUntilIndex(state.getIndex());
            if (isStart) {
                state.popEnd();
            }
        } else {
            state.popEnd();
        }
    }

    private void parseObject(@NotNull ParserState state) {
        state.markScope(m_types.C_OBJECT, m_types.OBJECT);
    }

    private void parseBegin(@NotNull ParserState state) {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.BEGIN);
    }

    private void parseEnd(@NotNull ParserState state) {
        MarkerScope scope = state.popEndUntilOneOfElementType(m_types.BEGIN, m_types.SIG, m_types.STRUCT, m_types.OBJECT);

        state.advance().popEnd();

        if (scope != null) {
            //if (scope.isCompositeType(m_types.C_MODULE_TYPE)) {
            //    IElementType nextToken = state.getTokenType();
            //    if (nextToken == m_types.WITH) {
            //        state.advance().mark(m_types.C_CONSTRAINTS).mark(m_types.C_CONSTRAINT);
            //    }
            //} else
            if (/*scope.isScopeToken(m_types.STRUCT) &&*/ state.is(m_types.C_MODULE_DECLARATION)) {
                // module M = struct .. |>end<|
                state.popEnd();

                IElementType nextToken = state.getTokenType();
                if (nextToken == m_types.AND) {
                    // module M = struct .. end |>and<|
                    state.advance().mark(m_types.C_MODULE_DECLARATION).setStart();
                }
            }
            // else if (scope.isCompositeType(m_types.C_OBJECT)) {
            //    // Close a class
            //    state.popEnd();
            //} else if (scope.isCompositeType(m_types.C_OBJECT)) {
            //    // Close a class
            //    state.popEnd();
            //}
        }
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.is(m_types.C_FUNCTOR_DECLARATION)) {
            // module M (...) |> :<| ...
            state.advance()
                    .mark(m_types.C_FUNCTOR_RESULT);
        } else if (state.isParent(m_types.C_NAMED_PARAM)) {
            state.advance();
            if (state.getTokenType() == m_types.LPAREN) {
                // ?x |> :<| ( ...
                MarkerScope namedScope = state.getPrevious();
                state.updateScopeToken(namedScope, m_types.LPAREN).advance();
                //if (state.getTokenType() == m_types.LPAREN) {
                // a named param with signature ::  ?x : |>(<| ( ...
                //state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
                //}
            }

            //if (state.isParent(m_types.C_SIG_ITEM)) {
            // A named param in signature ::  let x : c|> :<| ..
            //state.mark(m_types.C_SIG_EXPR)
            //        .mark(m_types.C_SIG_ITEM);
            //                .markDummy(m_types /* for template types */);
            //}
        } else if (state.inAny(m_types.C_EXTERNAL_DECLARATION, m_types.C_CLASS_METHOD, m_types.C_VAL_DECLARATION, m_types.C_LET_DECLARATION)) {
            // external x |> : <| ...  OR  val x |> : <| ...  OR  let x |> : <| ...
            state.advance();
            if (state.getTokenType() == m_types.TYPE) {
                // Local type
                state.mark(m_types.C_TYPE_VARIABLE);
            } else {
                state.mark(m_types.C_SIG_EXPR)
                        .mark(m_types.C_SIG_ITEM);
            }
        } else if (state.in(m_types.C_MODULE_DECLARATION)) {
            // module M |> : <| ...
            state.advance();
            IElementType nextToken = state.getTokenType();
            if (nextToken == m_types.LPAREN) {
                state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).advance();
            }
            state.mark(m_types.C_MODULE_TYPE);
            //if (nextToken == m_types.LPAREN || nextToken == m_types.SIG) {
            //    state.updateScopeToken((ORTokenElementType) nextToken).advance();
            //}
        }
        // else if (state.is(m_types.C_CLASS_DECLARATION) || state.is(m_types.C_CLASS_METHOD)) {
        //    // class x |> : <| ...  OR  method |> : <| ...
        //    state.advance().mark(m_types.C_SIG_EXPR)
        //            .mark(m_types.C_SIG_ITEM)
        //            .markDummy(m_types /* for template types */);
        //} else if (state.is(m_types.C_OBJECT_FIELD)) {
        //    // < x |> : <| ...
        //    state.resolution(objectFieldNamed);
        //}
        else if (state.in(m_types.C_RECORD_FIELD)) {
            state.advance().mark(m_types.C_SIG_EXPR)
                    .mark(m_types.C_SIG_ITEM);
        }
        // else if (state.is(m_types.C_FUN_PARAM)) {
        //    if (state.previousElementType2 == m_types.QUESTION_MARK) {
        //        // an optional param ::  ?x |> : <| ...
        //        state.advance().markOptionalParenDummyScope(m_types, m_types.C_NAMED_PARAM);
        //    } else {
        //        state.advance().mark(m_types.C_SIG_EXPR)
        //                .mark(m_types.C_SIG_ITEM)
        //                .markDummy(m_types /* for template types */);
        //    }
        //}
        //else
        // else if (state.is(m_types.C_NAMED_PARAM)) {
        //    state.advance()
        //            .markOptionalParenDummyScope(m_types);
        //    if (state.isParent(m_types.C_SIG_ITEM)) {
        //        // A named param in signature ::  let x : c|> :<| ..
        //        state.mark(m_types.C_SIG_EXPR)
        //                .mark(m_types.C_SIG_ITEM)
        //                .markDummy(m_types /* for template types */);
        //    }
        //} else if (state.is(m_types.C_SCOPED_EXPR)) {
        //    // Try to find the context
        //    ParserScope context = state.findScopeContext(m_types);
        //    if (context != null && context.isCompositeType(m_types.C_NAMED_PARAM)) {
        //        // a named param with a type signature ::  let fn ?x(|>(<|x: .. ) ..
        //        state.advance()
        //                .mark(m_types.C_SIG_EXPR)
        //                .mark(m_types.C_SIG_ITEM)
        //                .markDummy(m_types /* for template types */);
        //    }
        //}
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        // First param ::  let f |>?<| ( x ...
        if (state.is(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR)) {
            state.mark(m_types.C_FUN_PARAM)
                    .mark(m_types.C_NAMED_PARAM);
        }
        // Start of a new optional parameter ::  let f x |>?<|(y ...
        else if (state.in(m_types.C_FUN_PARAM) && !state.hasScopeToken()) {
            state.popEndUntil(m_types.C_FUN_PARAM).popEnd()
                    .mark(m_types.C_FUN_PARAM)
                    .mark(m_types.C_NAMED_PARAM);
        }
        // Condition ?
        else if (state.is(m_types.C_BINARY_CONDITION) && !state.isParent(m_types.C_MATCH_EXPR)) {
            IElementType nextType = state.rawLookup(1);
            // ... |>?<| ... : ...
            if (nextType != m_types.LIDENT) {
                state.popEnd();
            }
        }
    }

    private void parseFunction(@NotNull ParserState state) {
        if (state.inAny(m_types.C_LET_BINDING, m_types.C_FUN_EXPR)) {
            if (state.isFound(m_types.C_LET_BINDING)) {
                state.mark(m_types.C_FUN_EXPR).advance()
                        .mark(m_types.C_FUN_BODY);
            }

            state.mark(m_types.C_MATCH_EXPR).advance();
            if (state.getTokenType() != m_types.PIPE) {
                state.mark(m_types.C_PATTERN_MATCH_EXPR);
            }
        }
    }

    private void parseFun(@NotNull ParserState state) {
        state.mark(m_types.C_FUN_EXPR).advance();
        if (state.getTokenType() != m_types.PIPE) {
            state.mark(m_types.C_PARAMETERS);
        }
    }

    private void parseEq(@NotNull ParserState state) { // zzz lot of in()
        // Remove intermediate constructions
        //if (state.isParent(m_types.C_SIG_ITEM) || (state.is(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR))) {
        //    state.popEnd();
        //    if (state.is(m_types.C_SIG_ITEM)) {
        //        state.popEnd();
        //    }
        //}
        //if (state.is(m_types.C_FUNCTOR_RESULT) || state.is(m_types.C_SIG_EXPR) || state.is(m_types.C_DECONSTRUCTION)) {
        //    state.popEnd();
        //}

        if (state.isParent(m_types.C_EXTERNAL_DECLARATION)) {
            // external ( |> = <| ) ...
            // nope
        } else if (state.in(m_types.C_NAMED_PARAM)) {
            // let fn ?(x |> = <| ...
            state.advance().mark(m_types.C_DEFAULT_VALUE);
        } else if (state.in(m_types.C_RECORD_FIELD)) {
            // { x |> = <| ... }
            // nope
        } else if (state.in(m_types.C_FOR_LOOP)) {
            // for x |> = <| ...
            // nope
        } else if (state.in(m_types.C_TYPE_DECLARATION, /*not*/m_types.C_TYPE_BINDING)) {
            // type t |> =<| ...
            state.popEndUntil(m_types.C_TYPE_DECLARATION).advance()
                    .mark(m_types.C_TYPE_BINDING);
        } else if (state.in(m_types.C_EXTERNAL_DECLARATION)) {
            // external e : sig |> = <| ...
            state.popEndUntil(m_types.C_SIG_EXPR).popEnd().advance();
        } else if (state.in(m_types.C_LET_DECLARATION)) {
            int letPos = state.getIndex();
            if (state.in(m_types.C_LET_BINDING, null, letPos, false)) {
                // in a function ::  let (x) y z |> = <| ...
                state.popEndUntil(m_types.C_FUN_EXPR).advance()
                        .mark(m_types.C_FUN_BODY);
            } else {
                // let x |> = <| ...
                state.popEndUntilStart().advance().
                        mark(m_types.C_LET_BINDING);
            }

        } else if (state.in(m_types.C_MODULE_DECLARATION)) {
            // module M |> = <| ...
            state.popEndUntil(m_types.C_MODULE_DECLARATION).advance()
                    .mark(m_types.C_MODULE_BINDING);
        } else if (state.in(m_types.C_FUNCTOR_RESULT)) {
            state.popEndUntil(m_types.C_FUNCTOR_RESULT).popEnd();
        } else if (state.in(m_types.C_CONSTRAINTS)) {
            state.popEndUntil(m_types.C_CONSTRAINTS).popEnd();
        }
        // else if (state.is(m_types.C_CONSTRAINT) && (state.isGrandPreviousResolution(functorNamedColon) || state.isGrandParent(m_types.C_MODULE_DECLARATION))) {
        //    IElementType nextElementType = state.lookAhead(1);
        //    if (nextElementType == m_types.STRUCT) {
        //        // Functor constraints ::  module M (...) : S with ... |> = <| struct ... end
        //        if (state.isGrandPreviousResolution(functorNamedColon)) {
        //            state.popEnd().popEnd().resolution(functorNamedEq);
        //        } else {
        //            state.popEndUntil(m_types.C_CONSTRAINTS).popEnd();
        //        }
        //    }
        //} else if (state.isComplete(m_types.C_FUN_PARAM)) {
        //    state.popEndUntil(m_types.C_FUN_EXPR).advance().mark(m_types.C_FUN_BODY);
        //}
    }

    private void parseOf(@NotNull ParserState state) {
        if (state.isParent(m_types.C_VARIANT_DECLARATION)) {
            // Variant params ::  type t = | Variant |>of<| ..
            state.advance().mark(m_types.C_VARIANT_CONSTRUCTOR).mark(m_types.C_FUN_PARAM);
        }
        //else if (state.is(m_types.C_MODULE_DECLARATION) && state.previousElementType1 == m_types.TYPE) {
        // extracting a module type ::  module type |>of<| X ...
        //state.resolution(moduleTypeExtraction);
        //}
    }

    private void parseStar(@NotNull ParserState state) {
        if (state.in(m_types.C_FUN_PARAM) && state.in(m_types.C_VARIANT_CONSTRUCTOR)) {
            // type t = | Variant of x |>*<| y ..
            state.popEndUntil(m_types.C_FUN_PARAM).popEnd().advance()
                    .mark(m_types.C_FUN_PARAM);
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
            // Overloading an operator ::  external |>(<| ...
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.isParent(m_types.C_MODULE_DECLARATION) && state.previousElementType1 == m_types.UIDENT) {
            //  module M |>(<| ... )
            state.rollbackTo(state.getIndex())
                    .mark(m_types.C_FUNCTOR_DECLARATION).advance();
        } else if (state.isParent(m_types.C_FUNCTOR_DECLARATION)) {
            // module M |>(<| ... )
            state.popEnd()
                    .markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance()
                    .mark(m_types.C_FUN_PARAM);
        } else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Detecting a local open ::  M1.M2. |>(<| ... )
            state.popEndUntil(m_types.C_PATH).popEnd().
                    markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN);
        } else if (state.in(m_types.C_FUNCTOR_CALL)) {
            // module X = M |>(<| ...
            state.markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance()
                    .mark(m_types.C_FUN_PARAM);
        } else if (state.previousElementType1 == m_types.UIDENT && state.in(m_types.C_MODULE_BINDING)) {
            // Functor call detected
            state.rollbackTo(state.getIndex());
            state.mark(m_types.C_MODULE_BINDING).mark((m_types.C_FUNCTOR_CALL));
        } else if (state.is(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR)) {
            // Start of the first parameter ::  let x |>(<| ...
            state.markScope(m_types.C_FUN_PARAM, m_types.LPAREN);
        } else if (state.is(m_types.C_NAMED_PARAM)) {
            // A named param with default value ::  let fn ?|>(<| x ... )
            state.updateScopeToken(m_types.LPAREN);
        } else if (state.in(m_types.C_CLASS_DECLARATION)) {
            // class x |>(<| ...
            state.markScope(m_types.C_CLASS_CONSTR, m_types.LPAREN);
        } else if (state.inAny(m_types.C_FUN_PARAM, m_types.C_SIG_ITEM, m_types.C_NAMED_PARAM)
                /*&& !state.hasScopeToken()
                && state.previousElementType1 != m_types.QUESTION_MARK*/) {
            if (state.isFound(m_types.C_FUN_PARAM)) {
                // Start of a new parameter:: let f xxx |>(<| ..tuple? ) = ..
                state.popEndUntil(m_types.C_FUN_PARAM).popEnd()
                        .mark(m_types.C_FUN_PARAM)
                        .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
            } else if (state.is(m_types.C_SIG_ITEM) && !state.hasScopeToken()) {
                state.updateScopeToken(m_types.LPAREN);
            } else {
                state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
            }
        }
        // else if (state.is(m_types.C_MODULE_DECLARATION)) {
        //    // This is a functor ::  module Make |>(<| ... )
        //    state.updateCurrentCompositeElementType(m_types.C_FUNCTOR_DECLARATION)
        //            .markScope(m_types.C_PARAMETERS, m_types.LPAREN)
        //            .advance()
        //            .mark(m_types.C_FUNCTOR_PARAM);
        //}
        else {
            state.inAny(m_types.C_OPEN, m_types.C_INCLUDE);
            int openPos = state.getIndex();
            if (openPos >= 0) {
                // a functor call inside open/include ::  open/include M |>(<| ...
                state.markBefore(openPos - 1, m_types.C_FUNCTOR_CALL)
                        .markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance()
                        .mark(m_types.C_FUN_PARAM);
            } else {
                state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
            }
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        MarkerScope parenScope = state.popEndUntilScopeToken(m_types.LPAREN);
        if (parenScope == null) {
            return;
        }

        state.advance();

        int scopeLength = parenScope.getLength();
        if (scopeLength <= 3 && state.isParent(m_types.C_LET_DECLARATION)) {
            // unit ::  let ()
            parenScope.updateCompositeElementType(m_types.C_UNIT);
        }

        IElementType nextToken = state.getTokenType();
        if (nextToken == m_types.OPTION) {
            state.markBefore(0, m_types.C_OPTION);
        }
        //if (nextToken == m_types.LT || nextToken == m_types.LT_OR_EQUAL) {
        //     are we in a binary op ?
        //state.precedeScope(m_types.C_BINARY_CONDITION);
        //} else if (!state.isParent(m_types.C_NAMED_PARAM)) {
        //    if (nextToken == m_types.QUESTION_MARK && !state.isParent(m_types.C_TERNARY) && !parenScope.isCompositeType(m_types.C_NAMED_PARAM) && !parenScope.isCompositeType(m_types.C_FUN_PARAM) && !state.isParent(m_types.C_FUN_PARAM)) {
        //         ( ... |>)<| ? ...
        //state.updateCurrentCompositeElementType(m_types.C_BINARY_CONDITION)
        //        .precedeScope(m_types.C_TERNARY);
        //}
        //}

        state.popEnd();

        if (state.is(m_types.C_NAMED_PARAM) && nextToken != m_types.EQ) {
            state.popEnd();
        } else if (parenScope.isCompositeType(m_types.C_SCOPED_EXPR) && state.is(m_types.C_LET_DECLARATION) && nextToken != m_types.EQ) {
            // This is a custom infix operator
            state.mark(m_types.C_PARAMETERS);
        } else if (state.is(m_types.C_OPTION)) {
            state.advance().popEnd();
        } else if (nextToken == m_types.RIGHT_ARROW && parenScope.isCompositeType(m_types.C_SIG_ITEM)) {
            state.advance().mark(m_types.C_SIG_ITEM);
        } else if (state.is(m_types.C_FUN_PARAM)) {
            state.popEnd();
        } else if (nextToken == m_types.AND) {
            // close intermediate elements
            state.popEndUntilStart();
            if (state.in(m_types.C_LET_BINDING)) {
                state.popEndUntil(m_types.C_LET_BINDING);
            }
        }
        //else if (state.is(m_types.C_SIG_ITEM) && state.in(m_types.C_NAMED_PARAM) && nextToken == m_types.EQ) {
        // a named typed param with default value ::  ?x:((x:... |>)<| = ... )
        //state.popEnd();
        //}
    }

    private void parseLBrace(@NotNull ParserState state) {
        // let fn |>{<| ... } = ...
        if (state.is(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR)) {
            state.mark(m_types.C_FUN_PARAM);
        }

        state.markScope(m_types.C_RECORD_EXPR, m_types.LBRACE).advance()
                .mark(m_types.C_RECORD_FIELD);
    }

    private void parseRBrace(@NotNull ParserState state) {
        MarkerScope scope = state.popEndUntilScopeToken(m_types.LBRACE);
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
            // https://ocaml.org/manual/attributes.html

            // |> [ <| @?? ...
            if (nextElementType == m_types.ARROBASE) {
                state.markScope(m_types.C_ANNOTATION, m_types.LBRACKET);
            } else if (nextElementType == m_types.ARROBASE_2) {
                // attribute attached to a 'block' expression
                if (state.inAny(m_types.C_LET_BINDING, m_types.C_SIG_EXPR)) {
                    if (state.isFound(m_types.C_SIG_EXPR)) {
                        // block attribute inside a signature
                        state.popEnd();
                    }
                    state.popEndUntilIndex(state.getIndex());
                }
                state.markScope(m_types.C_ANNOTATION, m_types.LBRACKET);
            } else /*if (nextElementType == m_types.ARROBASE_3)*/ {
                // floating attribute
                endLikeSemi(state);
                state.markScope(m_types.C_ANNOTATION, m_types.LBRACKET);
            }
            //    else if (state.isOneOf(m_types.C_FUN_BODY, m_types.C_FUN_EXPR)) {
            //        // block attribute inside a function
            //        state.popEndUntil(m_types.C_FUN_EXPR).popEnd();
            //    }
        } else {
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
        }
    }

    private void parseRBracket(@NotNull ParserState state) {
        state.popEndUntilScopeToken(m_types.LBRACKET);
        state.advance().popEnd();
    }

    private void parseNumber(@NotNull ParserState state) {
        if (state.is(m_types.C_FUN_PARAM)) {
            // Start of a new parameter :: .. fn x |>1<| ..
            state.popEnd().mark(m_types.C_FUN_PARAM);
        } else if (state.is(m_types.C_PARAMETERS) && state.in(m_types.C_FUN_CALL)) {
            state.mark(m_types.C_FUN_PARAM);
        }
        //else if (state.previousElementType1 == m_types.C_LOWER_SYMBOL) {
        // fn |>1<|
        //}
    }

    private void parseLIdent(@NotNull ParserState state) {   // zzz merge symbol/ident ?
        if (state.in(m_types.C_PATH)) {
            state.popEndUntil(m_types.C_PATH).popEnd();
        }

        if (state.is(m_types.C_LET_DECLARATION)) {
            // let |>x<| ...
            IElementType nextToken = state.lookAhead(1);
            if (nextToken == m_types.COMMA) {
                // A deconstruction without parenthesis
                state.mark(m_types.C_DECONSTRUCTION);
            }
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
            if (nextToken != m_types.COMMA && nextToken != m_types.EQ && nextToken != m_types.COLON) {
                // This is a function, we need to create the let binding now, to be in sync with reason
                //  let |>x<| y z = ...  vs    let x = y z => ...
                state.mark(m_types.C_LET_BINDING).
                        mark(m_types.C_FUN_EXPR).
                        mark(m_types.C_PARAMETERS);
            }
        } else if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
            // external |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_TYPE_DECLARATION)) {
            // type |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_CLASS_METHOD)) {
            // ... object method |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_VAL_DECLARATION)) {
            // val |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_CLASS_DECLARATION)) {
            // class |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_RECORD_FIELD)) {
            // { |>x<| : ... }
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_MACRO_NAME)) {
            // [@ |>x.y<| ... ]
            state.advance();
            while (state.getTokenType() == m_types.DOT) {
                state.advance();
                if (state.getTokenType() == m_types.LIDENT) {
                    state.advance();
                }
            }
            state.popEnd();
        } else if (state.is(m_types.C_DECONSTRUCTION)) {
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        }
        //else if (state.is(m_types.C_PARAMETERS) && (state.isParent(m_types.C_FUN_CALL) || state.isParent(m_types.C_FUN_EXPR))) {
        // let fn |>x<| =
        //state.mark(m_types.C_PARAMETERS)
        //        .wrapWith(m_types.C_LOWER_IDENTIFIER);
        //}
        else if ((state.is(m_types.C_FUN_PARAM) /*&& !state.isPrevious(m_types.C_FUN_CALL_PARAMS)*/) || state.is(m_types.C_NAMED_PARAM) || (state.isDummy() && state.isParent(m_types.C_NAMED_PARAM))) {
            //;
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (!state.is(m_types.C_LET_BINDING) && !state.is(m_types.C_FUN_BODY) && !state.in(m_types.C_SIG_ITEM)
                && state.in(m_types.C_PARAMETERS)
                && !state.hasScopeToken()
                && state.previousElementType1 != m_types.AS
                && state.previousElementType1 != m_types.OF
                && state.previousElementType1 != m_types.STAR
                && state.previousElementType1 != m_types.QUESTION_MARK) {
            // Start of a new parameter
            //    .. ( xxx |>yyy<| ) ..
            state.popEndUntil(m_types.C_PARAMETERS)
                    .mark(m_types.C_FUN_PARAM).wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else {
            IElementType nextTokenType = state.lookAhead(1);

            if (nextTokenType == m_types.COLON && state.is(m_types.C_SIG_ITEM)) {
                // let fn: |>x<| : ...
                state.mark(m_types.C_NAMED_PARAM);
            } else if (!state.in(m_types.C_SIG_ITEM) && !state.is(m_types.C_TYPE_VARIABLE) && !state.is(m_types.C_CONSTRAINT) && !state.is(m_types.C_BINARY_CONDITION)
                    && !state.is(m_types.C_CLASS_FIELD) && !state.in(m_types.C_TYPE_BINDING) &&
                    !state.is(m_types.C_PARAMETERS)
            ) {
                if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.INT_VALUE || nextTokenType == m_types.FLOAT_VALUE) {
                    if (state.is(m_types.C_SCOPED_EXPR) || !state.in(m_types.C_FUN_CALL)) {
                        // a function call ::  |>fn<| ...
                        state.mark(m_types.C_FUN_CALL).wrapWith(m_types.C_LOWER_SYMBOL)
                                .mark(m_types.C_PARAMETERS);
                        return;
                    }
                }
            }

            state.wrapWith(m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        IElementType nextToken = state.lookAhead(1);
        MarkerScope latestScope = null;
        if (nextToken == m_types.DOT && !state.in(m_types.C_PATH)) {
            state.mark(m_types.C_PATH);
        } else if (nextToken != m_types.DOT && state.in(m_types.C_PATH)) {
            state.popEndUntil(m_types.C_PATH);
            latestScope = state.getLatestScope();
            state.popEnd();
        }

        if (state.is(m_types.C_MODULE_DECLARATION) && state.previousElementType1 != m_types.OF && (latestScope == null || !latestScope.isCompositeType(m_types.C_PATH))) {
            // module |>M<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_FUNCTOR_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_OPEN) || state.is(m_types.C_INCLUDE)) {
            // It is a module name/path, or might be a functor call  ::  open/include |>M<| ...
            state.wrapWith(m_types.C_UPPER_SYMBOL);

            if (nextToken != m_types.DOT && nextToken != m_types.LPAREN && nextToken != m_types.WITH) {
                // Not a path, nor a functor, must close that open
                state.popEndUntilOneOf(m_types.C_OPEN, m_types.C_INCLUDE);
                state.popEnd();
            }
            if (nextToken == m_types.IN) {
                // let _ = let open M |>in<| ..
                state.advance();
            }
        } else if (state.is(m_types.C_TYPE_BINDING)) {
            // Variant declaration without a pipe ::  type t = |>X<| | ...
            state.mark(m_types.C_VARIANT_DECLARATION).wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_VARIANT_DECLARATION)) {
            // Declaring a variant  ::  type t = | |>X<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_EXCEPTION_DECLARATION)) {
            // Declaring an exception  ::  exception |>X<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else {
            if (((state.is(m_types.C_PATTERN_MATCH_EXPR) || state.is(m_types.C_LET_BINDING))) && nextToken != m_types.DOT) {
                // Pattern matching a variant or using it
                // match c with | |>X<| ... / let x = |>X<| ...
                state.remapCurrentToken(m_types.VARIANT_NAME).wrapWith(m_types.C_VARIANT);
                return;
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
            state.mark(m_types.C_OPEN).setStart();
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
        if (state.is(m_types.C_MODULE_DECLARATION)) {
            // module |>type<| M = ...
        } else if (state.is(m_types.C_TYPE_VARIABLE)) {
            // let x : |>type<| ...
        } else if (state.is(m_types.C_CLASS_DECLARATION)) {
            // class |>type<| ...
        }
        // else if (state.is(m_types.C_CONSTRAINT) &&
        //        (state.previousElementType1 == m_types.WITH || state.previousElementType1 == m_types.AND)) {
        // module M : X with |>type<| t ...
        // include X with |>type<| t ...
        // ?
        //}
        else {
            if (state.previousElementType1 == m_types.AND && state.in(m_types.C_CONSTRAINT)) {
                state.popEndUntil(m_types.C_CONSTRAINT);
            } else if (!state.is(m_types.C_CONSTRAINT)) {
                state.popEndUntilScope();
            }
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
        boolean insideClass = state.in(m_types.C_OBJECT);
        if (insideClass) {
            state.popEndUntil(m_types.C_OBJECT);
        } else {
            state.popEndUntilScope();
            //endLikeSemi(state);
        }

        state.mark(insideClass ? m_types.C_CLASS_FIELD : m_types.C_VAL_DECLARATION).setStart();
    }

    private void parseMethod(@NotNull ParserState state) {
        //state.popEndUntilScope();
        state.popEndUntil(m_types.C_OBJECT);
        state.mark(m_types.C_CLASS_METHOD).setStart();
    }

    private void parseLet(@NotNull ParserState state) {
        if (!state.is(m_types.C_TRY_BODY) && state.previousElementType1 != m_types.RIGHT_ARROW) {
            //state.popEndUntilScope(); // zzz
            endLikeSemi(state);
        }
        state.mark(m_types.C_LET_DECLARATION).setStart();
    }

    private void parseModule(@NotNull ParserState state) {
        if (state.is(m_types.C_LET_DECLARATION)) {
            state.updateCurrentCompositeElementType(m_types.C_MODULE_DECLARATION);
        } else if (!state.is(m_types.C_MACRO_NAME)) {
            if (!state.is(m_types.C_MODULE_TYPE)) {
                state.popEndUntilScope();
            }
            state.mark(m_types.C_MODULE_DECLARATION).setStart();
        }
    }

    private void parseClass(@NotNull ParserState state) {
        endLikeSemi(state); // zzz ???
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
            MarkerScope markerScope = state.getLatestScope();
            while (!state.isRoot(markerScope) && !markerScope.hasScope()) {
                state.popEnd();
                markerScope = state.getLatestScope();
            }
        }
    }
}
