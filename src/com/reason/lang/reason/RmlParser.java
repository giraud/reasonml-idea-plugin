package com.reason.lang.reason;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

public class RmlParser extends CommonParser<RmlTypes> implements RmlStubBasedElementTypes {
    RmlParser(boolean isSafe) {
        super(isSafe, RmlTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType;

        long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            tokenType = state.getTokenType();
            if (tokenType == null) {
                break;
            }

            long parseTime = System.currentTimeMillis();
            if (5000 < parseTime - parseStart) {
                if (myIsSafe) { // Don't do that in tests
                    state.error("CANCEL");
                    LOG.error("CANCEL REASON PARSING");
                    break;
                }
            }

            if (state.in(myTypes.C_INTERPOLATION_EXPR)) {
                // special analysis when inside an interpolation string
                if (tokenType == myTypes.JS_STRING_CLOSE) {
                    parseJsStringClose(state);
                } else if (tokenType == myTypes.DOLLAR) {
                    if (state.is(myTypes.C_INTERPOLATION_PART)) {
                        state.popEnd();
                        state.advance().mark(myTypes.C_INTERPOLATION_REF);
                    } else if (state.is(myTypes.C_INTERPOLATION_EXPR)) {
                        // first element
                        state.advance().mark(myTypes.C_INTERPOLATION_REF);
                    }
                } else if (state.is(myTypes.C_INTERPOLATION_REF)) {
                    state.advance().popEnd();
                } else if (!state.is(myTypes.C_INTERPOLATION_PART)) {
                    state.mark(myTypes.C_INTERPOLATION_PART);
                }
            } else {
                // special keywords that can be used as lower identifier in records
                //if (tokenType == m_types.REF/* && state.isCurrentResolution(recordBinding)*/) {
                //parseLIdent(state);
                //} else
                if (tokenType == myTypes.METHOD /*&& state.isCurrentResolution(recordBinding)*/) {
                    parseLIdent(state);
                }
                //
                else if (tokenType == myTypes.SEMI) {
                    parseSemi(state);
                } else if (tokenType == myTypes.EQ) {
                    parseEq(state);
                } else if (tokenType == myTypes.ARROW) {
                    parseArrow(state);
                } else if (tokenType == myTypes.REF) {
                    parseRef(state);
                } else if (tokenType == myTypes.OPTION) {
                    parseOption(state);
                } else if (tokenType == myTypes.TRY) {
                    parseTry(state);
                } else if (tokenType == myTypes.SWITCH) {
                    parseSwitch(state);
                } else if (tokenType == myTypes.LIDENT) {
                    parseLIdent(state);
                } else if (tokenType == myTypes.UIDENT) {
                    parseUIdent(state);
                } else if (tokenType == myTypes.ARROBASE) {
                    parseArrobase(state);
                } else if (tokenType == myTypes.PERCENT) {
                    parsePercent(state);
                } else if (tokenType == myTypes.COLON) {
                    parseColon(state);
                } else if (tokenType == myTypes.RAW) {
                    parseRaw(state);
                } else if (tokenType == myTypes.STRING_VALUE) {
                    parseStringValue(state);
                } else if (tokenType == myTypes.PIPE) {
                    parsePipe(state);
                } else if (tokenType == myTypes.COMMA) {
                    parseComma(state);
                } else if (tokenType == myTypes.AND) {
                    parseAnd(state);
                } else if (tokenType == myTypes.FUN) {
                    parseFun(state);
                } else if (tokenType == myTypes.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == myTypes.IF) {
                    parseIf(state);
                } else if (tokenType == myTypes.ELSE) {
                    parseElse(state);
                } else if (tokenType == myTypes.THEN) {
                    parseThen(state);
                } else if (tokenType == myTypes.DOT) {
                    parseDot(state);
                } else if (tokenType == myTypes.DOTDOTDOT) {
                    parseDotDotDot(state);
                } else if (tokenType == myTypes.WITH) {
                    parseWith(state);
                } else if (tokenType == myTypes.TILDE) {
                    parseTilde(state);
                } else if (tokenType == myTypes.QUESTION_MARK) {
                    parseQuestionMark(state);
                } else if (tokenType == myTypes.UNDERSCORE) {
                    parseUnderscore(state);
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
                // [| ... |]
                else if (tokenType == myTypes.LARRAY) {
                    parseLArray(state);
                } else if (tokenType == myTypes.RARRAY) {
                    parseRArray(state);
                }
                // [ ... ]
                // [> ... ]
                else if (tokenType == myTypes.LBRACKET) {
                    parseLBracket(state);
                } else if (tokenType == myTypes.BRACKET_GT) {
                    parseBracketGt(state);
                } else if (tokenType == myTypes.RBRACKET) {
                    parseRBracket(state);
                }
                // < ... >
                else if (tokenType == myTypes.LT) {
                    parseLt(state);
                } else if (tokenType == myTypes.TAG_LT_SLASH) {
                    parseLtSlash(state);
                } else if (tokenType == myTypes.GT) {
                    parseGt(state);
                } else if (tokenType == myTypes.TAG_AUTO_CLOSE) {
                    parseGtAutoClose(state);
                }
                // {| ... |}
                else if (tokenType == myTypes.ML_STRING_OPEN) {
                    parseMlStringOpen(state);
                } else if (tokenType == myTypes.ML_STRING_CLOSE) {
                    parseMlStringClose(state);
                }
                // {j| ... |j}
                else if (tokenType == myTypes.JS_STRING_OPEN) {
                    parseJsStringOpen(state);
                }
                // Starts an expression
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
                } else if (tokenType == myTypes.PUB) {
                    parsePub(state);
                } else if (tokenType == myTypes.EXCEPTION) {
                    parseException(state);
                }
            }

            if (state.dontMove) {
                state.dontMove = false;
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "reasonFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS) && state.isParent(myTypes.C_FUN_EXPR)) {
            // ( |>_<| ...
            state.mark(myTypes.C_FUN_PARAM);
        }
    }

    private void parseTilde(@NotNull ParserState state) {
        IElementType nextType = state.rawLookup(1);
        if (nextType == myTypes.LIDENT) {
            if (!state.is(myTypes.C_FUN_PARAM) && !state.is(myTypes.C_SIG_ITEM)) {
                state.mark(myTypes.C_FUN_PARAM);
            }
            state.mark(myTypes.C_NAMED_PARAM).advance().wrapWith(myTypes.C_LOWER_IDENTIFIER);
        }
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        if (state.inAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
            if (state.isFound(myTypes.C_TAG_START)) {
                // <jsx |>?<|prop ...
                state.mark(myTypes.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state))
                        .advance()
                        .remapCurrentToken(myTypes.PROPERTY_NAME);
            }
        } else if (state.in(myTypes.C_BINARY_CONDITION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance().mark(myTypes.C_IF_THEN_SCOPE);
        } else if (!state.strictlyInAny(myTypes.C_TERNARY, myTypes.C_NAMED_PARAM)) {
            if (state.inScopeOrAny(myTypes.C_LET_BINDING, myTypes.C_FIELD_VALUE, myTypes.C_FUN_PARAM, myTypes.C_FUNCTOR_PARAM,
                    myTypes.C_PATTERN_MATCH_BODY, myTypes.C_IF_THEN_SCOPE, myTypes.C_FUN_BODY, myTypes.C_DUMMY_COLLECTION_ITEM)) {
                // a new ternary
                state.rollbackToFoundIndex()
                        .mark(myTypes.C_TERNARY)
                        .mark(myTypes.C_BINARY_CONDITION);
            }
        } else if (state.strictlyIn(myTypes.C_IF_THEN_SCOPE)) {
            // if_then_scope can be inside a ternary, we need to be explicit
            state.rollbackToFoundIndex()
                    .mark(myTypes.C_TERNARY)
                    .mark(myTypes.C_BINARY_CONDITION);
        }
    }

    private void parseRef(@NotNull ParserState state) {
        if (state.in(myTypes.C_TAG_START)) {
            state.remapCurrentToken(myTypes.PROPERTY_NAME)
                    .mark(myTypes.C_TAG_PROPERTY);
        }
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(myTypes.C_OPTION);
    }

    private void parseRaw(@NotNull ParserState state) {
        if (state.is(myTypes.C_MACRO_NAME)) {
            // % |>raw<| ...
            state.advance().popEnd();
        }
    }

    private void parseIf(@NotNull ParserState state) {
        state.mark(myTypes.C_IF);
    }

    private void parseThen(@NotNull ParserState state) {
        // if ... |>then<| ...
        state.popEndUntil(myTypes.C_IF).advance()
                .mark(myTypes.C_IF_THEN_SCOPE);
    }

    private void parseElse(@NotNull ParserState state) {
        // if ... then ... |>else<| ...
        state.popEndUntil(myTypes.C_IF).advance()
                .mark(myTypes.C_IF_THEN_SCOPE);
    }

    private void parseDot(@NotNull ParserState state) {
        if (state.previousElementType(1) == myTypes.LBRACE && (state.is(myTypes.C_JS_OBJECT))) { // Js object definition
            // ... { |>.<| ... }
            state.advance().mark(myTypes.C_OBJECT_FIELD);
        }
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        if (state.previousElementType(1) == myTypes.LBRACE) { // Mixin
            // { |>...<| x ...
            if (state.is(myTypes.C_FUN_BODY)) {
                state.rollbackTo(state.getIndex())
                        .mark(myTypes.C_FUN_BODY)
                        .markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE).advance()
                        .mark(myTypes.C_MIXIN_FIELD);
            } else {
                state.updateComposite(myTypes.C_RECORD_EXPR)
                        .mark(myTypes.C_MIXIN_FIELD);
            }
        }
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.in(myTypes.C_FUNCTOR_RESULT)) {
            // module M (X) : ( S |>with<| ... ) = ...
            state.popEndUntilFoundIndex().popEnd().advance()
                    .mark(myTypes.C_CONSTRAINTS);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(myTypes.C_ASSERT_STMT).advance();
    }

    private void parseFun(@NotNull ParserState state) {
        if (state.is(myTypes.C_LET_BINDING)) { // fun keyword is equivalent to a switch body
            // let x = |>fun<| | ...
            state.mark(myTypes.C_FUN_EXPR);
        }
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.in(myTypes.C_CONSTRAINT)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
            state.popEndUntilFoundIndex().popEnd();
        } else {
            Marker scope = state.popEndUntilScope();
            if (scope != null) {
                if (scope.isCompositeType(myTypes.C_TYPE_DECLARATION)) {
                    state.advance().mark(myTypes.C_TYPE_DECLARATION).setStart();
                } else if (scope.isCompositeType(myTypes.C_LET_DECLARATION)) {
                    state.advance().mark(myTypes.C_LET_DECLARATION).setStart();
                } else if (isModuleResolution(scope)) {
                    state.advance().mark(myTypes.C_MODULE_DECLARATION).setStart();
                }
            }
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.inScopeOrAny(
                myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.C_SIG_ITEM, myTypes.C_MIXIN_FIELD,
                myTypes.C_VARIANT_CONSTRUCTOR, myTypes.C_PARAMETERS, myTypes.C_SIG_EXPR, myTypes.C_DUMMY_COLLECTION_ITEM
        )) {

            if (state.isFound(myTypes.C_DUMMY_COLLECTION_ITEM)) {
                state.popEndUntilFoundIndex().popEnd()
                        .advance().markDummy(myTypes.C_DUMMY_COLLECTION_ITEM);
                return;
            }
            if (state.isFound(myTypes.C_SCOPED_EXPR) && state.isAtIndex(state.getIndex() + 1, myTypes.C_LET_DECLARATION)) { // It must be a deconstruction
                // let ( a |>,<| b ) = ...
                // We need to do it again because lower symbols must be wrapped with identifiers
                state.rollbackTo(state.getIndex())
                        .markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN).advance();
                return;
            }
            if (state.isFound(myTypes.C_SCOPED_EXPR) && state.isScope(myTypes.LBRACE) && state.isAtIndex(state.getIndex() + 1, myTypes.C_FUN_PARAM)) { // It must be a deconstruction in parameters
                // { a |>,<| b } => ...
                // We need to do it again because lower symbols must be wrapped with identifiers
                state.rollbackTo(state.getIndex())
                        .markScope(myTypes.C_DECONSTRUCTION, myTypes.LBRACE).advance();
                return;
            }

            if (state.isFound(myTypes.C_SIG_ITEM)) {
                state.popEndUntilFoundIndex();
                // double sig ? ~x:int,
                if (state.in(myTypes.C_NAMED_PARAM, /*not*/myTypes.C_SCOPED_EXPR)) {
                    state.popEndUntilFoundIndex().popEnd();
                }
                state.popEnd();
            }

            if (state.strictlyInAny(myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD)) {
                boolean isRecord = state.isFound(myTypes.C_RECORD_FIELD);
                state.popEndUntilFoundIndex().popEnd().advance();

                IElementType tokenType = state.getTokenType();
                if (tokenType != myTypes.RBRACE && tokenType != myTypes.LBRACKET) {
                    state.mark(isRecord ? myTypes.C_RECORD_FIELD : myTypes.C_OBJECT_FIELD);
                }
            } else if (state.strictlyIn(myTypes.C_MIXIN_FIELD)) {
                state.popEndUntilFoundIndex().popEnd().advance();
            } else if (state.strictlyIn(myTypes.C_DECONSTRUCTION)) {
                state.popEndUntilScope();
            } else if (state.strictlyInAny(myTypes.C_VARIANT_CONSTRUCTOR, myTypes.C_PARAMETERS)) {
                state.popEndUntilFoundIndex().advance();
                if (state.getTokenType() != myTypes.RPAREN) {
                    // not at the end of a list: ie not => (p1, p2<,> )
                    state.mark(myTypes.C_FUN_PARAM);
                }
            } else if (state.isDropped(myTypes.C_SCOPED_EXPR) && state.isParent(myTypes.C_SIG_EXPR)) {
                // type t = ( ... |>,<|
                state.advance().mark(myTypes.C_SIG_ITEM);
            } else if (state.strictlyIn(myTypes.C_SCOPED_EXPR)) {
                state.popEndUntilFoundIndex();
            }
        }

    }

    private void parsePipe(@NotNull ParserState state) {
        if (state.is(myTypes.C_TRY_HANDLERS)) {
            // try (...) { |>|<| ...
            state.mark(myTypes.C_TRY_HANDLER);
        } else if (state.is(myTypes.C_TYPE_BINDING)) {
            // type t = |>|<| ...
            state.popEndUntil(myTypes.C_TYPE_BINDING).advance()
                    .mark(myTypes.C_VARIANT_DECLARATION);
        } else if (state.in(myTypes.C_VARIANT_DECLARATION)) {
            // type t = | X |>|<| Y ...
            state.popEndUntil(myTypes.C_TYPE_BINDING).advance()
                    .mark(myTypes.C_VARIANT_DECLARATION);
        } else if (state.in(myTypes.C_PATTERN_MATCH_BODY)) {
            // can be a switchBody or a 'fun'
            if (!state.is(myTypes.C_SWITCH_BODY)) {
                state.popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd().advance();
            }
            state.mark(myTypes.C_PATTERN_MATCH_EXPR);
        } else {
            if (!state.is(myTypes.C_SWITCH_BODY) && state.in(myTypes.C_PATTERN_MATCH_EXPR, /*not*/myTypes.C_PATTERN_MATCH_BODY)) { // pattern grouping
                // | X |>|<| Y => ...
                state.popEndUntilIndex(state.getIndex()).popEnd();
            }

            if (state.isScope(myTypes.LBRACKET) && state.isParent(myTypes.C_TYPE_BINDING)) {
                // type t = [ |>|<| ...
                state.advance().mark(myTypes.C_VARIANT_DECLARATION);
            } else { // By default, a pattern match
                state.advance().mark(myTypes.C_PATTERN_MATCH_EXPR);
            }
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        if (state.is(myTypes.C_MACRO_EXPR)) {
            // [%raw |>"x"<| ...
            state.wrapWith(myTypes.C_MACRO_BODY);
        } else if (state.in(myTypes.C_MACRO_NAME)) {
            // [@bs |>"x<| ...
            state.popEndUntilScope();
        } else if (state.is(myTypes.C_JS_OBJECT)) {
            state.mark(myTypes.C_OBJECT_FIELD).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_OBJECT_FIELD)) {
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        }
    }

    private void parseMlStringOpen(@NotNull ParserState state) {
        if (state.is(myTypes.C_MACRO_EXPR)) {
            state.mark(myTypes.C_MACRO_BODY);
        }

        state.markScope(myTypes.C_ML_INTERPOLATOR, myTypes.ML_STRING_OPEN);
    }

    private void parseMlStringClose(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.ML_STRING_OPEN);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseJsStringOpen(@NotNull ParserState state) {
        state.markScope(myTypes.C_INTERPOLATION_EXPR, myTypes.JS_STRING_OPEN);
    }

    private void parseJsStringClose(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.JS_STRING_OPEN);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLet(@NotNull ParserState state) {
        state.mark(myTypes.C_LET_DECLARATION).setStart();
    }

    private void parseVal(@NotNull ParserState state) {
        if (!state.in(myTypes.C_MACRO_NAME)) {
            state.popEndUntilScope();
            if (state.is(myTypes.C_OBJECT)) {
                state.mark(myTypes.C_CLASS_FIELD);
            }
        }
    }

    private void parsePub(@NotNull ParserState state) {
        if (state.in(myTypes.C_OBJECT)) {
            state.popEndUntil(myTypes.C_OBJECT).
                    mark(myTypes.C_CLASS_METHOD);
        }
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.in(myTypes.C_MACRO_NAME)) {
            state.popEndUntilScope();
            state.mark(myTypes.C_MODULE_DECLARATION).setStart();
        }
    }

    private void parseException(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_EXCEPTION_DECLARATION).setStart();
    }

    private void parseClass(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_CLASS_DECLARATION).setStart();
    }

    private void parseType(@NotNull ParserState state) {
        if (state.is(myTypes.C_CONSTRAINTS)) {
            // module M = (X) : ( S with |>type<| ... ) = ...
            state.mark(myTypes.C_CONSTRAINT);
        } else if (!state.is(myTypes.C_MODULE_DECLARATION) && !state.is(myTypes.C_CLASS_DECLARATION)) {
            state.popEndUntilScope();
            state.mark(myTypes.C_TYPE_DECLARATION).setStart();
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_EXTERNAL_DECLARATION).setStart();
    }

    private void parseOpen(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_OPEN);
    }

    private void parseInclude(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_INCLUDE);
    }

    private void parsePercent(@NotNull ParserState state) {
        if (state.is(myTypes.C_MACRO_EXPR)) {
            state.mark(myTypes.C_MACRO_NAME);
        } else if (state.in(myTypes.C_LET_DECLARATION)) {
            // let name|>%<|private = ...
            state.mark(myTypes.C_LET_ATTR);
        } else {
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == myTypes.RAW) {
                // |>%<| raw ...
                state.mark(myTypes.C_MACRO_EXPR).
                        mark(myTypes.C_MACRO_NAME);
            }
        }
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.isCurrent(myTypes.C_MODULE_DECLARATION)) {
            // module M |> :<| ...
            state.advance();
            boolean isParen = state.getTokenType() == myTypes.LPAREN;
            if (isParen) {
                // module M : |>(<| ...
                state.advance();
            }
            state.mark(myTypes.C_MODULE_TYPE).updateScopeToken(isParen ? myTypes.LPAREN : null);
        } else if (state.isParent(myTypes.C_EXTERNAL_DECLARATION) || state.isParent(myTypes.C_LET_DECLARATION)) {
            // external/let e |> :<| ...
            state.advance().mark(myTypes.C_SIG_EXPR);
            if (state.getTokenType() == myTypes.LPAREN) {
                // external/let e : |>(<| ...
                state.markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                if (state.getTokenType() == myTypes.DOT) {
                    // external/let e : ( |>.<| ...
                    state.advance();
                }
            }
            state.mark(myTypes.C_SIG_ITEM);
        }
        //
        else if (state.inAny(
                myTypes.C_NAMED_PARAM, myTypes.C_FUN_PARAM, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD,
                myTypes.C_TERNARY
        )) {

            if (state.isFound(myTypes.C_NAMED_PARAM) || state.isFound(myTypes.C_FUN_PARAM)) {
                // let x = (~y |> : <| ...
                state.advance().
                        mark(myTypes.C_SIG_EXPR).markDummyParenthesisScope(myTypes).
                        advance().mark(myTypes.C_SIG_ITEM);
            } else if (state.isFound(myTypes.C_RECORD_FIELD) || state.isFound(myTypes.C_OBJECT_FIELD)) {
                state.advance();
                if (state.in(myTypes.C_TYPE_BINDING)) {
                    state.mark(myTypes.C_SIG_EXPR)
                            .mark(myTypes.C_SIG_ITEM);
                } else {
                    state.mark(myTypes.C_FIELD_VALUE);
                }
            } else if (state.isFound(myTypes.C_TERNARY)) {
                // x ? y |> :<| ...
                state.popEndUntilFoundIndex()
                        .advance().mark(myTypes.C_IF_THEN_SCOPE);
            }

        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        if (state.is(myTypes.C_ANNOTATION)) {
            state.mark(myTypes.C_MACRO_NAME);
        }
    }

    private void parseLt(@NotNull ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = state.rawLookup(1);
        // Note that option is a ReasonML keyword but also a JSX keyword !
        if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.UIDENT || nextTokenType == myTypes.OPTION) {
            // Surely a tag
            state.mark(myTypes.C_TAG)
                    .markScope(myTypes.C_TAG_START, myTypes.LT)
                    .advance()
                    .remapCurrentToken(myTypes.TAG_NAME)
                    .wrapWith(nextTokenType == myTypes.UIDENT ? myTypes.C_UPPER_SYMBOL : myTypes.C_LOWER_SYMBOL);
        } else if (nextTokenType == myTypes.GT) {
            // a React fragment start
            state.mark(myTypes.C_TAG)
                    .mark(myTypes.C_TAG_START)
                    .advance().advance().popEnd();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        // Note that option is a ReasonML keyword but also a JSX keyword !
        if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.UIDENT || nextTokenType == myTypes.OPTION) {
            // A closing tag
            if (state.in(myTypes.C_TAG_BODY)) {
                state.popEndUntil(myTypes.C_TAG);
            }

            state.remapCurrentToken(myTypes.TAG_LT_SLASH)
                    .mark(myTypes.C_TAG_CLOSE)
                    .advance()
                    .remapCurrentToken(myTypes.TAG_NAME)
                    .wrapWith(nextTokenType == myTypes.UIDENT ? myTypes.C_UPPER_SYMBOL : myTypes.C_LOWER_SYMBOL);
        } else if (nextTokenType == myTypes.GT) {
            // a React fragment end
            state.remapCurrentToken(myTypes.TAG_LT_SLASH)
                    .mark(myTypes.C_TAG_CLOSE)
                    .advance().advance().popEnd();
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.is(myTypes.C_TAG_PROP_VALUE)) {
            // ?prop=value |> > <| ...
            state.popEnd().popEnd();
        } else if (state.is(myTypes.C_TAG_PROPERTY)) {
            // ?prop |> > <| ...
            state.popEnd();
        }

        if (state.in(myTypes.C_TAG)) {
            if (state.inScopeOrAny(myTypes.C_TAG_PROP_VALUE, myTypes.C_TAG_START)) {
                state.advance().popEndUntilIndex(state.getIndex());
                if (state.is(myTypes.C_TAG_START)) {
                    state.popEnd().mark(myTypes.C_TAG_BODY);
                }
            } else if (state.in(myTypes.C_TAG_CLOSE)) {
                // end the tag
                state.advance().popEndUntil(myTypes.C_TAG).popEnd();
            }
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.advance().popEnd(/*tag_start*/).popEnd(/*tag*/);
    }

    private void parseLIdent(@NotNull ParserState state) {
        if (state.is(myTypes.C_LET_DECLARATION)) {
            // let |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_TYPE_DECLARATION)) {
            // type |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_EXTERNAL_DECLARATION)) {
            // external |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_CLASS_DECLARATION)) {
            // class |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_RECORD_EXPR)) {
            // let x = { |>y<| ...
            state.mark(myTypes.C_RECORD_FIELD).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_RECORD_FIELD)) {
            // let x = { y, |>z<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if ((state.is(myTypes.C_PARAMETERS) && state.isParent(myTypes.C_FUN_EXPR)) || state.is(myTypes.C_VARIANT_CONSTRUCTOR)) {
            // ( x , |>y<| ...
            state.mark(myTypes.C_FUN_PARAM).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.strictlyInAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
            if (state.previousElementType(1) != myTypes.LT && state.isFound(myTypes.C_TAG_START)) {
                // This is a property
                state.popEndUntilScope();
                state.remapCurrentToken(myTypes.PROPERTY_NAME)
                        .mark(myTypes.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state));
            }
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.is(myTypes.C_SCOPED_EXPR) && state.isScope(myTypes.LBRACE) && nextElementType == myTypes.COLON) {
                // this is a record usage ::  { |>x<| : ...
                state.updateComposite(myTypes.C_RECORD_EXPR)
                        .mark(myTypes.C_RECORD_FIELD)
                        .wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (nextElementType == myTypes.LPAREN && !state.inAny(myTypes.C_TYPE_BINDING, myTypes.C_CONSTRAINT, myTypes.C_SIG_ITEM)) {
                state.mark(myTypes.C_FUN_CALL)
                        .wrapWith(myTypes.C_LOWER_SYMBOL);
            } else if (state.is(myTypes.C_DECONSTRUCTION)) {
                state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else {
                state.wrapWith(myTypes.C_LOWER_SYMBOL);
            }
        }
    }

    private void parseLArray(@NotNull ParserState state) {
        state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LARRAY)
                .markDummy(myTypes.C_DUMMY_COLLECTION_ITEM); // Needed to rollback to start of item
    }

    private void parseRArray(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.LARRAY);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);

        if (nextTokenType == myTypes.ARROBASE) {
            // |>[ <| @ ...
            state.markScope(myTypes.C_ANNOTATION, myTypes.LBRACKET);
        } else if (nextTokenType == myTypes.PERCENT) {
            // |>[ <| % ...
            state.markScope(myTypes.C_MACRO_EXPR, myTypes.LBRACKET);
        } else if (state.previousElementType(2) == myTypes.UIDENT && state.previousElementType(1) == myTypes.DOT) { // Local open
            // M.|>(<| ...
            state.markScope(myTypes.C_LOCAL_OPEN, myTypes.LBRACKET);
        } else {
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET).advance();
            if (state.getTokenType() != myTypes.PIPE) {
                state.markDummy(myTypes.C_DUMMY_COLLECTION_ITEM); // Needed to rollback to individual item in collection
            }
        }
    }

    private void parseRBracket(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.LBRACKET);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseBracketGt(@NotNull ParserState state) {
        state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET);
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.previousElementType(2) == myTypes.UIDENT && state.previousElementType(1) == myTypes.DOT) { // Local open a js object or a record
            // Xxx.|>{<| ... }
            state.mark(myTypes.C_LOCAL_OPEN);
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == myTypes.LIDENT) {
                state.markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE);
            } else {
                state.markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
            }
        } else if (state.is(myTypes.C_TYPE_BINDING)) {
            boolean isJsObject = state.lookAhead(1) == myTypes.DOT;
            state.markScope(isJsObject ? myTypes.C_JS_OBJECT : myTypes.C_RECORD_EXPR, myTypes.LBRACE);
        } else if (state.is(myTypes.C_TRY_EXPR)) { // A try expression
            // try (..) |>{<| .. }
            state.markScope(myTypes.C_TRY_HANDLERS, myTypes.LBRACE);
        } else if (state.is(myTypes.C_MODULE_BINDING)) {
            // module M = |>{<| ...
            state.updateScopeToken(myTypes.LBRACE);
        } else if (state.is(myTypes.C_FUNCTOR_BINDING)) {
            // module M = (...) => |>{<| ...
            state.updateScopeToken(myTypes.LBRACE);
        } else if (state.isCurrent(myTypes.C_IF)) {
            state.markScope(myTypes.C_IF_THEN_SCOPE, myTypes.LBRACE);
        } else if (state.is(myTypes.C_MODULE_TYPE)) {
            // module M : |>{<| ...
            state.updateScopeToken(myTypes.LBRACE);
        } else if (state.in(myTypes.C_CLASS_DECLARATION)) {
            // class x = |>{<| ... }
            state.markScope(myTypes.C_OBJECT, myTypes.LBRACE);
        } else if (state.is(myTypes.C_SWITCH_EXPR)) {
            state.markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (nextElement == myTypes.STRING_VALUE || nextElement == myTypes.DOT) { // js object detected (in usage)
                // |>{<| "x" ... }
                state.markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
            } else if (state.is(myTypes.C_FUN_BODY) && !state.isScope(myTypes.LBRACE) && nextElement != myTypes.LIDENT) { // function body
                // x => |>{<| ... }
                state.updateScopeToken(myTypes.LBRACE);
            } else {
                state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE);
            }
        }
    }

    private void parseRBrace(@NotNull ParserState state) {
        Marker scope = state.popEndUntilOneOfElementType(myTypes.LBRACE, myTypes.RECORD, myTypes.SWITCH);
        state.advance();

        if (scope != null) {
            state.popEnd();

            if (state.is(myTypes.C_LOCAL_OPEN) && !state.hasScopeToken()) {
                // X.{ ... |>}<|
                state.popEnd();
            } else if (state.is(myTypes.C_TAG_PROP_VALUE)) {
                state.popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
            }
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS) && !state.hasScopeToken()) {
            // |>(<| ... ) => ...
            state.updateScopeToken(myTypes.LPAREN);
        } else if (state.is(myTypes.C_ASSERT_STMT)) {
            // assert |>(<| ...
            state.markScope(myTypes.C_BINARY_CONDITION, myTypes.LPAREN);
        } else if (state.is(myTypes.C_TRY_EXPR)) {
            // try |>(<| ...
            state.markScope(myTypes.C_TRY_BODY, myTypes.LPAREN);
        } else if (state.is(myTypes.C_IF) || state.is(myTypes.C_SWITCH_EXPR)) {
            // if |>(<| ...  OR  switch |>(<| ...
            state.markScope(myTypes.C_BINARY_CONDITION, myTypes.LPAREN);
        } else if (state.is(myTypes.C_LET_DECLARATION)) { // Overloading operator OR deconstructing a term
            //  let |>(<| + ) =
            //  let |>(<| a, b ) =
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
        } else if (state.previousElementType(2) == myTypes.UIDENT && state.previousElementType(1) == myTypes.DOT) { // Local open
            // M.|>(<| ...
            state.markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
        } else if (state.is(myTypes.C_MODULE_BINDING) && !state.in(myTypes.C_FUNCTOR_DECLARATION)) { // This is a functor
            // module M = |>(<| .. )
            int moduleIndex = state.indexOfComposite(myTypes.C_MODULE_DECLARATION);
            state.rollbackTo(moduleIndex - 1)
                    .updateComposite(myTypes.C_FUNCTOR_DECLARATION);
        } else if (state.is(myTypes.C_DECONSTRUCTION) && state.isParent(myTypes.C_LET_DECLARATION)) {
            // let ((x |>,<| ...
            state.markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN);
        } else if (state.inAny(myTypes.C_FUNCTOR_DECLARATION, myTypes.C_FUNCTOR_CALL, myTypes.C_FUNCTOR_RESULT)) {
            // module M = |>(<| ...
            // module M = ( ... ) : |>(<| ...
            if (state.isFound(myTypes.C_FUNCTOR_DECLARATION) || state.isFound(myTypes.C_FUNCTOR_CALL)) {
                state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .mark(myTypes.C_FUNCTOR_PARAM);
            }
        } else if (state.in(myTypes.C_VARIANT_DECLARATION)) { // Variant params
            // type t = | Variant |>(<| .. )
            state.markScope(myTypes.C_VARIANT_CONSTRUCTOR, myTypes.LPAREN).advance()
                    .mark(myTypes.C_FUN_PARAM);
        } else if (state.inAny(myTypes.C_CLASS_DECLARATION, myTypes.C_OBJECT)) {
            if (state.isFound(myTypes.C_CLASS_DECLARATION)) {
                state.popEndUntil(myTypes.C_CLASS_DECLARATION).
                        markScope(myTypes.C_CLASS_CONSTR, myTypes.LPAREN);
            }
        } else if (state.in(myTypes.C_FUN_CALL)
                && !(state.is(myTypes.C_TYPE_DECLARATION)
                || state.inAny(myTypes.C_TYPE_BINDING, myTypes.C_SIG_ITEM))) { // calling a function
            state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != myTypes.RPAREN) {
                state.mark(myTypes.C_FUN_PARAM);
            }
        } else if (state.inAny(myTypes.C_OPEN, myTypes.C_INCLUDE)) { // a functor call inside open/include
            // open/include M |>(<| ...
            state.markBefore(state.getIndex() - 1, myTypes.C_FUNCTOR_CALL)
                    .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                    .mark(myTypes.C_FUN_PARAM);
        } else {
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                    .markDummy(myTypes.C_DUMMY_COLLECTION_ITEM); // Needed to rollback to individual item in collection
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        // Removing intermediate resolutions
        Marker scope = state.popEndUntilScopeToken(myTypes.LPAREN);
        state.advance();

        if (state.is(myTypes.C_BINARY_CONDITION) && state.isParent(myTypes.C_IF)) {
            // if ( x |>)<| ...
            state.end();
            if (state.getTokenType() != myTypes.LBRACE) {
                state.mark(myTypes.C_IF_THEN_SCOPE);
            }
        } else if (scope != null) {
            state.popEnd();
            IElementType nextTokenType = state.getTokenType();

            if (state.isParent(myTypes.C_FUN_CALL)) {
                state.popEnd().popEnd();
            } else if (state.isParent(myTypes.C_FUNCTOR_DECLARATION)) {
                if (nextTokenType == myTypes.COLON) {
                    // module M = (P) |> :<| R ...
                    state.advance();
                    if (state.getTokenType() == myTypes.LPAREN) {
                        state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                    }
                    state.mark(myTypes.C_FUNCTOR_RESULT);
                } else if (nextTokenType == myTypes.ARROW) {
                    // module M = (P) |>=><| ...
                    state.advance().mark(myTypes.C_FUNCTOR_BINDING);
                }
            } else if (state.is(myTypes.C_TAG_PROP_VALUE)) {
                state.popEnd().popEnd();
            }
        }
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.is(myTypes.C_TAG_PROPERTY)) {
            // <X p|> =<| ...
            state.advance().mark(myTypes.C_TAG_PROP_VALUE);
        } else if (state.isParent(myTypes.C_MODULE_DECLARATION)) {
            // module M |> =<| ...
            state.advance().mark(myTypes.C_MODULE_BINDING);
        } else if (state.isParent(myTypes.C_NAMED_PARAM)) {
            // ( ~x |> =<| ...
            state.advance().mark(myTypes.C_DEFAULT_VALUE);
        } else if (state.in(myTypes.C_TYPE_DECLARATION)) {
            // type t |> =<| ...
            state.advance().mark(myTypes.C_TYPE_BINDING);
        } else if (state.in(myTypes.C_MODULE_TYPE)) {
            // module M : T |> =<| ...
            state.popEndUntilIndex(state.getIndex()).popEnd().advance()
                    .mark(myTypes.C_MODULE_BINDING);
        } else if (state.inScopeOrAny(myTypes.C_LET_DECLARATION, myTypes.C_SIG_EXPR) || state.is(myTypes.C_LET_ATTR)) {
            if (state.isFound(myTypes.C_SIG_EXPR)) {
                state.popEndUntil(myTypes.C_SIG_EXPR).popEnd();
                if (state.in(myTypes.C_NAMED_PARAM)) {
                    state.advance().mark(myTypes.C_DEFAULT_VALUE);
                } else if (state.in(myTypes.C_LET_DECLARATION)) {
                    // let ... |> =<| ...
                    state.popEndUntilStart().advance()
                            .mark(myTypes.C_LET_BINDING);
                }
            } else if (state.in(myTypes.C_LET_DECLARATION)) {
                // let ... |> =<| ...
                state.popEndUntilStart().advance()
                        .mark(myTypes.C_LET_BINDING);
            }
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        if (state.in(myTypes.C_PATTERN_MATCH_BODY)) {
            if (state.in(myTypes.C_FUN_EXPR)) { // Special case for the `fun` keyword
                state.popEndUntilScope();
            }
        } else {
            state.popEndUntilScope();
        }
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.is(myTypes.C_MODULE_DECLARATION) || state.is(myTypes.C_FUNCTOR_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_VARIANT_DECLARATION)) {
            // type t = | |>X<| ..
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_EXCEPTION_DECLARATION)) {
            // exception |>E<| ..
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_PATTERN_MATCH_EXPR)) {
            state.wrapWith(myTypes.C_UPPER_SYMBOL);
        } else {
            if ((state.in(myTypes.C_TAG_START) || state.in(myTypes.C_TAG_CLOSE)) && state.previousElementType(1) == myTypes.DOT) { // a namespaced custom component
                // <X.|>Y<| ...
                state.remapCurrentToken(myTypes.TAG_NAME);
            } else {
                IElementType nextElementType = state.lookAhead(1);

                if (state.is(myTypes.C_TYPE_BINDING) && nextElementType != myTypes.DOT) {
                    // We are declaring a variant without a pipe before ::  type t = |>X<| | ...
                    state.mark(myTypes.C_VARIANT_DECLARATION).wrapWith(myTypes.C_UPPER_IDENTIFIER);
                    return;
                } else if (state.isCurrent(myTypes.C_MODULE_BINDING) && nextElementType == myTypes.LPAREN) {
                    // functor call ::  |>X<| ( ...
                    // functor call with path :: A.B.|>X<| ( ...
                    Marker marker = state.getCurrentMarker();
                    if (marker != null) {
                        marker.drop();
                        state.mark(myTypes.C_FUNCTOR_CALL);
                    }
                }
            }

            state.wrapWith(myTypes.C_UPPER_SYMBOL);
        }
    }

    private void parseSwitch(@NotNull ParserState state) {
        state.mark(myTypes.C_SWITCH_EXPR);
    }

    private void parseTry(@NotNull ParserState state) {
        state.mark(myTypes.C_TRY_EXPR);
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.is(myTypes.C_SIG_EXPR)) {
            state.advance()
                    .mark(myTypes.C_SIG_ITEM);
        } else if (state.in(myTypes.C_SIG_ITEM, /*not*/ myTypes.C_SCOPED_EXPR)) {
            state.popEndUntil(myTypes.C_SIG_ITEM).popEnd().advance()
                    .mark(myTypes.C_SIG_ITEM);
        } else if (state.in(myTypes.C_FUNCTOR_RESULT)) {
            // module Make = (M) : R |>=><| ...
            state.popEndUntilFoundIndex().popEnd()
                    .advance().mark(myTypes.C_FUNCTOR_BINDING);
        } else if (state.inScopeOrAny(
                myTypes.C_LET_BINDING, myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_PARAMETERS,
                myTypes.C_FUN_PARAM, myTypes.C_FUN_EXPR, myTypes.C_FIELD_VALUE
        )) {

            if (state.isFound(myTypes.C_FUN_PARAM)) {
                int paramIndex = state.getIndex();
                if (state.inAny(myTypes.C_FUN_EXPR, myTypes.C_FUN_CALL)) {
                    if (state.isFound(myTypes.C_FUN_EXPR)) {
                        // x |>=><| ...
                        state.popEndUntil(myTypes.C_FUN_EXPR)
                                .advance().mark(myTypes.C_FUN_BODY);
                    } else {
                        // call(x |>=><| ...
                        state.rollbackTo(paramIndex);
                        state.mark(myTypes.C_FUN_PARAM)
                                .mark(myTypes.C_FUN_EXPR)
                                .mark(myTypes.C_PARAMETERS);
                    }
                }
            } else if (state.isFound(myTypes.C_LET_BINDING)) {
                // function parameters ::  |>x<| => ...
                state.rollbackTo(state.getIndex()).mark(myTypes.C_LET_BINDING)
                        .mark(myTypes.C_FUN_EXPR)
                        .mark(myTypes.C_PARAMETERS);
            } else if (state.isFound(myTypes.C_FIELD_VALUE)) {
                // function parameters ::  |>x<| => ...
                state.rollbackTo(state.getIndex()).mark(myTypes.C_FIELD_VALUE)
                        .mark(myTypes.C_FUN_EXPR)
                        .mark(myTypes.C_PARAMETERS);
            } else if (state.isFound(myTypes.C_PARAMETERS) || state.isFound(myTypes.C_FUN_EXPR)) {
                state.popEndUntilOneOf(myTypes.C_PARAMETERS, myTypes.C_FUN_EXPR);
                if (state.isParent(myTypes.C_FUN_EXPR) || state.isParent(myTypes.C_FUN_CALL)) {
                    state.popEnd();
                }
                state.advance().mark(myTypes.C_FUN_BODY);
            } else if (state.isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                state.advance().mark(myTypes.C_PATTERN_MATCH_BODY);
            } else if (state.is(myTypes.C_SCOPED_EXPR) && !state.in(myTypes.C_SIG_ITEM)) {
                // by default, a function
                Marker scope = state.getLatestMarker();
                ORTokenElementType scopeToken = scope == null ? null : scope.getScopeType();
                if (scopeToken != null) {
                    state.rollbackTo(0)
                            .markScope(myTypes.C_SCOPED_EXPR, scopeToken)
                            .advance().mark(myTypes.C_FUN_EXPR)
                            .mark(myTypes.C_PARAMETERS);
                }
            }

        }
    }
}
