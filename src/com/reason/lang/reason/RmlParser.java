package com.reason.lang.reason;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;

public class RmlParser extends CommonPsiParser {
    RmlParser(boolean isSafe) {
        super(isSafe);
    }

    @Override
    protected ORParser<RmlTypes> getORParser(@NotNull PsiBuilder builder) {
        return new RmlParserState(builder, myIsSafe);
    }

    static class RmlParserState extends ORLanguageParser<RmlTypes> {
        public RmlParserState(@NotNull PsiBuilder builder, boolean isSafe) {
            super(RmlTypes.INSTANCE, builder, isSafe);
        }

        @Override
        public void parse() {
            IElementType tokenType;

            long parseStart = System.currentTimeMillis();

            while (!myBuilder.eof()) {
                long parseTime = System.currentTimeMillis();
                if (5000 < parseTime - parseStart) {
                    if (myIsSafe) { // Don't do that in tests
                        error("CANCEL");
                        LOG.error("CANCEL REASON PARSING:\n" + myBuilder.getOriginalText().toString());
                        break;
                    }
                }

                tokenType = myBuilder.getTokenType();

                if (in(myTypes.C_INTERPOLATION_EXPR)) {
                    // special analysis when inside an interpolation string
                    if (tokenType == myTypes.JS_STRING_CLOSE) {
                        parseJsStringClose();
                    } else if (tokenType == myTypes.DOLLAR) {
                        if (is(myTypes.C_INTERPOLATION_PART)) {
                            popEnd();
                            advance().mark(myTypes.C_INTERPOLATION_REF);
                        } else if (is(myTypes.C_INTERPOLATION_EXPR)) {
                            // first element
                            advance().mark(myTypes.C_INTERPOLATION_REF);
                        }
                    } else if (is(myTypes.C_INTERPOLATION_REF)) {
                        advance().popEnd();
                    } else if (!is(myTypes.C_INTERPOLATION_PART)) {
                        mark(myTypes.C_INTERPOLATION_PART);
                    }
                } else {
                    if (tokenType == myTypes.SEMI) {
                        parseSemi();
                    } else if (tokenType == myTypes.EQ) {
                        parseEq();
                    } else if (tokenType == myTypes.ARROW) {
                        parseArrow();
                    } else if (tokenType == myTypes.REF) {
                        parseRef();
                    } else if (tokenType == myTypes.OPTION) {
                        parseOption();
                    } else if (tokenType == myTypes.TRY) {
                        parseTry();
                    } else if (tokenType == myTypes.SWITCH) {
                        parseSwitch();
                    } else if (tokenType == myTypes.LIDENT) {
                        parseLIdent();
                    } else if (tokenType == myTypes.UIDENT) {
                        parseUIdent();
                    } else if (tokenType == myTypes.ARROBASE) {
                        parseArrobase();
                    } else if (tokenType == myTypes.PERCENT) {
                        parsePercent();
                    } else if (tokenType == myTypes.COLON) {
                        parseColon();
                    } else if (tokenType == myTypes.RAW) {
                        parseRaw();
                    } else if (tokenType == myTypes.STRING_VALUE) {
                        parseStringValue();
                    } else if (tokenType == myTypes.PIPE) {
                        parsePipe();
                    } else if (tokenType == myTypes.COMMA) {
                        parseComma();
                    } else if (tokenType == myTypes.AND) {
                        parseAnd();
                    } else if (tokenType == myTypes.FUN) {
                        parseFun();
                    } else if (tokenType == myTypes.ASSERT) {
                        parseAssert();
                    } else if (tokenType == myTypes.IF) {
                        parseIf();
                    } else if (tokenType == myTypes.ELSE) {
                        parseElse();
                    } else if (tokenType == myTypes.THEN) {
                        parseThen();
                    } else if (tokenType == myTypes.DOT) {
                        parseDot();
                    } else if (tokenType == myTypes.DOTDOTDOT) {
                        parseDotDotDot();
                    } else if (tokenType == myTypes.WITH) {
                        parseWith();
                    } else if (tokenType == myTypes.TILDE) {
                        parseTilde();
                    } else if (tokenType == myTypes.QUESTION_MARK) {
                        parseQuestionMark();
                    } else if (tokenType == myTypes.UNDERSCORE) {
                        parseUnderscore();
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
                    // [| ... |]
                    else if (tokenType == myTypes.LARRAY) {
                        parseLArray();
                    } else if (tokenType == myTypes.RARRAY) {
                        parseRArray();
                    }
                    // [ ... ]
                    // [> ... ]
                    else if (tokenType == myTypes.LBRACKET) {
                        parseLBracket();
                    } else if (tokenType == myTypes.BRACKET_GT) {
                        parseBracketGt();
                    } else if (tokenType == myTypes.RBRACKET) {
                        parseRBracket();
                    }
                    // < ... >
                    else if (tokenType == myTypes.LT) {
                        parseLt();
                    } else if (tokenType == myTypes.TAG_LT_SLASH) {
                        parseLtSlash();
                    } else if (tokenType == myTypes.GT) {
                        parseGt();
                    } else if (tokenType == myTypes.TAG_AUTO_CLOSE) {
                        parseGtAutoClose();
                    }
                    // {| ... |}
                    else if (tokenType == myTypes.ML_STRING_OPEN) {
                        parseMlStringOpen();
                    } else if (tokenType == myTypes.ML_STRING_CLOSE) {
                        parseMlStringClose();
                    }
                    // {j| ... |j}
                    else if (tokenType == myTypes.JS_STRING_OPEN) {
                        parseJsStringOpen();
                    }
                    // Starts an expression
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
                    } else if (tokenType == myTypes.PUB) {
                        parsePub();
                    } else if (tokenType == myTypes.EXCEPTION) {
                        parseException();
                    }
                }

                if (dontMove) {
                    dontMove = false;
                } else {
                    myBuilder.advanceLexer();
                }
            }
        }

        private void parseUnderscore() {
            if (is(myTypes.C_PARAMETERS) && isParent(myTypes.C_FUN_EXPR)) {
                // ( |>_<| ...
                mark(myTypes.C_FUN_PARAM);
            }
        }

        private void parseTilde() {
            IElementType nextType = rawLookup(1);
            if (nextType == myTypes.LIDENT) {
                if (!isCurrent(myTypes.C_FUN_PARAM) && !isCurrent(myTypes.C_SIG_ITEM)) {
                    mark(myTypes.C_FUN_PARAM);
                }
                mark(myTypes.C_NAMED_PARAM).advance().wrapWith(myTypes.C_LOWER_IDENTIFIER);
            }
        }

        private void parseQuestionMark() {
            if (inAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
                if (isFound(myTypes.C_TAG_START)) {
                    // <jsx |>?<|prop ...
                    mark(myTypes.C_TAG_PROPERTY)
                            .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace())
                            .advance()
                            .remapCurrentToken(myTypes.PROPERTY_NAME);
                }
            } else if (strictlyIn(myTypes.C_IF_THEN_SCOPE)) {
                // if_then_scope can be inside a ternary
                parseTernary();
            } else if (!strictlyInAny(myTypes.C_TERNARY, myTypes.C_NAMED_PARAM)) {
                if (inScopeOrAny(myTypes.C_LET_BINDING, myTypes.C_FIELD_VALUE, myTypes.C_FUN_PARAM, myTypes.C_FUNCTOR_PARAM,
                        myTypes.C_PATTERN_MATCH_BODY, myTypes.C_IF_THEN_SCOPE, myTypes.C_FUN_BODY, myTypes.C_DUMMY_COLLECTION_ITEM)) {
                    // a new ternary
                    parseTernary();
                }
            }
        }

        private void parseTernary() {
            // «placeHolder» ... |>?<| ...
            int foundPos = getIndex();
            int nextPos = foundPos - 1;
            if (isAtIndex(nextPos, myTypes.C_PLACE_HOLDER)) {
                markBefore(nextPos, myTypes.C_TERNARY)
                        .updateCompositeAt(nextPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(nextPos).end()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE);
                markHolder(myTypes.C_PLACE_HOLDER);
            } else if (isAtIndex(foundPos, myTypes.C_DUMMY_COLLECTION_ITEM)) {
                markHolderBefore(foundPos, myTypes.C_DUMMY_COLLECTION_ITEM);
                markBefore(foundPos, myTypes.C_TERNARY)
                        .updateCompositeAt(foundPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(foundPos).end()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE);
            }
        }

        private void parseRef() {
            if (in(myTypes.C_TAG_START)) {
                remapCurrentToken(myTypes.PROPERTY_NAME)
                        .mark(myTypes.C_TAG_PROPERTY);
            }
        }

        private void parseOption() {
            mark(myTypes.C_OPTION);
        }

        private void parseRaw() {
            if (is(myTypes.C_MACRO_NAME)) {
                // % |>raw<| ...
                advance().popEnd();
            }
        }

        private void parseIf() {
            mark(myTypes.C_IF);
        }

        private void parseThen() {
            // if ... |>then<| ...
            popEndUntil(myTypes.C_IF).advance()
                    .mark(myTypes.C_IF_THEN_SCOPE);
        }

        private void parseElse() {
            // if ... then ... |>else<| ...
            popEndUntil(myTypes.C_IF).advance()
                    .mark(myTypes.C_IF_THEN_SCOPE);
        }

        private void parseDot() {
            if (previousElementType(1) == myTypes.LBRACE && (is(myTypes.C_JS_OBJECT))) { // Js object definition
                // ... { |>.<| ... }
                advance().mark(myTypes.C_OBJECT_FIELD);
            }
        }

        private void parseDotDotDot() {
            if (previousElementType(1) == myTypes.LBRACE) { // Mixin
                // { |>...<| x ...
                if (isCurrent(myTypes.C_FUN_BODY)) {
                    rollbackToPos(getIndex())
                            .mark(myTypes.C_FUN_BODY)
                            .markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE).advance()
                            .mark(myTypes.C_MIXIN_FIELD);
                } else {
                    popIfHold()
                            .updateComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_MIXIN_FIELD);
                }
            }
        }

        private void parseWith() {
            if (in(myTypes.C_FUNCTOR_RESULT)) {
                // module M (X) : ( S |>with<| ... ) = ...
                popEndUntilFoundIndex().popEnd().advance()
                        .mark(myTypes.C_CONSTRAINTS);
            }
        }

        private void parseAssert() {
            mark(myTypes.C_ASSERT_STMT).advance();
        }

        private void parseFun() {
            if (isCurrent(myTypes.C_LET_BINDING) && isHold()) { // fun keyword is equivalent to a switch body
                // let x = |>fun<| | ...
                updateComposite(myTypes.C_FUN_EXPR);
            }
        }

        private void parseAnd() {
            if (in(myTypes.C_CONSTRAINT)) {
                // module M = (X) : ( S with ... |>and<| ... ) = ...
                popEndUntilFoundIndex().popEnd();
            } else {
                Marker scope = popEndUntilScope();
                if (scope != null) {
                    if (scope.isCompositeType(myTypes.C_TYPE_DECLARATION)) {
                        advance().mark(myTypes.C_TYPE_DECLARATION).setStart();
                    } else if (scope.isCompositeType(myTypes.C_LET_DECLARATION)) {
                        advance().mark(myTypes.C_LET_DECLARATION).setStart();
                    } else if (scope.isCompositeType(myTypes.C_MODULE_DECLARATION) || scope.isCompositeType(myTypes.C_MODULE_TYPE)) {
                        advance().mark(myTypes.C_MODULE_DECLARATION).setStart();
                    }
                }
            }
        }

        private void parseComma() {
            if (inScopeOrAny(
                    myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.C_SIG_ITEM, myTypes.C_MIXIN_FIELD,
                    myTypes.C_VARIANT_CONSTRUCTOR, myTypes.C_PARAMETERS, myTypes.C_SIG_EXPR, myTypes.C_DUMMY_COLLECTION_ITEM
            )) {

                if (isFound(myTypes.C_DUMMY_COLLECTION_ITEM)) {
                    popEndUntilFoundIndex().popEnd().advance();
                    markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
                    return;
                }
                if (isFound(myTypes.C_SCOPED_EXPR) && isAtIndex(getIndex() + 1, myTypes.C_LET_DECLARATION)) { // It must be a deconstruction
                    // let ( a |>,<| b ) = ...
                    // We need to do it again because lower symbols must be wrapped with identifiers
                    rollbackToPos(getIndex())
                            .markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN).advance();
                    return;
                }
                if (isFound(myTypes.C_SCOPED_EXPR) && isFoundScope(myTypes.LBRACE) && isAtIndex(getIndex() + 1, myTypes.C_FUN_PARAM)) { // It must be a deconstruction in parameters
                    // { a |>,<| b } => ...
                    // We need to do it again because lower symbols must be wrapped with identifiers
                    rollbackToPos(getIndex())
                            .markScope(myTypes.C_DECONSTRUCTION, myTypes.LBRACE).advance();
                    return;
                }

                if (isFound(myTypes.C_SIG_ITEM)) {
                    popEndUntilFoundIndex();
                    // double sig ? ~x:int,
                    if (in(myTypes.C_NAMED_PARAM, /*not*/myTypes.C_SCOPED_EXPR)) {
                        popEndUntilFoundIndex().popEnd();
                    }
                    popEnd();
                }

                if (strictlyInAny(myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD)) {
                    boolean isRecord = isFound(myTypes.C_RECORD_FIELD);
                    popEndUntilFoundIndex().popEnd().advance();

                    IElementType tokenType = getTokenType();
                    if (tokenType != myTypes.RBRACE && tokenType != myTypes.LBRACKET) {
                        mark(isRecord ? myTypes.C_RECORD_FIELD : myTypes.C_OBJECT_FIELD);
                    }
                } else if (strictlyIn(myTypes.C_MIXIN_FIELD)) {
                    popEndUntilFoundIndex().popEnd().advance();
                } else if (strictlyIn(myTypes.C_DECONSTRUCTION)) {
                    popEndUntilScope();
                } else if (strictlyInAny(myTypes.C_VARIANT_CONSTRUCTOR, myTypes.C_PARAMETERS)) {
                    popEndUntilFoundIndex().advance();
                    if (getTokenType() != myTypes.RPAREN) {
                        // not at the end of a list: ie not => (p1, p2<,> )
                        markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
                        mark(myTypes.C_FUN_PARAM);
                        markHolder(myTypes.C_PLACE_HOLDER);
                    }
                } else if (isDropped(myTypes.C_SCOPED_EXPR) && isParent(myTypes.C_SIG_EXPR)) {
                    // type t = ( ... |>,<|
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (strictlyIn(myTypes.C_SCOPED_EXPR)) {
                    popEndUntilFoundIndex();
                }
            }

        }

        private void parsePipe() {
            if (is(myTypes.C_TRY_HANDLERS)) {
                // try (...) { |>|<| ...
                mark(myTypes.C_TRY_HANDLER);
            } else if (is(myTypes.C_TYPE_BINDING)) {
                // type t = |>|<| ...
                popEndUntil(myTypes.C_TYPE_BINDING).advance()
                        .mark(myTypes.C_VARIANT_DECLARATION);
            } else if (in(myTypes.C_VARIANT_DECLARATION)) {
                // type t = | X |>|<| Y ...
                popEndUntil(myTypes.C_TYPE_BINDING).advance()
                        .mark(myTypes.C_VARIANT_DECLARATION);
            } else if (in(myTypes.C_PATTERN_MATCH_BODY)) {
                // can be a switchBody or a 'fun'
                if (!is(myTypes.C_SWITCH_BODY)) {
                    popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd().advance();
                }
                mark(myTypes.C_PATTERN_MATCH_EXPR);
            } else {
                if (!is(myTypes.C_SWITCH_BODY) && in(myTypes.C_PATTERN_MATCH_EXPR, /*not*/myTypes.C_PATTERN_MATCH_BODY)) { // pattern grouping
                    // | X |>|<| Y => ...
                    popEndUntilIndex(getIndex()).popEnd();
                }

                if (isScope(myTypes.LBRACKET) && isParent(myTypes.C_TYPE_BINDING)) {
                    // type t = [ |>|<| ...
                    advance().mark(myTypes.C_VARIANT_DECLARATION);
                } else { // By default, a pattern match
                    advance().mark(myTypes.C_PATTERN_MATCH_EXPR);
                }
            }
        }

        private void parseStringValue() {
            if (is(myTypes.C_MACRO_EXPR)) {
                // [%raw |>"x"<| ...
                wrapWith(myTypes.C_MACRO_BODY);
            } else if (in(myTypes.C_MACRO_NAME)) {
                // [@bs |>"x<| ...
                popEndUntilScope();
            } else if (is(myTypes.C_JS_OBJECT)) {
                mark(myTypes.C_OBJECT_FIELD).wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (is(myTypes.C_OBJECT_FIELD)) {
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            }
        }

        private void parseMlStringOpen() {
            if (is(myTypes.C_MACRO_EXPR)) {
                mark(myTypes.C_MACRO_BODY);
            }

            markScope(myTypes.C_ML_INTERPOLATOR, myTypes.ML_STRING_OPEN);
        }

        private void parseMlStringClose() {
            Marker scope = popEndUntilScopeToken(myTypes.ML_STRING_OPEN);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseJsStringOpen() {
            markScope(myTypes.C_INTERPOLATION_EXPR, myTypes.JS_STRING_OPEN);
        }

        private void parseJsStringClose() {
            Marker scope = popEndUntilScopeToken(myTypes.JS_STRING_OPEN);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseLet() {
            mark(myTypes.C_LET_DECLARATION).setStart();
        }

        private void parseVal() {
            if (!in(myTypes.C_MACRO_NAME)) {
                popEndUntilScope();
                if (is(myTypes.C_OBJECT)) {
                    mark(myTypes.C_CLASS_FIELD);
                }
            }
        }

        private void parsePub() {
            if (in(myTypes.C_OBJECT)) {
                popEndUntil(myTypes.C_OBJECT).
                        mark(myTypes.C_CLASS_METHOD);
            }
        }

        private void parseModule() {
            if (!in(myTypes.C_MACRO_NAME)) {
                popEndUntilScope();
                mark(myTypes.C_MODULE_DECLARATION).setStart();
            }
        }

        private void parseException() {
            popEndUntilScope();
            mark(myTypes.C_EXCEPTION_DECLARATION).setStart();
        }

        private void parseClass() {
            popEndUntilScope();
            mark(myTypes.C_CLASS_DECLARATION).setStart();
        }

        private void parseType() {
            if (is(myTypes.C_CONSTRAINTS)) {
                // module M = (X) : ( S with |>type<| ... ) = ...
                mark(myTypes.C_CONSTRAINT);
            } else if (!is(myTypes.C_MODULE_DECLARATION) && !is(myTypes.C_CLASS_DECLARATION)) {
                popEndUntilScope();
                mark(myTypes.C_TYPE_DECLARATION).setStart();
            }
        }

        private void parseExternal() {
            popEndUntilScope();
            mark(myTypes.C_EXTERNAL_DECLARATION).setStart();
        }

        private void parseOpen() {
            popEndUntilScope();
            mark(myTypes.C_OPEN);
        }

        private void parseInclude() {
            popEndUntilScope();
            mark(myTypes.C_INCLUDE);
        }

        private void parsePercent() {
            if (is(myTypes.C_MACRO_EXPR)) {
                mark(myTypes.C_MACRO_NAME);
            } else if (in(myTypes.C_LET_DECLARATION)) {
                // let name|>%<|private = ...
                mark(myTypes.C_LET_ATTR);
            } else {
                IElementType nextTokenType = rawLookup(1);
                if (nextTokenType == myTypes.RAW) {
                    // |>%<| raw ...
                    mark(myTypes.C_MACRO_EXPR).
                            mark(myTypes.C_MACRO_NAME);
                }
            }
        }

        private void parseColon() {
            if (isCurrent(myTypes.C_MODULE_DECLARATION)) {
                // module M |> :<| ...
                advance();
                boolean isParen = getTokenType() == myTypes.LPAREN;
                if (isParen) {
                    // module M : |>(<| ...
                    advance();
                }
                mark(myTypes.C_MODULE_TYPE).updateScopeToken(isParen ? myTypes.LPAREN : null);
            } else if (isParent(myTypes.C_EXTERNAL_DECLARATION) || isParent(myTypes.C_LET_DECLARATION)) {
                // external/let e |> :<| ...
                advance().mark(myTypes.C_SIG_EXPR);
                if (getTokenType() == myTypes.LPAREN) {
                    // external/let e : |>(<| ...
                    markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                    if (getTokenType() == myTypes.DOT) {
                        // external/let e : ( |>.<| ...
                        advance();
                    }
                }
                mark(myTypes.C_SIG_ITEM);
            }
            //
            else if (inAny(
                    myTypes.C_NAMED_PARAM, myTypes.C_FUN_PARAM, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD,
                    myTypes.C_TERNARY
            )) {

                if (isFound(myTypes.C_NAMED_PARAM) || isFound(myTypes.C_FUN_PARAM)) {
                    // let x = (~y |> : <| ...
                    advance().mark(myTypes.C_SIG_EXPR);
                    markDummyParenthesisScope().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_RECORD_FIELD) || isFound(myTypes.C_OBJECT_FIELD)) {
                    advance();
                    if (in(myTypes.C_TYPE_BINDING)) {
                        mark(myTypes.C_SIG_EXPR)
                                .mark(myTypes.C_SIG_ITEM);
                    } else {
                        mark(myTypes.C_FIELD_VALUE);
                    }
                } else if (isFound(myTypes.C_TERNARY)) {
                    // x ? y |> :<| ...
                    popEndUntilFoundIndex()
                            .advance().mark(myTypes.C_IF_THEN_SCOPE);
                    markHolder(myTypes.C_PLACE_HOLDER);
                }

            }
        }

        private void parseArrobase() {
            if (is(myTypes.C_ANNOTATION)) {
                mark(myTypes.C_MACRO_NAME);
            }
        }

        private void parseLt() {
            // Can be a symbol or a JSX tag
            IElementType nextTokenType = rawLookup(1);
            // Note that option is a ReasonML keyword but also a JSX keyword !
            if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.UIDENT || nextTokenType == myTypes.OPTION) {
                // Surely a tag
                mark(myTypes.C_TAG)
                        .markScope(myTypes.C_TAG_START, myTypes.LT)
                        .advance()
                        .remapCurrentToken(myTypes.TAG_NAME)
                        .wrapWith(nextTokenType == myTypes.UIDENT ? myTypes.C_UPPER_SYMBOL : myTypes.C_LOWER_SYMBOL);
            } else if (nextTokenType == myTypes.GT) {
                // a React fragment start
                mark(myTypes.C_TAG)
                        .mark(myTypes.C_TAG_START)
                        .advance().advance().popEnd();
            }
        }

        private void parseLtSlash() {
            IElementType nextTokenType = rawLookup(1);
            // Note that option is a ReasonML keyword but also a JSX keyword !
            if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.UIDENT || nextTokenType == myTypes.OPTION) {
                // A closing tag
                if (in(myTypes.C_TAG_BODY)) {
                    popEndUntil(myTypes.C_TAG);
                }

                remapCurrentToken(myTypes.TAG_LT_SLASH)
                        .mark(myTypes.C_TAG_CLOSE)
                        .advance()
                        .remapCurrentToken(myTypes.TAG_NAME)
                        .wrapWith(nextTokenType == myTypes.UIDENT ? myTypes.C_UPPER_SYMBOL : myTypes.C_LOWER_SYMBOL);
            } else if (nextTokenType == myTypes.GT) {
                // a React fragment end
                remapCurrentToken(myTypes.TAG_LT_SLASH)
                        .mark(myTypes.C_TAG_CLOSE)
                        .advance().advance().popEnd();
            }
        }

        private void parseGt() {
            if (isCurrent(myTypes.C_TAG_PROP_VALUE)) {
                // ?prop=value |> > <| ...
                popEndUntil(myTypes.C_TAG_PROP_VALUE).popEnd().popEnd();
            } else if (is(myTypes.C_TAG_PROPERTY)) {
                // ?prop |> > <| ...
                popEnd();
            }

            if (in(myTypes.C_TAG)) {
                if (inScopeOrAny(myTypes.C_TAG_PROP_VALUE, myTypes.C_TAG_START)) {
                    advance().popEndUntilIndex(getIndex());
                    if (is(myTypes.C_TAG_START)) {
                        popEnd().mark(myTypes.C_TAG_BODY);
                    }
                } else if (in(myTypes.C_TAG_CLOSE)) {
                    // end the tag
                    advance().popEndUntil(myTypes.C_TAG).popEnd();
                }
            }
        }

        private void parseGtAutoClose() {
            popEndUntilScope();
            advance().popEnd(/*tag_start*/).popEnd(/*tag*/);
        }

        private void parseLIdent() {
            if (is(myTypes.C_LET_DECLARATION)) {
                // let |>x<| ...
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (is(myTypes.C_TYPE_DECLARATION)) {
                // type |>x<| ...
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (is(myTypes.C_EXTERNAL_DECLARATION)) {
                // external |>x<| ...
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (is(myTypes.C_CLASS_DECLARATION)) {
                // class |>x<| ...
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (is(myTypes.C_RECORD_EXPR)) {
                // let x = { |>y<| ...
                mark(myTypes.C_RECORD_FIELD).wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (is(myTypes.C_RECORD_FIELD)) {
                // let x = { y, |>z<| ...
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if ((isCurrent(myTypes.C_PARAMETERS) /*&& isCurrentParent(myTypes.C_FUN_EXPR)*/) || isCurrent(myTypes.C_VARIANT_CONSTRUCTOR)) {
                // ( x , |>y<| ...
                //if (!isHold()) {
                //    markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
                //}
                mark(myTypes.C_FUN_PARAM);
                markHolder(myTypes.C_PLACE_HOLDER);
                wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (strictlyInAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
                if (previousElementType(1) != myTypes.LT && isFound(myTypes.C_TAG_START)) {
                    // This is a property
                    popEndUntilScope();
                    remapCurrentToken(myTypes.PROPERTY_NAME)
                            .mark(myTypes.C_TAG_PROPERTY)
                            .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace());
                }
            } else {
                IElementType nextElementType = lookAhead(1);

                if (isCurrent(myTypes.C_SCOPED_EXPR) && isCurrentScope(myTypes.LBRACE) && nextElementType == myTypes.COLON) {
                    // this is a record usage ::  { |>x<| : ...
                    popIfHold()
                            .updateComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_RECORD_FIELD)
                            .wrapWith(myTypes.C_LOWER_IDENTIFIER);
                } else if (nextElementType == myTypes.LPAREN && !inAny(myTypes.C_TYPE_BINDING, myTypes.C_CONSTRAINT, myTypes.C_SIG_ITEM)) {
                    mark(myTypes.C_FUN_CALL)
                            .wrapWith(myTypes.C_LOWER_SYMBOL);
                } else if (is(myTypes.C_DECONSTRUCTION)) {
                    wrapWith(myTypes.C_LOWER_IDENTIFIER);
                } else {
                    wrapWith(myTypes.C_LOWER_SYMBOL);
                }
            }
        }

        private void parseLArray() {
            markScope(myTypes.C_SCOPED_EXPR, myTypes.LARRAY).advance();
            markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
        }

        private void parseRArray() {
            Marker scope = popEndUntilScopeToken(myTypes.LARRAY);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseLBracket() {
            IElementType nextTokenType = rawLookup(1);

            if (nextTokenType == myTypes.ARROBASE) {
                // |>[ <| @ ...
                markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
            } else if (nextTokenType == myTypes.PERCENT) {
                // |>[ <| % ...
                markScope(myTypes.C_MACRO_EXPR, myTypes.LBRACKET);
            } else if (previousElementType(2) == myTypes.UIDENT && previousElementType(1) == myTypes.DOT) { // Local open
                // M.|>(<| ...
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LBRACKET);
            } else {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET).advance();
                if (getTokenType() != myTypes.PIPE) {
                    markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
                }
            }
        }

        private void parseRBracket() {
            Marker scope = popEndUntilScopeToken(myTypes.LBRACKET);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseBracketGt() {
            markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET);
        }

        private void parseLBrace() {
            if (previousElementType(2) == myTypes.UIDENT && previousElementType(1) == myTypes.DOT) { // Local open a js object or a record
                // Xxx.|>{<| ... }
                mark(myTypes.C_LOCAL_OPEN);
                IElementType nextElementType = lookAhead(1);
                if (nextElementType == myTypes.LIDENT) {
                    markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE);
                } else {
                    markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
                }
            } else if (is(myTypes.C_TYPE_BINDING)) {
                boolean isJsObject = lookAhead(1) == myTypes.DOT;
                markScope(isJsObject ? myTypes.C_JS_OBJECT : myTypes.C_RECORD_EXPR, myTypes.LBRACE);
            } else if (is(myTypes.C_TRY_EXPR)) { // A try expression
                // try (..) |>{<| .. }
                markScope(myTypes.C_TRY_HANDLERS, myTypes.LBRACE);
            } else if (is(myTypes.C_MODULE_BINDING)) {
                // module M = |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (is(myTypes.C_FUNCTOR_BINDING)) {
                // module M = (...) => |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (isCurrent(myTypes.C_IF)) {
                markScope(myTypes.C_IF_THEN_SCOPE, myTypes.LBRACE);
            } else if (is(myTypes.C_MODULE_TYPE)) {
                // module M : |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (in(myTypes.C_CLASS_DECLARATION)) {
                // class x = |>{<| ... }
                markScope(myTypes.C_OBJECT, myTypes.LBRACE);
            } else if (is(myTypes.C_SWITCH_EXPR)) {
                markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
            } else {
                // it might be a js object
                IElementType nextElement = lookAhead(1);
                if (nextElement == myTypes.STRING_VALUE || nextElement == myTypes.DOT) { // js object detected (in usage)
                    // |>{<| "x" ... }
                    markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
                } else if (is(myTypes.C_FUN_BODY) && !isScope(myTypes.LBRACE) && nextElement != myTypes.LIDENT) { // function body
                    // x => |>{<| ... }
                    updateScopeToken(myTypes.LBRACE);
                } else {
                    if (isHold()) {
                        popEnd();
                    }
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE).advance();
                    markHolder(myTypes.C_PLACE_HOLDER);
                }
            }
        }

        private void parseRBrace() {
            Marker scope = popEndUntilOneOfElementType(myTypes.LBRACE, myTypes.RECORD, myTypes.SWITCH);
            advance();

            if (scope != null) {
                popEnd();

                if (is(myTypes.C_LOCAL_OPEN) && !rawHasScope()) {
                    // X.{ ... |>}<|
                    popEnd();
                } else if (is(myTypes.C_TAG_PROP_VALUE)) {
                    popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
                }
            }
        }

        private void parseLParen() {
            if (isCurrent(myTypes.C_PARAMETERS)) {
                if (!currentHasScope()) {
                    // |>(<| ... ) => ...
                    updateScopeToken(myTypes.LPAREN);
                } else {
                    // ( |>(<| ... ) , ... ) => ...
                    mark(myTypes.C_FUN_PARAM).advance();
                    markHolder(myTypes.C_PLACE_HOLDER);
                }
            } else if (is(myTypes.C_ASSERT_STMT)) {
                // assert |>(<| ...
                markScope(myTypes.C_BINARY_CONDITION, myTypes.LPAREN);
            } else if (is(myTypes.C_TRY_EXPR)) {
                // try |>(<| ...
                markScope(myTypes.C_TRY_BODY, myTypes.LPAREN);
            } else if (is(myTypes.C_IF) || is(myTypes.C_SWITCH_EXPR)) {
                // if |>(<| ...  OR  switch |>(<| ...
                markScope(myTypes.C_BINARY_CONDITION, myTypes.LPAREN);
            } else if (is(myTypes.C_LET_DECLARATION)) { // Overloading operator OR deconstructing a term
                //  let |>(<| + ) =
                //  let |>(<| a, b ) =
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
            } else if (previousElementType(2) == myTypes.UIDENT && previousElementType(1) == myTypes.DOT) { // Local open
                // M.|>(<| ...
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
            } else if (is(myTypes.C_MODULE_BINDING) && !in(myTypes.C_FUNCTOR_DECLARATION)) { // This is a functor
                // module M = |>(<| .. )
                int moduleIndex = indexOfComposite(myTypes.C_MODULE_DECLARATION);
                rollbackToPos(moduleIndex - 1)
                        .updateComposite(myTypes.C_FUNCTOR_DECLARATION);
            } else if (is(myTypes.C_DECONSTRUCTION) && isParent(myTypes.C_LET_DECLARATION)) {
                // let ((x |>,<| ...
                markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN);
            } else if (inAny(myTypes.C_FUNCTOR_DECLARATION, myTypes.C_FUNCTOR_CALL, myTypes.C_FUNCTOR_RESULT)) {
                // module M = |>(<| ...
                // module M = ( ... ) : |>(<| ...
                if (isFound(myTypes.C_FUNCTOR_DECLARATION) || isFound(myTypes.C_FUNCTOR_CALL)) {
                    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                    mark(myTypes.C_FUNCTOR_PARAM);
                    markHolder(myTypes.C_PLACE_HOLDER);
                }
            } else if (in(myTypes.C_VARIANT_DECLARATION)) { // Variant params
                // type t = | Variant |>(<| .. )
                markScope(myTypes.C_VARIANT_CONSTRUCTOR, myTypes.LPAREN).advance();
                markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
                mark(myTypes.C_FUN_PARAM);
            } else if (inAny(myTypes.C_CLASS_DECLARATION, myTypes.C_OBJECT)) {
                if (isFound(myTypes.C_CLASS_DECLARATION)) {
                    popEndUntil(myTypes.C_CLASS_DECLARATION).
                            markScope(myTypes.C_CLASS_CONSTR, myTypes.LPAREN);
                }
            } else if (in(myTypes.C_FUN_CALL)
                    && !(is(myTypes.C_TYPE_DECLARATION)
                    || inAny(myTypes.C_TYPE_BINDING, myTypes.C_SIG_ITEM))) { // calling a function
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                IElementType nextTokenType = getTokenType();
                if (nextTokenType == myTypes.RPAREN) {
                    markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
                } else {
                    mark(myTypes.C_FUN_PARAM);
                    markHolder(myTypes.C_PLACE_HOLDER);
                }
            } else if (inAny(myTypes.C_OPEN, myTypes.C_INCLUDE)) { // a functor call inside open/include
                // open/include M |>(<| ...
                markBefore(getIndex() - 1, myTypes.C_FUNCTOR_CALL)
                        .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .mark(myTypes.C_FUN_PARAM);
            } else {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                markHolder(myTypes.C_DUMMY_COLLECTION_ITEM);
            }
        }

        private void parseRParen() {
            // Removing intermediate resolutions
            Marker scope = popEndUntilScopeToken(myTypes.LPAREN);
            advance();

            if (is(myTypes.C_BINARY_CONDITION) && isParent(myTypes.C_IF)) {
                // if ( x |>)<| ...
                end();
                if (getTokenType() != myTypes.LBRACE) {
                    mark(myTypes.C_IF_THEN_SCOPE);
                }
            } else if (scope != null) {
                popEnd();
                IElementType nextTokenType = getTokenType();

                if (isParent(myTypes.C_FUN_CALL)) {
                    popEnd().popEnd();
                } else if (isParent(myTypes.C_FUNCTOR_DECLARATION)) {
                    if (nextTokenType == myTypes.COLON) {
                        // module M = (P) |> :<| R ...
                        advance();
                        if (getTokenType() == myTypes.LPAREN) {
                            markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                        }
                        mark(myTypes.C_FUNCTOR_RESULT);
                    } else if (nextTokenType == myTypes.ARROW) {
                        // module M = (P) |>=><| ...
                        advance().mark(myTypes.C_FUNCTOR_BINDING);
                    }
                } else if (isCurrent(myTypes.C_TAG_PROP_VALUE)) {
                    popEndUntilFoundIndex().popEnd();
                }
            }
        }

        private void parseEq() {
            if (is(myTypes.C_TAG_PROPERTY)) {
                // <X p|> =<| ...
                advance().mark(myTypes.C_TAG_PROP_VALUE);
            } else if (isParent(myTypes.C_MODULE_DECLARATION)) {
                // module M |> =<| ...
                advance().mark(myTypes.C_MODULE_BINDING);
            } else if (isParent(myTypes.C_NAMED_PARAM)) {
                // ( ~x |> =<| ...
                advance().mark(myTypes.C_DEFAULT_VALUE);
            } else if (in(myTypes.C_TYPE_DECLARATION)) {
                // type t |> =<| ...
                advance().mark(myTypes.C_TYPE_BINDING);
            } else if (in(myTypes.C_MODULE_TYPE)) {
                // module M : T |> =<| ...
                popEndUntilIndex(getIndex()).popEnd().advance()
                        .mark(myTypes.C_MODULE_BINDING);
            } else if (inScopeOrAny(myTypes.C_LET_DECLARATION, myTypes.C_SIG_EXPR)) {
                if (isFound(myTypes.C_SIG_EXPR)) {
                    popEndUntil(myTypes.C_SIG_EXPR).popEnd();
                    if (in(myTypes.C_NAMED_PARAM)) {
                        advance().mark(myTypes.C_DEFAULT_VALUE);
                    } else if (in(myTypes.C_LET_DECLARATION)) {
                        parseLetBinding();
                    }
                } else if (in(myTypes.C_LET_DECLARATION)) {
                    parseLetBinding();
                }
            }
        }

        // let ... |> =<| ...
        private void parseLetBinding() {
            popEndUntil(myTypes.C_LET_DECLARATION).advance()
                    .mark(myTypes.C_LET_BINDING);
            markHolder(myTypes.C_PLACE_HOLDER);
        }

        private void parseSemi() {
            if (in(myTypes.C_PATTERN_MATCH_BODY)) {
                if (in(myTypes.C_FUN_EXPR)) { // Special case for the `fun` keyword
                    popEndUntilScope();
                }
            } else {
                popEndUntilScope();
            }

            if (is(myTypes.C_LET_BINDING) || rawHasScope()) {
                // let x = ... |>;<|
                // { ... |>;<|
                advance().markHolder(myTypes.C_PLACE_HOLDER);
            }
        }

        private void parseUIdent() {
            if (DUMMY_IDENTIFIER_TRIMMED.equals(getTokenText())) {
                return;
            }

            if (is(myTypes.C_MODULE_DECLARATION) || is(myTypes.C_FUNCTOR_DECLARATION)) {
                // module |>M<| ...
                wrapWith(myTypes.C_UPPER_IDENTIFIER);
            } else if (is(myTypes.C_VARIANT_DECLARATION)) {
                // type t = | |>X<| ..
                wrapWith(myTypes.C_UPPER_IDENTIFIER);
            } else if (is(myTypes.C_EXCEPTION_DECLARATION)) {
                // exception |>E<| ..
                wrapWith(myTypes.C_UPPER_IDENTIFIER);
            } else if (is(myTypes.C_PATTERN_MATCH_EXPR)) {
                wrapWith(myTypes.C_UPPER_SYMBOL);
            } else {
                if ((in(myTypes.C_TAG_START) || in(myTypes.C_TAG_CLOSE)) && previousElementType(1) == myTypes.DOT) { // a namespaced custom component
                    // <X.|>Y<| ...
                    remapCurrentToken(myTypes.TAG_NAME);
                } else {
                    IElementType nextElementType = lookAhead(1);

                    if (is(myTypes.C_TYPE_BINDING) && nextElementType != myTypes.DOT) {
                        // We are declaring a variant without a pipe before ::  type t = |>X<| | ...
                        mark(myTypes.C_VARIANT_DECLARATION).wrapWith(myTypes.C_UPPER_IDENTIFIER);
                        return;
                    } else if (isCurrent(myTypes.C_MODULE_BINDING) && nextElementType == myTypes.LPAREN) {
                        // functor call ::  |>X<| ( ...
                        // functor call with path :: A.B.|>X<| ( ...
                        Marker marker = getCurrentMarker();
                        if (marker != null) {
                            marker.drop();
                            mark(myTypes.C_FUNCTOR_CALL);
                        }
                    }
                }

                wrapWith(myTypes.C_UPPER_SYMBOL);
            }
        }

        private void parseSwitch() {
            mark(myTypes.C_SWITCH_EXPR);
        }

        private void parseTry() {
            mark(myTypes.C_TRY_EXPR);
        }

        private void parseArrow() {
            if (is(myTypes.C_SIG_EXPR)) {
                advance()
                        .mark(myTypes.C_SIG_ITEM);
            } else if (in(myTypes.C_SIG_ITEM, /*not*/ myTypes.C_SCOPED_EXPR)) {
                popEndUntil(myTypes.C_SIG_ITEM).popEnd().advance()
                        .mark(myTypes.C_SIG_ITEM);
            } else if (in(myTypes.C_FUNCTOR_RESULT)) {
                // module Make = (M) : R |>=><| ...
                popEndUntilFoundIndex().popEnd()
                        .advance().mark(myTypes.C_FUNCTOR_BINDING);
            } else if (inScopeOrAny(
                    myTypes.C_LET_BINDING, myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_PARAMETERS,
                    myTypes.C_FUN_PARAM, myTypes.C_FUN_EXPR, myTypes.C_FIELD_VALUE
            )) {

                if (isFound(myTypes.C_FUN_PARAM)) {
                    int paramIndex = getIndex();
                    if (inAny(myTypes.C_FUN_EXPR, myTypes.C_FUN_CALL)) {
                        if (isFound(myTypes.C_FUN_EXPR)) {
                            // x |>=><| ...
                            popEndUntil(myTypes.C_FUN_EXPR)
                                    .advance().mark(myTypes.C_FUN_BODY);
                            markHolder(myTypes.C_PLACE_HOLDER);
                        } else {
                            // call(x |>=><| ...
                            rollbackToPos(paramIndex);
                            mark(myTypes.C_FUN_PARAM)
                                    .mark(myTypes.C_FUN_EXPR)
                                    .mark(myTypes.C_PARAMETERS);
                        }
                    }
                } else if (isFound(myTypes.C_LET_BINDING)) {
                    // function parameters ::  |>x<| => ...
                    rollbackToPos(getIndex()).mark(myTypes.C_LET_BINDING)
                            .mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                } else if (isFound(myTypes.C_FIELD_VALUE)) {
                    // function parameters ::  |>x<| => ...
                    rollbackToPos(getIndex()).mark(myTypes.C_FIELD_VALUE)
                            .mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                } else if (isFound(myTypes.C_PARAMETERS) || isFound(myTypes.C_FUN_EXPR)) {
                    popEndUntilOneOf(myTypes.C_PARAMETERS, myTypes.C_FUN_EXPR);
                    if (isParent(myTypes.C_FUN_EXPR) || isParent(myTypes.C_FUN_CALL)) {
                        popEnd();
                    }
                    advance().mark(myTypes.C_FUN_BODY);
                } else if (isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                    advance().mark(myTypes.C_PATTERN_MATCH_BODY);
                    markHolder(myTypes.C_PLACE_HOLDER);
                } else if (isHold() && !in(myTypes.C_SIG_ITEM)) {
                    // by default, a function
                    rollbackToPos(0)
                            .mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                }
            }
        }
    }
}
