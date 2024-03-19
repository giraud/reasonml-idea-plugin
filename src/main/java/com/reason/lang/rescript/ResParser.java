package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;

public class ResParser extends CommonPsiParser {
    ResParser(boolean isSafe) {
        super(isSafe);
    }

    @Override
    protected ORParser<ResTypes> getORParser(@NotNull PsiBuilder builder) {
        return new ResParserState(builder, myIsSafe);
    }

    static class ResParserState extends ORLanguageParser<ResTypes> {
        public ResParserState(@NotNull PsiBuilder builder, boolean isSafe) {
            super(ResTypes.INSTANCE, builder, isSafe);
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
                if (tokenType == TokenType.BAD_CHARACTER) {
                    error("Bad character");
                    break;
                }

                if (tokenType == myTypes.EOL) {
                    IElementType previousType = previousElementType(1);
                    boolean previousOperator = previousType == myTypes.ARROW
                            || previousType == myTypes.RIGHT_ARROW
                            || previousType == myTypes.COLON
                            || previousType == myTypes.COMMA
                            || previousType == myTypes.EQ
                            || previousType == myTypes.LPAREN
                            || previousType == myTypes.STRING_CONCAT;

                    myBuilder.remapCurrentToken(myTypes.WHITE_SPACE);
                    advanceSkipEOL();

                    if (!previousOperator) {
                        IElementType nextType = getTokenType();
                        boolean nextOperator = nextType == myTypes.QUESTION_MARK
                                || nextType == myTypes.RIGHT_ARROW // first pipe
                                || nextType == myTypes.COLON // ternary
                                || nextType == myTypes.PIPE // variant
                                || (nextType == myTypes.LPAREN && isRawParent(myTypes.C_FUNCTION_CALL)); // function call
                        if (!nextOperator && !myMarkers.isEmpty()) {
                            Marker marker = myMarkers.peek();
                            // like popEndUntilScope
                            while (marker != null && !marker.hasScope() && !marker.isCompositeType(myTypes.C_TAG_BODY)) {
                                marker = pop();
                                if (marker != null) {
                                    marker.end();
                                }
                                marker = getLatestMarker();
                            }

                            if (currentHasScope() && !isHold()) {
                                markHolder(myTypes.H_PLACE_HOLDER);
                            }
                        }
                    }
                }
                // Special analysis when inside an interpolation string
                else if (is(myTypes.C_INTERPOLATION_EXPR)
                        || is(myTypes.C_INTERPOLATION_PART)
                        || is(myTypes.C_INTERPOLATION_REF)) {
                    if (tokenType == myTypes.JS_STRING_CLOSE) {
                        if (is(myTypes.C_INTERPOLATION_REF)) {
                            // not closed ref
                            dropLatest();
                        }
                        popEndUntil(myTypes.C_INTERPOLATION_EXPR).advance().popEnd();
                    } else if (tokenType == myTypes.DOLLAR) {
                        IElementType nextElementType = rawLookup(1);
                        if (nextElementType == myTypes.LBRACE) {
                            if (is(myTypes.C_INTERPOLATION_REF)) {
                                dropLatest();
                            }
                            advance().advance().markScope(myTypes.C_INTERPOLATION_REF, myTypes.LBRACE);
                        } else {
                            remapCurrentToken(myTypes.STRING_VALUE);
                        }
                    } else if (is(myTypes.C_INTERPOLATION_REF) && tokenType == myTypes.RBRACE) {
                        popEnd().advance();
                    } else if (tokenType == myTypes.LBRACE || tokenType == myTypes.RBRACE) {   // not a ref
                        remapCurrentToken(myTypes.STRING_VALUE);
                    }
                } else if (tokenType == myTypes.EQ) {
                    parseEq();
                } else if (tokenType == myTypes.SOME) {
                    parseSome();
                } else if (tokenType == myTypes.NONE) {
                    parseNone();
                } else if (tokenType == myTypes.WITH) {
                    parseWith();
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
                } else if (tokenType == myTypes.CATCH) {
                    parseCatch();
                } else if (tokenType == myTypes.SWITCH) {
                    parseSwitch();
                } else if (tokenType == myTypes.LIDENT) {
                    parseLIdent();
                } else if (tokenType == myTypes.UIDENT) {
                    parseUIdent();
                } else if (tokenType == myTypes.PROPERTY_NAME) {
                    parseLTagName();
                } else if (tokenType == myTypes.A_UPPER_TAG_NAME) {
                    parseUTagName();
                } else if (tokenType == myTypes.POLY_VARIANT) {
                    parsePolyVariant();
                } else if (tokenType == myTypes.UNIT) {
                    parseUnit();
                } else if (tokenType == myTypes.ARROBASE) {
                    parseArrobase();
                } else if (tokenType == myTypes.PERCENT) {
                    parsePercent();
                } else if (tokenType == myTypes.COLON) {
                    parseColon();
                } else if (tokenType == myTypes.STRING_VALUE) {
                    parseStringValue();
                } else if (tokenType == myTypes.PIPE) {
                    parsePipe();
                } else if (tokenType == myTypes.COMMA) {
                    parseComma();
                } else if (tokenType == myTypes.AND) {
                    parseAnd();
                } else if (tokenType == myTypes.ASSERT) {
                    parseAssert();
                } else if (tokenType == myTypes.DOTDOTDOT) {
                    parseDotDotDot();
                } else if (tokenType == myTypes.QUESTION_MARK) {
                    parseQuestionMark();
                } else if (tokenType == myTypes.TILDE) {
                    parseTilde();
                } else if (tokenType == myTypes.UNDERSCORE) {
                    parseUnderscore();
                } else if (tokenType == myTypes.INT_VALUE || tokenType == myTypes.FLOAT_VALUE) {
                    parseNumeric();
                } else if (tokenType == myTypes.UNPACK) {
                    parseUnpack();
                }
                // if ... else
                else if (tokenType == myTypes.IF) {
                    parseIf();
                } else if (tokenType == myTypes.ELSE) {
                    parseElse();
                }
                // ( ... )
                else if (tokenType == myTypes.LPAREN) {
                    parseLParen();
                } else if (tokenType == myTypes.RPAREN) {
                    parseRParen();
                }
                // { ... }
                // list{ ... }
                else if (tokenType == myTypes.LBRACE) {
                    parseLBrace();
                } else if (tokenType == myTypes.LIST) {
                    parseList();
                } else if (tokenType == myTypes.RBRACE) {
                    parseRBrace();
                }
                // [ ... ]
                else if (tokenType == myTypes.LBRACKET) {
                    parseLBracket();
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
                // j` ... `
                else if (tokenType == myTypes.JS_STRING_OPEN) {
                    parseTemplateStringOpen();
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
                } else if (tokenType == myTypes.LET) {
                    parseLet();
                } else if (tokenType == myTypes.EXCEPTION) {
                    parseException();
                }

                if (dontMove) {
                    dontMove = false;
                } else {
                    myBuilder.advanceLexer();
                }
            }
        }

        private void parseUnpack() {
            if (is(myTypes.C_MODULE_BINDING)) {
                updateComposite(myTypes.C_UNPACK);
            } else {
                mark(myTypes.C_UNPACK);
            }

            advance();
            if (getTokenType() != myTypes.LPAREN) {
                error("Missing parenthesis");
            } else {
                updateScopeToken(myTypes.LPAREN).advance();
            }
        }

        private void parseUnit() {
            if (!is(myTypes.C_SIG_ITEM)) {
                mark(myTypes.C_SIG_ITEM);
            }
        }

        private void parseSome() {
            mark(myTypes.C_SOME);
            advance();
            if (getTokenType() != myTypes.LPAREN) {
                error("Missing parenthesis");
            } else {
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM);
            }
        }

        private void parseNone() {
            wrapWith(myTypes.C_SOME);
        }

        private void parseList() {
            if (lookAhead(1) == myTypes.LBRACE) {
                // |>list<| { ... }
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE)
                        .advance().advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM); // Needed to roll back to individual item in collection
            }
        }

        private void parseNumeric() {
            if (is(myTypes.C_PARAMETERS)) {
                boolean inCall = in(myTypes.C_FUNCTION_CALL) || in(myTypes.C_FUNCTOR_CALL);
                mark(inCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
            }
            wrapAtom(myTypes.H_ATOM);
        }

        private void parseUnderscore() {
            if (is(myTypes.C_PARAMETERS)) {
                boolean inCall = isParent(myTypes.C_FUNCTION_CALL) || isParent(myTypes.C_FUNCTOR_CALL);
                mark(inCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
            } else {
                IElementType nextElementType = lookAhead(1);
                if (nextElementType == myTypes.ARROW && strictlyInAny(
                        myTypes.C_LET_BINDING, myTypes.C_DEFAULT_VALUE, myTypes.C_PARAM, myTypes.C_FIELD_VALUE
                )) {
                    // A paren-less function definition ::  |>_<| =>
                    popIfHold().mark(myTypes.C_FUNCTION_EXPR)
                            .mark(myTypes.C_PARAMETERS).mark(myTypes.C_PARAM_DECLARATION)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL);
                }
            }
        }

        private void parseRef() {
            if (isCurrent(myTypes.C_RECORD_EXPR)) {
                // { |>x<| ...
                remapCurrentToken(myTypes.LIDENT).
                        mark(myTypes.C_RECORD_FIELD).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (strictlyIn(myTypes.C_TAG_START)) {
                remapCurrentToken(myTypes.PROPERTY_NAME).mark(myTypes.C_TAG_PROPERTY);
            }
        }

        private void parseMethod() {
            if (is(myTypes.C_RECORD_EXPR)) {
                remapCurrentToken(myTypes.LIDENT).
                        mark(myTypes.C_RECORD_FIELD).wrapAtom(myTypes.CA_LOWER_SYMBOL);
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
                mark(myTypes.C_OPTION).advance();
                if (getTokenType() == myTypes.LPAREN) {
                    updateScopeToken(myTypes.LPAREN).advance();
                }
            }
        }

        private void parseMatch() {
            if (strictlyIn(myTypes.C_TAG_START)) {
                generateJsxPropertyName();
            }
        }

        private void parseIf() {
            if (isCurrent(myTypes.C_PATTERN_MATCH_EXPR)) {
                // switch x { | X |>if<| ...
                mark(myTypes.C_GUARD).advance()
                        .mark(myTypes.C_BINARY_CONDITION);
            } else {
                mark(myTypes.C_IF).advance()
                        .mark(myTypes.C_BINARY_CONDITION);
            }
        }

        private void parseElse() {
            // if ... |>else<| ...
            popEndUntil(myTypes.C_IF)
                    .advance().mark(myTypes.C_IF_THEN_ELSE);
        }

        private void parseDotDotDot() {
            if (previousElementType(1) == myTypes.LBRACE) { // Mixin
                // ... { |>...<| x ...
                updateComposite(myTypes.C_RECORD_EXPR)
                        .mark(myTypes.C_MIXIN_FIELD);
            }
        }

        private void parseQuestionMark() {
            if (strictlyInAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE, myTypes.C_RECORD_FIELD, myTypes.C_FIELD_VALUE)) {
                // <jsx |>?<|prop ...
                if (isFound(myTypes.C_TAG_START)) {
                    mark(myTypes.C_TAG_PROPERTY)
                            .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace())
                            .advance()
                            .remapCurrentToken(myTypes.PROPERTY_NAME);
                }
                // type t = { key|>?<|: ... }
                //else if (isFound(myTypes.C_RECORD_FIELD)) {
                // skip
                //}
                // let _ = { key: a |>?<| ... }
                else if (isFound(myTypes.C_FIELD_VALUE)) {
                    if (!inAny(myTypes.C_TERNARY) && previousElementType(1) != myTypes.EQ) {
                        if (inScopeOrAny(myTypes.H_PLACE_HOLDER, myTypes.H_COLLECTION_ITEM)) { // a new ternary
                            parseTernary(getIndex());
                        }
                    }
                }
            } else if (isDone(myTypes.C_BINARY_CONDITION) || strictlyIn(myTypes.C_BINARY_CONDITION)) { // a ternary in progress
                // ... |>?<| ...
                popEndUntilFoundIndex().end()
                        .advance()
                        .mark(myTypes.C_IF_THEN_ELSE);
            } else if (!inAny(myTypes.C_TERNARY) && previousElementType(1) != myTypes.EQ) {
                if (inScopeOrAny(myTypes.H_PLACE_HOLDER, myTypes.H_COLLECTION_ITEM)) { // a new ternary
                    parseTernary(getIndex());
                }
            }
        }

        private void parseTernary(int foundPos) {
            if (isAtIndex(foundPos, myTypes.H_PLACE_HOLDER)) {
                // «placeHolder» ... |>?<| ...
                markBefore(foundPos, myTypes.C_TERNARY)
                        .updateCompositeAt(foundPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(foundPos).end()
                        .advance().mark(myTypes.C_IF_THEN_ELSE)
                        .markHolder(myTypes.H_PLACE_HOLDER);
            } else if (isAtIndex(foundPos, myTypes.H_COLLECTION_ITEM)) {
                markHolderBefore(foundPos, myTypes.H_COLLECTION_ITEM)
                        .markBefore(foundPos, myTypes.C_TERNARY)
                        .updateCompositeAt(foundPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(foundPos).end()
                        .advance().mark(myTypes.C_IF_THEN_ELSE);
            }
        }

        private void parseTilde() {
            if (is(myTypes.C_PARAMETERS)) {
                mark(myTypes.C_PARAM_DECLARATION)
                        .mark(myTypes.C_PARAM_DECLARATION).markHolder(myTypes.H_NAMED_PARAM_DECLARATION)
                        .advance().wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (in(myTypes.C_SIG_EXPR) && !is(myTypes.C_SIG_ITEM)) {
                mark(myTypes.C_SIG_ITEM)
                        .mark(myTypes.C_PARAM_DECLARATION).markHolder(myTypes.H_NAMED_PARAM_DECLARATION)
                        .advance().wrapAtom(myTypes.CA_LOWER_SYMBOL);
            } else if (isCurrent(myTypes.C_PARAM)) {
                updateComposite(myTypes.C_NAMED_PARAM);
            } else if (isCurrent(myTypes.C_PARAM_DECLARATION)) {
                if (isHold()) {
                    updateLatestComposite(myTypes.H_NAMED_PARAM_DECLARATION).updateToHolder();
                }
            } else {
                mark(myTypes.C_PARAM_DECLARATION).markHolder(myTypes.H_NAMED_PARAM_DECLARATION)
                        .advance().wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
        }

        private void parseAssert() {
            mark(myTypes.C_ASSERT_STMT);
        }

        private void parseAnd() {
            if (in(myTypes.C_TYPE_CONSTRAINT)) {
                // module M = (X) : ( S with ... |>and<| ... ) = ...
                popEndUntilFoundIndex().popEnd();
            } else if (strictlyIn(myTypes.C_TYPE_DECLARATION)) {
                popEndUntilFoundIndex().popEnd()
                        .advance()
                        .mark(myTypes.C_TYPE_DECLARATION);
            } else if (strictlyIn(myTypes.C_MODULE_DECLARATION)) {
                popEndUntilFoundIndex().popEnd()
                        .advance()
                        .mark(myTypes.C_MODULE_DECLARATION);
            } else if (strictlyIn(myTypes.C_LET_DECLARATION)) {
                popEndUntilFoundIndex().popEnd()
                        .advance()
                        .mark(myTypes.C_LET_DECLARATION);
            }
        }

        private void parseComma() {
            // remove intermediate signatures
            if (strictlyInAny(myTypes.C_SIG_EXPR, myTypes.C_SCOPED_EXPR)) {
                popEndUntilFoundIndex();
                if (strictlyIn(myTypes.C_SIG_ITEM)) {
                    popEndUntilFoundIndex().popEnd();
                }
            }

            if (strictlyIn(myTypes.C_SCOPED_EXPR) && isAtIndex(getIndex() + 1, myTypes.C_LET_DECLARATION)) {
                // It must be a deconstruction ::  let ( a |>,<| b ) = ...
                // We need to do it again because lower symbols must be wrapped with identifiers
                rollbackToFoundIndex()
                        .updateComposite(myTypes.C_DECONSTRUCTION);
            }
            // Same priority
            else if (inScopeOrAny(
                    myTypes.C_PARAM_DECLARATION, myTypes.C_PARAM, myTypes.C_NAMED_PARAM, myTypes.C_DECONSTRUCTION,
                    myTypes.C_RECORD_FIELD, myTypes.C_MIXIN_FIELD, myTypes.C_OBJECT_FIELD, myTypes.H_COLLECTION_ITEM
            )) {

                if (isFound(myTypes.H_COLLECTION_ITEM)) {
                    popEndUntilFoundIndex().popEnd()
                            .advance().markHolder(myTypes.H_COLLECTION_ITEM);
                } else if (isFound(myTypes.C_PARAM_DECLARATION) || isFound(myTypes.C_PARAM) || isFound(myTypes.C_NAMED_PARAM)) {
                    boolean isDeclaration = isAtIndex(getIndex(), myTypes.C_PARAM_DECLARATION) || isAtIndex(getIndex(), myTypes.H_NAMED_PARAM_DECLARATION);
                    popEndUntilFoundIndex().popEnd();
                    if (is(myTypes.H_COLLECTION_ITEM) && isHold()) {
                        popEnd();
                    }
                    advanceSkipEOL();
                    if (getTokenType() != myTypes.RPAREN) {
                        // not at the end of a list: ie not => (p1, p2<,> )
                        markHolder(myTypes.H_COLLECTION_ITEM)
                                .mark(isDeclaration ? myTypes.C_PARAM_DECLARATION : myTypes.C_PARAM)
                                .markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (isFound(myTypes.C_DECONSTRUCTION)) {
                    popEndUntilScope();
                } else if (isFound(myTypes.C_RECORD_FIELD) || isFound(myTypes.C_MIXIN_FIELD) || isFound(myTypes.C_OBJECT_FIELD)) {
                    popEndUntilFoundIndex().popEnd();
                } else {
                    popEndUntilFoundIndex();
                    advanceSkipEOL();
                    markHolder(myTypes.H_COLLECTION_ITEM);
                }
            }
        }

        private void parsePipe() {
            if (is(myTypes.C_TYPE_BINDING)) {
                // type x = |>|<| ...
                advance().mark(myTypes.C_VARIANT_DECLARATION);
            } else if (is(myTypes.C_TRY_HANDLERS)) { // Start of a try handler
                // try (...) { |>|<| ... }
                advance().mark(myTypes.C_TRY_HANDLER);
            } else if (is(myTypes.C_SWITCH_BODY)) {
                // switch x { |>|<| ... }
                advance().mark(myTypes.C_PATTERN_MATCH_EXPR);
            } else if (strictlyIn(myTypes.C_PATTERN_MATCH_BODY)) {
                // can be a switchBody or a 'fun'
                popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd()
                        .advance()
                        .mark(myTypes.C_PATTERN_MATCH_EXPR);
            } else if (strictlyIn(myTypes.C_PATTERN_MATCH_EXPR)) { // pattern grouping
                // | X |>|<| Y => ...
                popEndUntilFoundIndex().popEnd()
                        .advance()
                        .mark(myTypes.C_PATTERN_MATCH_EXPR);
            } else if (strictlyIn(myTypes.C_VARIANT_DECLARATION)) {
                // type t = | X |>|<| Y ...
                popEndUntil(myTypes.C_VARIANT_DECLARATION).popEnd()
                        .advance()
                        .mark(myTypes.C_VARIANT_DECLARATION);
            }
        }

        private void parseStringValue() {
            if (isCurrent(myTypes.C_JS_OBJECT)) {
                popIfHold().mark(myTypes.C_OBJECT_FIELD);
            }
        }

        private void parseTemplateStringOpen() {
            // |>`<| ...
            markScope(myTypes.C_INTERPOLATION_EXPR, myTypes.JS_STRING_OPEN).advance();
        }

        private void parseLet() {
            popIfHold();
            if (!isCurrent(myTypes.C_PATTERN_MATCH_BODY)) {
                popEndUntilScope();
            }
            mark(myTypes.C_LET_DECLARATION);
        }

        private void parseModule() {
            if (isCurrent(myTypes.C_DEFAULT_VALUE)) {
                // let fn = (~x:module(X) = »module«
                mark(myTypes.C_FIRST_CLASS);
            } else if (!is(myTypes.C_MACRO_NAME) && !is(myTypes.C_FIRST_CLASS)) {
                popEndUntilScope();
                mark(myTypes.C_MODULE_DECLARATION);
            }
        }

        private void parseException() {
            popEndUntilScope();
            mark(myTypes.C_EXCEPTION_DECLARATION);
        }

        private void parseType() {
            if (is(myTypes.C_CONSTRAINTS)) {
                // module M = (X) : ( S with |>type<| ... ) = ...
                mark(myTypes.C_TYPE_CONSTRAINT);
            } else if (!is(myTypes.C_MODULE_DECLARATION)) {
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
            // |>%<| raw ...
            mark(myTypes.C_MACRO_EXPR).mark(myTypes.C_MACRO_NAME).advance();
            if (getTokenType() == myTypes.PERCENT) {
                // %|>%<| raw ...
                advance();
            }
        }

        private void parseColon() {
            if (is(myTypes.C_SCOPED_EXPR) && isRawParent(myTypes.C_FIELD_VALUE)) {
                return;
            }

            if (strictlyInAny(
                    myTypes.C_MODULE_DECLARATION, myTypes.C_LET_DECLARATION, myTypes.C_EXTERNAL_DECLARATION,
                    myTypes.C_PARAM_DECLARATION, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.H_NAMED_PARAM_DECLARATION,
                    myTypes.C_IF_THEN_ELSE, myTypes.C_UNPACK)) {

                if (isFound(myTypes.C_PARAM_DECLARATION) || isFound(myTypes.H_NAMED_PARAM_DECLARATION)) {
                    advance();
                    if (getTokenType() == myTypes.MODULE) {
                        // let _ = (~x »:« module(...
                        mark(myTypes.C_MODULE_SIGNATURE).advance();
                        if (getTokenType() == myTypes.LPAREN) {
                            markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                        }
                    } else {
                        // let _ = (x »:« ...
                        // let _ = (~x »:« ...
                        mark(myTypes.C_SIG_EXPR).mark(myTypes.C_SIG_ITEM);
                    }
                } else if (isFound(myTypes.C_MODULE_DECLARATION)) {
                    // module M |> :<| ...
                    popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_MODULE_SIGNATURE);
                } else if (isFound(myTypes.C_EXTERNAL_DECLARATION) || isFound(myTypes.C_LET_DECLARATION)) {
                    popEndUntilFoundIndex().advance();
                    if (getTokenType() == myTypes.MODULE) {
                        // let x >:< module ...
                        mark(myTypes.C_MODULE_SIGNATURE).advance();
                    } else {
                        // external/let x |> :<| ...
                        mark(myTypes.C_SIG_EXPR);
                        if (getTokenType() == myTypes.LPAREN) {
                            // external/let x : |>(<| ...
                            markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                                    .advance().markHolder(myTypes.H_COLLECTION_ITEM);
                            if (getTokenType() == myTypes.DOT) {
                                // external/let : ( |>.<| ...
                                advance();
                            }
                        }
                        mark(myTypes.C_SIG_ITEM);
                    }
                } else if (isFound(myTypes.C_RECORD_FIELD) || isFound(myTypes.C_OBJECT_FIELD)) {
                    advance();
                    if (in(myTypes.C_TYPE_BINDING)) {
                        mark(myTypes.C_SIG_EXPR)
                                .mark(myTypes.C_SIG_ITEM);
                    } else {
                        mark(myTypes.C_FIELD_VALUE)
                                .markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (isFound(myTypes.C_IF_THEN_ELSE)) {
                    // ternary ::  cond ? x |> :<| ...
                    popEndUntilFoundIndex().popEnd()
                            .advance()
                            .mark(myTypes.C_IF_THEN_ELSE);
                } else if (isFound(myTypes.C_UNPACK)) {
                    advance();
                    mark(myTypes.C_MODULE_SIGNATURE);
                }
            }
        }

        private void parseArrobase() {
            popEndUntilScope();
            mark(myTypes.C_ANNOTATION).mark(myTypes.C_MACRO_NAME);
        }

        private void parseLt() {
            if (is(myTypes.C_OPTION) || in(myTypes.C_SIG_EXPR)) {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LT).advance()
                        .markHolder(myTypes.H_PLACE_HOLDER);
            } else if (strictlyIn(myTypes.C_VARIANT_DECLARATION)) { // type parameters
                // type t |> < <| 'a >
                markScope(myTypes.C_PARAMETERS, myTypes.LT);
            } else if (in(myTypes.C_TYPE_DECLARATION)) { // type parameters
                // type t |> < <| 'a >
                markScope(myTypes.C_PARAMETERS, myTypes.LT);
            } else if (in(myTypes.C_VARIANT_DECLARATION)) {
                // type t = #X(array |> < <| ...
                markScope(myTypes.C_PARAMETERS, myTypes.LT);
            } else {
                // Can be a symbol or a JSX tag
                IElementType nextTokenType = rawLookup(1);
                if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.UIDENT || nextTokenType == myTypes.OPTION) {
                    // Note that option is a keyword but also a JSX keyword !
                    mark(myTypes.C_TAG)
                            .markScope(myTypes.C_TAG_START, myTypes.LT);
                } else if (nextTokenType == myTypes.GT) {
                    // a React fragment start
                    mark(myTypes.C_TAG)
                            .mark(myTypes.C_TAG_START)
                            .advance()
                            .advance()
                            .popEnd()
                            .mark(myTypes.C_TAG_BODY);
                }
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

            if (strictlyInAny(myTypes.C_PARAMETERS, myTypes.C_SCOPED_EXPR, myTypes.C_TAG_START, myTypes.C_TAG_CLOSE)) {
                if (isFound(myTypes.C_PARAMETERS)) {
                    if (isFoundScope(myTypes.LT)) {
                        // type x< ... |> ><|
                        popEndUntilFoundIndex().advance().end();
                    }
                } else if (isFound(myTypes.C_SCOPED_EXPR)) {
                    if (isFoundScope(myTypes.LT)) {
                        popEndUntilFoundIndex().advance().popEnd();
                        if (strictlyIn(myTypes.C_OPTION)) {
                            // option < ... |> > <| ...
                            popEnd();
                        }
                    }
                } else if (isFound(myTypes.C_TAG_START)) {
                    // <Comp ... |> ><|
                    popEndUntilFoundIndex().advance().end();
                    mark(myTypes.C_TAG_BODY);
                } else if (isFound(myTypes.C_TAG_CLOSE)) {
                    // </Comp ... |> ><|
                    advance().popEndUntil(myTypes.C_TAG).end();
                }
            }
        }

        private void parseGtAutoClose() {
            if (is(myTypes.C_TAG_PROP_VALUE)) {
                // ?prop=value |> /> <| ...
                popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
            } else if (is(myTypes.C_TAG_PROPERTY)) {
                // ?prop |> /> <| ...
                popEnd();
            }

            if (in(myTypes.C_TAG_START)) {
                popEndUntilFoundIndex()
                        .advance().popEnd()
                        .end();
            }
        }

        private void parseLtSlash() {
            if (in(myTypes.C_TAG)) {
                if (in(myTypes.C_TAG_BODY)) {
                    popEndUntilFoundIndex().end();
                }
                remapCurrentToken(myTypes.TAG_LT_SLASH)
                        .mark(myTypes.C_TAG_CLOSE);
            }
        }

        private void parseLIdent() {
            // external |>x<| ...
            if (is(myTypes.C_EXTERNAL_DECLARATION)) {
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            // let |>x<| ...
            else if (is(myTypes.C_LET_DECLARATION)) {
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            // type |>x<| ...
            else if (is(myTypes.C_TYPE_DECLARATION)) {
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            //
            else if (is(myTypes.C_FUNCTION_EXPR)) {
                mark(myTypes.C_PARAMETERS)
                        .mark(myTypes.C_PARAM_DECLARATION).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            //
            else if (isCurrent(myTypes.C_PARAMETERS) && (isParent(myTypes.C_FUNCTION_EXPR) || isParent(myTypes.C_FUNCTION_CALL))) {
                mark(myTypes.C_PARAM_DECLARATION).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            // { |>x<| ...
            else if (isCurrent(myTypes.C_RECORD_EXPR)) {
                mark(myTypes.C_RECORD_FIELD)
                        .wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            // tag name
            else if (is(myTypes.C_TAG_START)) {
                remapCurrentToken(myTypes.A_LOWER_TAG_NAME).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            //
            else if (is(myTypes.C_DECONSTRUCTION)) {
                wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
            // This is a property
            else if (strictlyIn(myTypes.C_TAG_START) && !isCurrent(myTypes.C_TAG_PROP_VALUE)) { // no scope
                generateJsxPropertyName();
            }
            //
            else {
                IElementType nextElementType = lookAheadSkipEOL();

                if (isCurrent(myTypes.C_MACRO_NAME)) {
                    // @ |>x<|  or  @x. |>y<|
                    if (nextElementType != myTypes.DOT && nextElementType != myTypes.LPAREN) {
                        wrapAtom(myTypes.CA_LOWER_SYMBOL).
                                popEndUntil(myTypes.C_ANNOTATION).popEnd();
                    }
                } else if (isCurrent(myTypes.C_SCOPED_EXPR) && isCurrentScope(myTypes.LBRACE) && nextElementType == myTypes.COLON) {
                    // this is a record usage ::  { |>x<| : ...
                    updateComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_RECORD_FIELD)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else if (nextElementType == myTypes.QUESTION_MARK && isHold()) {
                    if (!isCurrent(myTypes.C_TAG_PROP_VALUE) || currentHasScope()) { // not an inline tag value like <x p=|>v<| ?w>
                        // a ternary ::  |>x<| ? ...
                        wrapAtom(myTypes.CA_LOWER_SYMBOL);
                        parseTernary(1);
                    }
                } else if (nextElementType == myTypes.LPAREN && !is(myTypes.C_MACRO_NAME)) { // a function call
                    // |>x<| ( ...
                    mark(myTypes.C_FUNCTION_CALL).wrapAtom(myTypes.CA_LOWER_SYMBOL);
                } else if (is(myTypes.C_SIG_EXPR) || (isScopeAtIndex(1, myTypes.LPAREN) && isAtIndex(2, myTypes.C_SIG_EXPR))) {
                    mark(myTypes.C_SIG_ITEM)
                            .wrapAtom(myTypes.CA_LOWER_SYMBOL).popEnd();
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

        private void parseLBracket() {
            IElementType nextType = rawLookup(1);

            if (nextType == myTypes.GT) {
                // |> [ <| > ... ]
                markScope(myTypes.C_OPEN_VARIANT, myTypes.LBRACKET).advance().advance();
                if (getTokenType() != myTypes.RBRACKET) {
                    mark(myTypes.C_VARIANT_DECLARATION);
                }
            } else if (nextType == myTypes.LT) {
                // |> [ <| < ... ]
                markScope(myTypes.C_CLOSED_VARIANT, myTypes.LBRACKET).advance().advance();
                if (getTokenType() != myTypes.RBRACKET) {
                    mark(myTypes.C_VARIANT_DECLARATION);
                }
            } else {
                markScope(myTypes.C_ARRAY, myTypes.LBRACKET).advance();
                if (getTokenType() != myTypes.PIPE && getTokenType() != myTypes.POLY_VARIANT) {
                    markHolder(myTypes.H_COLLECTION_ITEM); // Needed to roll back to individual item in collection
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

        private void parseLBrace() {
            if (previousElementType(1) == myTypes.DOT && previousElementType(2) == myTypes.A_MODULE_NAME) { // Local open a js object or a record
                // Xxx.|>{<| ... }
                mark(myTypes.C_LOCAL_OPEN);
                IElementType nextElementType = lookAhead(1);
                if (nextElementType == myTypes.LIDENT) {
                    markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE);
                } else {
                    markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
                }
            } else if (is(myTypes.C_LET_DECLARATION)) {
                // let |>{<| ..
                markScope(myTypes.C_DECONSTRUCTION, myTypes.LBRACE);
            } else if (is(myTypes.C_TYPE_BINDING)) {
                boolean isJsObject = lookAheadSkipEOL() == myTypes.STRING_VALUE;
                markScope(isJsObject ? myTypes.C_JS_OBJECT : myTypes.C_RECORD_EXPR, myTypes.LBRACE);
            } else if (is(myTypes.C_MODULE_BINDING)) {
                // module M = |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (is(myTypes.C_FUNCTOR_BINDING)) {
                // module M = (...) => |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (isCurrent(myTypes.C_TAG_PROP_VALUE) || isCurrent(myTypes.C_TAG_BODY)) {
                // A scoped property
                popIfHold().markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE)
                        .advance().markHolder(myTypes.H_PLACE_HOLDER);
            } else if (is(myTypes.C_MODULE_SIGNATURE)) {
                // module M : |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (isDone(myTypes.C_TRY_BODY)) { // A try expression
                // try ... |>{<| ... }
                markScope(myTypes.C_TRY_HANDLERS, myTypes.LBRACE);
            } else if (isDone(myTypes.C_BINARY_CONDITION) && isRawParent(myTypes.C_IF)) {
                // if x |>{<| ... }
                markScope(myTypes.C_IF_THEN_ELSE, myTypes.LBRACE);
            } else if (isDone(myTypes.C_BINARY_CONDITION) && isRawParent(myTypes.C_SWITCH_EXPR)) {
                // switch (x) |>{<| ... }
                markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
            } else if (strictlyIn(myTypes.C_BINARY_CONDITION)) {
                popEndUntilFoundIndex().popEnd();
                if (isCurrent(myTypes.C_IF)) {
                    // if ... |>{<|
                    markScope(myTypes.C_IF_THEN_ELSE, myTypes.LBRACE);
                } else if (strictlyIn(myTypes.C_SWITCH_EXPR)) {
                    // switch x |>{<| ... }
                    markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
                }
            } else {
                // it might be a js object
                IElementType nextElement = lookAheadSkipEOL();
                if (nextElement == myTypes.STRING_VALUE || nextElement == myTypes.DOT) {
                    boolean hasDot = nextElement == myTypes.DOT;
                    // js object detected ::  |>{<| ./"x" ___ }
                    markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE).advance();
                    if (hasDot) {
                        advance();
                    }
                } else {
                    popIfHold();
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE);
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
                } else if (scope.isCompositeType(myTypes.C_RECORD_EXPR) && is(myTypes.C_TYPE_BINDING)) {
                    // Record type, end the type itself
                    popEndUntil(myTypes.C_TYPE_DECLARATION).popEnd();
                }

                //endInstruction(getTokenType());
            }
        }

        private void parseLParen() {
            if (previousElementType(2) == myTypes.A_MODULE_NAME && previousElementType(1) == myTypes.DOT) { // Local open
                // M. |>(<| ... )
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
            } else if (is(myTypes.C_MODULE_BINDING) && !in(myTypes.C_FUNCTOR_DECLARATION)) {
                if (myBuilder.lookAhead(1) == myTypes.UNPACK) {
                    // module M = |>(<| unpack .. )
                    markParenthesisScope(true);
                } else {
                    // This is a functor
                    // module M = |>(<| .. )
                    int moduleIndex = latestIndexOfCompositeAtMost(3, myTypes.C_MODULE_DECLARATION);
                    dropLatest()
                            .popEndUntilIndex(moduleIndex)
                            .updateComposite(myTypes.C_FUNCTOR_DECLARATION)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM_DECLARATION)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (is(myTypes.C_FUNCTION_EXPR)) { // A function
                // |>(<| .  OR  |>(<| ~
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN);
            }
            // x |>(<| ...
            else if (isCurrent(myTypes.C_FUNCTION_CALL)) {
                popEnd().markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                if (getTokenType() == myTypes.DOT) {
                    advance();
                }
                markHolder(myTypes.H_COLLECTION_ITEM);
                if (getTokenType() != myTypes.RPAREN) {
                    mark(myTypes.C_PARAM)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (isCurrent(myTypes.C_PARAM_DECLARATION) || isCurrent(myTypes.C_PARAM)) {
                popIfHold();
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                advance().markHolder(myTypes.H_COLLECTION_ITEM);
            } else if (isCurrent(myTypes.C_PARAMETERS) && !isParent(myTypes.C_SOME)) {
                if (!currentHasScope()) {
                    // |>(<| ... ) => ...
                    updateScopeToken(myTypes.LPAREN).advance()
                            .markHolder(myTypes.H_COLLECTION_ITEM);
                } else {
                    // ( |>(<| ... ) , ... ) => ...
                    mark(isParent(myTypes.C_FUNCTION_CALL) ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION)
                            .markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (is(myTypes.C_MACRO_NAME) && isRawParent(myTypes.C_ANNOTATION)) {
                // @ann |>(<| ... )
                popEnd()
                        .markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
            } else if (is(myTypes.C_MACRO_NAME)) {
                // %raw |>(<| ...
                popEnd()
                        .markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                        .advance()
                        .mark(myTypes.C_MACRO_BODY);
            } else if (is(myTypes.C_BINARY_CONDITION) && !currentHasScope()) {
                updateScopeToken(myTypes.LPAREN);
            } else if (is(myTypes.C_DECONSTRUCTION) && isRawParent(myTypes.C_LET_DECLARATION)) {
                // let ((x |>,<| ...
                markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN);
            }
            //
            else if (strictlyInAny(
                    myTypes.C_OPEN, myTypes.C_INCLUDE, myTypes.C_FUNCTOR_DECLARATION, myTypes.C_FUNCTOR_RESULT
            )) {

                if (isFound(myTypes.C_OPEN) || isFound(myTypes.C_INCLUDE)) { // Functor call
                    // open M |>(<| ...
                    markBefore(0, myTypes.C_FUNCTOR_CALL)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                } else if (isFound(myTypes.C_FUNCTOR_DECLARATION)) {
                    // module M = |>(<| ...
                    // module M = ( ... ) : |>(<| ...
                    boolean isCall = isFound(myTypes.C_FUNCTOR_CALL);
                    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(isCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
                }
            } else {
                Marker marker = getActiveMarker();
                if (marker != null && marker.isCompositeType(myTypes.C_LET_BINDING) && !marker.hasScope()) {
                    if (rawHasScope() && isParent(myTypes.C_LET_BINDING)) {
                        markHolder(myTypes.H_PLACE_HOLDER);
                    }
                }
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM);
            }
        }

        private void parseRParen() {
            Marker lParen = popEndUntilScopeToken(myTypes.LPAREN);
            advance();

            IElementType nextTokenType = getTokenType();
            if (is(myTypes.C_BINARY_CONDITION)) {
                end();
                if (isRawParent(myTypes.C_IF) && nextTokenType != myTypes.LBRACE) {
                    // if ( x ) |><| ...
                    mark(myTypes.C_IF_THEN_ELSE);
                }
            } else if (lParen != null) {
                if (isRawParent(myTypes.C_FUNCTOR_DECLARATION)) {
                    popEnd();
                    if (nextTokenType == myTypes.COLON) {
                        // module M = (P) |> :<| R ...
                        advance();
                        markParenthesisScope(true);
                        mark(myTypes.C_FUNCTOR_RESULT);
                    } else if (nextTokenType == myTypes.ARROW) {
                        // module M = (P) |>=><| ...
                        advance().mark(myTypes.C_FUNCTOR_BINDING);
                    }
                } else if (isRawParent(myTypes.C_MODULE_SIGNATURE)) {
                    popEnd();
                } else if (nextTokenType == myTypes.ARROW && !isParent(myTypes.C_FUNCTION_EXPR)) {
                    popEnd();
                    if (strictlyInAny(myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_TRY_HANDLER, myTypes.C_PARAM, myTypes.C_PARAMETERS,
                            myTypes.C_SIG_ITEM, myTypes.C_SIG_EXPR, myTypes.C_DEFAULT_VALUE, myTypes.C_SCOPED_EXPR,
                            myTypes.C_LET_BINDING, myTypes.H_PLACE_HOLDER)) {
                        int foundIndex = getIndex();
                        if (isFound(myTypes.C_DEFAULT_VALUE) && isHold()) {
                            // fn(~p=() |>=><| ...
                            rollbackToIndex(0);
                            mark(myTypes.C_FUNCTION_EXPR);
                            mark(myTypes.C_PARAMETERS).markHolder(myTypes.H_COLLECTION_ITEM);
                        } else if (isFound(myTypes.C_PARAM) || isFound(myTypes.H_PLACE_HOLDER) || (isFound(myTypes.C_PARAMETERS) && isAtIndex(foundIndex + 1, myTypes.C_SOME))) {
                            rollbackToFoundIndex();
                            mark(myTypes.C_FUNCTION_EXPR);
                            if (getTokenType() == myTypes.LPAREN) {
                                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                            } else {
                                mark(myTypes.C_PARAMETERS);
                            }
                            markHolder(myTypes.H_COLLECTION_ITEM);
                        } else if (isFound(myTypes.C_LET_BINDING) || isFound(myTypes.C_SCOPED_EXPR)) {
                            // a missed function expression
                            int foundHolderIndex = latestIndexOfCompositeAtMost(getIndex(), myTypes.H_PLACE_HOLDER, myTypes.H_COLLECTION_ITEM);
                            if (-1 < foundHolderIndex) {
                                rollbackToIndex(foundHolderIndex);
                                updateLatestComposite(myTypes.C_FUNCTION_EXPR);
                                mark(myTypes.C_PARAMETERS).markHolder(myTypes.H_COLLECTION_ITEM);
                            }
                        } else {
                            popEndUntilFoundIndex();
                        }
                    }
                } else {
                    popEnd();
                    if (is(myTypes.C_ANNOTATION)) {
                        popEnd();
                    } else if (is(myTypes.C_FUNCTION_CALL)) {
                        if (nextTokenType == myTypes.LPAREN) {
                            // Chaining function calls fn()( ...
                            markBefore(0, myTypes.C_FUNCTION_CALL);
                        } else {
                            popEnd();
                            if (isCurrent(myTypes.C_LET_BINDING) && !currentHasScope()) {
                                IElementType nextValidToken = nextTokenType == myTypes.EOL ? lookAheadSkipEOL() : nextTokenType;
                                if (nextValidToken != myTypes.QUESTION_MARK && nextValidToken != myTypes.RIGHT_ARROW) {
                                    // let _ = fn(|>)<|
                                    popEndUntil(myTypes.C_LET_DECLARATION).popEnd();
                                } else if (nextTokenType == myTypes.EOL) {
                                    advanceSkipEOL();
                                }
                            }
                        }
                    } else if (strictlyIn(myTypes.C_TAG_PROP_VALUE)) {
                        popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
                    }
                }
            }
        }

        private void parseEq() {
            if (strictlyInAny(
                    myTypes.C_TYPE_DECLARATION, myTypes.C_LET_DECLARATION, myTypes.C_MODULE_SIGNATURE, myTypes.C_MODULE_DECLARATION,
                    myTypes.C_TAG_PROPERTY, myTypes.C_SIG_EXPR, myTypes.H_NAMED_PARAM_DECLARATION, myTypes.C_NAMED_PARAM,
                    myTypes.C_TYPE_CONSTRAINT, myTypes.C_TYPE_BINDING
            )) {
                if (isFound(myTypes.C_TYPE_DECLARATION)) {
                    // type t |> = <| ...
                    popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_TYPE_BINDING);
                } else if (isFound(myTypes.C_LET_DECLARATION)) {
                    // let x |> = <| ...
                    popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_LET_BINDING)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_MODULE_SIGNATURE)) {
                    popEndUntilFoundIndex().end().advance();
                    if (isRawParent(myTypes.C_LET_DECLARATION)) {
                        // let x : I >=< ...
                        mark(myTypes.C_FIRST_CLASS);
                    } else if (isRawGrandParent(myTypes.H_NAMED_PARAM_DECLARATION)) {
                        // let _ = (~x: module(I) »=«
                        popEndUntilIndex(2).mark(myTypes.C_DEFAULT_VALUE).mark(myTypes.C_FIRST_CLASS);
                    } else {
                        // module M : T |> = <| ...
                        mark(myTypes.C_MODULE_BINDING);
                    }
                } else if (isFound(myTypes.C_MODULE_DECLARATION)) {
                    // module M |> = <| ...
                    advance().mark(myTypes.C_MODULE_BINDING);
                } else if (isFound(myTypes.C_TAG_PROPERTY)) {
                    // <Comp prop |> = <| ...
                    advance().mark(myTypes.C_TAG_PROP_VALUE)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_SIG_EXPR)) {
                    popEndUntilFoundIndex();
                    if (isRawGrandParent(myTypes.H_NAMED_PARAM_DECLARATION)) {
                        popEndUntilIndex(2)
                                .advance().mark(myTypes.C_DEFAULT_VALUE);
                    } else if (isRawParent(myTypes.C_LET_DECLARATION)) {
                        // let x : M.t |> =<| ...
                        end().popEndUntilFoundIndex()
                                .advance().mark(myTypes.C_LET_BINDING)
                                .markHolder(myTypes.H_PLACE_HOLDER);
                    } else {
                        end();
                    }
                } else if (isFound(myTypes.H_NAMED_PARAM_DECLARATION) || isFound(myTypes.C_NAMED_PARAM)) {
                    // ( ~x |> =<| ...
                    popEndUntilFoundIndex()
                            .advance().mark(myTypes.C_DEFAULT_VALUE)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_TYPE_CONSTRAINT)) {
                    // ... with type t |> =<| ...
                    advance().mark(myTypes.C_TYPE_BINDING);
                } else if (isFound(myTypes.C_TYPE_BINDING) && strictlyIn(myTypes.C_CONSTRAINTS)) {
                    // .. with type .. = .. |> =<| ..
                    popEndUntilFoundIndex().popEnd();
                    if (strictlyIn(myTypes.C_MODULE_DECLARATION)) {
                        popEndUntilFoundIndex()
                                .advance().mark(myTypes.C_MODULE_BINDING);
                    }
                }
            } else {
                // nothing found, just add a placeholder
                advance().markHolder(myTypes.H_PLACE_HOLDER);
            }
        }

        private void parseUIdent() {
            if (DUMMY_IDENTIFIER_TRIMMED.equals(getTokenText())) {
                return;
            }

            // module |>M<| ...
            if (is(myTypes.C_MODULE_DECLARATION)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            // module M = ( |>P<| ...
            else if (isCurrent(myTypes.C_PARAM_DECLARATION) && in(myTypes.C_FUNCTOR_DECLARATION)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            // module M = (P: |>S<| ...
            else if (isCurrent(myTypes.C_SIG_ITEM) && in(myTypes.C_FUNCTOR_DECLARATION, /*not*/myTypes.C_FUNCTOR_BINDING)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            // module M = ( ... ) : |>S<| ...
            // module M : |>S<| ...
            else if (isCurrent(myTypes.C_FUNCTOR_RESULT) || isCurrent(myTypes.C_MODULE_SIGNATURE)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            //
            else if (isCurrent(myTypes.C_MODULE_BINDING)) {
                IElementType nextElement = lookAhead(1);
                if (nextElement == myTypes.LPAREN) {
                    // functor call ::  |>X<| ( ...
                    // functor call with path :: A.B.|>X<| ( ...
                    mark(myTypes.C_FUNCTOR_CALL)
                            .remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else {
                    // module M = |>X<|
                    // module M = X.|>Y<|
                    remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                    if (nextElement != myTypes.DOT) {
                        popEndUntil(myTypes.C_MODULE_DECLARATION).popEnd();
                    }
                }
            }
            // exception |>E<| ...
            else if (is(myTypes.C_EXCEPTION_DECLARATION)) {
                remapCurrentToken(myTypes.A_EXCEPTION_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            // try .. catch { | |>X<| ..
            else if (isCurrent(myTypes.C_TRY_HANDLER)) {
                remapCurrentToken(myTypes.A_EXCEPTION_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            // tag name
            else if (isCurrent(myTypes.C_TAG_START) || isCurrent(myTypes.C_TAG_CLOSE)) {
                remapCurrentToken(myTypes.A_UPPER_TAG_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            // open |>M<| ...
            else if (isCurrent(myTypes.C_OPEN)) { // It is a module name/path, or maybe a functor call
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                IElementType nextToken = getTokenType();
                if (nextToken != myTypes.LPAREN && nextToken != myTypes.DOT) {
                    popEndUntil(myTypes.C_OPEN).popEnd();
                }
            }
            // include |>M<| ...
            else if (isCurrent(myTypes.C_INCLUDE)) { // It is a module name/path, or maybe a functor call
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                IElementType nextToken = getTokenType();
                if (nextToken != myTypes.LPAREN && nextToken != myTypes.DOT) {
                    popEndUntil(myTypes.C_INCLUDE).popEnd();
                }
            }
            // type t = | |>X<| ..
            else if (is(myTypes.C_VARIANT_DECLARATION)) { // Declaring a variant
                IElementType nextElementType = rawLookup(1);
                remapCurrentToken(nextElementType == myTypes.DOT ? myTypes.A_MODULE_NAME : myTypes.A_VARIANT_NAME);
                wrapAtom(myTypes.CA_UPPER_SYMBOL);
                if (getTokenType() == myTypes.LPAREN) {
                    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM_DECLARATION);
                }
            }
            // type |>M<|.t += ...
            else if (is(myTypes.C_TYPE_DECLARATION)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            //
            else if (is(myTypes.C_TYPE_BINDING)) {
                IElementType nextToken = lookAhead(1);
                if (nextToken == myTypes.DOT) { // a path
                    remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                } else { // We are declaring a variant without a pipe before
                    // type t = |>X<| | ...
                    // type t = |>X<| (...) | ...
                    mark(myTypes.C_VARIANT_DECLARATION)
                            .remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                    if (getTokenType() == myTypes.LPAREN) {
                        markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                                .mark(myTypes.C_PARAM_DECLARATION);
                    }
                }
            } else if (in(myTypes.C_FIRST_CLASS)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
            //
            else {
                IElementType nextToken = lookAhead(1);

                if (((isCurrent(myTypes.C_PATTERN_MATCH_EXPR) || isCurrent(myTypes.C_LET_BINDING))) && nextToken != myTypes.DOT) { // Pattern matching a variant or using it
                    // switch c { | |>X<| ... / let x = |>X<| ...
                    remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
                    if (getTokenType() == myTypes.LPAREN) {
                        markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                                .markHolder(myTypes.H_COLLECTION_ITEM);
                    }
                } else {
                    boolean isModule = nextToken == myTypes.DOT || nextToken == myTypes.WITH || isParent(myTypes.C_MODULE_SIGNATURE);
                    remapCurrentToken(isModule ? myTypes.A_MODULE_NAME : myTypes.A_VARIANT_NAME)
                            .wrapAtom(myTypes.CA_UPPER_SYMBOL);
                }
            }
        }

        private void parseLTagName() {
            // LIdent might have already incorrectly been remapped to a tag name. need to revert that update.
            if (in(myTypes.C_SIG_ITEM)) {
                remapCurrentToken(myTypes.LIDENT).wrapAtom(myTypes.CA_LOWER_SYMBOL);
            }
        }

        private void parseUTagName() {
            // UIdent might have already incorrectly been remapped to a tag name. need to revert that update.
            if (in(myTypes.C_SIG_ITEM)) {
                remapCurrentToken(myTypes.A_MODULE_NAME).wrapAtom(myTypes.CA_UPPER_SYMBOL);
            }
        }

        private void parsePolyVariant() {
            if (isRawParent(myTypes.C_TYPE_BINDING)) {
                // type t = [ |>#xxx<| ...
                mark(myTypes.C_VARIANT_DECLARATION);
            }
            advance();
            markParenthesisScope(false);
        }

        private void parseSwitch() {
            mark(myTypes.C_SWITCH_EXPR)
                    .advance()
                    .mark(myTypes.C_BINARY_CONDITION);
        }

        private void parseTry() {
            mark(myTypes.C_TRY_EXPR).advance()
                    .mark(myTypes.C_TRY_BODY);
        }

        private void parseCatch() {
            if (strictlyIn(myTypes.C_TRY_BODY)) {
                popEndUntilFoundIndex().end();
            }
        }

        private void parseWith() {
            if (strictlyInAny(myTypes.C_FUNCTOR_RESULT, myTypes.C_MODULE_SIGNATURE)) {
                // module M (X) : ( S |>with<| ... ) = ...
                popEndUntilFoundIndex().popEnd().advance()
                        .mark(myTypes.C_CONSTRAINTS);
            }
        }

        private void parseArrow() {
            if (inScopeOrAny(
                    myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_TRY_HANDLER,
                    myTypes.C_PARAM_DECLARATION, myTypes.C_PARAMETERS, myTypes.C_FUNCTION_EXPR,
                    myTypes.C_SIG_EXPR, myTypes.C_SIG_ITEM,
                    myTypes.C_FUNCTOR_RESULT
            )) {
                if (isFound(myTypes.C_PARAMETERS) || isFound(myTypes.C_FUNCTION_EXPR)) {
                    popEndUntilOneOf(myTypes.C_PARAMETERS, myTypes.C_FUNCTION_EXPR);
                    if (isRawParent(myTypes.C_FUNCTION_EXPR) || isRawParent(myTypes.C_FUNCTION_CALL)) {
                        popEnd();
                    }
                    advance().mark(myTypes.C_FUNCTION_BODY)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_SIG_EXPR)) {
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_SIG_ITEM)) {
                    popEndUntilFoundIndex().popEnd();
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                    // switch ( ... ) { | ... |>=><| ... }
                    popEndUntilFoundIndex().advance()
                            .markScope(myTypes.C_PATTERN_MATCH_BODY, myTypes.ARROW)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_TRY_HANDLER)) {
                    // try .. { | X |>=><| .. }
                    popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_TRY_HANDLER_BODY);
                } else if (isFound(myTypes.C_PARAM_DECLARATION)) { // anonymous function
                    // x( y |>=><| ... )
                    popEndUntil(myTypes.C_FUNCTION_EXPR).advance()
                            .mark(myTypes.C_FUNCTION_BODY)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_FUNCTOR_RESULT)) {
                    // module Make = (M) : R |>=><| ...
                    popEndUntilFoundIndex().popEnd()
                            .advance().mark(myTypes.C_FUNCTOR_BINDING);
                }
            }
        }


        private @Nullable IElementType lookAheadSkipEOL() {
            IElementType elementType = lookAhead(1);
            if (elementType == myTypes.EOL) {
                elementType = lookAhead(1 + 1);
            }
            return elementType;
        }

        private void advanceSkipEOL() {
            advance();
            while (getTokenType() == myTypes.EOL) {
                myBuilder.remapCurrentToken(myTypes.WHITE_SPACE);
                advance();
            }
        }
    }
}
