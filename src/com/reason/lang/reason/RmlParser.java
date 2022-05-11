package com.reason.lang.reason;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

public class RmlParser extends CommonParser<RmlTypes> implements RmlStubBasedElementTypes {

    RmlParser() {
        super(RmlTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;
        state.previousElementType1 = null;

        //long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //state.error("ABORT");
            //break;
            //}

            state.previousElementType2 = state.previousElementType1;
            state.previousElementType1 = tokenType;
            tokenType = state.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (state.in(m_types.C_INTERPOLATION_EXPR)) {
                // special analysis when inside an interpolation string
                if (tokenType == m_types.JS_STRING_CLOSE) {
                    parseJsStringClose(state);
                } else if (tokenType == m_types.DOLLAR) {
                    if (state.is(m_types.C_INTERPOLATION_PART)) {
                        state.popEnd();
                        state.advance().mark(m_types.C_INTERPOLATION_REF);
                    } else if (state.is(m_types.C_INTERPOLATION_EXPR)) {
                        // first element
                        state.advance().mark(m_types.C_INTERPOLATION_REF);
                    }
                } else if (state.is(m_types.C_INTERPOLATION_REF)) {
                    state.advance().popEnd();
                } else if (!state.is(m_types.C_INTERPOLATION_PART)) {
                    state.mark(m_types.C_INTERPOLATION_PART);
                }
            } else {
                // special keywords that can be used as lower identifier in records
                //if (tokenType == m_types.REF/* && state.isCurrentResolution(recordBinding)*/) {
                //parseLIdent(state);
                //} else
                if (tokenType == m_types.METHOD /*&& state.isCurrentResolution(recordBinding)*/) {
                    parseLIdent(state);
                }
                //
                else if (tokenType == m_types.SEMI) {
                    parseSemi(state);
                } else if (tokenType == m_types.EQ) {
                    parseEq(state);
                } else if (tokenType == m_types.ARROW) {
                    parseArrow(state);
                } else if (tokenType == m_types.REF) {
                    parseRef(state);
                } else if (tokenType == m_types.OPTION) {
                    parseOption(state);
                } else if (tokenType == m_types.TRY) {
                    parseTry(state);
                } else if (tokenType == m_types.SWITCH) {
                    parseSwitch(state);
                } else if (tokenType == m_types.LIDENT) {
                    parseLIdent(state);
                } else if (tokenType == m_types.UIDENT) {
                    parseUIdent(state);
                } else if (tokenType == m_types.ARROBASE) {
                    parseArrobase(state);
                } else if (tokenType == m_types.PERCENT) {
                    parsePercent(state);
                } else if (tokenType == m_types.COLON) {
                    parseColon(state);
                } else if (tokenType == m_types.RAW) {
                    parseRaw(state);
                } else if (tokenType == m_types.STRING_VALUE) {
                    parseStringValue(state);
                } else if (tokenType == m_types.PIPE) {
                    parsePipe(state);
                } else if (tokenType == m_types.COMMA) {
                    parseComma(state);
                } else if (tokenType == m_types.AND) {
                    parseAnd(state);
                } else if (tokenType == m_types.FUN) {
                    parseFun(state);
                } else if (tokenType == m_types.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == m_types.IF) {
                    parseIf(state);
                } else if (tokenType == m_types.ELSE) {
                    parseElse(state);
                } else if (tokenType == m_types.THEN) {
                    parseThen(state);
                } else if (tokenType == m_types.DOT) {
                    parseDot(state);
                } else if (tokenType == m_types.DOTDOTDOT) {
                    parseDotDotDot(state);
                } else if (tokenType == m_types.WITH) {
                    parseWith(state);
                } else if (tokenType == m_types.TILDE) {
                    parseTilde(state);
                } else if (tokenType == m_types.QUESTION_MARK) {
                    parseQuestionMark(state);
                } else if (tokenType == m_types.UNDERSCORE) {
                    parseUnderscore(state);
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
                // [| ... |]
                else if (tokenType == m_types.LARRAY) {
                    parseLArray(state);
                } else if (tokenType == m_types.RARRAY) {
                    parseRArray(state);
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
                } else if (tokenType == m_types.TAG_LT_SLASH) {
                    parseLtSlash(state);
                } else if (tokenType == m_types.GT) {
                    parseGt(state);
                } else if (tokenType == m_types.TAG_AUTO_CLOSE) {
                    parseGtAutoClose(state);
                }
                // {| ... |}
                else if (tokenType == m_types.ML_STRING_OPEN) {
                    parseMlStringOpen(state);
                } else if (tokenType == m_types.ML_STRING_CLOSE) {
                    parseMlStringClose(state);
                }
                // {j| ... |j}
                else if (tokenType == m_types.JS_STRING_OPEN) {
                    parseJsStringOpen(state);
                }
                // Starts an expression
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
                } else if (tokenType == m_types.PUB) {
                    parsePub(state);
                } else if (tokenType == m_types.EXCEPTION) {
                    parseException(state);
                }
            }

            if (state.dontMove) {
                state.dontMove = false;
                // revert
                tokenType = state.previousElementType1;
                state.previousElementType1 = state.previousElementType2;
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
        if (state.is(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR)) {
            // ( |>_<| ...
            state.mark(m_types.C_FUN_PARAM);
        }
    }

    private void parseTilde(@NotNull ParserState state) {
        IElementType nextType = state.rawLookup(1);
        if (nextType == m_types.LIDENT) {
            if (!state.is(m_types.C_FUN_PARAM) && !state.is(m_types.C_SIG_ITEM)) {
                state.mark(m_types.C_FUN_PARAM);
            }
            state.mark(m_types.C_NAMED_PARAM).advance().wrapWith(m_types.C_LOWER_IDENTIFIER);
        }
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        if (state.inAny(m_types.C_TAG_START, m_types.C_TAG_PROP_VALUE)) {
            if (state.isFound(m_types.C_TAG_START)) {
                // <jsx |>?<|prop ...
                state.mark(m_types.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state))
                        .advance()
                        .remapCurrentToken(m_types.PROPERTY_NAME);
            }
        } else if (state.in(m_types.C_BINARY_CONDITION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance().mark(m_types.C_IF_THEN_SCOPE);
        } else if (state.in(m_types.C_FIELD_VALUE)) {
            // {"x": y |>?<| ...
            state.rollbackTo(state.getIndex() - 1)
                    .mark(m_types.C_TERNARY)
                    .mark(m_types.C_BINARY_CONDITION);
        } else if (!state.inAny(m_types.C_TERNARY, m_types.C_NAMED_PARAM)) {
            if (state.inScopeOr(m_types.C_LET_BINDING)) {
                state.rollbackTo(state.getIndex() - 1)
                        .mark(m_types.C_TERNARY)
                        .mark(m_types.C_BINARY_CONDITION);
            }
        }
    }

    private void parseRef(@NotNull ParserState state) {
        if (state.in(m_types.C_TAG_START)) {
            state.remapCurrentToken(m_types.PROPERTY_NAME)
                    .mark(m_types.C_TAG_PROPERTY);
        }
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(m_types.C_OPTION);
    }

    private void parseRaw(@NotNull ParserState state) {
        if (state.is(m_types.C_MACRO_NAME)) {
            // % |>raw<| ...
            state.advance().popEnd();
        }
    }

    private void parseIf(@NotNull ParserState state) {
        state.mark(m_types.C_IF);
    }

    private void parseThen(@NotNull ParserState state) {
        // if ... |>then<| ...
        state.popEndUntil(m_types.C_IF).advance()
                .mark(m_types.C_IF_THEN_SCOPE);
    }

    private void parseElse(@NotNull ParserState state) {
        // if ... then ... |>else<| ...
        state.popEndUntil(m_types.C_IF).advance()
                .mark(m_types.C_IF_THEN_SCOPE);
    }

    private void parseDot(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LBRACE && (state.is(m_types.C_JS_OBJECT))) { // Js object definition
            // ... { |>.<| ... }
            state.advance().mark(m_types.C_OBJECT_FIELD);
        }
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LBRACE) { // Mixin
            // { |>...<| x ...
            if (state.is(m_types.C_FUN_BODY)) {
                state.rollbackTo(state.getIndex())
                        .mark(m_types.C_FUN_BODY)
                        .markScope(m_types.C_RECORD_EXPR, m_types.LBRACE).advance()
                        .mark(m_types.C_MIXIN_FIELD);
            } else {
                state.updateComposite(m_types.C_RECORD_EXPR)
                        .mark(m_types.C_MIXIN_FIELD);
            }
        }
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.in(m_types.C_FUNCTOR_RESULT)) {
            // module M (X) : ( S |>with<| ... ) = ...
            state.popEndUntilFoundIndex().popEnd().advance()
                    .mark(m_types.C_CONSTRAINTS);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(m_types.C_ASSERT_STMT).advance();
    }

    private void parseFun(@NotNull ParserState state) {
        if (state.is(m_types.C_LET_BINDING)) { // fun keyword is equivalent to a switch body
            // let x = |>fun<| | ...
            state.mark(m_types.C_FUN_EXPR);
        }
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.in(m_types.C_CONSTRAINT)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
            state.popEndUntilFoundIndex().popEnd();
        } else {
            Marker latestScope = state.popEndUntilScope();

            if (latestScope.isCompositeType(m_types.C_TYPE_DECLARATION)) {
                state.advance().mark(m_types.C_TYPE_DECLARATION).setStart();
            } else if (latestScope.isCompositeType(m_types.C_LET_DECLARATION)) {
                state.advance().mark(m_types.C_LET_DECLARATION).setStart();
            } else if (isModuleResolution(latestScope)) {
                state.advance().mark(m_types.C_MODULE_DECLARATION).setStart();
            }
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.inScopeOrAny(m_types.C_RECORD_FIELD, m_types.C_OBJECT_FIELD, m_types.C_SIG_ITEM, m_types.C_MIXIN_FIELD,
                m_types.C_VARIANT_CONSTRUCTOR, m_types.C_PARAMETERS, m_types.C_SIG_EXPR)) {

            if (state.isFound(m_types.C_SIG_ITEM)) {
                state.popEndUntilFoundIndex();
                // double sig ? ~x:int,
                if (state.in(m_types.C_NAMED_PARAM, /*not*/m_types.C_SCOPED_EXPR)) {
                    state.popEndUntilFoundIndex().popEnd();
                }
                state.popEnd();
            } else if (state.isFound(m_types.C_SCOPED_EXPR) && state.isAtIndex(state.getIndex() + 1, m_types.C_LET_DECLARATION)) { // It must be a deconstruction
                // let ( a |>,<| b ) = ...
                // We need to do it again because lower symbols must be wrapped with identifiers
                state.rollbackTo(state.getIndex())
                        .markScope(m_types.C_DECONSTRUCTION, m_types.LPAREN).advance();
                return;
            } else if (state.isFound(m_types.C_SCOPED_EXPR) && state.isAtIndex(state.getIndex() + 1, m_types.C_FUN_PARAM)) { // It must be a deconstruction in parameters
                // { a |>,<| b } => ...
                // We need to do it again because lower symbols must be wrapped with identifiers
                state.rollbackTo(state.getIndex())
                        .markScope(m_types.C_DECONSTRUCTION, m_types.LBRACE).advance();
                return;
            }

            if (state.inAny(m_types.C_RECORD_FIELD, m_types.C_OBJECT_FIELD)) {
                boolean isRecord = state.isFound(m_types.C_RECORD_FIELD);
                state.popEndUntilFoundIndex().popEnd().advance();

                IElementType tokenType = state.getTokenType();
                if (tokenType != m_types.RBRACE && tokenType != m_types.LBRACKET) {
                    state.mark(isRecord ? m_types.C_RECORD_FIELD : m_types.C_OBJECT_FIELD);  // zzz: use field
                }
            } else if (state.in(m_types.C_MIXIN_FIELD)) {
                state.popEndUntilFoundIndex().popEnd().advance();
            } else if (state.in(m_types.C_DECONSTRUCTION)) {
                state.popEndUntilScope();
            } else if (state.inAny(m_types.C_VARIANT_CONSTRUCTOR, m_types.C_PARAMETERS)) {
                state.popEndUntilFoundIndex().advance();
                if (state.getTokenType() != m_types.RPAREN) {
                    // not at the end of a list: ie not => (p1, p2<,> )
                    state.mark(m_types.C_FUN_PARAM);
                }
            } else if (state.isDropped(m_types.C_SCOPED_EXPR) && state.isParent(m_types.C_SIG_EXPR)) {
                // type t = ( ... |>,<|
                state.advance().mark(m_types.C_SIG_ITEM);
            }
        }

    }

    private void parsePipe(@NotNull ParserState state) {
        if (state.is(m_types.C_TRY_HANDLERS)) {
            // try (...) { |>|<| ...
            state.mark(m_types.C_TRY_HANDLER);
        } else if (state.is(m_types.C_TYPE_BINDING)) {
            // type t = |>|<| ...
            state.popEndUntil(m_types.C_TYPE_BINDING).advance()
                    .mark(m_types.C_VARIANT_DECLARATION);
        } else if (state.in(m_types.C_VARIANT_DECLARATION)) {
            // type t = | X |>|<| Y ...
            state.popEndUntil(m_types.C_TYPE_BINDING).advance()
                    .mark(m_types.C_VARIANT_DECLARATION);
        } else if (state.in(m_types.C_PATTERN_MATCH_BODY)) {
            // can be a switchBody or a 'fun'
            if (!state.is(m_types.C_SWITCH_BODY)) {
                state.popEndUntil(m_types.C_PATTERN_MATCH_EXPR).popEnd().advance();
            }
            state.mark(m_types.C_PATTERN_MATCH_EXPR);
        } else {
            if (!state.is(m_types.C_SWITCH_BODY) && state.in(m_types.C_PATTERN_MATCH_EXPR, /*not*/m_types.C_PATTERN_MATCH_BODY)) { // pattern grouping
                // | X |>|<| Y => ...
                state.popEndUntilIndex(state.getIndex()).popEnd();
            }

            if (state.isScopeTokenElementType(m_types.LBRACKET) && state.isParent(m_types.C_TYPE_BINDING)) {
                // type t = [ |>|<| ...
                state.advance().mark(m_types.C_VARIANT_DECLARATION);
            } else { // By default, a pattern match
                state.advance().mark(m_types.C_PATTERN_MATCH_EXPR);
            }
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        if (state.is(m_types.C_MACRO_EXPR)) {
            // [%raw |>"x"<| ...
            state.wrapWith(m_types.C_MACRO_BODY);
        } else if (state.in(m_types.C_MACRO_NAME)) {
            // [@bs |>"x<| ...
            state.popEndUntilScope();
        } else if (state.is(m_types.C_JS_OBJECT)) {
            state.mark(m_types.C_OBJECT_FIELD).wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_OBJECT_FIELD)) {
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        }
    }

    private void parseMlStringOpen(@NotNull ParserState state) {
        if (state.is(m_types.C_MACRO_EXPR)) {
            state.mark(m_types.C_MACRO_BODY);
        }

        state.markScope(m_types.C_ML_INTERPOLATOR, m_types.ML_STRING_OPEN);
    }

    private void parseMlStringClose(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(m_types.ML_STRING_OPEN);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseJsStringOpen(@NotNull ParserState state) {
        state.markScope(m_types.C_INTERPOLATION_EXPR, m_types.JS_STRING_OPEN);
    }

    private void parseJsStringClose(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(m_types.JS_STRING_OPEN);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLet(@NotNull ParserState state) {
        state.mark(m_types.C_LET_DECLARATION).setStart();
    }

    private void parseVal(@NotNull ParserState state) {
        if (!state.in(m_types.C_MACRO_NAME)) {
            state.popEndUntilScope();
            if (state.is(m_types.C_OBJECT)) {
                state.mark(m_types.C_CLASS_FIELD);
            }
        }
    }

    private void parsePub(@NotNull ParserState state) {
        if (state.in(m_types.C_OBJECT)) {
            state.popEndUntil(m_types.C_OBJECT).
                    mark(m_types.C_CLASS_METHOD);
        }
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.in(m_types.C_MACRO_NAME)) {
            state.popEndUntilScope();
            state.mark(m_types.C_MODULE_DECLARATION).setStart();
        }
    }

    private void parseException(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_EXCEPTION_DECLARATION).setStart();
    }

    private void parseClass(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_CLASS_DECLARATION).setStart();
    }

    private void parseType(@NotNull ParserState state) {
        if (state.is(m_types.C_CONSTRAINTS)) {
            // module M = (X) : ( S with |>type<| ... ) = ...
            state.mark(m_types.C_CONSTRAINT);
        } else if (!state.is(m_types.C_MODULE_DECLARATION) && !state.is(m_types.C_CLASS_DECLARATION)) {
            state.popEndUntilScope();
            state.mark(m_types.C_TYPE_DECLARATION).setStart();
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_EXTERNAL_DECLARATION).setStart();
    }

    private void parseOpen(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_OPEN);
    }

    private void parseInclude(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_INCLUDE);
    }

    private void parsePercent(@NotNull ParserState state) {
        if (state.is(m_types.C_MACRO_EXPR)) {
            state.mark(m_types.C_MACRO_NAME);
        } else if (state.in(m_types.C_LET_DECLARATION)) {
            // let name|>%<|private = ...
            state.mark(m_types.C_LET_ATTR);
        } else {
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == m_types.RAW) {
                // |>%<| raw ...
                state.mark(m_types.C_MACRO_EXPR).
                        mark(m_types.C_MACRO_NAME);
            }
        }
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.inAny(m_types.C_RECORD_FIELD, m_types.C_OBJECT_FIELD)) {
            state.advance();
            if (state.in(m_types.C_TYPE_BINDING)) {
                state.mark(m_types.C_SIG_EXPR)
                        .mark(m_types.C_SIG_ITEM);
            } else {
                state.mark(m_types.C_FIELD_VALUE);
            }
        } else if (state.isCurrent(m_types.C_MODULE_DECLARATION)) {
            // module M |> :<| ...
            state.advance();
            boolean isParen = state.getTokenType() == m_types.LPAREN;
            if (isParen) {
                // module M : |>(<| ...
                state.advance();
            }
            state.mark(m_types.C_MODULE_TYPE).updateScopeToken(isParen ? m_types.LPAREN : null);
        } else if (state.in(m_types.C_TERNARY)) {
            // x ? y |> :<| ...
            state.popEndUntilFoundIndex()
                    .advance().mark(m_types.C_IF_THEN_SCOPE);
        } else if (state.isParent(m_types.C_EXTERNAL_DECLARATION)) {
            // external e |> :<| ...
            state.advance().mark(m_types.C_SIG_EXPR);
            if (state.getTokenType() == m_types.LPAREN) {
                state.markDummyScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).advance();
            }
            state.mark(m_types.C_SIG_ITEM);
        } else if (state.isParent(m_types.C_LET_DECLARATION)) {
            // let e |> :<| ...
            state.advance().mark(m_types.C_SIG_EXPR);
            if (state.getTokenType() == m_types.LPAREN) {
                state.markDummyScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).advance();
            }
            state.mark(m_types.C_SIG_ITEM);
        } else if (state.inAny(m_types.C_NAMED_PARAM, m_types.C_FUN_PARAM)) {
            // let x = (~y |> : <| ...
            state.advance().
                    mark(m_types.C_SIG_EXPR).markOptionalParenDummyScope(m_types).
                    mark(m_types.C_SIG_ITEM);
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        if (state.is(m_types.C_ANNOTATION)) {
            state.mark(m_types.C_MACRO_NAME);
        }
    }

    private void parseLt(@NotNull ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = state.rawLookup(1);
        // Note that option is a ReasonML keyword but also a JSX keyword !
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT || nextTokenType == m_types.OPTION) {
            // Surely a tag
            state.mark(m_types.C_TAG)
                    .markScope(m_types.C_TAG_START, m_types.LT)
                    .advance()
                    .remapCurrentToken(m_types.TAG_NAME)
                    .wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        } else if (nextTokenType == m_types.GT) {
            // a React fragment start
            state.mark(m_types.C_TAG)
                    .mark(m_types.C_TAG_START)
                    .advance().advance().popEnd();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        // Note that option is a ReasonML keyword but also a JSX keyword !
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT || nextTokenType == m_types.OPTION) {
            // A closing tag
            if (state.in(m_types.C_TAG_BODY)) {
                state.popEndUntil(m_types.C_TAG);
            }

            state.remapCurrentToken(m_types.TAG_LT_SLASH)
                    .mark(m_types.C_TAG_CLOSE)
                    .advance()
                    .remapCurrentToken(m_types.TAG_NAME)
                    .wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        } else if (nextTokenType == m_types.GT) {
            // a React fragment end
            state.remapCurrentToken(m_types.TAG_LT_SLASH)
                    .mark(m_types.C_TAG_CLOSE)
                    .advance().advance().popEnd();
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.is(m_types.C_TAG_PROP_VALUE)) {
            // ?prop=value |> > <| ...
            state.popEnd().popEnd();
        } else if (state.is(m_types.C_TAG_PROPERTY)) {
            // ?prop |> > <| ...
            state.popEnd();
        }

        if (state.inScopeOrAny(m_types.C_TAG_PROP_VALUE, m_types.C_TAG_START)) {
            state.advance().popEndUntilIndex(state.getIndex());
            if (state.is(m_types.C_TAG_START)) {
                state.popEnd().mark(m_types.C_TAG_BODY);
            }
        } else if (state.in(m_types.C_TAG_CLOSE)) {
            // end the tag
            state.advance().popEndUntil(m_types.C_TAG).popEnd();
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.advance().popEnd(/*tag_start*/).popEnd(/*tag*/);
    }

    private void parseLIdent(@NotNull ParserState state) {
        if (state.is(m_types.C_LET_DECLARATION)) {
            // let |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_TYPE_DECLARATION)) {
            // type |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
            // external |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_CLASS_DECLARATION)) {
            // class |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_RECORD_EXPR)) {
            // let x = { |>y<| ...
            state.mark(m_types.C_RECORD_FIELD).wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_RECORD_FIELD)) {
            // let x = { y, |>z<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if ((state.is(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR)) || state.is(m_types.C_VARIANT_CONSTRUCTOR)) {
            // ( x , |>y<| ...
            state.mark(m_types.C_FUN_PARAM).wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.inAny(m_types.C_TAG_START, m_types.C_TAG_PROP_VALUE)) {
            if (state.previousElementType1 != m_types.LT && state.isFound(m_types.C_TAG_START)) {
                // This is a property
                state.popEndUntilScope();
                state.remapCurrentToken(m_types.PROPERTY_NAME)
                        .mark(m_types.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state));
            }
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.is(m_types.C_SCOPED_EXPR) && state.isScopeTokenElementType(m_types.LBRACE) && nextElementType == m_types.COLON) {
                // this is a record usage ::  { |>x<| : ...
                state.updateComposite(m_types.C_RECORD_EXPR)
                        .mark(m_types.C_RECORD_FIELD)
                        .wrapWith(m_types.C_LOWER_IDENTIFIER);
            } else if (nextElementType == m_types.LPAREN && !state.inAny(m_types.C_TYPE_BINDING, m_types.C_CONSTRAINT, m_types.C_SIG_ITEM)) {
                state.mark(m_types.C_FUN_CALL)
                        .wrapWith(m_types.C_LOWER_SYMBOL);
            } else if (state.is(m_types.C_DECONSTRUCTION)) {
                state.wrapWith(m_types.C_LOWER_IDENTIFIER);
            } else {
                state.wrapWith(m_types.C_LOWER_SYMBOL);
            }
        }
    }

    private void parseLArray(@NotNull ParserState state) {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LARRAY);
    }

    private void parseRArray(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(m_types.LARRAY);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);

        if (nextTokenType == m_types.ARROBASE) {
            // |>[ <| @ ...
            state.markScope(m_types.C_ANNOTATION, m_types.LBRACKET);
        } else if (nextTokenType == m_types.PERCENT) {
            // |>[ <| % ...
            state.markScope(m_types.C_MACRO_EXPR, m_types.LBRACKET);
        } else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) { // Local open
            // M.|>(<| ...
            state.markScope(m_types.C_LOCAL_OPEN, m_types.LBRACKET);
        } else {
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
        }
    }

    private void parseRBracket(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(m_types.LBRACKET);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseBracketGt(@NotNull ParserState state) {
        state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.DOT && state.previousElementType2 == m_types.UIDENT) { // Local open a js object or a record
            // Xxx.|>{<| ... }
            state.mark(m_types.C_LOCAL_OPEN);
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == m_types.LIDENT) {
                state.markScope(m_types.C_RECORD_EXPR, m_types.LBRACE);
            } else {
                state.markScope(m_types.C_JS_OBJECT, m_types.LBRACE);
            }
        } else if (state.is(m_types.C_TYPE_BINDING)) {
            boolean isJsObject = state.lookAhead(1) == m_types.DOT;
            state.markScope(isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE);
        } else if (state.is(m_types.C_TRY_EXPR)) { // A try expression
            // try (..) |>{<| .. }
            state.markScope(m_types.C_TRY_HANDLERS, m_types.LBRACE);
        } else if (state.is(m_types.C_MODULE_BINDING)) {
            // module M = |>{<| ...
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.is(m_types.C_FUNCTOR_BINDING)) {
            // module M = (...) => |>{<| ...
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.is(m_types.C_IF)) {
            state.markScope(m_types.C_IF_THEN_SCOPE, m_types.LBRACE);
        } else if (state.is(m_types.C_MODULE_TYPE)) {
            // module M : |>{<| ...
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.in(m_types.C_CLASS_DECLARATION)) {
            // class x = |>{<| ... }
            state.markScope(m_types.C_OBJECT, m_types.LBRACE);
        } else if (state.is(m_types.C_SWITCH_EXPR)) {
            state.markScope(m_types.C_SWITCH_BODY, m_types.LBRACE);
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (nextElement == m_types.STRING_VALUE || nextElement == m_types.DOT) { // js object detected (in usage)
                // |>{<| "x" ... }
                state.markScope(m_types.C_JS_OBJECT, m_types.LBRACE);
            } else if (state.is(m_types.C_FUN_BODY) && !state.isScopeTokenElementType(m_types.LBRACE) && nextElement != m_types.LIDENT) { // function body
                // x => |>{<| ... }
                state.updateScopeToken(m_types.LBRACE);
            } else {
                state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE);
            }
        }
    }

    private void parseRBrace(@NotNull ParserState state) {
        Marker scope = state.popEndUntilOneOfElementType(m_types.LBRACE, m_types.RECORD, m_types.SWITCH);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }

        if (state.is(m_types.C_TAG_PROP_VALUE)) {
            state.popEndUntil(m_types.C_TAG_PROPERTY).popEnd();
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.is(m_types.C_PARAMETERS) && !state.hasScopeToken()) {
            // |>(<| ... ) => ...
            state.updateScopeToken(m_types.LPAREN);
        } else if (state.is(m_types.C_ASSERT_STMT)) {
            // assert |>(<| ...
            state.markScope(m_types.C_BINARY_CONDITION, m_types.LPAREN);
        } else if (state.is(m_types.C_TRY_EXPR)) {
            // try |>(<| ...
            state.markScope(m_types.C_TRY_BODY, m_types.LPAREN);
        } else if (state.is(m_types.C_IF) || state.is(m_types.C_SWITCH_EXPR)) {
            // if |>(<| ...  OR  switch |>(<| ...
            state.markScope(m_types.C_BINARY_CONDITION, m_types.LPAREN);
        } else if (state.is(m_types.C_LET_DECLARATION)) { // Overloading operator OR deconstructing a term
            //  let |>(<| + ) =
            //  let |>(<| a, b ) =
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) { // Local open
            // M.|>(<| ...
            state.markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN);
        } else if (state.is(m_types.C_MODULE_BINDING) && !state.in(m_types.C_FUNCTOR_DECLARATION)) { // This is a functor
            // module M = |>(<| .. )
            int moduleIndex = state.indexOfComposite(m_types.C_MODULE_DECLARATION);
            state.rollbackTo(moduleIndex - 1)
                    .updateComposite(m_types.C_FUNCTOR_DECLARATION);
        } else if (state.inAny(m_types.C_FUNCTOR_DECLARATION, m_types.C_FUNCTOR_CALL, m_types.C_FUNCTOR_RESULT)) {
            // module M = |>(<| ...
            // module M = ( ... ) : |>(<| ...
            if (state.isFound(m_types.C_FUNCTOR_DECLARATION) || state.isFound(m_types.C_FUNCTOR_CALL)) {
                state.markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance()
                        .mark(m_types.C_FUNCTOR_PARAM);
            }
        } else if (state.in(m_types.C_VARIANT_DECLARATION)) { // Variant params
            // type t = | Variant |>(<| .. )
            state.markScope(m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN).advance()
                    .mark(m_types.C_FUN_PARAM);
        } else if (state.inAny(m_types.C_CLASS_DECLARATION, m_types.C_OBJECT)) {
            if (state.isFound(m_types.C_CLASS_DECLARATION)) {
                state.popEndUntil(m_types.C_CLASS_DECLARATION).
                        markScope(m_types.C_CLASS_CONSTR, m_types.LPAREN);
            }
        } else if (state.in(m_types.C_FUN_CALL)
                && !(state.is(m_types.C_TYPE_DECLARATION)
                || state.inAny(m_types.C_TYPE_BINDING, m_types.C_SIG_ITEM))) { // calling a function
            state.markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance();
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.RPAREN) {
                state.mark(m_types.C_FUN_PARAM);
            }
        } else if (state.inAny(m_types.C_OPEN, m_types.C_INCLUDE)) { // a functor call inside open/include
            // open/include M |>(<| ...
            state.markBefore(state.getIndex() - 1, m_types.C_FUNCTOR_CALL)
                    .markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance()
                    .mark(m_types.C_FUN_PARAM);
        } else {
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        // Removing intermediate resolutions
        Marker parenScope = state.popEndUntilScopeToken(m_types.LPAREN);
        state.advance();

        state.popEnd();
        if (parenScope != null) {
            IElementType nextTokenType = state.getTokenType();

            if (state.is(m_types.C_FUN_CALL)) {
                state.popEnd();
            } else if (state.isParent(m_types.C_FUNCTOR_DECLARATION)) {
                if (nextTokenType == m_types.COLON) {
                    // module M = (P) |> :<| R ...
                    state.advance();
                    if (state.getTokenType() == m_types.LPAREN) {
                        state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).advance();
                    }
                    state.mark(m_types.C_FUNCTOR_RESULT);
                } else if (nextTokenType == m_types.ARROW) {
                    // module M = (P) |>=><| ...
                    state.advance().mark(m_types.C_FUNCTOR_BINDING);
                }
            } else if (state.is(m_types.C_TAG_PROP_VALUE)) {
                state.popEnd().popEnd();
            }
        }
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.is(m_types.C_TAG_PROPERTY)) {
            // <X p|> =<| ...
            state.advance().mark(m_types.C_TAG_PROP_VALUE);
        } else if (state.isParent(m_types.C_MODULE_DECLARATION)) {
            // module M |> =<| ...
            state.advance().mark(m_types.C_MODULE_BINDING);
        } else if (state.isParent(m_types.C_NAMED_PARAM)) {
            // ( ~x |> =<| ...
            state.advance().mark(m_types.C_DEFAULT_VALUE);
        } else if (state.in(m_types.C_TYPE_DECLARATION)) {
            // type t |> =<| ...
            state.advance().mark(m_types.C_TYPE_BINDING);
        } else if (state.in(m_types.C_MODULE_TYPE)) {
            // module M : T |> =<| ...
            state.popEndUntilIndex(state.getIndex()).popEnd().advance()
                    .mark(m_types.C_MODULE_BINDING);
        } else if (state.inScopeOrAny(m_types.C_LET_DECLARATION, m_types.C_SIG_EXPR) || state.is(m_types.C_LET_ATTR)) {
            if (state.isFound(m_types.C_SIG_EXPR)) {
                state.popEndUntil(m_types.C_SIG_EXPR).popEnd();
                if (state.in(m_types.C_NAMED_PARAM)) {
                    state.advance().mark(m_types.C_DEFAULT_VALUE);
                } else if (state.in(m_types.C_LET_DECLARATION)) {
                    // let ... |> =<| ...
                    state.popEndUntilStart().advance()
                            .mark(m_types.C_LET_BINDING);
                }
            } else if (state.in(m_types.C_LET_DECLARATION)) {
                // let ... |> =<| ...
                state.popEndUntilStart().advance()
                        .mark(m_types.C_LET_BINDING);
            }
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        if (state.in(m_types.C_PATTERN_MATCH_BODY)) {
            if (state.in(m_types.C_FUN_EXPR)) { // Special case for the `fun` keyword
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

        if (state.is(m_types.C_MODULE_DECLARATION) || state.is(m_types.C_FUNCTOR_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_VARIANT_DECLARATION)) {
            // type t = | |>X<| ..
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_EXCEPTION_DECLARATION)) {
            // exception |>E<| ..
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_PATTERN_MATCH_EXPR)) {
            state.wrapWith(m_types.C_UPPER_SYMBOL);
        } else {
            if ((state.in(m_types.C_TAG_START) || state.in(m_types.C_TAG_CLOSE))
                    && state.previousElementType1 == m_types.DOT) {
                // a namespaced custom component ::  <X.|>Y<| ...
                state.remapCurrentToken(m_types.TAG_NAME);
            } else {
                IElementType nextElementType = state.lookAhead(1);


                if (state.is(m_types.C_TYPE_BINDING) && nextElementType != m_types.DOT) {
                    // We are declaring a variant without a pipe before ::  type t = |>X<| | ...
                    state.mark(m_types.C_VARIANT_DECLARATION).wrapWith(m_types.C_UPPER_IDENTIFIER);
                    return;
                } else if (state.isCurrent(m_types.C_MODULE_BINDING) && nextElementType == m_types.LPAREN) {
                    // functor call ::  |>X<| ( ...
                    // functor call with path :: A.B.|>X<| ( ...
                    state.getCurrentMarker().drop();
                    state.mark(m_types.C_FUNCTOR_CALL);
                }
            }

            state.wrapWith(m_types.C_UPPER_SYMBOL);
        }
    }

    private void parseSwitch(@NotNull ParserState state) {
        state.mark(m_types.C_SWITCH_EXPR);
    }

    private void parseTry(@NotNull ParserState state) {
        state.mark(m_types.C_TRY_EXPR);
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.is(m_types.C_SIG_EXPR)) {
            state.advance()
                    .mark(m_types.C_SIG_ITEM);
        } else if (state.in(m_types.C_SIG_ITEM, /*not*/ m_types.C_SCOPED_EXPR)) {
            state.popEndUntil(m_types.C_SIG_ITEM).popEnd().advance()
                    .mark(m_types.C_SIG_ITEM);
        } else if (state.in(m_types.C_FUNCTOR_RESULT)) {
            // module Make = (M) : R |>=><| ...
            state.popEndUntilFoundIndex().popEnd()
                    .advance().mark(m_types.C_FUNCTOR_BINDING);
        } else if (state.inScopeOrAny(m_types.C_LET_BINDING, m_types.C_PATTERN_MATCH_EXPR, m_types.C_PARAMETERS,
                m_types.C_FUN_PARAM, m_types.C_FUN_EXPR, m_types.C_FIELD_VALUE)) {
            if (state.isFound(m_types.C_FUN_PARAM)) {
                int paramIndex = state.getIndex();
                if (state.inAny(m_types.C_FUN_EXPR, m_types.C_FUN_CALL)) {
                    if (state.isFound(m_types.C_FUN_EXPR)) {
                        // x |>=><| ...
                        state.popEndUntil(m_types.C_FUN_EXPR)
                                .advance().mark(m_types.C_FUN_BODY);
                    } else {
                        // call(x |>=><| ...
                        state.rollbackTo(paramIndex);
                        state.mark(m_types.C_FUN_PARAM)
                                .mark(m_types.C_FUN_EXPR)
                                .mark(m_types.C_PARAMETERS);
                    }
                }
            } else if (state.isFound(m_types.C_LET_BINDING)) {
                // function parameters ::  |>x<| => ...
                state.rollbackTo(state.getIndex()).mark(m_types.C_LET_BINDING)
                        .mark(m_types.C_FUN_EXPR)
                        .mark(m_types.C_PARAMETERS);
            } else if (state.isFound(m_types.C_FIELD_VALUE)) {
                // function parameters ::  |>x<| => ...
                state.rollbackTo(state.getIndex()).mark(m_types.C_FIELD_VALUE)
                        .mark(m_types.C_FUN_EXPR)
                        .mark(m_types.C_PARAMETERS);
            } else if (state.isFound(m_types.C_PARAMETERS) || state.isFound(m_types.C_FUN_EXPR)) {
                state.popEndUntilOneOf(m_types.C_PARAMETERS, m_types.C_FUN_EXPR);
                if (state.isParent(m_types.C_FUN_EXPR) || state.isParent(m_types.C_FUN_CALL)) {
                    state.popEnd();
                }
                state.advance().mark(m_types.C_FUN_BODY);
            } else {
                state.advance().mark(m_types.C_PATTERN_MATCH_BODY);
            }
        }
    }
}
