package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;

public class OclParser extends CommonPsiParser {
    public OclParser(boolean isSafe) {
        super(isSafe);
    }

    public static ASTNode parseOcamlNode(@NotNull ILazyParseableElementType root, @NotNull ASTNode chameleon) {
        PsiElement parentElement = chameleon.getTreeParent().getPsi();
        Project project = parentElement.getProject();

        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new OclLexer(), root.getLanguage(), chameleon.getText());
        OclParser parser = new OclParser(true);

        return parser.parse(root, builder).getFirstChildNode();
    }

    @Override
    protected ORParser<OclTypes> getORParser(@NotNull PsiBuilder builder) {
        return new OclParserState(builder, myIsSafe);
    }

    static class OclParserState extends ORLanguageParser<OclTypes> {
        protected OclParserState(@NotNull PsiBuilder builder, boolean isSafe) {
            super(OclTypes.INSTANCE, builder, isSafe);
        }

        @Override public void parse() {
            IElementType tokenType;

            long parseStart = System.currentTimeMillis();
            int parseCount = 0;

            while (!myBuilder.eof()) {
                parseCount++;
                if (parseCount > 100) {
                    parseCount = 0;
                    long parseTime = System.currentTimeMillis();
                    if (PARSE_MAX_TIME < parseTime - parseStart) {
                        if (myIsSafe) { // Don't do that in tests
                            error("Parsing cancelled, you should create a github issue with the source code");
                            break;
                        }
                    }
                }

                tokenType = myBuilder.getTokenType();

                if (tokenType == myTypes.SEMI) {
                    parseSemi();
                } else if (tokenType == myTypes.IN) {
                    parseIn();
                } else if (tokenType == myTypes.RIGHT_ARROW) {
                    parseRightArrow();
                } else if (tokenType == myTypes.PIPE) {
                    parsePipe();
                } else if (tokenType == myTypes.EQ) {
                    parseEq();
                } else if (tokenType == myTypes.OF) {
                    parseOf();
                } else if (tokenType == myTypes.STAR) {
                    parseStar();
                } else if (tokenType == myTypes.COLON) {
                    parseColon();
                } else if (tokenType == myTypes.QUESTION_MARK) {
                    parseQuestionMark();
                } else if (tokenType == myTypes.INT_VALUE) {
                    parseNumber();
                } else if (tokenType == myTypes.FLOAT_VALUE) {
                    parseNumber();
                } else if (tokenType == myTypes.STRING_VALUE) {
                    parseStringValue();
                } else if (tokenType == myTypes.STRING_CONCAT) {
                    parseStringConcat();
                } else if (tokenType == myTypes.TILDE) {
                    parseTilde();
                } else if (tokenType == myTypes.LIDENT) {
                    parseLIdent();
                } else if (tokenType == myTypes.UIDENT) {
                    parseUIdent();
                } else if (tokenType == myTypes.SIG) {
                    parseSig();
                } else if (tokenType == myTypes.OBJECT) {
                    parseObject();
                } else if (tokenType == myTypes.IF) {
                    parseIf();
                } else if (tokenType == myTypes.THEN) {
                    parseThen();
                } else if (tokenType == myTypes.ELSE) {
                    parseElse();
                } else if (tokenType == myTypes.MATCH) {
                    parseMatch();
                } else if (tokenType == myTypes.TRY) {
                    parseTry();
                } else if (tokenType == myTypes.WITH) {
                    parseWith();
                } else if (tokenType == myTypes.AND) {
                    parseAnd();
                } else if (tokenType == myTypes.DOT) {
                    parseDot();
                } else if (tokenType == myTypes.DOTDOT) {
                    parseDotDot();
                } else if (tokenType == myTypes.FUNCTION) { // function is a shortcut for a pattern match
                    parseFunction();
                } else if (tokenType == myTypes.FUN) {
                    parseFun();
                } else if (tokenType == myTypes.ASSERT) {
                    parseAssert();
                } else if (tokenType == myTypes.RAISE) {
                    parseRaise();
                } else if (tokenType == myTypes.COMMA) {
                    parseComma();
                } else if (tokenType == myTypes.ARROBASE) {
                    parseArrobase();
                } else if (tokenType == myTypes.ARROBASE_2) {
                    parseArrobase2();
                } else if (tokenType == myTypes.ARROBASE_3) {
                    parseArrobase3();
                } else if (tokenType == myTypes.OPTION) {
                    parseOption();
                }
                // while ... do ... done
                else if (tokenType == myTypes.WHILE) {
                    parseWhile();
                }
                // for ... to ... do ... done
                else if (tokenType == myTypes.FOR) {
                    parseFor();
                }
                // do ... done
                else if (tokenType == myTypes.DO) {
                    parseDo();
                } else if (tokenType == myTypes.DONE) {
                    parseDone();
                }
                // begin/struct ... end
                else if (tokenType == myTypes.BEGIN) {
                    parseBegin();
                } else if (tokenType == myTypes.STRUCT) {
                    parseStruct();
                } else if (tokenType == myTypes.END) {
                    parseEnd();
                }
                // ( ... )
                else if (tokenType == myTypes.LPAREN) {
                    parseLParen();
                } else if (tokenType == myTypes.RPAREN) {
                    parseRParen();
                }
                // { ... }
                else if (tokenType == myTypes.LBRACE) {
                    parseLBrace();
                } else if (tokenType == myTypes.RBRACE) {
                    parseRBrace();
                }
                // [ ... ]
                else if (tokenType == myTypes.LBRACKET) {
                    parseLBracket();
                } else if (tokenType == myTypes.RBRACKET) {
                    parseRBracket();
                }
                // [| ... |]
                else if (tokenType == myTypes.LARRAY) {
                    parseLArray();
                } else if (tokenType == myTypes.RARRAY) {
                    parseRArray();
                }
                // < ... >
                else if (tokenType == myTypes.LT) {
                    parseLt();
                } else if (tokenType == myTypes.GT) {
                    parseGt();
                }
                // Starts expression
                else if (tokenType == myTypes.OPEN) {
                    parseOpen();
                } else if (tokenType == myTypes.INCLUDE) {
                    parseInclude();
                } else if (tokenType == myTypes.EXTERNAL) {
                    parseExternal();
                } else if (tokenType == myTypes.TYPE) {
                    parseType();
                } else if (tokenType == myTypes.MODULE) {
                    parseModule();
                } else if (tokenType == myTypes.CLASS) {
                    parseClass();
                } else if (tokenType == myTypes.LET) {
                    parseLet();
                } else if (tokenType == myTypes.VAL) {
                    parseVal();
                } else if (tokenType == myTypes.REF) {
                    parseRef();
                } else if (tokenType == myTypes.METHOD) {
                    parseMethod();
                } else if (tokenType == myTypes.EXCEPTION) {
                    parseException();
                } else if (tokenType == myTypes.DIRECTIVE_IF) {
                    parseDirectiveIf();
                } else if (tokenType == myTypes.DIRECTIVE_ELSE) {
                    parseDirectiveElse();
                } else if (tokenType == myTypes.DIRECTIVE_ELIF) {
                    parseDirectiveElif();
                } else if (tokenType == myTypes.DIRECTIVE_END || tokenType == myTypes.DIRECTIVE_ENDIF) {
                    parseDirectiveEnd();
                }

                if (dontMove) {
                    dontMove = false;
                } else {
                    myBuilder.advanceLexer();
                }
            }
        }

        private void parseTilde() {
            if (is(myTypes.C_PARAMETERS)) {
                if (in(myTypes.C_FUNCTION_CALL)) {
                    mark(myTypes.C_NAMED_PARAM);
                } else {
                    mark(myTypes.C_PARAM_DECLARATION)
                            .markHolder(myTypes.H_NAMED_PARAM_DECLARATION);
                }
            } else if (strictlyIn(myTypes.C_DEFAULT_VALUE)) {
                popEndUntil(myTypes.C_PARAMETERS);
                boolean isCall = in(myTypes.C_FUNCTION_CALL);
                if (isCall) {
                    mark(myTypes.C_NAMED_PARAM);
                } else {
                    mark(myTypes.C_PARAM_DECLARATION)
                            .markHolder(myTypes.H_NAMED_PARAM_DECLARATION);
                }
            }
        }

        private void parseStringConcat() {
            if (strictlyIn(myTypes.C_FUNCTION_CALL)) {
                popEndUntilFoundIndex().popEnd();
            }
        }

        private void parseOption() {
            if (strictlyInAny(myTypes.C_TYPE_BINDING, myTypes.C_SIG_ITEM)) {
                // in type      :  type t = xxx |>option<|
                // or signature :  ... -> xxx |>option<| ...
                int pos = getIndex();
                if (pos > 0) {
                    markBefore(pos - 1, myTypes.C_OPTION);
                }
            }
        }

        private void parseRaise() {
            if (is(myTypes.C_EXTERNAL_DECLARATION)) {
                // external |>raise<| ...
                remapCurrentToken(myTypes.LIDENT).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
        }

        private void parseComma() {
            if (in(myTypes.C_TUPLE)) {
                // a tuple
                popEndUntilFoundIndex();
            } else if (inAny( // all same priority
                    myTypes.C_LET_DECLARATION, myTypes.C_SIG_ITEM, myTypes.C_MATCH_EXPR, myTypes.C_PARAM_DECLARATION, myTypes.C_SCOPED_EXPR
            )) {

                if (isFound(myTypes.C_LET_DECLARATION)) {
                    if (in(myTypes.C_DECONSTRUCTION)) {
                        popEndUntilFoundIndex();
                    } else if (isDone(myTypes.C_DECONSTRUCTION)) { // nested deconstructions
                        // let (a, b) |>,<| ... = ...
                        markBefore(0, myTypes.C_DECONSTRUCTION);
                    }
                } else if (isFound(myTypes.C_SCOPED_EXPR)) {
                    Marker blockScope = find(getIndex());
                    Marker parentScope = find(getIndex() + 1);
                    if (blockScope != null && parentScope != null) {
                        if (parentScope.isCompositeType(myTypes.C_LET_DECLARATION)) {
                            // let (x |>,<| ... )
                            // We need to do it again because lower symbols must be wrapped with identifiers
                            rollbackToPos(getIndex());
                            mark(myTypes.C_DECONSTRUCTION);
                            if (getTokenType() == myTypes.LPAREN) {
                                updateScopeToken(myTypes.LPAREN).advance();
                            }
                        } else if (parentScope.isCompositeType(myTypes.C_PARAM_DECLARATION)) {
                            // a tuple ::  let fn (x |>,<| ... ) ...
                            blockScope.updateCompositeType(myTypes.C_TUPLE);
                            popEndUntil(myTypes.C_TUPLE);
                        }
                    }
                }
            }
        }

        private void parseArrobase() {
            if (is(myTypes.C_ANNOTATION)) {
                mark(myTypes.C_MACRO_NAME);
            }
        }

        private void parseArrobase2() {
            if (is(myTypes.C_ANNOTATION)) {
                mark(myTypes.C_MACRO_NAME);
            }
        }

        private void parseArrobase3() {
            if (is(myTypes.C_ANNOTATION)) {
                mark(myTypes.C_MACRO_NAME);
            }
        }

        private void parseLt() {
            if (is(myTypes.C_SIG_ITEM) || in(myTypes.C_TYPE_BINDING) || is(myTypes.C_OBJECT_FIELD)) {
                // |> < <| .. > ..
                markScope(myTypes.C_OBJECT, myTypes.LT).advance()
                        .mark(myTypes.C_OBJECT_FIELD);
            }
        }

        private void parseGt() {
            if (in(myTypes.C_OBJECT)) {
                popEndUntil(myTypes.C_OBJECT);
                advance().end();
                popEnd();
            }
        }

        private void parseWhile() {
            mark(myTypes.C_WHILE).advance()
                    .mark(myTypes.C_BINARY_CONDITION);
        }

        private void parseFor() {
            mark(myTypes.C_FOR_LOOP);
        }

        private void parseDo() {
            if (in(myTypes.C_BINARY_CONDITION)) {
                popEndUntil(myTypes.C_BINARY_CONDITION).popEnd();
            }

            if (strictlyInAny(myTypes.C_WHILE, myTypes.C_FOR_LOOP)) {
                popEndUntilFoundIndex()
                        .markScope(myTypes.C_SCOPED_EXPR, myTypes.DO);
            } else {
                markScope(myTypes.C_DO_LOOP, myTypes.DO);
            }
        }

        private void parseDone() {
            Marker scope = popEndUntilScopeToken(myTypes.DO);
            if (scope != null) {
                advance().popEnd();
            }
        }

        private void parseRightArrow() {
            if (is(myTypes.C_SIG_EXPR)) {
                advance();
                markDummyParenthesisScope().mark(myTypes.C_SIG_ITEM);
            } else if (strictlyIn(myTypes.C_SIG_ITEM)) {
                popEndUntilFoundIndex().popEnd();
                if (in(myTypes.H_NAMED_PARAM_DECLARATION)) { // can't have an arrow in a named param signature
                    // let fn x:int |>-><| y:int
                    popEnd().popEndUntil(myTypes.C_SIG_EXPR);
                }
                advance();
                markDummyParenthesisScope().mark(myTypes.C_SIG_ITEM);
            }
            // same priority
            else if (inAny(
                    myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_FUNCTION_EXPR
            )) {

                if (isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                    // | ... |>-><|
                    popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).advance()
                            .mark(myTypes.C_PATTERN_MATCH_BODY);
                } else if (isFound(myTypes.C_FUNCTION_EXPR)) {
                    // fun ... |>-><| ...
                    popEndUntil(myTypes.C_FUNCTION_EXPR).advance()
                            .mark(myTypes.C_FUNCTION_BODY);
                }

            }
        }

        private void parseAssert() {
            mark(myTypes.C_ASSERT_STMT);
        }

        private void parseAnd() {
            if (in(myTypes.C_TYPE_CONSTRAINT)) {
                popEndUntil(myTypes.C_TYPE_CONSTRAINT).popEnd()
                        .advance().mark(myTypes.C_TYPE_CONSTRAINT);
            } else if (inAny(myTypes.C_LET_DECLARATION, myTypes.C_TYPE_DECLARATION)) {
                // pop scopes until a chainable expression is found
                popEndUntilIndex(getIndex());
                Marker marker = getLatestMarker();

                popEnd().advance();
                if (marker != null) {
                    if (marker.isCompositeType(myTypes.C_LET_DECLARATION)) {
                        mark(myTypes.C_LET_DECLARATION).setStart();
                    } else if (marker.isCompositeType(myTypes.C_TYPE_DECLARATION)) {
                        mark(myTypes.C_TYPE_DECLARATION).setStart();
                    }
                }
            }
        }

        private void parseDot() {
            if (in(myTypes.C_TYPE_VARIABLE)) {
                popEndUntil(myTypes.C_TYPE_VARIABLE).popEnd().advance()
                        .mark(myTypes.C_SIG_EXPR)
                        .mark(myTypes.C_SIG_ITEM);
            }
        }

        private void parseDotDot() {
            if (is(myTypes.C_OBJECT_FIELD)) {
                advance().popEnd();
            }
        }

        private void parsePipe() {
            if (is(myTypes.C_SCOPED_EXPR) && isRawParent(myTypes.C_LET_DECLARATION)) {
                // let ( |>|<| ...
                return;
            }

            if (in(myTypes.C_PATTERN_MATCH_BODY)) {
                popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd().advance()
                        .mark(myTypes.C_PATTERN_MATCH_EXPR);
            } else if (in(myTypes.C_VARIANT_DECLARATION)) {
                // type t = | X |>|<| Y ...
                popEndUntilFoundIndex().popEnd().advance()
                        .mark(myTypes.C_VARIANT_DECLARATION);
            } else if (in(myTypes.C_TYPE_BINDING)) { // remap an upper symbol to a variant if first element is missing pipe
                // type t = (|) V1 |>|<| ...
                popEndUntil(myTypes.C_TYPE_BINDING).advance()
                        .mark(myTypes.C_VARIANT_DECLARATION);
            } else {
                if (in(myTypes.C_PATTERN_MATCH_EXPR)) { // pattern group
                    // | X |>|<| Y ...
                    popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd();
                }

                // By default, a pattern match
                advance().mark(myTypes.C_PATTERN_MATCH_EXPR);
            }
        }

        private void parseMatch() {
            mark(myTypes.C_MATCH_EXPR).advance()
                    .mark(myTypes.C_BINARY_CONDITION);
        }

        private void parseTry() {
            mark(myTypes.C_TRY_EXPR).advance()
                    .mark(myTypes.C_TRY_BODY);
        }

        private void parseWith() {
            if (in(myTypes.C_FUNCTOR_RESULT)) { // A functor with constraints
                //  module Make (M : Input) : S |>with<| ...
                popEndUntil(myTypes.C_FUNCTOR_RESULT).popEnd().advance()
                        .mark(myTypes.C_CONSTRAINTS)
                        .mark(myTypes.C_TYPE_CONSTRAINT);
            } else if (in(myTypes.C_MODULE_TYPE)) { // A module with a signature and constraints
                //  module G : sig ... end |>with<| ...
                //  module G : X |>with<| ...
                popEndUntil(myTypes.C_MODULE_TYPE).popEnd().advance()
                        .mark(myTypes.C_CONSTRAINTS)
                        .mark(myTypes.C_TYPE_CONSTRAINT);
            } else if (in(myTypes.C_INCLUDE)) { // include with constraints
                // include M |>with<| ...
                mark(myTypes.C_CONSTRAINTS).advance()
                        .mark(myTypes.C_TYPE_CONSTRAINT);
            } else if (in(myTypes.C_TRY_BODY)) { // A try handler
                // try ... |>with<| ...
                popEndUntil(myTypes.C_TRY_EXPR).advance()
                        .mark(myTypes.C_TRY_HANDLERS)
                        .mark(myTypes.C_TRY_HANDLER);
            } else if (in(myTypes.C_BINARY_CONDITION)) {
                if (isPrevious(myTypes.C_MATCH_EXPR, getIndex())) {
                    // match ... |>with<| ...
                    popEndUntil(myTypes.C_MATCH_EXPR);
                }
            }
        }

        private void parseIf() {
            // |>if<| ...
            mark(myTypes.C_IF).advance()
                    .mark(myTypes.C_BINARY_CONDITION);
        }

        private void parseThen() {
            if (!in(myTypes.C_DIRECTIVE)) {
                // if ... |>then<| ...
                popEndUntil(myTypes.C_IF).advance()
                        .mark(myTypes.C_IF_THEN_SCOPE);
            }
        }

        private void parseElse() {
            // if ... then ... |>else<| ...
            popEndUntil(myTypes.C_IF).advance()
                    .mark(myTypes.C_IF_THEN_SCOPE);
        }

        private void parseStruct() {
            if (is(myTypes.C_FUNCTOR_DECLARATION)) {
                // module X (...) = |>struct<| ...
                markScope(myTypes.C_FUNCTOR_BINDING, myTypes.STRUCT);
            } else if (is(myTypes.C_MODULE_BINDING)) {
                // module X = |>struct<| ...
                updateScopeToken(myTypes.STRUCT);
            } else {
                markScope(myTypes.C_STRUCT_EXPR, myTypes.STRUCT);
            }
        }

        private void parseSig() {
            if (is(myTypes.C_MODULE_BINDING)) { // This is the body of a module type
                // module type X = |>sig<| ...
                updateScopeToken(myTypes.SIG);
            } else {
                markScope(myTypes.C_SIG_EXPR, myTypes.SIG);
            }
        }

        private void parseSemi() {
            if (inScopeOrAny(
                    myTypes.C_FUNCTION_BODY, myTypes.C_LET_BINDING, myTypes.C_DIRECTIVE,
                    myTypes.C_OBJECT, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD
            )) {

                if (isFound(myTypes.C_FUNCTION_BODY) || isFound(myTypes.C_LET_BINDING) || isFound(myTypes.C_DIRECTIVE)) {
                    // A SEMI operator ends the previous expression
                    popEndUntilFoundIndex();
                } else if (isRawParent(myTypes.C_OBJECT)) {
                    // SEMI ends the field, and starts a new one
                    popEnd().advance().mark(myTypes.C_OBJECT_FIELD);
                } else if (strictlyIn(myTypes.C_RECORD_FIELD)) {
                    // SEMI ends the field, and starts a new one
                    popEndUntil(myTypes.C_RECORD_FIELD).popEnd().advance();
                    if (getTokenType() != myTypes.RBRACE) {
                        mark(myTypes.C_RECORD_FIELD);
                    }
                } else if (strictlyIn(myTypes.C_OBJECT_FIELD)) {
                    // SEMI ends the field, and starts a new one
                    popEndUntil(myTypes.C_OBJECT_FIELD).popEnd().advance();
                    if (getTokenType() != myTypes.RBRACE) {
                        mark(myTypes.C_OBJECT_FIELD);
                    }
                } else if (rawHasScope()) {
                    popEndUntilScope();
                }
            }
        }

        private void parseIn() {
            if (in(myTypes.C_TRY_HANDLER)) {
                popEndUntil(myTypes.C_TRY_EXPR);
            } else if (inAny(myTypes.C_LET_DECLARATION, myTypes.C_PATTERN_MATCH_BODY)) {
                boolean isStart = isFound(myTypes.C_LET_DECLARATION);
                popEndUntilIndex(getIndex());
                if (isStart) {
                    popEnd();
                }
            } else {
                popEnd();
            }
        }

        private void parseObject() {
            markScope(myTypes.C_OBJECT, myTypes.OBJECT);
        }

        private void parseBegin() {
            markScope(myTypes.C_SCOPED_EXPR, myTypes.BEGIN);
        }

        private void parseEnd() {
            Marker scope = popEndUntilOneOfElementType(myTypes.BEGIN, myTypes.SIG, myTypes.STRUCT, myTypes.OBJECT);
            advance().popEnd();

            if (scope != null) {
                if (is(myTypes.C_MODULE_DECLARATION)) {
                    // module M = struct .. |>end<|
                    popEnd();

                    IElementType nextToken = getTokenType();
                    if (nextToken == myTypes.AND) {
                        // module M = struct .. end |>and<|
                        advance().mark(myTypes.C_MODULE_DECLARATION).setStart();
                    }
                }
            }
        }

        private void parseColon() {
            if (is(myTypes.C_FUNCTOR_DECLARATION)) {
                // module M (...) |> :<| ...
                advance().mark(myTypes.C_FUNCTOR_RESULT);
            } else if (isRawParent(myTypes.H_NAMED_PARAM_DECLARATION)) {
                advance();
                if (getTokenType() == myTypes.LPAREN) {
                    // ?x : |>(<| ...
                    Marker namedScope = getPrevious();
                    updateScopeToken(namedScope, myTypes.LPAREN).advance();
                }

                if (strictlyIn(myTypes.C_SIG_ITEM)) { // A named param in signature
                    // let x : c|> :<| ..
                    mark(myTypes.C_SIG_EXPR)
                            .mark(myTypes.C_SIG_ITEM);
                }
            } else if (isRawParent(myTypes.C_NAMED_PARAM)) {
                advance().mark(myTypes.C_DEFAULT_VALUE);
            } else if (inAny(
                    myTypes.C_EXTERNAL_DECLARATION, myTypes.C_CLASS_METHOD, myTypes.C_VAL_DECLARATION, myTypes.C_LET_DECLARATION,
                    myTypes.C_TERNARY
            )) {

                if (isFound(myTypes.C_TERNARY)) {
                    // x ? y |> :<| ...
                    popEndUntilFoundIndex()
                            .advance().mark(myTypes.C_IF_THEN_SCOPE);
                    markHolder(myTypes.H_PLACE_HOLDER);
                } else {
                    // external x |> : <| ...  OR  val x |> : <| ...  OR  let x |> : <| ...
                    advance();
                    if (getTokenType() == myTypes.TYPE) {
                        // Local type
                        mark(myTypes.C_TYPE_VARIABLE);
                    } else {
                        mark(myTypes.C_SIG_EXPR)
                                .mark(myTypes.C_SIG_ITEM);
                    }
                }

            } else if (in(myTypes.C_MODULE_DECLARATION)) {
                // module M |> : <| ...
                advance();
                markDummyParenthesisScope()
                        .mark(myTypes.C_MODULE_TYPE);
            } else if (in(myTypes.C_RECORD_FIELD)) {
                advance().mark(myTypes.C_SIG_EXPR)
                        .mark(myTypes.C_SIG_ITEM);
            }
        }

        private void parseQuestionMark() {
            if (is(myTypes.C_PARAMETERS) && isRawParent(myTypes.C_FUNCTION_EXPR)) { // First param
                // let f |>?<| ( x ...
                mark(myTypes.C_PARAM_DECLARATION)
                        .markHolder(myTypes.H_NAMED_PARAM_DECLARATION);
            } else if (!strictlyInAny(myTypes.C_TERNARY)) {
                if (inScopeOrAny(myTypes.C_LET_BINDING)) {
                    // a new ternary
                    int foundPos = getIndex();
                    int nextPos = foundPos - 1;
                    if (isAtIndex(nextPos, myTypes.H_PLACE_HOLDER)) {
                        markBefore(nextPos, myTypes.C_TERNARY)
                                .updateCompositeAt(nextPos, myTypes.C_BINARY_CONDITION)
                                .popEndUntilIndex(nextPos).end()
                                .advance().mark(myTypes.C_IF_THEN_SCOPE);
                        markHolder(myTypes.H_PLACE_HOLDER);
                    }
                }
            }
        }

        private void parseFunction() {
            if (inAny(myTypes.C_LET_BINDING, myTypes.C_FUNCTION_EXPR)) {
                if (isFound(myTypes.C_LET_BINDING)) {
                    mark(myTypes.C_FUNCTION_EXPR).advance()
                            .mark(myTypes.C_FUNCTION_BODY);
                }

                mark(myTypes.C_MATCH_EXPR).advance();
                if (getTokenType() != myTypes.PIPE) {
                    mark(myTypes.C_PATTERN_MATCH_EXPR);
                }
            }
        }

        private void parseFun() {
            mark(myTypes.C_FUNCTION_EXPR).advance();
            if (getTokenType() != myTypes.PIPE) {
                mark(myTypes.C_PARAMETERS);
            }
        }

        private void parseEq() {
            if (in(myTypes.H_NAMED_PARAM_DECLARATION)) {
                // let fn ?(x |> = <| ...
                advance().mark(myTypes.C_DEFAULT_VALUE);
            } else if (in(myTypes.C_RECORD_FIELD)) {
                // { x |> = <| ... }
                // nope
            } else if (in(myTypes.C_FOR_LOOP)) {
                // for x |> = <| ...
                // nope
            } else if (strictlyIn(myTypes.C_BINARY_CONDITION)) {
                // nope
            } else if (in(myTypes.C_TYPE_DECLARATION, /*not*/myTypes.C_TYPE_BINDING)) {
                // type t |> =<| ...
                popEndUntil(myTypes.C_TYPE_DECLARATION).advance()
                        .mark(myTypes.C_TYPE_BINDING);
            } else if (strictlyIn(myTypes.C_EXTERNAL_DECLARATION)) {
                // external e : sig |> = <| ...
                popEndUntil(myTypes.C_SIG_EXPR).popEnd().advance();
            } else if (strictlyIn(myTypes.C_LET_DECLARATION)) {
                int letPos = getIndex();
                if (in(myTypes.C_LET_BINDING, null, letPos, false)) {
                    // in a function ::  let (x) y z |> = <| ...
                    popEndUntil(myTypes.C_FUNCTION_EXPR).advance()
                            .mark(myTypes.C_FUNCTION_BODY);
                } else {
                    // let x |> = <| ...
                    popEndUntilStart().advance().
                            mark(myTypes.C_LET_BINDING).
                            markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (in(myTypes.C_MODULE_DECLARATION)) {
                // module M |> = <| ...
                popEndUntil(myTypes.C_MODULE_DECLARATION).advance()
                        .mark(myTypes.C_MODULE_BINDING);
            } else if (in(myTypes.C_FUNCTOR_RESULT)) {
                popEndUntil(myTypes.C_FUNCTOR_RESULT).popEnd();
            } else if (in(myTypes.C_CONSTRAINTS)) {
                popEndUntil(myTypes.C_CONSTRAINTS).popEnd();
            }
        }

        private void parseOf() {
            if (isRawParent(myTypes.C_VARIANT_DECLARATION)) {
                // Variant params ::  type t = | Variant |>of<| ..
                advance().mark(myTypes.C_VARIANT_CONSTRUCTOR).mark(myTypes.C_PARAM_DECLARATION);
            }
        }

        private void parseStar() {
            if (strictlyIn(myTypes.C_TUPLE)) {
                popEndUntilFoundIndex();
            } else if (in(myTypes.C_PARAM_DECLARATION) && in(myTypes.C_VARIANT_CONSTRUCTOR)) {
                // type t = | Variant of x |>*<| y ..
                popEndUntil(myTypes.C_PARAM_DECLARATION).popEnd().advance()
                        .mark(myTypes.C_PARAM_DECLARATION);
            }
        }

        private void parseLParen() {
            if (is(myTypes.C_EXTERNAL_DECLARATION)) { // Overloading an operator
                // external |>(<| ...
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
            } else if (isRawParent(myTypes.C_MODULE_DECLARATION) && previousElementType(1) == myTypes.A_MODULE_NAME) {
                //  module M |>(<| ... )
                updateCompositeAt(1, myTypes.C_FUNCTOR_DECLARATION)
                        .popEnd()
                        .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .mark(myTypes.C_PARAM_DECLARATION);
            } else if (previousElementType(2) == myTypes.A_MODULE_NAME && previousElementType(1) == myTypes.DOT) { // Detecting a local open
                // M1.M2. |>(<| ... )
                popEnd().
                        markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
            } else if (is(myTypes.C_PARAMETERS) && isRawParent(myTypes.C_FUNCTION_EXPR)) { // Start of the first parameter
                // let x |>(<| ...
                markScope(myTypes.C_PARAM_DECLARATION, myTypes.LPAREN);
            } else if (is(myTypes.C_PARAMETERS) && isRawParent(myTypes.C_FUNCTION_CALL)) { // Start of the first parameter
                // fn |>(<| ...
                markScope(myTypes.C_PARAM, myTypes.LPAREN);
            } else if (is(myTypes.H_NAMED_PARAM_DECLARATION)) { // A named param with default value
                // let fn ?|>(<| x ... )
                updateScopeToken(myTypes.LPAREN);
            } else if (isRawParent(myTypes.H_NAMED_PARAM_DECLARATION)) {
                popEnd();
                markParenthesisScope(false);
            } else if (in(myTypes.C_CLASS_DECLARATION)) {
                // class x |>(<| ...
                markScope(myTypes.C_CLASS_CONSTR, myTypes.LPAREN);
            } else if (is(myTypes.C_MODULE_BINDING)) {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                if (getTokenType() == myTypes.VAL) {
                    advance(); // skip 'val' in a first class module decoding
                }
            } else if (inAny(//
                    myTypes.C_PARAM_DECLARATION, myTypes.C_PARAMETERS, myTypes.C_PARAM, myTypes.C_SIG_ITEM
            )) {
                boolean isDeclaration = isFound(myTypes.C_PARAM_DECLARATION);
                int foundIndex = getIndex();

                if (isFound(myTypes.C_PARAMETERS)) {
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                } else if (isDeclaration && isRawParent(myTypes.C_VARIANT_CONSTRUCTOR)) {
                    // Tuple in variant declaration
                    markScope(myTypes.C_TUPLE, myTypes.LPAREN);
                } else if (isDeclaration || isFound(myTypes.C_PARAM)) { // Start of a new parameter
                    popEndUntilIndex(foundIndex).popEnd()
                            .mark(isDeclaration ? myTypes.C_PARAM_DECLARATION : myTypes.C_PARAM);
                    if (lookAhead(1) == myTypes.LIDENT) {
                        // let f x |>(<| fn ...
                        markDummyParenthesisScope()
                                .mark(myTypes.C_FUNCTION_CALL)
                                .wrapAtom(myTypes.CA_LOWER_SYMBOL).popEnd()
                                .mark(myTypes.C_PARAMETERS);
                    } else {
                        // let f x |>(<| ...tuple?
                        markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                    }
                } else if (is(myTypes.C_SIG_ITEM) && !rawHasScope()) {
                    updateScopeToken(myTypes.LPAREN);
                } else {
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                }
            } else {
                inAny(myTypes.C_OPEN, myTypes.C_INCLUDE);
                int openPos = getIndex();
                if (openPos >= 0) {
                    // a functor call inside open/include ::  open/include M |>(<| ...
                    markBefore(0, myTypes.C_FUNCTOR_CALL)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM_DECLARATION);
                } else {
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                }
            }
        }

        private void parseRParen() {
            Marker lParen = popEndUntilScopeToken(myTypes.LPAREN);
            if (lParen == null) {
                return;
            }

            advance();

            int scopeLength = lParen.getLength();
            if (scopeLength <= 3 && isRawParent(myTypes.C_LET_DECLARATION)) {
                // unit ::  let ()
                lParen.updateCompositeType(myTypes.C_UNIT);
            }

            IElementType nextToken = getTokenType();
            if (nextToken == myTypes.OPTION) {
                markBefore(0, myTypes.C_OPTION);
            }

            if (is(myTypes.C_DECONSTRUCTION)) {
                end();
            } else {
                popEnd();

                if (lParen.isCompositeType(myTypes.H_NAMED_PARAM_DECLARATION) && nextToken != myTypes.EQ) {
                    popEnd();
                    if (is(myTypes.C_PARAM_DECLARATION)) {
                        popEnd();
                    }
                } else if (lParen.isCompositeType(myTypes.C_SCOPED_EXPR) && is(myTypes.C_LET_DECLARATION) && nextToken != myTypes.EQ) { // This is a custom infix operator
                    mark(myTypes.C_PARAMETERS);
                } else if (is(myTypes.C_OPTION)) {
                    advance().popEnd();
                } else if (nextToken == myTypes.RIGHT_ARROW && lParen.isCompositeType(myTypes.C_SIG_ITEM)) {
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (is(myTypes.C_PARAM_DECLARATION) && isRawParent(myTypes.C_VARIANT_CONSTRUCTOR) && !lParen.isCompositeType(myTypes.C_TUPLE)) {
                    popEnd();
                } else if (is(myTypes.C_PARAM)) {
                    popEnd();
                } else if (nextToken == myTypes.AND) { // close intermediate elements
                    popEndUntilStart();
                    if (in(myTypes.C_LET_BINDING)) {
                        popEndUntil(myTypes.C_LET_BINDING);
                    }
                }
            }
        }

        private void parseLBrace() {
            if (is(myTypes.C_PARAMETERS) && isRawParent(myTypes.C_FUNCTION_EXPR)) {
                // let fn |>{<| ... } = ...
                mark(myTypes.C_PARAM_DECLARATION);
            }

            if (is(myTypes.C_LET_DECLARATION)) {
                // let |>{<| .. ,
                markScope(myTypes.C_DECONSTRUCTION, myTypes.LBRACE);
            } else {
                markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE).advance()
                        .mark(myTypes.C_RECORD_FIELD);
            }
        }

        private void parseRBrace() {
            Marker scope = popEndUntilScopeToken(myTypes.LBRACE);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseLBracket() {
            IElementType nextType = rawLookup(1);
            if (nextType == myTypes.ARROBASE
                    || nextType == myTypes.ARROBASE_2
                    || nextType == myTypes.ARROBASE_3) {
                // https://ocaml.org/manual/attributes.html

                // |> [ <| @?? ...
                if (nextType == myTypes.ARROBASE) {
                    markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
                } else if (nextType == myTypes.ARROBASE_2) {
                    // attribute attached to a 'block' expression
                    if (inAny(myTypes.C_LET_BINDING, myTypes.C_SIG_EXPR)) {
                        if (isFound(myTypes.C_SIG_EXPR)) {
                            // block attribute inside a signature
                            popEnd();
                        }
                        popEndUntilIndex(getIndex());
                    }
                    markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
                } else { // floating attribute
                    endLikeSemi();
                    markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
                }
            } else if (nextType == myTypes.GT) {
                // |> [ <| > ... ]
                markScope(myTypes.C_OPEN_VARIANT, myTypes.LBRACKET).advance().advance();
                if (getTokenType() != myTypes.RBRACKET) {
                    mark(myTypes.C_VARIANT_DECLARATION).advance();
                }
            } else if (nextType == myTypes.LT) {
                // |> [ <| < ... ]
                markScope(myTypes.C_CLOSED_VARIANT, myTypes.LBRACKET).advance().advance();
                if (getTokenType() != myTypes.RBRACKET) {
                    mark(myTypes.C_VARIANT_DECLARATION).advance();
                }
            } else {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET);
            }
        }

        private void parseRBracket() {
            popEndUntilScopeToken(myTypes.LBRACKET);
            advance().popEnd();
        }

        private void parseLArray() {
            markScope(myTypes.C_SCOPED_EXPR, myTypes.LARRAY);
        }

        private void parseRArray() {
            Marker scope = popEndUntilScopeToken(myTypes.LARRAY);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseNumber() {
            if (is(myTypes.C_PARAM_DECLARATION)) { // Start of a new parameter
                // ... fn x |>1<| ..
                popEnd().mark(myTypes.C_PARAM_DECLARATION).advance().popEnd();
            } else if (is(myTypes.C_PARAM) && !currentHasScope()) { // Start of new parameter reference
                // ... fn x |>1<| ..
                popEnd().mark(myTypes.C_PARAM).advance().popEnd();
            } else if (is(myTypes.C_PARAMETERS) && strictlyIn(myTypes.C_FUNCTION_CALL)) {
                mark(myTypes.C_PARAM);
            }
        }

        private void parseStringValue() {
            if (is(myTypes.C_PARAMETERS)) {
                boolean isCall = strictlyIn(myTypes.C_FUNCTION_CALL);
                mark(isCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION).advance().popEnd();
            } else if (is(myTypes.C_PARAM_DECLARATION)) {
                popEnd().mark(myTypes.C_PARAM_DECLARATION).advance().popEnd();
            } else if (is(myTypes.C_PARAM)) {
                popEnd().mark(myTypes.C_PARAM).advance().popEnd();
            }
        }

        private void parseLIdent() {
            if (is(myTypes.C_LET_DECLARATION)) {
                // let |>x<| ...
                IElementType nextToken = lookAhead(1);
                if (nextToken == myTypes.COMMA) { // A deconstruction without parenthesis
                    mark(myTypes.C_DECONSTRUCTION);
                }
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
                if (nextToken != myTypes.COMMA && nextToken != myTypes.EQ && nextToken != myTypes.COLON) { // This is a function, we need to create the let binding now, to be in sync with reason
                    //  let |>x<| y z = ...  vs    let x = y z => ...
                    mark(myTypes.C_LET_BINDING).
                            mark(myTypes.C_FUNCTION_EXPR).
                            mark(myTypes.C_PARAMETERS);
                }
            } else if (is(myTypes.C_EXTERNAL_DECLARATION)) {
                // external |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_TYPE_DECLARATION)) {
                // type |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_CLASS_DECLARATION)) {
                // class |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_CLASS_METHOD)) {
                // ... object method |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_VAL_DECLARATION)) {
                // val |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_RECORD_FIELD)) {
                // { |>x<| : ... }
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_MACRO_NAME)) {
                // [@ |>x.y<| ... ]
                advance();
                while (getTokenType() == myTypes.DOT) {
                    advance();
                    if (getTokenType() == myTypes.LIDENT) {
                        advance();
                    }
                }
                popEnd();
            } else if (is(myTypes.C_DECONSTRUCTION)) {
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_PARAMETERS) && !rawHasScope()) {
                // ... ( xxx |>yyy<| ) ..
                popEndUntil(myTypes.C_PARAMETERS);
                boolean isCall = strictlyIn(myTypes.C_FUNCTION_CALL);
                mark(isCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION)
                        .wrapAtom(myTypes.CA_LOWER_SYMBOL).popEndUntil(myTypes.C_PARAMETERS);
            } else {
                IElementType nextTokenType = lookAhead(1);

                if (nextTokenType == myTypes.COLON && is(myTypes.C_SIG_ITEM)) {
                    // let fn: |>x<| : ...
                    mark(myTypes.C_PARAM_DECLARATION).markHolder(myTypes.H_NAMED_PARAM_DECLARATION);
                } else if (!in(myTypes.C_SIG_ITEM) && !is(myTypes.C_TYPE_VARIABLE) && !is(myTypes.C_TYPE_CONSTRAINT)
                        && !is(myTypes.C_BINARY_CONDITION) && !is(myTypes.C_CLASS_FIELD) && !in(myTypes.C_TYPE_BINDING)
                        && !is(myTypes.C_PARAMETERS) && !strictlyIn(myTypes.C_DEFAULT_VALUE)) {
                    if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.INT_VALUE || nextTokenType == myTypes.FLOAT_VALUE
                            || nextTokenType == myTypes.STRING_VALUE || nextTokenType == myTypes.TILDE) {
                        if (isCurrent(myTypes.C_SCOPED_EXPR) || !in(myTypes.C_FUNCTION_CALL, /*not*/myTypes.C_PARAMETERS)) { // a function call
                            // |>fn<| ...
                            mark(myTypes.C_FUNCTION_CALL)
                                    .wrapAtom(myTypes.CA_LOWER_SYMBOL).popEnd()
                                    .mark(myTypes.C_PARAMETERS);
                            return;
                        }
                    }
                }

                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
        }

        private void parseUIdent() {
            if (DUMMY_IDENTIFIER_TRIMMED.equals(getTokenText())) {
                return;
            }

            if (is(myTypes.C_MODULE_DECLARATION) && previousElementType(1) != myTypes.OF) {
                // module |>M<| ...
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_MODULE_BINDING)) {
                IElementType nextElement = lookAhead(1);
                if (nextElement == myTypes.LPAREN) {
                    // functor call ::  |>X<| ( ...
                    // functor call with path :: A.B.|>X<| ( ...
                    mark(myTypes.C_FUNCTOR_CALL)
                            .remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM);
                } else {
                    // module M = |>X<|
                    // module M = X.|>Y<|
                    remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                }
            } else if (is(myTypes.C_OPEN) || is(myTypes.C_INCLUDE)) { // It is a module name/path, or might be a functor call
                // open/include |>M<| ...
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);

                IElementType nextToken = getTokenType();
                if (nextToken != myTypes.DOT && nextToken != myTypes.LPAREN && nextToken != myTypes.WITH) { // Not a path, nor a functor, must close that open
                    popEndUntilOneOf(myTypes.C_OPEN, myTypes.C_INCLUDE);
                    popEnd();
                }
                if (nextToken == myTypes.IN) {
                    // let _ = let open M |>in<| ..
                    advance();
                }
            } else if (is(myTypes.C_TYPE_BINDING)) {
                IElementType nextToken = lookAhead(1);
                if (nextToken == myTypes.DOT) { // a path
                    remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                } else { // Variant declaration without a pipe
                    // type t = |>X<| | ...
                    mark(myTypes.C_VARIANT_DECLARATION)
                            .remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                }
            } else if (is(myTypes.C_VARIANT_DECLARATION)) { // Declaring a variant
                // type t = | |>X<| ...
                remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (is(myTypes.C_EXCEPTION_DECLARATION)) { // Declaring an exception
                // exception |>X<| ...
                wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else {
                IElementType nextToken = lookAhead(1);

                if (((in(myTypes.C_PATTERN_MATCH_EXPR, /*not*/myTypes.C_PATTERN_MATCH_BODY) || isCurrent(myTypes.C_LET_BINDING)))
                        && nextToken != myTypes.DOT) { // Pattern matching a variant or using it
                    // match c with | |>X<| ... / let x = |>X<| ...
                    remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                    return;
                }

                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
        }

        private void parseOpen() {
            if (is(myTypes.C_LET_DECLARATION)) {
                // let open X (coq/indtypes.ml)
                updateComposite(myTypes.C_OPEN);
            } else {
                popEndUntilScope();
                mark(myTypes.C_OPEN).setStart();
            }
        }

        private void parseInclude() {
            popEndUntilScope();
            mark(myTypes.C_INCLUDE).setStart();
        }

        private void parseExternal() {
            popEndUntilScope();
            mark(myTypes.C_EXTERNAL_DECLARATION).setStart();
        }

        private void parseType() {
            if (is(myTypes.C_MODULE_DECLARATION)) {
                // module |>type<| M = ...
            } else if (is(myTypes.C_TYPE_VARIABLE)) {
                // let x : |>type<| ...
            } else if (is(myTypes.C_CLASS_DECLARATION)) {
                // class |>type<| ...
            } else {
                if (previousElementType(1) == myTypes.AND && in(myTypes.C_TYPE_CONSTRAINT)) {
                    popEndUntil(myTypes.C_TYPE_CONSTRAINT);
                } else if (!is(myTypes.C_TYPE_CONSTRAINT)) {
                    popEndUntilScope();
                }
                mark(myTypes.C_TYPE_DECLARATION).setStart();
            }
        }

        private void parseException() {
            if (previousElementType(1) != myTypes.PIPE) {
                popEndUntilScope();
                mark(myTypes.C_EXCEPTION_DECLARATION);
            }
        }

        private void parseDirectiveIf() {
            endLikeSemi();
            mark(myTypes.C_DIRECTIVE).setStart();
        }

        private void parseDirectiveElse() {
            popEndUntil(myTypes.C_DIRECTIVE);
        }

        private void parseDirectiveElif() {
            popEndUntil(myTypes.C_DIRECTIVE);
        }

        private void parseDirectiveEnd() {
            popEndUntil(myTypes.C_DIRECTIVE);
            if (is(myTypes.C_DIRECTIVE)) {
                advance().popEnd();
            }
        }

        private void parseVal() {
            boolean insideClass = in(myTypes.C_OBJECT);
            if (insideClass) {
                popEndUntil(myTypes.C_OBJECT);
            } else {
                popEndUntilScope();
            }

            mark(insideClass ? myTypes.C_CLASS_FIELD : myTypes.C_VAL_DECLARATION).setStart();
        }

        private void parseRef() {
            if (is(myTypes.C_RECORD_FIELD)) {
                remapCurrentToken(myTypes.LIDENT).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
        }

        private void parseMethod() {
            if (is(myTypes.C_RECORD_FIELD)) {
                remapCurrentToken(myTypes.LIDENT).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else {
                popEndUntil(myTypes.C_OBJECT);
                mark(myTypes.C_CLASS_METHOD).setStart();
            }
        }

        private void parseLet() {
            if (!is(myTypes.C_TRY_BODY) && previousElementType(1) != myTypes.RIGHT_ARROW) {
                endLikeSemi();
            }
            mark(myTypes.C_LET_DECLARATION).setStart();
        }

        private void parseModule() {
            if (is(myTypes.C_LET_DECLARATION)) {
                updateComposite(myTypes.C_MODULE_DECLARATION);
            } else if (!is(myTypes.C_MACRO_NAME)) {
                if (!is(myTypes.C_MODULE_TYPE)) {
                    popEndUntilScope();
                }
                mark(myTypes.C_MODULE_DECLARATION).setStart();
            }
        }

        private void parseClass() {
            endLikeSemi();
            mark(myTypes.C_CLASS_DECLARATION).setStart();
        }

        private void endLikeSemi() {
            int previousStep = -1;
            IElementType previousElementType = previousElementType(previousStep);
            while (previousElementType == myTypes.MULTI_COMMENT) {
                previousStep--;
                previousElementType = previousElementType(previousStep);
            }

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
                popEndUntilScope();
            }
        }
    }
}
