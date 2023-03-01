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
                    } else if (tokenType == myTypes.METHOD) {
                        parseMethod();
                    } else if (tokenType == myTypes.OPTION) {
                        parseOption();
                    } else if (tokenType == myTypes.MATCH) {
                        parseMatch();
                    } else if (tokenType == myTypes.TRY) {
                        parseTry();
                    } else if (tokenType == myTypes.SWITCH) {
                        parseSwitch();
                    } else if (tokenType == myTypes.LIDENT) {
                        parseLIdent();
                    } else if (tokenType == myTypes.UIDENT) {
                        parseUIdent();
                    } else if (tokenType == myTypes.POLY_VARIANT) {
                        parsePolyVariant();
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
                    } else if (tokenType == myTypes.SOME) {
                        parseSome();
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

        private void parseSome() {
            markHolder(myTypes.C_SOME); // holder or real ?
            advance();
            if (getTokenType() != myTypes.LPAREN) {
                error("Missing parenthesis");
            } else {
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM);
            }
        }

        private void parseMethod() {
            if (isCurrent(myTypes.C_RECORD_EXPR)) {
                // { |>method<| : ...
                remapCurrentToken(myTypes.LIDENT).mark(myTypes.C_RECORD_FIELD);
            }
        }

        private void parsePolyVariant() {
            if (isRawParent(myTypes.C_TYPE_BINDING)) {
                // type t = [ |>`xxx<| ...
                mark(myTypes.C_VARIANT_DECLARATION);
            }
            advance();
            markParenthesisScope(false);
        }

        private void parseUnderscore() {
            if (is(myTypes.C_PARAMETERS) && isRawParent(myTypes.C_FUNCTION_EXPR)) {
                // ( |>_<| ...
                mark(myTypes.C_PARAM_DECLARATION);
            } else {
                IElementType nextElementType = lookAhead(1);
                if (nextElementType == myTypes.ARROW && strictlyInAny(myTypes.C_LET_BINDING, myTypes.C_DEFAULT_VALUE, myTypes.C_PARAM, myTypes.C_FIELD_VALUE)) {
                    // A paren-less function definition ::  |>_<| =>
                    mark(myTypes.C_FUNCTION_EXPR)
                            .mark(myTypes.C_PARAMETERS)
                            .mark(myTypes.C_PARAM_DECLARATION)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL);
                }
            }
        }

        private void parseTilde() {
            IElementType nextType = rawLookup(1);
            if (nextType == myTypes.LIDENT) {
                if (isCurrent(myTypes.C_PARAM_DECLARATION)) {
                    markBefore(0, myTypes.C_PARAM_DECLARATION)
                            .updateComposite(myTypes.H_NAMED_PARAM_DECLARATION).updateToHolder()
                            .advance().wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else if (isCurrent(myTypes.C_SIG_ITEM)) {
                    mark(myTypes.C_PARAM_DECLARATION)
                            .markHolder(myTypes.H_NAMED_PARAM_DECLARATION)
                            .advance().wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else {
                    if (isCurrent(myTypes.C_PARAMETERS) && in(myTypes.C_FUNCTION_EXPR)) {
                        mark(myTypes.C_PARAM_DECLARATION).markHolder(myTypes.H_NAMED_PARAM_DECLARATION);
                    } else if (isCurrent(myTypes.C_PARAM)) {
                        updateComposite(myTypes.C_NAMED_PARAM);
                    } else {
                        mark(myTypes.C_NAMED_PARAM);
                    }
                    advance().wrapAtom(myTypes.CA_LOWER_SYMBOL);
                }
            }
        }

        private void parseQuestionMark() {
            if (is(myTypes.C_TAG_START)) {
                // <jsx |>?<|prop ...
                mark(myTypes.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace())
                        .advance()
                        .remapCurrentToken(myTypes.PROPERTY_NAME);
            } else if (strictlyIn(myTypes.C_IF_THEN_SCOPE)) {
                // if_then_scope can be inside a ternary
                parseTernary();
            } else if (!strictlyInAny(myTypes.C_TERNARY) && previousElementType(1) != myTypes.EQ /*default optional value*/) {
                if (inScopeOrAny(myTypes.C_LET_BINDING, myTypes.C_FIELD_VALUE, myTypes.C_PARAM_DECLARATION, myTypes.C_PARAM,
                        myTypes.C_PATTERN_MATCH_BODY, myTypes.C_IF_THEN_SCOPE, myTypes.C_FUNCTION_BODY, myTypes.H_COLLECTION_ITEM,
                        myTypes.C_DEFAULT_VALUE)) {
                    // a new ternary
                    parseTernary();
                }
            }
        }

        private void parseTernary() {
            // «placeHolder» ... |>?<| ...
            int foundPos = getIndex();
            int nextPos = foundPos - 1;
            if (isAtIndex(nextPos, myTypes.H_PLACE_HOLDER)) {
                markBefore(nextPos, myTypes.C_TERNARY)
                        .updateCompositeAt(nextPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(nextPos).end()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE);
                markHolder(myTypes.H_PLACE_HOLDER);
            } else if (isAtIndex(foundPos, myTypes.H_COLLECTION_ITEM)) {
                markHolderBefore(foundPos, myTypes.H_COLLECTION_ITEM)
                        .markBefore(foundPos, myTypes.C_TERNARY)
                        .updateCompositeAt(foundPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(foundPos).end()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE);
            }
        }

        private void parseRef() {
            if (isCurrent(myTypes.C_RECORD_EXPR)) {
                remapCurrentToken(myTypes.LIDENT).mark(myTypes.C_RECORD_FIELD);
            } else if (strictlyIn(myTypes.C_TAG_START)) {
                remapCurrentToken(myTypes.PROPERTY_NAME)
                        .mark(myTypes.C_TAG_PROPERTY);
            }
        }

        private void generateJsxPropertyName() {
            remapCurrentToken(myTypes.PROPERTY_NAME)
                    .mark(myTypes.C_TAG_PROPERTY)
                    .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace());
        }

        private void parseOption() {
            if (strictlyIn(myTypes.C_TAG_START)) {
                generateJsxPropertyName();
            } else {
                mark(myTypes.C_OPTION);
            }
        }

        private void parseMatch() {
            if (strictlyIn(myTypes.C_TAG_START)) {
                generateJsxPropertyName();
            }
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
            if (previousElementType(1) == myTypes.LBRACE) { // Mixin, always first element in a record
                // { |>...<| x ...
                if (isCurrent(myTypes.C_FUNCTION_BODY)) {
                    markBefore(0, myTypes.C_FUNCTION_BODY)
                            .updateLatestComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_MIXIN_FIELD);
                } else {
                    popIfHold()
                            .updateComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_MIXIN_FIELD);
                }
            }
        }

        private void parseWith() {
            if (strictlyInAny(myTypes.C_FUNCTOR_RESULT, myTypes.C_MODULE_TYPE)) {
                // module M (X) : ( S |>with<| ... ) = ...
                popEndUntilFoundIndex().popEnd()
                        .advance().mark(myTypes.C_CONSTRAINTS);
            }
        }

        private void parseAssert() {
            mark(myTypes.C_ASSERT_STMT).advance();
        }

        private void parseFun() {
            if (isCurrent(myTypes.C_LET_BINDING) && isHold()) { // fun keyword is equivalent to a switch body
                // let x = |>fun<| | ...
                updateLatestComposite(myTypes.C_FUN_EXPR);
            }
        }

        private void parseAnd() {
            if (in(myTypes.C_TYPE_CONSTRAINT)) {
                // module M = (X) : ( S with ... |>and<| ... ) = ...
                popEndUntilFoundIndex().popEnd();
            } else {
                Marker scope = popEndUntilScope();
                if (scope != null) {
                    if (scope.isCompositeType(myTypes.C_TYPE_DECLARATION)) {
                        advance().mark(myTypes.C_TYPE_DECLARATION);
                    } else if (scope.isCompositeType(myTypes.C_LET_DECLARATION)) {
                        advance().mark(myTypes.C_LET_DECLARATION);
                    } else if (scope.isCompositeType(myTypes.C_MODULE_DECLARATION) || scope.isCompositeType(myTypes.C_MODULE_TYPE)) {
                        advance().mark(myTypes.C_MODULE_DECLARATION);
                    }
                }
            }
        }

        private void parseComma() {
            if (inScopeOrAny(
                    myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.C_MIXIN_FIELD,
                    myTypes.C_VARIANT_CONSTRUCTOR, myTypes.C_SIG_EXPR, myTypes.C_SIG_ITEM, myTypes.H_COLLECTION_ITEM,
                    myTypes.C_PARAMETERS, myTypes.C_PARAM_DECLARATION, myTypes.C_NAMED_PARAM, myTypes.C_PARAM
            )) {

                if (isFound(myTypes.H_COLLECTION_ITEM)) {
                    popEndUntilFoundIndex().popEnd().advance();
                    markHolder(myTypes.H_COLLECTION_ITEM);
                    return;
                }
                if (isFound(myTypes.C_SCOPED_EXPR) && isAtIndex(getIndex() + 1, myTypes.C_LET_DECLARATION)) { // It must be a deconstruction
                    // let ( a |>,<| b ) = ...
                    // We need to do it again because lower symbols must be wrapped with identifiers
                    rollbackToFoundIndex()
                            .updateComposite(myTypes.C_DECONSTRUCTION);
                    return;
                }
                if (isFound(myTypes.C_SCOPED_EXPR) && isFoundScope(myTypes.LBRACE) && isAtIndex(getIndex() + 1, myTypes.C_PARAM_DECLARATION)) { // It must be a deconstruction in parameters
                    // { a |>,<| b } => ...
                    // We need to do it again because lower symbols must be wrapped with identifiers
                    rollbackToFoundIndex()
                            .updateComposite(myTypes.C_DECONSTRUCTION);
                    return;
                }

                if (isFound(myTypes.C_SIG_ITEM) || (isFound(myTypes.C_PARAM_DECLARATION) && isAtIndex(getIndex() + 1, myTypes.C_SIG_ITEM))) {
                    popEndUntilFoundIndex();
                    if (isRawParent(myTypes.H_COLLECTION_ITEM)) {
                        // ( ... |>,<|
                        popEnd().popEnd().advance()
                                .markHolder(myTypes.H_COLLECTION_ITEM)
                                .mark(myTypes.C_SIG_ITEM);
                        return;
                    }

                    if (strictlyInAny(myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD)) {
                        // { x:t , ...
                        popEndUntilFoundIndex().popEnd();
                    } else if (in(myTypes.C_PARAM_DECLARATION, /*not*/myTypes.C_SCOPED_EXPR)) {
                        // double sig ? ~x:int,
                        popEndUntilFoundIndex().popEnd();
                        popEnd();
                    } else {
                        popEnd();
                    }
                }

                if (isScope(myTypes.LPAREN) && isRawParent(myTypes.C_SIG_EXPR)) {
                    // type t = ( ... |>,<|
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (strictlyInAny(myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD)) {
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
                } else if (strictlyInAny(myTypes.C_PARAM_DECLARATION)) {
                    popEndUntilFoundIndex().popEnd().advance();
                    if (getTokenType() != myTypes.RPAREN) {
                        // not at the end of a list: ie not => (p1, p2<,> )
                        mark(myTypes.C_PARAM_DECLARATION);
                        markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (strictlyInAny(myTypes.C_NAMED_PARAM, myTypes.C_PARAM)) {
                    popEndUntilFoundIndex().popEnd().advance();
                    if (getTokenType() != myTypes.RPAREN) {
                        // not at the end of a list: ie not => (p1, p2<,> )
                        mark(myTypes.C_PARAM);
                        markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (strictlyInAny(myTypes.C_VARIANT_CONSTRUCTOR, myTypes.C_PARAMETERS)) {
                    popEndUntilFoundIndex().advance();
                    if (getTokenType() != myTypes.RPAREN) {
                        // not at the end of a list: ie not => (p1, p2<,> )
                        markHolder(myTypes.H_COLLECTION_ITEM);
                        mark(myTypes.C_PARAM_DECLARATION);
                        markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (strictlyIn(myTypes.C_SCOPED_EXPR)) {
                    popEndUntilFoundIndex();
                }
            }

        }

        private void parsePipe() {
            if (is(myTypes.C_TRY_HANDLERS)) {
                // try (...) { |>|<| ...
                advance().mark(myTypes.C_TRY_HANDLER);
            } else if (is(myTypes.C_TYPE_BINDING)) {
                // type t = |>|<| ...
                popEndUntil(myTypes.C_TYPE_BINDING).advance()
                        .mark(myTypes.C_VARIANT_DECLARATION);
            } else if (in(myTypes.C_VARIANT_DECLARATION)) {
                // type t = | X |>|<| Y ...
                popEndUntilFoundIndex().popEnd().advance()
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

                if (isScope(myTypes.LBRACKET) && isRawParent(myTypes.C_TYPE_BINDING)) {
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
                mark(myTypes.C_OBJECT_FIELD).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_OBJECT_FIELD)) {
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
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
            mark(myTypes.C_LET_DECLARATION);
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
            if (is(myTypes.C_SIG_ITEM)) {
                // let fn = (~x : ( |>module<| ... ) ...
                mark(myTypes.C_MODULE_VALUE);
            } else if (in(myTypes.C_DEFAULT_VALUE)) {
                // let fn = (~x:(module X)= ( |>module<| ... ) ...
                popIfHold()
                        .updateLatestComposite(myTypes.H_PLACE_HOLDER).dropLatest()
                        .mark(myTypes.C_MODULE_VALUE);
            } else if (!in(myTypes.C_MACRO_NAME)) {
                popEndUntilScope();
                mark(myTypes.C_MODULE_DECLARATION);
            }
        }

        private void parseException() {
            popEndUntilScope();
            mark(myTypes.C_EXCEPTION_DECLARATION);
        }

        private void parseClass() {
            popEndUntilScope();
            mark(myTypes.C_CLASS_DECLARATION);
        }

        private void parseType() {
            if (is(myTypes.C_CONSTRAINTS)) {
                // module M = (X) : ( S with |>type<| ... ) = ...
                mark(myTypes.C_TYPE_CONSTRAINT);
            } else if (!is(myTypes.C_MODULE_DECLARATION) && !is(myTypes.C_CLASS_DECLARATION)) {
                popEndUntilScope();
                mark(myTypes.C_TYPE_DECLARATION);
            }
        }

        private void parseExternal() {
            popEndUntilScope();
            mark(myTypes.C_EXTERNAL_DECLARATION);
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
            } else if (isRawParent(myTypes.C_EXTERNAL_DECLARATION) || isRawParent(myTypes.C_LET_DECLARATION)) {
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
            else if (inScopeOrAny(
                    myTypes.H_NAMED_PARAM_DECLARATION, myTypes.C_PARAM_DECLARATION, myTypes.C_NAMED_PARAM,
                    myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.C_TERNARY
            )) {

                if (isFound(myTypes.H_NAMED_PARAM_DECLARATION) || isFound(myTypes.C_PARAM_DECLARATION) || isFound(myTypes.C_NAMED_PARAM)) {
                    // let x = (~y |> : <| ...
                    advance().mark(myTypes.C_SIG_EXPR);
                    markParenthesisScope(true).
                            mark(myTypes.C_SIG_ITEM);
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
                    markHolder(myTypes.H_PLACE_HOLDER);
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
                        .remapCurrentToken(nextTokenType == myTypes.UIDENT ? myTypes.A_UPPER_TAG_NAME : myTypes.A_LOWER_TAG_NAME)
                        .wrapAtom(nextTokenType == myTypes.UIDENT ? myTypes.CA_UPPER_SYMBOL : myTypes.CA_LOWER_SYMBOL)
                        .popEnd();
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

                mark(myTypes.C_TAG_CLOSE)
                        .advance()
                        .remapCurrentToken(nextTokenType == myTypes.UIDENT ? myTypes.A_UPPER_TAG_NAME : myTypes.A_LOWER_TAG_NAME)
                        .wrapAtom(nextTokenType == myTypes.UIDENT ? myTypes.CA_UPPER_SYMBOL : myTypes.CA_LOWER_SYMBOL);
            } else if (nextTokenType == myTypes.GT) {
                // a React fragment end
                mark(myTypes.C_TAG_CLOSE)
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
                if (inScopeOrAny(myTypes.C_TAG_PROP_VALUE, myTypes.C_TAG_START, myTypes.C_TAG_CLOSE)) {
                    advance().popEndUntilFoundIndex();
                    if (is(myTypes.C_TAG_START)) {
                        popEnd().mark(myTypes.C_TAG_BODY);
                    } else if (is(myTypes.C_TAG_CLOSE)) {
                        // end the tag
                        popEndUntil(myTypes.C_TAG).popEnd();
                    }
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
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_TYPE_DECLARATION)) {
                // type |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_EXTERNAL_DECLARATION)) {
                // external |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_CLASS_DECLARATION)) {
                // class |>x<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_RECORD_EXPR)) {
                // let x = { |>y<| ...
                mark(myTypes.C_RECORD_FIELD).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (is(myTypes.C_RECORD_FIELD)) {
                // let x = { y, |>z<| ...
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if ((isCurrent(myTypes.C_PARAMETERS)) || isCurrent(myTypes.C_VARIANT_CONSTRUCTOR)) {
                // ( x , |>y<| ...
                mark(myTypes.C_PARAM_DECLARATION);
                markHolder(myTypes.H_PLACE_HOLDER);
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (strictlyInAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
                if (previousElementType(1) != myTypes.LT && isFound(myTypes.C_TAG_START)) {
                    // This is a property
                    popEndUntilScope();
                    generateJsxPropertyName();
                }
            } else {
                IElementType nextElementType = lookAhead(1);

                if (isCurrent(myTypes.C_SCOPED_EXPR) && isCurrentScope(myTypes.LBRACE) && nextElementType == myTypes.COLON) {
                    // this is a record usage ::  { |>x<| : ...
                    popIfHold()
                            .updateComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_RECORD_FIELD)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else if (nextElementType == myTypes.LPAREN && !inAny(myTypes.C_TYPE_BINDING, myTypes.C_TYPE_CONSTRAINT, myTypes.C_SIG_ITEM)) {
                    mark(myTypes.C_FUNCTION_CALL)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else if (is(myTypes.C_DECONSTRUCTION)) {
                    wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else if (nextElementType == myTypes.ARROW && strictlyInAny(
                        myTypes.C_LET_BINDING, myTypes.C_DEFAULT_VALUE, myTypes.C_PARAM, myTypes.C_FIELD_VALUE, myTypes.C_SCOPED_EXPR
                )) {
                    // A paren-less function definition ::  |>x<| =>
                    mark(myTypes.C_FUNCTION_EXPR)
                            .mark(myTypes.C_PARAMETERS)
                            .mark(myTypes.C_PARAM_DECLARATION)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else {
                    wrapAtom(myTypes.CA_LOWER_SYMBOL);
                }
            }
        }

        private void parseLArray() {
            markScope(myTypes.C_SCOPED_EXPR, myTypes.LARRAY).advance();
            markHolder(myTypes.H_COLLECTION_ITEM);
        }

        private void parseRArray() {
            Marker scope = popEndUntilScopeToken(myTypes.LARRAY);
            advance();

            if (scope != null) {
                popEnd();
            }
        }

        private void parseLBracket() {
            IElementType nextType = rawLookup(1);

            if (nextType == myTypes.ARROBASE) {
                // |>[ <| @ ...
                markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
            } else if (nextType == myTypes.PERCENT) {
                // |>[ <| % ...
                markScope(myTypes.C_MACRO_EXPR, myTypes.LBRACKET);
            } else if (previousElementType(2) == myTypes.A_MODULE_NAME && previousElementType(1) == myTypes.DOT) { // Local open
                // M.|>(<| ...
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LBRACKET);
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
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET).advance();
                if (getTokenType() != myTypes.PIPE) {
                    markHolder(myTypes.H_COLLECTION_ITEM);
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
            if (previousElementType(2) == myTypes.A_MODULE_NAME && previousElementType(1) == myTypes.DOT) { // Local open a js object or a record
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
                } else if (is(myTypes.C_FUNCTION_BODY) && !isScope(myTypes.LBRACE) && nextElement != myTypes.LIDENT) { // function body
                    // x => |>{<| ... }
                    updateScopeToken(myTypes.LBRACE);
                } else {
                    if (isHold()) {
                        popEnd();
                    }
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE)
                            .advance().markHolder(myTypes.H_PLACE_HOLDER);
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
            if (isCurrent(myTypes.C_PARAM_DECLARATION)) {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                markHolder(myTypes.H_PLACE_HOLDER);
            } else if (isCurrent(myTypes.C_PARAMETERS)) {
                if (!currentHasScope()) {
                    // |>(<| ... ) => ...
                    updateScopeToken(myTypes.LPAREN);
                    markHolder(myTypes.H_COLLECTION_ITEM);
                } else {
                    // ( |>(<| ... ) , ... ) => ...
                    mark(myTypes.C_PARAM_DECLARATION)
                            .markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance()
                            .markHolder(myTypes.H_COLLECTION_ITEM);
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
            } else if (previousElementType(2) == myTypes.A_MODULE_NAME && previousElementType(1) == myTypes.DOT) { // Local open
                // M.|>(<| ...
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
            } else if (is(myTypes.C_MODULE_BINDING) && !in(myTypes.C_FUNCTOR_DECLARATION)) {
                if (myBuilder.lookAhead(1) == myTypes.VAL) {
                    markParenthesisScope(true);
                } else if (in(myTypes.C_MODULE_DECLARATION)) {
                    // This is a functor ::  module M = |>(<| .. )
                    updateCompositeAt(getIndex(), myTypes.C_FUNCTOR_DECLARATION)
                            .updateComposite(myTypes.C_PARAMETERS).updateScopeToken(myTypes.LPAREN).advance()
                            .markHolder(myTypes.H_COLLECTION_ITEM)
                            .mark(myTypes.C_PARAM_DECLARATION)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (is(myTypes.C_DECONSTRUCTION) && isRawParent(myTypes.C_LET_DECLARATION)) {
                // let ((x |>,<| ...
                markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN);
            } else if (previousElementType(1) == myTypes.SOME) {
                // Some |>(<|
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                        .advance().markHolder(myTypes.H_PLACE_HOLDER);
            } else if (in(myTypes.C_FUNCTION_CALL)
                    && !(is(myTypes.C_TYPE_DECLARATION) || isCurrent(myTypes.C_PARAM)
                    || inAny(myTypes.C_TYPE_BINDING, myTypes.C_SIG_ITEM))) { // calling a function
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                IElementType nextTokenType = getTokenType();
                if (nextTokenType == myTypes.RPAREN) {
                    markHolder(myTypes.H_COLLECTION_ITEM);
                } else {
                    mark(myTypes.C_PARAM);
                    markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (inAny(myTypes.C_FUNCTOR_CALL, myTypes.C_FUNCTOR_RESULT)) {
                // module M = ( ... ) : |>(<| ...
                if (isFound(myTypes.C_FUNCTOR_CALL)) {
                    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (in(myTypes.C_VARIANT_DECLARATION)) { // Variant params
                // type t = | Variant |>(<| .. )
                markScope(myTypes.C_VARIANT_CONSTRUCTOR, myTypes.LPAREN).advance();
                markHolder(myTypes.H_COLLECTION_ITEM);
                mark(myTypes.C_PARAM_DECLARATION);
            } else if (inAny(myTypes.C_CLASS_DECLARATION, myTypes.C_OBJECT)) {
                if (isFound(myTypes.C_CLASS_DECLARATION)) {
                    popEndUntil(myTypes.C_CLASS_DECLARATION).
                            markScope(myTypes.C_CLASS_CONSTR, myTypes.LPAREN);
                }
            } else {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                markHolder(myTypes.H_COLLECTION_ITEM);
            }
        }

        private void parseRParen() {
            // Removing intermediate resolutions
            Marker lParen = popEndUntilScopeToken(myTypes.LPAREN);
            advance();

            if (is(myTypes.C_BINARY_CONDITION) && isRawParent(myTypes.C_IF)) {
                // if ( x |>)<| ...
                end();
                if (getTokenType() != myTypes.LBRACE) {
                    mark(myTypes.C_IF_THEN_SCOPE);
                }
            } else if (lParen != null) {
                IElementType nextTokenType = getTokenType();

                if (nextTokenType == myTypes.ARROW && !isParent(myTypes.C_FUNCTION_EXPR) && !inAny(myTypes.C_SIG_EXPR, myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_FUNCTOR_DECLARATION)) {
                    // a missed function expression
                    rollbackToIndex(0)
                            .markBefore(0, myTypes.C_FUNCTION_EXPR)
                            .updateComposite(myTypes.C_PARAMETERS)
                            .markHolder(myTypes.H_COLLECTION_ITEM);
                } else {
                    popEnd();

                    if (isRawParent(myTypes.C_FUNCTION_CALL)) {
                        popEnd().popEnd();
                    } else if (isCurrent(myTypes.C_FUNCTOR_DECLARATION)) {
                        if (nextTokenType == myTypes.COLON) {
                            // module M = (P) |> :<| R ...
                            advance();
                            markParenthesisScope(true);
                            //if (getTokenType() == myTypes.LPAREN) {
                            //    markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                            //}
                            mark(myTypes.C_FUNCTOR_RESULT);
                        } else if (nextTokenType == myTypes.ARROW) {
                            // module M = (P) |>=><| ...
                            advance().mark(myTypes.C_FUNCTOR_BINDING);
                        }
                    } else if (isCurrent(myTypes.C_TAG_PROP_VALUE)) {
                        popEndUntil(myTypes.C_TAG_PROP_VALUE).popEnd();
                    }
                }
            }
        }

        private void parseEq() {
            if (is(myTypes.C_TAG_PROPERTY)) {
                // <X p|> =<| ...
                advance().mark(myTypes.C_TAG_PROP_VALUE);
            } else if (isCurrent(myTypes.C_MODULE_DECLARATION) || isRawParent(myTypes.C_MODULE_DECLARATION)) {
                // module M |> =<| ...
                advance().mark(myTypes.C_MODULE_BINDING);
            } else if (isParent(myTypes.C_PARAM_DECLARATION) || isParent(myTypes.C_NAMED_PARAM)) {
                // ( ~x |> =<| ...
                popEndUntilFoundIndex()
                        .advance().mark(myTypes.C_DEFAULT_VALUE)
                        .markHolder(myTypes.H_PLACE_HOLDER);
            } else if (strictlyIn(myTypes.C_TYPE_BINDING) && strictlyIn(myTypes.C_CONSTRAINTS)) {
                // .. with type .. = .. |> =<| ..
                popEndUntilFoundIndex().popEnd();
                if (strictlyIn(myTypes.C_MODULE_DECLARATION)) {
                    popEndUntilFoundIndex()
                            .advance().mark(myTypes.C_MODULE_BINDING);
                }
            } else if (strictlyInAny(myTypes.C_TYPE_DECLARATION, myTypes.C_TYPE_CONSTRAINT)) {
                // type t |> =<| ...
                advance().mark(myTypes.C_TYPE_BINDING);
            } else if (inScopeOrAny(myTypes.C_LET_DECLARATION, myTypes.C_MODULE_TYPE, myTypes.C_SIG_EXPR)) {

                if (isFound(myTypes.C_SIG_EXPR)) {
                    popEndUntil(myTypes.C_SIG_EXPR).popEnd();
                    if (inAny(myTypes.H_NAMED_PARAM_DECLARATION, myTypes.C_NAMED_PARAM)) {
                        advance().mark(myTypes.C_DEFAULT_VALUE);
                    } else if (in(myTypes.C_LET_DECLARATION)) {
                        parseLetBinding();
                    }
                } else if (isFound(myTypes.C_MODULE_TYPE)) {
                    // module M : T |> =<| ...
                    popEndUntilIndex(getIndex()).popEnd()
                            .advance().mark(myTypes.C_MODULE_BINDING);
                } else if (!isFound(myTypes.C_SCOPED_EXPR) && in(myTypes.C_LET_DECLARATION)) {
                    if (!isAtIndex(getIndex() - 1, myTypes.C_LET_BINDING)) { // already parsed
                        parseLetBinding();
                    }
                }

            }
        }

        // let ... |> =<| ...
        private void parseLetBinding() {
            popEndUntil(myTypes.C_LET_DECLARATION).advance()
                    .mark(myTypes.C_LET_BINDING);
            markHolder(myTypes.H_PLACE_HOLDER);
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
                advance().markHolder(myTypes.H_PLACE_HOLDER);
            }
        }

        private void parseUIdent() {
            if (DUMMY_IDENTIFIER_TRIMMED.equals(getTokenText())) {
                return;
            }

            if (is(myTypes.C_MODULE_DECLARATION) || is(myTypes.C_MODULE_VALUE)) {
                // module |>M<| ...
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_PARAM_DECLARATION) && in(myTypes.C_FUNCTOR_DECLARATION)) {
                // module M = ( |>P<| ...
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_FUNCTOR_RESULT)) {
                // module M = ( .. ) : |>R<| ...
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_SIG_ITEM) && in(myTypes.C_FUNCTOR_DECLARATION, /*not*/myTypes.C_FUNCTOR_BINDING)) {
                // module M = (P: |>S<| ...
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (is(myTypes.C_VARIANT_DECLARATION)) {
                // type t = | |>X<| ..
                remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (is(myTypes.C_EXCEPTION_DECLARATION)) {
                // exception |>E<| ..
                remapCurrentToken(myTypes.EXCEPTION_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_TRY_HANDLER)) {
                // try .. { | |>X<| ..
                remapCurrentToken(myTypes.EXCEPTION_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else if ((in(myTypes.C_TAG_START, /*not*/myTypes.C_TAG_PROP_VALUE) || in(myTypes.C_TAG_CLOSE)) && previousElementType(1) == myTypes.DOT) { // a namespaced custom component
                // <X.|>Y<| ...
                remapCurrentToken(myTypes.A_UPPER_TAG_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            } else {
                IElementType nextToken = lookAhead(1);

                if (is(myTypes.C_TYPE_BINDING) && nextToken != myTypes.DOT) {
                    // We are declaring a variant without a pipe before ::  type t = |>X<| | ...
                    mark(myTypes.C_VARIANT_DECLARATION)
                            .remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                } else if (nextToken == myTypes.LPAREN && (isCurrent(myTypes.C_MODULE_BINDING) || isCurrent(myTypes.C_OPEN) || isCurrent(myTypes.C_INCLUDE))) {
                    // functor call ::  |>X<| ( ...
                    // functor call with path :: A.B.|>X<| ( ...
                    mark(myTypes.C_FUNCTOR_CALL)
                            .remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                } else if (((isCurrent(myTypes.C_PATTERN_MATCH_EXPR) || isCurrent(myTypes.C_LET_BINDING))) && nextToken != myTypes.DOT) { // Pattern matching a variant or using it
                    // switch (c) { | |>X<| ... / let x = |>X<| ...
                    remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                } else {
                    remapCurrentToken(nextToken == myTypes.DOT || isCurrent(myTypes.C_MODULE_BINDING) || isCurrent(myTypes.C_MODULE_TYPE)
                            || isCurrent(myTypes.C_OPEN) || isCurrent(myTypes.C_INCLUDE)
                            ? myTypes.A_MODULE_NAME : myTypes.A_VARIANT_NAME);
                    wrapAtom(myTypes.CA_UPPER_SYMBOL);
                }
            }
        }

        private void parseSwitch() {
            mark(myTypes.C_SWITCH_EXPR);
        }

        private void parseTry() {
            mark(myTypes.C_TRY_EXPR);
        }

        private void parseArrow() {
            if (inScopeOrAny(
                    myTypes.C_SIG_EXPR, myTypes.C_SIG_ITEM, myTypes.C_FUNCTOR_RESULT, myTypes.C_PATTERN_MATCH_EXPR,
                    myTypes.C_FUNCTION_EXPR, myTypes.C_PARAMETERS, myTypes.C_PARAM_DECLARATION, myTypes.C_TRY_HANDLER
            )) {
                if (isFound(myTypes.C_SIG_EXPR)) {
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_SIG_ITEM)) {
                    popEndUntil(myTypes.C_SIG_ITEM).popEnd()
                            .advance().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_FUNCTOR_RESULT)) {
                    // module Make = (M) : R |>=><| ...
                    popEndUntilFoundIndex().popEnd()
                            .advance().mark(myTypes.C_FUNCTOR_BINDING);
                } else if (isFound(myTypes.C_PARAM_DECLARATION)) {
                    if (isRawParent(myTypes.H_COLLECTION_ITEM)) {
                        // inside a parenthesis, function not declared yet
                    } else {
                        // x |>=><| ...
                        popEndUntil(myTypes.C_FUNCTION_EXPR).advance()
                                .mark(myTypes.C_FUNCTION_BODY);
                        markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (isFound(myTypes.C_PARAMETERS) || isFound(myTypes.C_FUNCTION_EXPR)) {
                    popEndUntilOneOf(myTypes.C_PARAMETERS, myTypes.C_FUNCTION_EXPR);
                    if (isRawParent(myTypes.C_FUNCTION_EXPR) || isRawParent(myTypes.C_FUNCTION_CALL)) {
                        popEnd();
                    }
                    advance().mark(myTypes.C_FUNCTION_BODY);
                } else if (isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                    advance().mark(myTypes.C_PATTERN_MATCH_BODY);
                    markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_TRY_HANDLER)) {
                    popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_TRY_HANDLER_BODY);
                }
            }
        }
    }
}
