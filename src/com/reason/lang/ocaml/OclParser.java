package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;

public class OclParser extends CommonParser<OclTypes> {
    public OclParser(boolean isSafe) {
        super(isSafe, OclTypes.INSTANCE);
    }

    public static ASTNode parseOcamlNode(@NotNull ILazyParseableElementType root, @NotNull ASTNode chameleon) {
        PsiElement parentElement = chameleon.getTreeParent().getPsi();
        Project project = parentElement.getProject();

        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new OclLexer(), root.getLanguage(), chameleon.getText());
        OclParser parser = new OclParser(true);

        return parser.parse(root, builder).getFirstChildNode();
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType;

        long parseStart = System.currentTimeMillis();

        while (true) {
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            long parseTime = System.currentTimeMillis();
            if (5000 < parseTime - parseStart) {
                if (myIsSafe) { // Don't do that in tests
                    state.error("CANCEL");
                    LOG.error("CANCEL OCAML PARSING");
                    break;
                }
            }

            if (tokenType == myTypes.SEMI) {
                parseSemi(state);
            } else if (tokenType == myTypes.IN) {
                parseIn(state);
            } else if (tokenType == myTypes.RIGHT_ARROW) {
                parseRightArrow(state);
            } else if (tokenType == myTypes.PIPE) {
                parsePipe(state);
            } else if (tokenType == myTypes.EQ) {
                parseEq(state);
            } else if (tokenType == myTypes.OF) {
                parseOf(state);
            } else if (tokenType == myTypes.STAR) {
                parseStar(state);
            } else if (tokenType == myTypes.COLON) {
                parseColon(state);
            } else if (tokenType == myTypes.QUESTION_MARK) {
                parseQuestionMark(state);
            } else if (tokenType == myTypes.INT_VALUE) {
                parseNumber(state);
            } else if (tokenType == myTypes.FLOAT_VALUE) {
                parseNumber(state);
            } else if (tokenType == myTypes.STRING_VALUE) {
                parseStringValue(state);
            } else if (tokenType == myTypes.LIDENT) {
                parseLIdent(state);
            } else if (tokenType == myTypes.UIDENT) {
                parseUIdent(state);
            } else if (tokenType == myTypes.SIG) {
                parseSig(state);
            } else if (tokenType == myTypes.OBJECT) {
                parseObject(state);
            } else if (tokenType == myTypes.IF) {
                parseIf(state);
            } else if (tokenType == myTypes.THEN) {
                parseThen(state);
            } else if (tokenType == myTypes.ELSE) {
                parseElse(state);
            } else if (tokenType == myTypes.MATCH) {
                parseMatch(state);
            } else if (tokenType == myTypes.TRY) {
                parseTry(state);
            } else if (tokenType == myTypes.WITH) {
                parseWith(state);
            } else if (tokenType == myTypes.AND) {
                parseAnd(state);
            } else if (tokenType == myTypes.DOT) {
                parseDot(state);
            } else if (tokenType == myTypes.DOTDOT) {
                parseDotDot(state);
            } else if (tokenType == myTypes.FUNCTION) { // function is a shortcut for a pattern match
                parseFunction(state);
            } else if (tokenType == myTypes.FUN) {
                parseFun(state);
            } else if (tokenType == myTypes.ASSERT) {
                parseAssert(state);
            } else if (tokenType == myTypes.RAISE) {
                parseRaise(state);
            } else if (tokenType == myTypes.COMMA) {
                parseComma(state);
            } else if (tokenType == myTypes.ARROBASE) {
                parseArrobase(state);
            } else if (tokenType == myTypes.ARROBASE_2) {
                parseArrobase2(state);
            } else if (tokenType == myTypes.ARROBASE_3) {
                parseArrobase3(state);
            } else if (tokenType == myTypes.OPTION) {
                parseOption(state);
            }
            // while ... do ... done
            else if (tokenType == myTypes.WHILE) {
                parseWhile(state);
            }
            // for ... to ... do ... done
            else if (tokenType == myTypes.FOR) {
                parseFor(state);
            }
            // do ... done
            else if (tokenType == myTypes.DO) {
                parseDo(state);
            } else if (tokenType == myTypes.DONE) {
                parseDone(state);
            }
            // begin/struct ... end
            else if (tokenType == myTypes.BEGIN) {
                parseBegin(state);
            } else if (tokenType == myTypes.STRUCT) {
                parseStruct(state);
            } else if (tokenType == myTypes.END) {
                parseEnd(state);
            }
            // ( ... )
            else if (tokenType == myTypes.LPAREN) {
                parseLParen(state);
            } else if (tokenType == myTypes.RPAREN) {
                parseRParen(state);
            }
            // { ... }
            else if (tokenType == myTypes.LBRACE) {
                parseLBrace(state);
            } else if (tokenType == myTypes.RBRACE) {
                parseRBrace(state);
            }
            // [ ... ]
            else if (tokenType == myTypes.LBRACKET) {
                parseLBracket(state);
            } else if (tokenType == myTypes.RBRACKET) {
                parseRBracket(state);
            }
            // [| ... |]
            else if (tokenType == myTypes.LARRAY) {
                parseLArray(state);
            } else if (tokenType == myTypes.RARRAY) {
                parseRArray(state);
            }
            // < ... >
            else if (tokenType == myTypes.LT) {
                parseLt(state);
            } else if (tokenType == myTypes.GT) {
                parseGt(state);
            }
            // Starts expression
            else if (tokenType == myTypes.OPEN) {
                parseOpen(state);
            } else if (tokenType == myTypes.INCLUDE) {
                parseInclude(state);
            } else if (tokenType == myTypes.EXTERNAL) {
                parseExternal(state);
            } else if (tokenType == myTypes.TYPE) {
                parseType(state);
            } else if (tokenType == myTypes.MODULE) {
                parseModule(state);
            } else if (tokenType == myTypes.CLASS) {
                parseClass(state);
            } else if (tokenType == myTypes.LET) {
                parseLet(state);
            } else if (tokenType == myTypes.VAL) {
                parseVal(state);
            } else if (tokenType == myTypes.METHOD) {
                parseMethod(state);
            } else if (tokenType == myTypes.EXCEPTION) {
                parseException(state);
            } else if (tokenType == myTypes.DIRECTIVE_IF) {
                parseDirectiveIf(state);
            } else if (tokenType == myTypes.DIRECTIVE_ELSE) {
                parseDirectiveElse(/*builder,*/ state);
            } else if (tokenType == myTypes.DIRECTIVE_ELIF) {
                parseDirectiveElif(/*builder,*/ state);
            } else if (tokenType == myTypes.DIRECTIVE_END || tokenType == myTypes.DIRECTIVE_ENDIF) {
                parseDirectiveEnd(/*builder,*/ state);
            }

            if (state.dontMove) {
                state.dontMove = false;
            } else {
                builder.advanceLexer();
            }
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS) ) {
            state.mark(myTypes.C_FUN_PARAM).advance().popEnd();
        } else if (state.is(myTypes.C_FUN_PARAM)) {
            state.popEnd().mark(myTypes.C_FUN_PARAM).advance().popEnd();
        }
    }

    private void parseOption(@NotNull ParserState state) {
        if (state.strictlyInAny(myTypes.C_TYPE_BINDING, myTypes.C_SIG_ITEM)) {
            // in type      :  type t = xxx |>option<|
            // or signature :  ... -> xxx |>option<| ...
            int pos = state.getIndex();
            if (pos > 0) {
                state.markBefore(pos - 1, myTypes.C_OPTION);
            }
        }
    }

    private void parseRaise(@NotNull ParserState state) {
        if (state.is(myTypes.C_EXTERNAL_DECLARATION)) {
            // external |>raise<| ...
            state.remapCurrentToken(myTypes.LIDENT).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.in(myTypes.C_TUPLE)) {
            // a tuple
            state.popEndUntilFoundIndex();
        } else if (state.inAny( // all same priority
                myTypes.C_LET_DECLARATION, myTypes.C_SIG_ITEM, myTypes.C_MATCH_EXPR, myTypes.C_FUN_PARAM, myTypes.C_SCOPED_EXPR
        )) {

            if (state.isFound(myTypes.C_LET_DECLARATION)) {
                if (state.in(myTypes.C_DECONSTRUCTION)) {
                    state.popEndUntilFoundIndex();
                } else if (state.isDone(myTypes.C_DECONSTRUCTION)) { // nested deconstructions
                    // let (a, b) |>,<| ... = ...
                    state.markBefore(0, myTypes.C_DECONSTRUCTION);
                }
            } else if (state.isFound(myTypes.C_SCOPED_EXPR)) {
                Marker blockScope = state.find(state.getIndex());
                Marker parentScope = state.find(state.getIndex() + 1);
                if (blockScope != null && parentScope != null) {
                    if (parentScope.isCompositeType(myTypes.C_LET_DECLARATION)) {
                        // let (x |>,<| ... )
                        // We need to do it again because lower symbols must be wrapped with identifiers
                        //int letPos = state.indexOfComposite(m_types.C_LET_DECLARATION);
                        state.rollbackTo(state.getIndex());
                        state.mark(myTypes.C_DECONSTRUCTION);
                        if (state.getTokenType() == myTypes.LPAREN) {
                            state.updateScopeToken(myTypes.LPAREN).advance();
                        }
                    } else if (parentScope.isCompositeType(myTypes.C_FUN_PARAM)) {
                        // a tuple ::  let fn (x |>,<| ... ) ...
                        blockScope.updateCompositeType(myTypes.C_TUPLE);
                        state.popEndUntil(myTypes.C_TUPLE);
                    }
                }
            }
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        if (state.is(myTypes.C_ANNOTATION)) {
            state.mark(myTypes.C_MACRO_NAME);
        }
    }

    private void parseArrobase2(@NotNull ParserState state) {
        if (state.is(myTypes.C_ANNOTATION)) {
            state.mark(myTypes.C_MACRO_NAME);
        }
    }

    private void parseArrobase3(@NotNull ParserState state) {
        if (state.is(myTypes.C_ANNOTATION)) {
            state.mark(myTypes.C_MACRO_NAME);
        }
    }

    private void parseLt(@NotNull ParserState state) {
        if (state.is(myTypes.C_SIG_ITEM) || state.in(myTypes.C_TYPE_BINDING) || state.is(myTypes.C_OBJECT_FIELD)) {
            // |> < <| .. > ..
            state.markScope(myTypes.C_OBJECT, myTypes.LT).advance()
                    .mark(myTypes.C_OBJECT_FIELD);
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.in(myTypes.C_OBJECT)) {
            state.popEndUntil(myTypes.C_OBJECT);
            state.advance().end();
            state.popEnd();
        }
    }

    private void parseWhile(@NotNull ParserState state) {
        state.mark(myTypes.C_WHILE).advance()
                .mark(myTypes.C_BINARY_CONDITION);
    }

    private void parseFor(@NotNull ParserState state) {
        state.mark(myTypes.C_FOR_LOOP);
    }

    private void parseDo(@NotNull ParserState state) {
        if (state.in(myTypes.C_BINARY_CONDITION)) {
            state.popEndUntil(myTypes.C_BINARY_CONDITION).popEnd();
        }

        if (state.strictlyInAny(myTypes.C_WHILE, myTypes.C_FOR_LOOP)) {
            state.popEndUntilFoundIndex()
                    .markScope(myTypes.C_SCOPED_EXPR, myTypes.DO);
        } else {
            state.markScope(myTypes.C_DO_LOOP, myTypes.DO);
        }
    }

    private void parseDone(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.DO);
        if (scope != null) {
            state.advance().popEnd();
        }

        //if (state.inAny(m_types.C_DO_LOOP, m_types.C_FOR_LOOP)) {
        //    state.popEndUntilFoundIndex().
        //            advance().popEnd();
        //}
        //if (state.is(m_types.C_WHILE)) {
        //    state.popEnd();
        //}
    }

    private void parseRightArrow(@NotNull ParserState state) {
        if (state.is(myTypes.C_SIG_EXPR)) {
            state.advance();
            if (state.getTokenType() == myTypes.LPAREN) {
                state.markDummyParenthesisScope(myTypes).advance();
            }
            state.mark(myTypes.C_SIG_ITEM);
        } else if (state.strictlyIn(myTypes.C_SIG_ITEM)) {
            state.popEndUntilFoundIndex().popEnd();
            if (state.in(myTypes.C_NAMED_PARAM)) { // can't have an arrow in a named param signature
                // let fn x:int |>-><| y:int
                state.popEnd().popEndUntil(myTypes.C_SIG_EXPR);
            }
            state.advance();
            if (state.getTokenType() == myTypes.LPAREN) {
                state.markDummyParenthesisScope(myTypes).advance();
            }
            state.mark(myTypes.C_SIG_ITEM);
        }
        // same priority
        else if (state.inAny(
                myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_FUN_EXPR
        )) {

            if (state.isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                // | ... |>-><|
                state.popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).advance()
                        .mark(myTypes.C_PATTERN_MATCH_BODY);
            } else if (state.isFound(myTypes.C_FUN_EXPR)) {
                // fun ... |>-><| ...
                state.popEndUntil(myTypes.C_FUN_EXPR).advance()
                        .mark(myTypes.C_FUN_BODY);
            }

        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(myTypes.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.in(myTypes.C_CONSTRAINT)) {
            state.popEndUntil(myTypes.C_CONSTRAINT).popEnd().advance()
                    .mark(myTypes.C_CONSTRAINT);
        } else if (state.inAny(myTypes.C_LET_DECLARATION, myTypes.C_TYPE_DECLARATION)) {
            // pop scopes until a chainable expression is found
            state.popEndUntilIndex(state.getIndex());
            Marker marker = state.getLatestMarker();

            state.popEnd().advance();
            if (marker != null) {
                if (marker.isCompositeType(myTypes.C_LET_DECLARATION)) {
                    state.mark(myTypes.C_LET_DECLARATION).setStart();
                } else if (marker.isCompositeType(myTypes.C_TYPE_DECLARATION)) {
                    state.mark(myTypes.C_TYPE_DECLARATION).setStart();
                }
            }
        }
    }

    private void parseDot(@NotNull ParserState state) {
        if (state.in(myTypes.C_TYPE_VARIABLE)) {
            state.popEndUntil(myTypes.C_TYPE_VARIABLE).popEnd().advance()
                    .mark(myTypes.C_SIG_EXPR)
                    .mark(myTypes.C_SIG_ITEM);
        }
    }

    private void parseDotDot(@NotNull ParserState state) {
        if (state.is(myTypes.C_OBJECT_FIELD)) {
            state.advance().popEnd();
        }
    }

    private void parsePipe(@NotNull ParserState state) {
        if (state.is(myTypes.C_SCOPED_EXPR) && state.isParent(myTypes.C_LET_DECLARATION)) {
            // let ( |>|<| ...
            return;
        }

        if (state.in(myTypes.C_PATTERN_MATCH_BODY)) {
            state.popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd().advance()
                    .mark(myTypes.C_PATTERN_MATCH_EXPR);
        } else if (state.in(myTypes.C_TYPE_BINDING)) { // remap an upper symbol to a variant if first element is missing pipe
            // type t = (|) V1 |>|<| ...
            state.popEndUntil(myTypes.C_TYPE_BINDING).advance()
                    .mark(myTypes.C_VARIANT_DECLARATION);
        } else {
            if (state.in(myTypes.C_PATTERN_MATCH_EXPR)) { // pattern group
                // | X |>|<| Y ...
                state.popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd();
            }

            // By default, a pattern match
            state.advance().mark(myTypes.C_PATTERN_MATCH_EXPR);
        }
    }

    private void parseMatch(@NotNull ParserState state) {
        state.mark(myTypes.C_MATCH_EXPR).advance()
                .mark(myTypes.C_BINARY_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        state.mark(myTypes.C_TRY_EXPR).advance()
                .mark(myTypes.C_TRY_BODY);
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.in(myTypes.C_FUNCTOR_RESULT)) { // A functor with constraints
            //  module Make (M : Input) : S |>with<| ...
            state.popEndUntil(myTypes.C_FUNCTOR_RESULT).popEnd().advance()
                    .mark(myTypes.C_CONSTRAINTS)
                    .mark(myTypes.C_CONSTRAINT);
        } else if (state.in(myTypes.C_MODULE_TYPE)) { // A module with a signature and constraints
            //  module G : sig ... end |>with<| ...
            //  module G : X |>with<| ...
            state.popEndUntil(myTypes.C_MODULE_TYPE).popEnd().advance()
                    .mark(myTypes.C_CONSTRAINTS)
                    .mark(myTypes.C_CONSTRAINT);
        } else if (state.in(myTypes.C_INCLUDE)) { // include with constraints
            // include M |>with<| ...
            state.mark(myTypes.C_CONSTRAINTS).advance()
                    .mark(myTypes.C_CONSTRAINT);
        } else if (state.in(myTypes.C_TRY_BODY)) { // A try handler
            // try ... |>with<| ...
            state.popEndUntil(myTypes.C_TRY_EXPR).advance()
                    .mark(myTypes.C_TRY_HANDLERS)
                    .mark(myTypes.C_TRY_HANDLER);
        } else if (state.in(myTypes.C_BINARY_CONDITION)) {
            if (state.isPrevious(myTypes.C_MATCH_EXPR, state.getIndex())) {
                // match ... |>with<| ...
                state.popEndUntil(myTypes.C_MATCH_EXPR);
            }
        }
    }

    private void parseIf(@NotNull ParserState state) {
        // |>if<| ...
        state.mark(myTypes.C_IF).advance()
                .mark(myTypes.C_BINARY_CONDITION);
    }

    private void parseThen(@NotNull ParserState state) {
        if (!state.in(myTypes.C_DIRECTIVE)) {
            // if ... |>then<| ...
            state.popEndUntil(myTypes.C_IF).advance()
                    .mark(myTypes.C_IF_THEN_SCOPE);
        }
    }

    private void parseElse(@NotNull ParserState state) {
        // if ... then ... |>else<| ...
        state.popEndUntil(myTypes.C_IF).advance()
                .mark(myTypes.C_IF_THEN_SCOPE);
    }

    private void parseStruct(@NotNull ParserState state) {
        if (state.is(myTypes.C_FUNCTOR_DECLARATION)) {
            // module X (...) = |>struct<| ...
            state.markScope(myTypes.C_FUNCTOR_BINDING, myTypes.STRUCT);
        } else if (state.is(myTypes.C_MODULE_BINDING)) {
            // module X = |>struct<| ...
            state.updateScopeToken(myTypes.STRUCT);
        } else {
            state.markScope(myTypes.C_STRUCT_EXPR, myTypes.STRUCT);
        }
    }

    private void parseSig(@NotNull ParserState state) {
        if (state.is(myTypes.C_MODULE_BINDING)) { // This is the body of a module type
            // module type X = |>sig<| ...
            state.updateScopeToken(myTypes.SIG);
        } else {
            state.markScope(myTypes.C_SIG_EXPR, myTypes.SIG);
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        if (state.inScopeOrAny(
                myTypes.C_OBJECT, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD
        )) {

            if (state.isParent(myTypes.C_OBJECT)) {
                // SEMI ends the field, and starts a new one
                state.popEnd().advance().mark(myTypes.C_OBJECT_FIELD);
            } else if (state.strictlyIn(myTypes.C_RECORD_FIELD)) {
                // SEMI ends the field, and starts a new one
                state.popEndUntil(myTypes.C_RECORD_FIELD).popEnd().advance();
                if (state.getTokenType() != myTypes.RBRACE) {
                    state.mark(myTypes.C_RECORD_FIELD);
                }
            } else if (state.strictlyIn(myTypes.C_OBJECT_FIELD)) {
                // SEMI ends the field, and starts a new one
                state.popEndUntil(myTypes.C_OBJECT_FIELD).popEnd().advance();
                if (state.getTokenType() != myTypes.RBRACE) {
                    state.mark(myTypes.C_OBJECT_FIELD);
                }
            } else if (state.hasScopeToken()) {
                state.popEndUntilScope();
            }

        } else {
            boolean isImplicitScope = state.isOneOf(myTypes.C_FUN_BODY, myTypes.C_LET_BINDING);
            if (!isImplicitScope && !state.hasScopeToken()) {
                // A SEMI operator ends the previous expression
                state.popEnd();
                if (state.is(myTypes.C_OBJECT)) {
                    state.advance().mark(myTypes.C_OBJECT_FIELD);
                }
            }
        }
    }

    private void parseIn(@NotNull ParserState state) {
        if (state.in(myTypes.C_TRY_HANDLER)) {
            state.popEndUntil(myTypes.C_TRY_EXPR);
        } else if (state.inAny(myTypes.C_LET_DECLARATION, myTypes.C_PATTERN_MATCH_BODY)) {
            boolean isStart = state.isFound(myTypes.C_LET_DECLARATION);
            state.popEndUntilIndex(state.getIndex());
            if (isStart) {
                state.popEnd();
            }
        } else {
            state.popEnd();
        }
    }

    private void parseObject(@NotNull ParserState state) {
        state.markScope(myTypes.C_OBJECT, myTypes.OBJECT);
    }

    private void parseBegin(@NotNull ParserState state) {
        state.markScope(myTypes.C_SCOPED_EXPR, myTypes.BEGIN);
    }

    private void parseEnd(@NotNull ParserState state) {
        Marker scope = state.popEndUntilOneOfElementType(myTypes.BEGIN, myTypes.SIG, myTypes.STRUCT, myTypes.OBJECT);
        state.advance().popEnd();

        if (scope != null) {
            if (state.is(myTypes.C_MODULE_DECLARATION)) {
                // module M = struct .. |>end<|
                state.popEnd();

                IElementType nextToken = state.getTokenType();
                if (nextToken == myTypes.AND) {
                    // module M = struct .. end |>and<|
                    state.advance().mark(myTypes.C_MODULE_DECLARATION).setStart();
                }
            }
        }
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.is(myTypes.C_FUNCTOR_DECLARATION)) {
            // module M (...) |> :<| ...
            state.advance().mark(myTypes.C_FUNCTOR_RESULT);
        } else if (state.isParent(myTypes.C_NAMED_PARAM)) {
            state.advance();
            if (state.getTokenType() == myTypes.LPAREN) {
                // ?x |> :<| ( ...
                Marker namedScope = state.getPrevious();
                state.updateScopeToken(namedScope, myTypes.LPAREN).advance();
            }

            if (state.strictlyIn(myTypes.C_SIG_ITEM)) { // A named param in signature
                // let x : c|> :<| ..
                state.mark(myTypes.C_SIG_EXPR)
                        .mark(myTypes.C_SIG_ITEM);
            }
        } else if (state.inAny(
                myTypes.C_EXTERNAL_DECLARATION, myTypes.C_CLASS_METHOD, myTypes.C_VAL_DECLARATION, myTypes.C_LET_DECLARATION
        )) {

            // external x |> : <| ...  OR  val x |> : <| ...  OR  let x |> : <| ...
            state.advance();
            if (state.getTokenType() == myTypes.TYPE) {
                // Local type
                state.mark(myTypes.C_TYPE_VARIABLE);
            } else {
                state.mark(myTypes.C_SIG_EXPR)
                        .mark(myTypes.C_SIG_ITEM);
            }
        } else if (state.in(myTypes.C_MODULE_DECLARATION)) {
            // module M |> : <| ...
            state.advance();
            IElementType nextToken = state.getTokenType();
            if (nextToken == myTypes.LPAREN) {
                state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
            }
            state.mark(myTypes.C_MODULE_TYPE);
        } else if (state.in(myTypes.C_RECORD_FIELD)) {
            state.advance().mark(myTypes.C_SIG_EXPR)
                    .mark(myTypes.C_SIG_ITEM);
        }
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS) && state.isParent(myTypes.C_FUN_EXPR)) { // First param
            // let f |>?<| ( x ...
            state.mark(myTypes.C_FUN_PARAM)
                    .mark(myTypes.C_NAMED_PARAM);
        } else if (state.in(myTypes.C_FUN_PARAM) && !state.hasScopeToken()) { // Start of a new optional parameter
            // let f x |>?<|(y ...
            state.popEndUntil(myTypes.C_FUN_PARAM).popEnd()
                    .mark(myTypes.C_FUN_PARAM)
                    .mark(myTypes.C_NAMED_PARAM);
        } else if (state.is(myTypes.C_BINARY_CONDITION) && !state.isParent(myTypes.C_MATCH_EXPR)) { // Condition ?
            // ... |>?<| ... : ...
            IElementType nextType = state.rawLookup(1);
            if (nextType != myTypes.LIDENT) {
                state.popEnd();
            }
        }
    }

    private void parseFunction(@NotNull ParserState state) {
        if (state.inAny(myTypes.C_LET_BINDING, myTypes.C_FUN_EXPR)) {
            if (state.isFound(myTypes.C_LET_BINDING)) {
                state.mark(myTypes.C_FUN_EXPR).advance()
                        .mark(myTypes.C_FUN_BODY);
            }

            state.mark(myTypes.C_MATCH_EXPR).advance();
            if (state.getTokenType() != myTypes.PIPE) {
                state.mark(myTypes.C_PATTERN_MATCH_EXPR);
            }
        }
    }

    private void parseFun(@NotNull ParserState state) {
        state.mark(myTypes.C_FUN_EXPR).advance();
        if (state.getTokenType() != myTypes.PIPE) {
            state.mark(myTypes.C_PARAMETERS);
        }
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.in(myTypes.C_NAMED_PARAM)) {
            // let fn ?(x |> = <| ...
            state.advance().mark(myTypes.C_DEFAULT_VALUE);
        } else if (state.in(myTypes.C_RECORD_FIELD)) {
            // { x |> = <| ... }
            // nope
        } else if (state.in(myTypes.C_FOR_LOOP)) {
            // for x |> = <| ...
            // nope
        } else if (state.strictlyIn(myTypes.C_BINARY_CONDITION)) {
            // nope
        } else if (state.in(myTypes.C_TYPE_DECLARATION, /*not*/myTypes.C_TYPE_BINDING)) {
            // type t |> =<| ...
            state.popEndUntil(myTypes.C_TYPE_DECLARATION).advance()
                    .mark(myTypes.C_TYPE_BINDING);
        } else if (state.strictlyIn(myTypes.C_EXTERNAL_DECLARATION)) {
            // external e : sig |> = <| ...
            state.popEndUntil(myTypes.C_SIG_EXPR).popEnd().advance();
        } else if (state.strictlyIn(myTypes.C_LET_DECLARATION)) {
            int letPos = state.getIndex();
            if (state.in(myTypes.C_LET_BINDING, null, letPos, false)) {
                // in a function ::  let (x) y z |> = <| ...
                state.popEndUntil(myTypes.C_FUN_EXPR).advance()
                        .mark(myTypes.C_FUN_BODY);
            } else {
                // let x |> = <| ...
                state.popEndUntilStart().advance().
                        mark(myTypes.C_LET_BINDING);
            }

        } else if (state.in(myTypes.C_MODULE_DECLARATION)) {
            // module M |> = <| ...
            state.popEndUntil(myTypes.C_MODULE_DECLARATION).advance()
                    .mark(myTypes.C_MODULE_BINDING);
        } else if (state.in(myTypes.C_FUNCTOR_RESULT)) {
            state.popEndUntil(myTypes.C_FUNCTOR_RESULT).popEnd();
        } else if (state.in(myTypes.C_CONSTRAINTS)) {
            state.popEndUntil(myTypes.C_CONSTRAINTS).popEnd();
        }
    }

    private void parseOf(@NotNull ParserState state) {
        if (state.isParent(myTypes.C_VARIANT_DECLARATION)) {
            // Variant params ::  type t = | Variant |>of<| ..
            state.advance().mark(myTypes.C_VARIANT_CONSTRUCTOR).mark(myTypes.C_FUN_PARAM);
        }
    }

    private void parseStar(@NotNull ParserState state) {
        if (state.in(myTypes.C_FUN_PARAM) && state.in(myTypes.C_VARIANT_CONSTRUCTOR)) {
            // type t = | Variant of x |>*<| y ..
            state.popEndUntil(myTypes.C_FUN_PARAM).popEnd().advance()
                    .mark(myTypes.C_FUN_PARAM);
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.is(myTypes.C_EXTERNAL_DECLARATION)) { // Overloading an operator
            // external |>(<| ...
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
        } else if (state.isParent(myTypes.C_MODULE_DECLARATION) && state.previousElementType(1) == myTypes.UIDENT) {
            //  module M |>(<| ... )
            state.rollbackTo(state.getIndex())
                    .mark(myTypes.C_FUNCTOR_DECLARATION).advance();
        } else if (state.isParent(myTypes.C_FUNCTOR_DECLARATION)) {
            // module M |>(<| ... )
            state.popEnd()
                    .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                    .mark(myTypes.C_FUNCTOR_PARAM);
        } else if (state.previousElementType(2) == myTypes.UIDENT && state.previousElementType(1) == myTypes.DOT) { // Detecting a local open
            // M1.M2. |>(<| ... )
            state.popEnd().
                    markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
        } else if (state.in(myTypes.C_FUNCTOR_CALL)) {
            // module X = M |>(<| ...
            state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                    .mark(myTypes.C_FUN_PARAM);
        } else if (state.previousElementType(1) == myTypes.UIDENT && state.in(myTypes.C_MODULE_BINDING)) { // Functor call detected
            state.rollbackTo(state.getIndex())
                    .mark(myTypes.C_FUNCTOR_CALL);
        } else if (state.is(myTypes.C_PARAMETERS) && state.isParent(myTypes.C_FUN_EXPR)) { // Start of the first parameter
            // let x |>(<| ...
            state.markScope(myTypes.C_FUN_PARAM, myTypes.LPAREN);
        } else if (state.is(myTypes.C_NAMED_PARAM)) { // A named param with default value
            // let fn ?|>(<| x ... )
            state.updateScopeToken(myTypes.LPAREN);
        } else if (state.in(myTypes.C_CLASS_DECLARATION)) {
            // class x |>(<| ...
            state.markScope(myTypes.C_CLASS_CONSTR, myTypes.LPAREN);
        } else if (state.inAny(myTypes.C_FUN_PARAM, myTypes.C_SIG_ITEM, myTypes.C_NAMED_PARAM)) {
            if (state.isFound(myTypes.C_FUN_PARAM)) { // Start of a new parameter
                state.popEndUntilFoundIndex().popEnd()
                        .mark(myTypes.C_FUN_PARAM);
                if (state.lookAhead(1) == myTypes.LIDENT) {
                    // let f x |>(<| fn ...
                    state.markDummyParenthesisScope(myTypes)
                            .advance().mark(myTypes.C_FUN_CALL).wrapWith(myTypes.C_LOWER_SYMBOL)
                            .mark(myTypes.C_PARAMETERS);
                } else {
                    // let f x |>(<| ...tuple?
                    state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                }
            } else if (state.is(myTypes.C_SIG_ITEM) && !state.hasScopeToken()) {
                state.updateScopeToken(myTypes.LPAREN);
            } else {
                state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
            }
        } else {
            state.inAny(myTypes.C_OPEN, myTypes.C_INCLUDE);
            int openPos = state.getIndex();
            if (openPos >= 0) {
                // a functor call inside open/include ::  open/include M |>(<| ...
                state.markBefore(openPos - 1, myTypes.C_FUNCTOR_CALL)
                        .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .mark(myTypes.C_FUN_PARAM);
            } else {
                state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
            }
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.LPAREN);
        if (scope == null) {
            return;
        }

        state.advance();

        int scopeLength = scope.getLength();
        if (scopeLength <= 3 && state.isParent(myTypes.C_LET_DECLARATION)) {
            // unit ::  let ()
            scope.updateCompositeType(myTypes.C_UNIT);
        }

        IElementType nextToken = state.getTokenType();
        if (nextToken == myTypes.OPTION) {
            state.markBefore(0, myTypes.C_OPTION);
        }

        if (state.is(myTypes.C_DECONSTRUCTION)) {
            state.end();
        } else {
            state.popEnd();

            if (state.is(myTypes.C_NAMED_PARAM) && nextToken != myTypes.EQ) {
                state.popEnd();
            } else if (scope.isCompositeType(myTypes.C_SCOPED_EXPR) && state.is(myTypes.C_LET_DECLARATION) && nextToken != myTypes.EQ) { // This is a custom infix operator
                state.mark(myTypes.C_PARAMETERS);
            } else if (state.is(myTypes.C_OPTION)) {
                state.advance().popEnd();
            } else if (nextToken == myTypes.RIGHT_ARROW && scope.isCompositeType(myTypes.C_SIG_ITEM)) {
                state.advance().mark(myTypes.C_SIG_ITEM);
            } else if (state.is(myTypes.C_FUN_PARAM)) {
                state.popEnd();
            } else if (nextToken == myTypes.AND) { // close intermediate elements
                state.popEndUntilStart();
                if (state.in(myTypes.C_LET_BINDING)) {
                    state.popEndUntil(myTypes.C_LET_BINDING);
                }
            }
        }
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS) && state.isParent(myTypes.C_FUN_EXPR)) {
            // let fn |>{<| ... } = ...
            state.mark(myTypes.C_FUN_PARAM);
        }

        state.markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE).advance()
                .mark(myTypes.C_RECORD_FIELD);
    }

    private void parseRBrace(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.LBRACE);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        IElementType nextElementType = state.rawLookup(1);
        if (nextElementType == myTypes.ARROBASE
                || nextElementType == myTypes.ARROBASE_2
                || nextElementType == myTypes.ARROBASE_3) {
            // https://ocaml.org/manual/attributes.html

            // |> [ <| @?? ...
            if (nextElementType == myTypes.ARROBASE) {
                state.markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
            } else if (nextElementType == myTypes.ARROBASE_2) {
                // attribute attached to a 'block' expression
                if (state.inAny(myTypes.C_LET_BINDING, myTypes.C_SIG_EXPR)) {
                    if (state.isFound(myTypes.C_SIG_EXPR)) {
                        // block attribute inside a signature
                        state.popEnd();
                    }
                    state.popEndUntilIndex(state.getIndex());
                }
                state.markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
            } else { // floating attribute
                endLikeSemi(state);
                state.markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
            }
        } else {
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET);
        }
    }

    private void parseRBracket(@NotNull ParserState state) {
        state.popEndUntilScopeToken(myTypes.LBRACKET);
        state.advance().popEnd();
    }

    private void parseLArray(@NotNull ParserState state) {
        state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LARRAY);
    }

    private void parseRArray(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.LARRAY);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseNumber(@NotNull ParserState state) {
        if (state.is(myTypes.C_FUN_PARAM)) { // Start of a new parameter
            // ... fn x |>1<| ..
            state.popEnd().mark(myTypes.C_FUN_PARAM);
        } else if (state.is(myTypes.C_PARAMETERS) && state.in(myTypes.C_FUN_CALL)) {
            state.mark(myTypes.C_FUN_PARAM);
        }
    }

    private void parseLIdent(@NotNull ParserState state) {
        if (state.is(myTypes.C_LET_DECLARATION)) {
            // let |>x<| ...
            IElementType nextToken = state.lookAhead(1);
            if (nextToken == myTypes.COMMA) { // A deconstruction without parenthesis
                state.mark(myTypes.C_DECONSTRUCTION);
            }
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
            if (nextToken != myTypes.COMMA && nextToken != myTypes.EQ && nextToken != myTypes.COLON) { // This is a function, we need to create the let binding now, to be in sync with reason
                //  let |>x<| y z = ...  vs    let x = y z => ...
                state.mark(myTypes.C_LET_BINDING).
                        mark(myTypes.C_FUN_EXPR).
                        mark(myTypes.C_PARAMETERS);
            }
        } else if (state.is(myTypes.C_EXTERNAL_DECLARATION)) {
            // external |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_TYPE_DECLARATION)) {
            // type |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_CLASS_DECLARATION)) {
            // class |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_CLASS_METHOD)) {
            // ... object method |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_VAL_DECLARATION)) {
            // val |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_RECORD_FIELD)) {
            // { |>x<| : ... }
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_MACRO_NAME)) {
            // [@ |>x.y<| ... ]
            state.advance();
            while (state.getTokenType() == myTypes.DOT) {
                state.advance();
                if (state.getTokenType() == myTypes.LIDENT) {
                    state.advance();
                }
            }
            state.popEnd();
        } else if (state.is(myTypes.C_DECONSTRUCTION)) {
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_FUN_PARAM) || state.is(myTypes.C_NAMED_PARAM) || state.isParent(myTypes.C_NAMED_PARAM)) {
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (!state.is(myTypes.C_LET_BINDING) && !state.is(myTypes.C_FUN_BODY) && !state.in(myTypes.C_SIG_ITEM)
                && state.in(myTypes.C_PARAMETERS)
                && !state.hasScopeToken()
                && state.previousElementType(1) != myTypes.AS
                && state.previousElementType(1) != myTypes.OF
                && state.previousElementType(1) != myTypes.STAR
                && state.previousElementType(1) != myTypes.QUESTION_MARK) { // Start of a new parameter
            // ... ( xxx |>yyy<| ) ..
            state.popEndUntil(myTypes.C_PARAMETERS)
                    .mark(myTypes.C_FUN_PARAM).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else {
            IElementType nextTokenType = state.lookAhead(1);

            if (nextTokenType == myTypes.COLON && state.is(myTypes.C_SIG_ITEM)) {
                // let fn: |>x<| : ...
                state.mark(myTypes.C_NAMED_PARAM);
            } else if (!state.in(myTypes.C_SIG_ITEM) && !state.is(myTypes.C_TYPE_VARIABLE) && !state.is(myTypes.C_CONSTRAINT) && !state.is(myTypes.C_BINARY_CONDITION)
                    && !state.is(myTypes.C_CLASS_FIELD) && !state.in(myTypes.C_TYPE_BINDING) &&
                    !state.is(myTypes.C_PARAMETERS)
            ) {
                if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.INT_VALUE || nextTokenType == myTypes.FLOAT_VALUE) {
                    if (state.is(myTypes.C_SCOPED_EXPR) || !state.in(myTypes.C_FUN_CALL)) { // a function call
                        // |>fn<| ...
                        state.mark(myTypes.C_FUN_CALL).wrapWith(myTypes.C_LOWER_SYMBOL)
                                .mark(myTypes.C_PARAMETERS);
                        return;
                    }
                }
            }

            state.wrapWith(myTypes.C_LOWER_SYMBOL);
        }
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.is(myTypes.C_MODULE_DECLARATION) && state.previousElementType(1) != myTypes.OF) {
            // module |>M<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_FUNCTOR_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_OPEN) || state.is(myTypes.C_INCLUDE)) { // It is a module name/path, or might be a functor call
            // open/include |>M<| ...
            state.wrapWith(myTypes.C_UPPER_SYMBOL);

            IElementType nextToken = state.getTokenType();
            if (nextToken != myTypes.DOT && nextToken != myTypes.LPAREN && nextToken != myTypes.WITH) { // Not a path, nor a functor, must close that open
                state.popEndUntilOneOf(myTypes.C_OPEN, myTypes.C_INCLUDE);
                state.popEnd();
            }
            if (nextToken == myTypes.IN) {
                // let _ = let open M |>in<| ..
                state.advance();
            }
        } else if (state.is(myTypes.C_TYPE_BINDING)) {
            IElementType nextToken = state.lookAhead(1);
            if (nextToken == myTypes.DOT) { // a path
                state.wrapWith(myTypes.C_UPPER_SYMBOL);
            } else { // Variant declaration without a pipe
                // type t = |>X<| | ...
                state.mark(myTypes.C_VARIANT_DECLARATION).wrapWith(myTypes.C_UPPER_IDENTIFIER);
            }
        } else if (state.is(myTypes.C_VARIANT_DECLARATION)) { // Declaring a variant
            // type t = | |>X<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_EXCEPTION_DECLARATION)) { // Declaring an exception
            // exception |>X<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else {
            IElementType nextToken = state.lookAhead(1);

            if (((state.is(myTypes.C_PATTERN_MATCH_EXPR) || state.is(myTypes.C_LET_BINDING))) && nextToken != myTypes.DOT) { // Pattern matching a variant or using it
                // match c with | |>X<| ... / let x = |>X<| ...
                state.remapCurrentToken(myTypes.VARIANT_NAME).wrapWith(myTypes.C_VARIANT);
                return;
            }

            state.wrapWith(myTypes.C_UPPER_SYMBOL);
        }
    }

    private void parseOpen(@NotNull ParserState state) {
        if (state.is(myTypes.C_LET_DECLARATION)) {
            // let open X (coq/indtypes.ml)
            state.updateComposite(myTypes.C_OPEN);
        } else {
            state.popEndUntilScope();
            state.mark(myTypes.C_OPEN).setStart();
        }
    }

    private void parseInclude(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_INCLUDE).setStart();
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_EXTERNAL_DECLARATION).setStart();
    }

    private void parseType(@NotNull ParserState state) {
        if (state.is(myTypes.C_MODULE_DECLARATION)) {
            // module |>type<| M = ...
        } else if (state.is(myTypes.C_TYPE_VARIABLE)) {
            // let x : |>type<| ...
        } else if (state.is(myTypes.C_CLASS_DECLARATION)) {
            // class |>type<| ...
        } else {
            if (state.previousElementType(1) == myTypes.AND && state.in(myTypes.C_CONSTRAINT)) {
                state.popEndUntil(myTypes.C_CONSTRAINT);
            } else if (!state.is(myTypes.C_CONSTRAINT)) {
                state.popEndUntilScope();
            }
            state.mark(myTypes.C_TYPE_DECLARATION).setStart();
        }
    }

    private void parseException(@NotNull ParserState state) {
        if (state.previousElementType(1) != myTypes.PIPE) {
            state.popEndUntilScope();
            state.mark(myTypes.C_EXCEPTION_DECLARATION);
        }
    }

    private void parseDirectiveIf(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(myTypes.C_DIRECTIVE).setStart();
    }

    private void parseDirectiveElse(@NotNull ParserState state) {
        state.popEndUntil(myTypes.C_DIRECTIVE);
    }

    private void parseDirectiveElif(@NotNull ParserState state) {
        state.popEndUntil(myTypes.C_DIRECTIVE);
    }

    private void parseDirectiveEnd(@NotNull ParserState state) {
        state.popEndUntil(myTypes.C_DIRECTIVE);
        if (state.is(myTypes.C_DIRECTIVE)) {
            state.advance().popEnd();
        }
    }

    private void parseVal(@NotNull ParserState state) {
        boolean insideClass = state.in(myTypes.C_OBJECT);
        if (insideClass) {
            state.popEndUntil(myTypes.C_OBJECT);
        } else {
            state.popEndUntilScope();
        }

        state.mark(insideClass ? myTypes.C_CLASS_FIELD : myTypes.C_VAL_DECLARATION).setStart();
    }

    private void parseMethod(@NotNull ParserState state) {
        state.popEndUntil(myTypes.C_OBJECT);
        state.mark(myTypes.C_CLASS_METHOD).setStart();
    }

    private void parseLet(@NotNull ParserState state) {
        if (!state.is(myTypes.C_TRY_BODY) && state.previousElementType(1) != myTypes.RIGHT_ARROW) {
            endLikeSemi(state);
        }
        state.mark(myTypes.C_LET_DECLARATION).setStart();
    }

    private void parseModule(@NotNull ParserState state) {
        if (state.is(myTypes.C_LET_DECLARATION)) {
            state.updateComposite(myTypes.C_MODULE_DECLARATION);
        } else if (!state.is(myTypes.C_MACRO_NAME)) {
            if (!state.is(myTypes.C_MODULE_TYPE)) {
                state.popEndUntilScope();
            }
            state.mark(myTypes.C_MODULE_DECLARATION).setStart();
        }
    }

    private void parseClass(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(myTypes.C_CLASS_DECLARATION).setStart();
    }

    private void endLikeSemi(@NotNull ParserState state) {
        IElementType previousElementType = state.previousElementType(1);
        if (previousElementType != myTypes.EQ
                && previousElementType != myTypes.RIGHT_ARROW
                && previousElementType != myTypes.TRY
                && previousElementType != myTypes.SEMI
                && previousElementType != myTypes.THEN
                && previousElementType != myTypes.ELSE
                && previousElementType != myTypes.IN
                && previousElementType != myTypes.LPAREN
                && previousElementType != myTypes.DO
                && previousElementType != myTypes.STRUCT
                && previousElementType != myTypes.SIG
                && previousElementType != myTypes.COLON) {
            state.popEndUntilScope();
        }
    }
}
