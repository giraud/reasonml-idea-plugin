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
                    if (5000 < parseTime - parseStart) {
                        if (myIsSafe) { // Don't do that in tests
                            error("CANCEL");
                            LOG.error("CANCEL RESCRIPT PARSING:\n" + myBuilder.getOriginalText());
                            break;
                        }
                    }
                }

                tokenType = myBuilder.getTokenType();

                // Special analyse when inside an interpolation string
                if (is(myTypes.C_INTERPOLATION_EXPR)
                        || is(myTypes.C_INTERPOLATION_PART)
                        || is(myTypes.C_INTERPOLATION_REF)) {
                    if (tokenType == myTypes.JS_STRING_OPEN) {
                        popEndUntil(myTypes.C_INTERPOLATION_EXPR).advance().popEnd();
                    } else if (tokenType == myTypes.DOLLAR) {
                        if (is(myTypes.C_INTERPOLATION_PART)) {
                            popEnd();
                        }
                        advance();
                        IElementType nextElement = getTokenType();
                        if (nextElement == myTypes.LBRACE) {
                            advance().markScope(myTypes.C_INTERPOLATION_REF, myTypes.LBRACE);
                        }
                    } else if (is(myTypes.C_INTERPOLATION_REF) && tokenType == myTypes.RBRACE) {
                        popEnd().advance();
                        if (getTokenType() != myTypes.JS_STRING_OPEN) {
                            mark(myTypes.C_INTERPOLATION_PART);
                        }
                    }
                } else {
                    // EOL is a statement separator
                    if (tokenType == myTypes.EOL) {
                        // We identify it at the lexer level, because We try to detect current construction that can't have
                        // EOL in it ; but we don't want to have it in the parsed tree, we change it to a whitespace
                        myBuilder.remapCurrentToken(TokenType.WHITE_SPACE);

                        if (is(myTypes.C_FUNCTOR_CALL/*PsiOpen is potential functor call*/) && isRawParent(myTypes.C_OPEN)) {
                            popEnd().popEnd();
                        } else if (isRawParent(myTypes.C_ANNOTATION) && !is(myTypes.C_SCOPED_EXPR)) {
                            // inside an annotation
                            popEnd().popEnd();
                        } else if (is(myTypes.C_LET_BINDING)) {
                            // simple let with no scope
                            popEnd().popEnd();
                        }
                    } else if (tokenType == myTypes.EQ) {
                        parseEq();
                    } else if (tokenType == myTypes.SOME) {
                        parseSome();
                    } else if (tokenType == myTypes.ARROW) {
                        parseArrow();
                    } else if (tokenType == myTypes.REF) {
                        parseRef();
                    } else if (tokenType == myTypes.OPTION) {
                        parseOption();
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
                    } else if (tokenType == myTypes.POLY_VARIANT) {
                        parsePolyVariant();
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
                }

                if (dontMove) {
                    dontMove = false;
                } else {
                    myBuilder.advanceLexer();
                }
            }
        }

        private void parseSome() {
            advance();
            if (getTokenType() == myTypes.LPAREN) {
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM);
            }
        }

        private void parseList() {
            if (lookAhead(1) == myTypes.LBRACE) {
                // |>list<| { ... }
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE)
                        .advance().advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM); // Needed to rollback to individual item in collection
            }
        }

        private void parseNumeric() {
            if (is(myTypes.C_PARAMETERS)) {
                boolean inCall = in(myTypes.C_FUN_CALL) || in(myTypes.C_FUNCTOR_CALL);
                mark(inCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
            }
        }

        private void parseUnderscore() {
            if (is(myTypes.C_PARAMETERS)) {
                boolean inCall = isParent(myTypes.C_FUN_CALL) || isParent(myTypes.C_FUNCTOR_CALL);
                mark(inCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
            }
        }

        private void parseRef() {
            if (is(myTypes.C_RECORD_EXPR)) {
                // { |>x<| ...
                mark(myTypes.C_RECORD_FIELD).wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (strictlyIn(myTypes.C_TAG_START)) {
                remapCurrentToken(myTypes.PROPERTY_NAME).mark(myTypes.C_TAG_PROPERTY);
            }
        }

        private void parseOption() {
            mark(myTypes.C_OPTION);
        }

        private void parseIf() {
            mark(myTypes.C_IF)
                    .advance().mark(myTypes.C_BINARY_CONDITION);
        }

        private void parseElse() {
            // if ... |>else<| ...
            popEndUntil(myTypes.C_IF)
                    .advance().mark(myTypes.C_IF_THEN_SCOPE);
        }

        private void parseDotDotDot() {
            if (previousElementType(1) == myTypes.LBRACE) { // Mixin
                // ... { |>...<| x ...
                updateComposite(myTypes.C_RECORD_EXPR)
                        .mark(myTypes.C_MIXIN_FIELD);
            }
        }

        private void parseQuestionMark() {
            if (strictlyInAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
                // <jsx |>?<|prop ...
                if (isFound(myTypes.C_TAG_START)) {
                    mark(myTypes.C_TAG_PROPERTY)
                            .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace())
                            .advance()
                            .remapCurrentToken(myTypes.PROPERTY_NAME);
                }
            } else if (isDone(myTypes.C_BINARY_CONDITION) || strictlyIn(myTypes.C_BINARY_CONDITION)) { // a ternary in progress
                // ... |>?<| ...
                popEndUntilFoundIndex().end()
                        .advance()
                        .mark(myTypes.C_IF_THEN_SCOPE);
            } else if (!inAny(myTypes.C_TERNARY, myTypes.C_NAMED_PARAM_DECLARATION)) {
                if (inScopeOrAny(myTypes.H_PLACE_HOLDER, myTypes.H_COLLECTION_ITEM)) { // a new ternary
                    parseTernary();
                }
            }
        }

        private void parseTernary() {
            int foundPos = getIndex();
            if (isAtIndex(foundPos, myTypes.H_PLACE_HOLDER)) {
                // «placeHolder» ... |>?<| ...
                markBefore(foundPos, myTypes.C_TERNARY)
                        .updateCompositeAt(foundPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(foundPos).end()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE)
                        .markHolder(myTypes.H_PLACE_HOLDER);
            } else if (isAtIndex(foundPos, myTypes.H_COLLECTION_ITEM)) {
                markHolderBefore(foundPos, myTypes.H_COLLECTION_ITEM);
                markBefore(foundPos, myTypes.C_TERNARY)
                        .updateCompositeAt(foundPos, myTypes.C_BINARY_CONDITION)
                        .popEndUntilIndex(foundPos).end()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE);
            }
        }

        private void parseTilde() {
            if (is(myTypes.C_PARAMETERS)) {
                mark(myTypes.C_PARAM_DECLARATION)
                        .mark(myTypes.C_NAMED_PARAM_DECLARATION).advance()
                        .wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (strictlyIn(myTypes.C_SIG_EXPR) && !is(myTypes.C_SIG_ITEM)) {
                mark(myTypes.C_SIG_ITEM)
                        .mark(myTypes.C_NAMED_PARAM_DECLARATION).advance()
                        .wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (isCurrent(myTypes.C_PARAM)) {
                updateComposite(myTypes.C_NAMED_PARAM);
            } else if (isCurrent(myTypes.C_PARAM_DECLARATION)) {
                updateComposite(myTypes.C_NAMED_PARAM_DECLARATION);
            } else {
                mark(myTypes.C_NAMED_PARAM_DECLARATION).advance()
                        .wrapAtom(myTypes.A_LOWER_SYMBOL);
            }
        }

        private void parseAssert() {
            mark(myTypes.C_ASSERT_STMT);
        }

        private void parseAnd() {
            //noinspection StatementWithEmptyBody
            if (is(myTypes.C_CONSTRAINT)) {
                // module M = (X) : ( S with ... |>and<| ... ) = ...
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
            if (strictlyIn(myTypes.C_SIG_EXPR)) {  // remove intermediate signatures
                if (isAtIndex(getIndex() - 1, myTypes.C_SCOPED_EXPR)) { // a dummy scope
                    popEndUntilIndex(getIndex() - 1);
                } else {
                    popEndUntilFoundIndex();
                }
                if (strictlyIn(myTypes.C_SIG_ITEM)) {
                    popEndUntilFoundIndex().popEnd();
                }
            }

            if (strictlyIn(myTypes.C_SCOPED_EXPR) && isAtIndex(getIndex() + 1, myTypes.C_LET_DECLARATION)) {
                // It must be a deconstruction ::  let ( a |>,<| b ) = ...
                // We need to do it again because lower symbols must be wrapped with identifiers
                rollbackToPos(getIndex())
                        .markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN).advance();
            }
            // Same priority
            else if (inScopeOrAny(
                    myTypes.C_PARAM_DECLARATION, myTypes.C_PARAM, myTypes.C_NAMED_PARAM_DECLARATION, myTypes.C_NAMED_PARAM,
                    myTypes.C_DECONSTRUCTION, myTypes.C_RECORD_FIELD, myTypes.C_MIXIN_FIELD,
                    myTypes.C_OBJECT_FIELD, myTypes.H_COLLECTION_ITEM
            )) {

                if (isFound(myTypes.H_COLLECTION_ITEM)) {
                    popEndUntilFoundIndex().popEnd()
                            .advance().markHolder(myTypes.H_COLLECTION_ITEM);
                } else if (isFound(myTypes.C_PARAM_DECLARATION) || isFound(myTypes.C_NAMED_PARAM_DECLARATION) ||
                        isFound(myTypes.C_PARAM) || isFound(myTypes.C_NAMED_PARAM)) {
                    boolean isDeclaration = isAtIndex(getIndex(), myTypes.C_PARAM_DECLARATION) || isAtIndex(getIndex(), myTypes.C_NAMED_PARAM_DECLARATION);
                    popEndUntilFoundIndex().popEnd();
                    if (is(myTypes.H_COLLECTION_ITEM) && isHold()) {
                        popEnd();
                    }
                    advance();
                    if (getTokenType() != myTypes.RPAREN) {
                        // not at the end of a list: ie not => (p1, p2<,> )
                        markHolder(myTypes.H_COLLECTION_ITEM)
                                .mark(isDeclaration ? myTypes.C_PARAM_DECLARATION : myTypes.C_PARAM)
                                .markHolder(myTypes.H_PLACE_HOLDER);
                    }
                } else if (isFound(myTypes.C_DECONSTRUCTION)) {
                    popEndUntilScope();
                } else if (isFound(myTypes.C_RECORD_FIELD) || isFound(myTypes.C_MIXIN_FIELD) || isFound(myTypes.C_OBJECT_FIELD)) {
                    popEndUntilFoundIndex().popEnd(); //.advance();
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
            if (is(myTypes.C_JS_OBJECT)) {
                mark(myTypes.C_OBJECT_FIELD);
            }
        }

        private void parseTemplateStringOpen() {
            // |>`<| ...
            markScope(myTypes.C_INTERPOLATION_EXPR, myTypes.JS_STRING_OPEN).advance();
            if (getTokenType() != myTypes.DOLLAR) {
                mark(myTypes.C_INTERPOLATION_PART);
            }
        }

        private void parseLet() {
            popIfHold();
            if (!isCurrent(myTypes.C_PATTERN_MATCH_BODY)) {
                popEndUntilScope();
            }
            mark(myTypes.C_LET_DECLARATION);
        }

        private void parseModule() {
            if (!is(myTypes.C_MACRO_NAME)) {
                popEndUntilScope();
                mark(myTypes.C_MODULE_DECLARATION);
            }
        }

        private void parseException() {
            popEndUntilScope();
            mark(myTypes.C_EXCEPTION_DECLARATION);
        }

        private void parseType() {
            if (!is(myTypes.C_MODULE_DECLARATION)) {
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
                //rollbackToPos(getIndex())
                //        .markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE)
                //        .advance();
            } else if (strictlyInAny(
                    myTypes.C_MODULE_DECLARATION, myTypes.C_LET_DECLARATION, myTypes.C_EXTERNAL_DECLARATION,
                    myTypes.C_PARAM_DECLARATION, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.C_NAMED_PARAM_DECLARATION,
                    myTypes.C_IF_THEN_SCOPE)) {

                if (isFound(myTypes.C_PARAM_DECLARATION)) {
                    // let x = (y |> :<| ...
                    advance()
                            .mark(myTypes.C_SIG_EXPR)
                            .mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_MODULE_DECLARATION)) {
                    // module M |> :<| ...
                    popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_MODULE_TYPE);
                } else if (isFound(myTypes.C_EXTERNAL_DECLARATION) || isFound(myTypes.C_LET_DECLARATION)) {
                    // external/let x |> :<| ...
                    popEndUntilFoundIndex().advance().mark(myTypes.C_SIG_EXPR);
                    if (getTokenType() == myTypes.LPAREN) {
                        // external/let x : |>(<| ...
                        markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                        if (getTokenType() == myTypes.DOT) {
                            // external/let : ( |>.<| ...
                            advance();
                        }
                    }
                    mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_RECORD_FIELD) || isFound(myTypes.C_OBJECT_FIELD)) {
                    advance();
                    if (in(myTypes.C_TYPE_BINDING)) {
                        mark(myTypes.C_SIG_EXPR)
                                .mark(myTypes.C_SIG_ITEM);
                    } else {
                        mark(myTypes.C_FIELD_VALUE);
                    }
                } else if (isFound(myTypes.C_NAMED_PARAM_DECLARATION)) {
                    advance().mark(myTypes.C_SIG_EXPR)
                            .mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_IF_THEN_SCOPE)) {
                    popEndUntilFoundIndex().popEnd()
                            .advance()
                            .mark(myTypes.C_IF_THEN_SCOPE);
                }

            }
        }

        private void parseArrobase() {
            popEndUntilScope();
            mark(myTypes.C_ANNOTATION).mark(myTypes.C_MACRO_NAME);
        }

        private void parseLt() {
            if (is(myTypes.C_OPTION) || is(myTypes.C_SIG_ITEM)) {
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LT);
            } else if (strictlyIn(myTypes.C_TYPE_DECLARATION)) { // type parameters
                // type t |> < <| 'a >
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
                            .popEnd();
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


            // else
            if (is(myTypes.C_SCOPED_EXPR)) {
                if (isRawParent(myTypes.C_OPTION)) {
                    // option < ... |> > <| ...
                    advance().popEnd().popEnd();
                } else if (isRawParent(myTypes.C_SIG_ITEM)) {
                    // : ... < ... |> ><| ...
                    advance().popEnd();
                }
            } else if (strictlyIn(myTypes.C_TAG_START)) {
                advance()
                        .popEndUntilFoundIndex()
                        .end();
                mark(myTypes.C_TAG_BODY);
            } else if (strictlyIn(myTypes.C_TAG_CLOSE)) {
                advance()
                        .popEndUntil(myTypes.C_TAG)
                        .end();
            } else if (strictlyIn(myTypes.C_PARAMETERS)) {
                popEndUntilFoundIndex().advance().end();
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

            if (strictlyIn(myTypes.C_TAG_START)) {
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
            // Must stop annotation if no dot/@ before
            if (is(myTypes.C_MACRO_NAME)) {
                IElementType previousElementType = previousElementType(1);
                if (previousElementType != myTypes.DOT && previousElementType != myTypes.ARROBASE) {
                    popEnd().popEnd();
                }
            }

            if (is(myTypes.C_EXTERNAL_DECLARATION)) {
                // external |>x<| ...
                wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (is(myTypes.C_LET_DECLARATION)) {
                // let |>x<| ...
                wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (is(myTypes.C_TYPE_DECLARATION)) {
                // type |>x<| ...
                wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (is(myTypes.C_FUN_EXPR)) {
                mark(myTypes.C_PARAMETERS)
                        .mark(myTypes.C_PARAM_DECLARATION).wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (isCurrent(myTypes.C_PARAMETERS) && (isParent(myTypes.C_FUN_EXPR) || isParent(myTypes.C_FUN_CALL))) {
                mark(myTypes.C_PARAM_DECLARATION).wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (is(myTypes.C_RECORD_EXPR)) {
                // { |>x<| ...
                mark(myTypes.C_RECORD_FIELD)
                        .wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (is(myTypes.C_TAG_START)) {
                // tag name
                remapCurrentToken(myTypes.A_LOWER_TAG_NAME).wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (is(myTypes.C_DECONSTRUCTION)) {
                wrapAtom(myTypes.A_LOWER_SYMBOL);
            } else if (strictlyIn(myTypes.C_TAG_START) && !isCurrent(myTypes.C_TAG_PROP_VALUE)) { // no scope
                // This is a property
                remapCurrentToken(myTypes.PROPERTY_NAME)
                        .mark(myTypes.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace());
            } else {
                IElementType nextElementType = lookAhead(1);

                if (is(myTypes.C_SCOPED_EXPR) && isScope(myTypes.LBRACE) && nextElementType == myTypes.COLON) {
                    // this is a record usage ::  { |>x<| : ...
                    updateComposite(myTypes.C_RECORD_EXPR)
                            .mark(myTypes.C_RECORD_FIELD)
                            .wrapAtom(myTypes.A_LOWER_SYMBOL);
                } else if (nextElementType == myTypes.LPAREN && !is(myTypes.C_MACRO_NAME)) { // a function call
                    // |>x<| ( ...
                    endLikeSemi2();
                    mark(myTypes.C_FUN_CALL).wrapAtom(myTypes.A_LOWER_SYMBOL);
                } else if (is(myTypes.C_SIG_EXPR) || (isScope(myTypes.LPAREN) && isRawParent(myTypes.C_SIG_EXPR))) {
                    mark(myTypes.C_SIG_ITEM)
                            .wrapAtom(myTypes.A_LOWER_SYMBOL).popEnd();
                } else {
                    wrapAtom(myTypes.A_LOWER_SYMBOL).popEnd();
                }
            }
        }

        private void parseLBracket() {
            if (previousElementType(2) == myTypes.UIDENT && previousElementType(1) == myTypes.DOT) { // Local open
                // M. |>[ <| ...
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LBRACKET);
            } else {
                IElementType nextType = rawLookup(1);

                if (nextType == myTypes.GT) { // Lower bound type constraint
                    // |> [ <| > ... ]
                    markScope(myTypes.C_LOWER_BOUND_CONSTRAINT, myTypes.LBRACKET).advance();
                } else if (nextType == myTypes.LT) { // Upper bound type constraint
                    // |> [ <| < ... ]
                    markScope(myTypes.C_UPPER_BOUND_CONSTRAINT, myTypes.LBRACKET).advance();
                } else {
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET).advance();
                    if (getTokenType() != myTypes.PIPE && getTokenType() != myTypes.POLY_VARIANT) {
                        markHolder(myTypes.H_COLLECTION_ITEM); // Needed to rollback to individual item in collection
                    }
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
            if (previousElementType(1) == myTypes.DOT && previousElementType(2) == myTypes.UIDENT) { // Local open a js object or a record
                // Xxx.|>{<| ... }
                mark(myTypes.C_LOCAL_OPEN);
                IElementType nextElementType = lookAhead(1);
                if (nextElementType == myTypes.LIDENT) {
                    markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE);
                } else {
                    markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
                }
            } else if (is(myTypes.C_TYPE_BINDING)) {
                boolean isJsObject = lookAhead(1) == myTypes.STRING_VALUE;
                markScope(isJsObject ? myTypes.C_JS_OBJECT : myTypes.C_RECORD_EXPR, myTypes.LBRACE);
            } else if (is(myTypes.C_MODULE_BINDING)) {
                // module M = |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (isCurrent(myTypes.C_TAG_PROP_VALUE) || isCurrent(myTypes.C_TAG_BODY)) {
                // A scoped property
                popIfHold().markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE)
                        .advance().markHolder(myTypes.H_PLACE_HOLDER);
            } else if (is(myTypes.C_MODULE_TYPE)) {
                // module M : |>{<| ...
                updateScopeToken(myTypes.LBRACE);
            } else if (isDone(myTypes.C_TRY_BODY)) { // A try expression
                // try ... |>{<| ... }
                markScope(myTypes.C_TRY_HANDLERS, myTypes.LBRACE);
            } else if (isDone(myTypes.C_BINARY_CONDITION) && isRawParent(myTypes.C_IF)) {
                // if x |>{<| ... }
                markScope(myTypes.C_IF_THEN_SCOPE, myTypes.LBRACE);
            } else if (isDone(myTypes.C_BINARY_CONDITION) && isRawParent(myTypes.C_SWITCH_EXPR)) {
                // switch (x) |>{<| ... }
                markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
            } else if (strictlyIn(myTypes.C_BINARY_CONDITION)) {
                popEndUntilFoundIndex().end();
                if (strictlyIn(myTypes.C_SWITCH_EXPR)) {
                    // switch x |>{<| ... }
                    markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
                }
            } else {
                // it might be a js object
                IElementType nextElement = lookAhead(1);
                if (nextElement == myTypes.STRING_VALUE || nextElement == myTypes.DOT) {
                    boolean hasDot = nextElement == myTypes.DOT;
                    // js object detected ::  |>{<| ./"x" ___ }
                    markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE).advance();
                    if (hasDot) {
                        advance();
                    }
                } else {
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
            }
        }

        private void parseLParen() {
            if (previousElementType(2) == myTypes.UIDENT && previousElementType(1) == myTypes.DOT) { // Local open
                // M. |>(<| ... )
                markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
            } else if (is(myTypes.C_FUN_EXPR)) { // A function
                // |>(<| .  OR  |>(<| ~
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN);
            } else if (isCurrent(myTypes.C_FUN_CALL)) {
                // x |>(<| ...
                markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                markHolder(myTypes.H_COLLECTION_ITEM);
                if (getTokenType() != myTypes.RPAREN) {
                    mark(myTypes.C_PARAM)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                }
            } else if (isCurrent(myTypes.C_PARAM_DECLARATION) || isCurrent(myTypes.C_PARAM)) {
                popIfHold();
                if (!currentHasScope()) {
                    updateScopeToken(myTypes.LPAREN);
                } else {
                    markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
                }
                advance().markHolder(myTypes.H_PLACE_HOLDER);
            } else if (isCurrent(myTypes.C_PARAMETERS)) {
                if (!currentHasScope()) {
                    // |>(<| ... ) => ...
                    updateScopeToken(myTypes.LPAREN).advance()
                            .markHolder(myTypes.H_COLLECTION_ITEM);
                } else {
                    // ( |>(<| ... ) , ... ) => ...
                    mark(myTypes.C_PARAM_DECLARATION)
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
            } else if (is(myTypes.C_BINARY_CONDITION)) {
                updateScopeToken(myTypes.LPAREN);
            } else if (is(myTypes.C_DECONSTRUCTION) && isRawParent(myTypes.C_LET_DECLARATION)) {
                // let ((x |>,<| ...
                markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN);
            }
            //
            else if (strictlyInAny(
                    myTypes.C_OPEN, myTypes.C_INCLUDE, myTypes.C_VARIANT_DECLARATION, myTypes.C_FUNCTOR_DECLARATION,
                    myTypes.C_FUNCTOR_CALL, myTypes.C_FUNCTOR_RESULT
            )) {

                if (isFound(myTypes.C_OPEN) || isFound(myTypes.C_INCLUDE)) { // Functor call
                    // open M |>(<| ...
                    markBefore(0, myTypes.C_FUNCTOR_CALL)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
                }
                //else if (isFound(myTypes.C_VARIANT_DECLARATION)) { // Variant constructor
                //    // type t = | Variant |>(<| .. )
                //    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN)
                //            .advance()
                //            .mark(myTypes.C_PARAM_DECLARATION);
                //}
                else if (isFound(myTypes.C_FUNCTOR_DECLARATION) || isFound(myTypes.C_FUNCTOR_CALL)) {
                    // module M = |>(<| ...
                    // module M = ( ... ) : |>(<| ...
                    boolean isCall = isFound(myTypes.C_FUNCTOR_CALL);
                    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(isCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
                }
            } else {
                Marker marker = getActiveMarker();
                if (marker != null && marker.isCompositeType(myTypes.C_LET_BINDING) && !marker.hasScope()) {
                    endLikeSemi2();
                }
                markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance()
                        .markHolder(myTypes.H_COLLECTION_ITEM);
            }
        }

        private void parseRParen() {
            Marker scope = popEndUntilScopeToken(myTypes.LPAREN);
            advance();

            IElementType tokenType = getTokenType();
            if (is(myTypes.C_BINARY_CONDITION)) {
                end();
                if (isRawParent(myTypes.C_IF) && tokenType != myTypes.LBRACE) {
                    // if ( x ) |><| ...
                    mark(myTypes.C_IF_THEN_SCOPE);
                }
            } else if (scope != null) {
                if (is(myTypes.C_PARAMETERS)) {
                    end();
                    if (isGrandParent(myTypes.C_FUN_CALL)) {
                        popEnd().popEnd().popEnd();
                    }
                } else if (is(myTypes.C_SCOPED_EXPR) && tokenType == myTypes.ARROW && !inAny(myTypes.C_SIG_EXPR, myTypes.C_PATTERN_MATCH_EXPR)) {
                    // a missed function expression
                    rollbackToPos(0)
                            .mark(myTypes.C_FUN_EXPR)
                            .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN)
                            .markHolder(myTypes.H_COLLECTION_ITEM)
                            .advance();
                } else {
                    popEnd();
                    if (is(myTypes.C_ANNOTATION)) {
                        popEnd();
                    } else if (strictlyIn(myTypes.C_TAG_PROP_VALUE)) {// && !hasScopeToken()) {
                        popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
                    }
                }
            }
        }

        private void parseEq() {
            if (strictlyInAny(
                    myTypes.C_TYPE_DECLARATION, myTypes.C_LET_DECLARATION, myTypes.C_MODULE_TYPE, myTypes.C_MODULE_DECLARATION,
                    myTypes.C_TAG_PROPERTY, myTypes.C_SIG_EXPR, myTypes.C_NAMED_PARAM_DECLARATION
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
                } else if (isFound(myTypes.C_MODULE_TYPE)) {
                    // module M : T |> = <| ...
                    popEndUntilFoundIndex().end()
                            .advance()
                            .mark(myTypes.C_MODULE_BINDING);
                } else if (isFound(myTypes.C_MODULE_DECLARATION)) {
                    // module M |> = <| ...
                    advance().mark(myTypes.C_MODULE_BINDING);
                } else if (isFound(myTypes.C_TAG_PROPERTY)) {
                    // <Comp prop |> = <| ...
                    advance().mark(myTypes.C_TAG_PROP_VALUE)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_SIG_EXPR)) {
                    popEndUntilFoundIndex().end();
                    if (isGrandParent(myTypes.C_NAMED_PARAM_DECLARATION)) {
                        advance().mark(myTypes.C_DEFAULT_VALUE);
                    } else if (isRawParent(myTypes.C_LET_DECLARATION)) {
                        // let x : M.t |> =<| ...
                        popEndUntilFoundIndex().advance()
                                .mark(myTypes.C_LET_BINDING);
                    }
                } else if (isFound(myTypes.C_NAMED_PARAM_DECLARATION)) {
                    // ... => ~x |> =<| ...
                    advance().mark(myTypes.C_DEFAULT_VALUE);
                }

            }
        }

        private void parseUIdent() {
            if (DUMMY_IDENTIFIER_TRIMMED.equals(getTokenText())) {
                return;
            }

            if (is(myTypes.C_MODULE_DECLARATION)) {
                // module |>M<| ...
                wrapAtom(myTypes.A_UPPER_SYMBOL);
            } else if (is(myTypes.C_EXCEPTION_DECLARATION)) {
                // exception |>E<| ...
                wrapAtom(myTypes.A_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_TAG_START) || isCurrent(myTypes.C_TAG_CLOSE)) {
                // tag name
                remapCurrentToken(myTypes.A_UPPER_TAG_NAME).wrapAtom(myTypes.A_UPPER_SYMBOL);
            } else if (isCurrent(myTypes.C_OPEN)) { // It is a module name/path, or maybe a functor call
                // open |>M<| ...
                wrapAtom(myTypes.A_UPPER_SYMBOL);
                IElementType nextToken = getTokenType();
                if (nextToken != myTypes.LPAREN && nextToken != myTypes.DOT) {
                    popEndUntil(myTypes.C_OPEN).popEnd();
                }
            } else if (isCurrent(myTypes.C_INCLUDE)) { // It is a module name/path, or maybe a functor call
                // include |>M<| ...
                wrapAtom(myTypes.A_UPPER_SYMBOL);
                IElementType nextToken = getTokenType();
                if (nextToken != myTypes.LPAREN && nextToken != myTypes.DOT) {
                    popEndUntil(myTypes.C_INCLUDE).popEnd();
                }
            } else if (is(myTypes.C_VARIANT_DECLARATION)) { // Declaring a variant
                // type t = | |>X<| ..
                remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.A_UPPER_SYMBOL);
                if (getTokenType() == myTypes.LPAREN) {
                    markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                            .mark(myTypes.C_PARAM_DECLARATION);
                }
            } else if (is(myTypes.C_TYPE_BINDING)) {
                IElementType nextToken = lookAhead(1);
                if (nextToken == myTypes.DOT) { // a path
                    wrapAtom(myTypes.A_UPPER_SYMBOL);
                } else { // We are declaring a variant without a pipe before
                    // type t = |>X<| | ...
                    // type t = |>X<| (...) | ...
                    mark(myTypes.C_VARIANT_DECLARATION)
                            .remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.A_UPPER_SYMBOL);
                    if (getTokenType() == myTypes.LPAREN) {
                        markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                                .mark(myTypes.C_PARAM_DECLARATION);
                    }
                }
            } else {
                IElementType nextToken = lookAhead(1);

                if (isCurrent(myTypes.C_MODULE_BINDING) && nextToken == myTypes.LPAREN) {
                    // functor call ::  |>X<| ( ...
                    // functor call with path :: A.B.|>X<| ( ...
                    Marker current = getActiveMarker();
                    if (current != null) {
                        current.drop();
                    }
                    mark(myTypes.C_FUNCTOR_CALL);
                } else if (((isCurrent(myTypes.C_PATTERN_MATCH_EXPR) || isCurrent(myTypes.C_LET_BINDING))) && nextToken != myTypes.DOT) { // Pattern matching a variant or using it
                    // switch c { | |>X<| ... / let x = |>X<| ...
                    remapCurrentToken(myTypes.A_VARIANT_NAME).wrapAtom(myTypes.A_UPPER_SYMBOL);
                    if (getTokenType() == myTypes.LPAREN) {
                        markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                                .markHolder(myTypes.H_COLLECTION_ITEM);
                    }
                } else {
                    endLikeSemi2();
                    wrapAtom(myTypes.A_UPPER_SYMBOL);
                }
            }
        }

        private void parsePolyVariant() {
            if (isRawParent(myTypes.C_TYPE_BINDING)) {
                // type t = [ |>#xxx<| ...
                mark(myTypes.C_VARIANT_DECLARATION);
            }
        }

        private void parseSwitch() {
            mark(myTypes.C_SWITCH_EXPR)
                    .advance()
                    .mark(myTypes.C_BINARY_CONDITION);
        }

        private void parseTry() {
            popEndUntilScope();
            mark(myTypes.C_TRY_EXPR).advance()
                    .mark(myTypes.C_TRY_BODY);
        }

        private void parseCatch() {
            if (strictlyIn(myTypes.C_TRY_BODY)) {
                popEndUntilFoundIndex().end();
            }
        }

        private void parseArrow() {
            if (isDone(myTypes.C_PARAMETERS) && isRawParent(myTypes.C_FUN_EXPR)) {
                advance().mark(myTypes.C_FUN_BODY);
            } else if (strictlyIn(myTypes.C_PARAMETERS) && isAtIndex(getIndex() + 1, myTypes.C_FUN_EXPR)) { // parenless param
                // x |>=><| ...
                popEndUntil(myTypes.C_PARAMETERS).end()
                        .advance().mark(myTypes.C_FUN_BODY)
                        .markHolder(myTypes.H_PLACE_HOLDER);
            } else if (inScopeOrAny(
                    myTypes.C_LET_BINDING, myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_FIELD_VALUE, myTypes.C_FUN_EXPR,
                    myTypes.C_PARAM_DECLARATION, myTypes.C_SIG_ITEM, myTypes.C_SIG_EXPR, myTypes.C_TAG_PROP_VALUE
            )) {

                if (isFound(myTypes.C_LET_BINDING)) {
                    // let x = .?. |>=><| ...
                    rollbackToPos(getIndex())
                            .mark(myTypes.C_LET_BINDING)
                            .mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                } else if (isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                    // switch ( ... ) { | ... |>=><| ... }
                    popEndUntilFoundIndex().advance()
                            .markScope(myTypes.C_PATTERN_MATCH_BODY, myTypes.ARROW)
                            .markHolder(myTypes.H_PLACE_HOLDER);
                } else if (isFound(myTypes.C_FIELD_VALUE)) {
                    // { x : .?. |>=><| ...
                    rollbackToPos(getIndex())
                            .mark(myTypes.C_FIELD_VALUE)
                            .mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                } else if (isFound(myTypes.C_SIG_ITEM)) {
                    popEndUntilFoundIndex().popEnd();
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_SIG_EXPR)) {
                    advance().mark(myTypes.C_SIG_ITEM);
                } else if (isFound(myTypes.C_PARAM_DECLARATION)) { // anonymous function
                    // x( y |>=><| ... )
                    rollbackToFoundIndex()
                            .mark(myTypes.C_PARAM_DECLARATION)
                            .mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                } else if (isHold() && !in(myTypes.C_SIG_ITEM)) {
                    // by default, a function
                    rollbackToPos(0);
                    if (isCurrent(myTypes.C_PARAMETERS)) {
                        mark(isParent(myTypes.C_FUN_EXPR) ? myTypes.C_PARAM_DECLARATION : myTypes.C_PARAM);
                    }
                    mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                }
            }
        }

        private void endLikeSemi() {
            boolean end = isHold() ?
                    !isAtIndex(1, myTypes.C_LET_BINDING) && !isAtIndex(1, myTypes.C_BINARY_CONDITION) && !isAtIndex(1, myTypes.C_PARAM_DECLARATION) :
                    !is(myTypes.C_LET_BINDING) && !is(myTypes.C_BINARY_CONDITION) && !is(myTypes.C_PARAM_DECLARATION);

            if (end) {
                popIfHold();
                IElementType previousElementType = previousElementType(1);
                if (previousElementType != null && previousElementType != myTypes.DOT && previousElementType != myTypes.ARROW) {
                    popEndUntilScope();
                }
            }
        }

        // Languages with no delimiter are so bad for parsers
        private void endLikeSemi2() {
            IElementType previousType = previousElementType(1);
            if (previousType != myTypes.EQ
                    && previousType != myTypes.EQEQ
                    && previousType != myTypes.EQEQEQ
                    && previousType != myTypes.NOT_EQ
                    && previousType != myTypes.NOT_EQEQ
                    && previousType != myTypes.DOT
                    && previousType != myTypes.ARROW
                    && previousType != myTypes.PIPE
                    && previousType != myTypes.RIGHT_ARROW
                    && previousType != myTypes.TRY
                    && previousType != myTypes.SEMI
                    && previousType != myTypes.THEN
                    && previousType != myTypes.ELSE
                    && previousType != myTypes.IN
                    && previousType != myTypes.LPAREN
                    && previousType != myTypes.LBRACE
                    && previousType != myTypes.LBRACKET
                    && previousType != myTypes.DO
                    && previousType != myTypes.STRUCT
                    && previousType != myTypes.SIG
                    && previousType != myTypes.COLON
                    && previousType != myTypes.COMMA
                    && previousType != myTypes.L_OR
            ) {
                Marker marker = getActiveMarker();
                boolean end = marker == null || !marker.isCompositeType(myTypes.C_BINARY_CONDITION);
                if (end) {
                    popEndUntilScope();
                }
            }
        }
    }
}
