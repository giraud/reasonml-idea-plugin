package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;

public class ResParser extends CommonParser<ResTypes> {
    ResParser(boolean isSafe) {
        super(isSafe, ResTypes.INSTANCE);
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
                    LOG.error("CANCEL RESCRIPT PARSING");
                    break;
                }
            }

            // Special analyse when inside an interpolation string
            if (state.is(myTypes.C_INTERPOLATION_EXPR)
                    || state.is(myTypes.C_INTERPOLATION_PART)
                    || state.is(myTypes.C_INTERPOLATION_REF)) {
                if (tokenType == myTypes.JS_STRING_OPEN) {
                    state.popEndUntil(myTypes.C_INTERPOLATION_EXPR).advance().popEnd();
                } else if (tokenType == myTypes.DOLLAR) {
                    if (state.is(myTypes.C_INTERPOLATION_PART)) {
                        state.popEnd();
                    }
                    state.advance();
                    IElementType nextElement = state.getTokenType();
                    if (nextElement == myTypes.LBRACE) {
                        state.advance().markScope(myTypes.C_INTERPOLATION_REF, myTypes.LBRACE);
                    }
                } else if (state.is(myTypes.C_INTERPOLATION_REF) && tokenType == myTypes.RBRACE) {
                    state.popEnd().advance();
                    if (state.getTokenType() != myTypes.JS_STRING_OPEN) {
                        state.mark(myTypes.C_INTERPOLATION_PART);
                    }
                }
            } else {
                // EOL is a statement separator
                if (tokenType == myTypes.EOL) {
                    // We identify it at the lexer level, because We try to detect current construction that can't have
                    // EOL in it ; but we don't want to have it in the parsed tree, we change it to a whitespace
                    builder.remapCurrentToken(TokenType.WHITE_SPACE);

                    if (state.is(myTypes.C_FUNCTOR_CALL/*PsiOpen is potential functor call*/) && state.isParent(myTypes.C_OPEN)) {
                        state.popEnd().popEnd();
                    } else if (state.isParent(myTypes.C_ANNOTATION) && !state.is(myTypes.C_SCOPED_EXPR)) {
                        // inside an annotation
                        state.popEnd().popEnd();
                    } else if (state.is(myTypes.C_LET_BINDING)) {
                        // simple let with no scope
                        state.popEnd().popEnd();
                    }
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
                } else if (tokenType == myTypes.CATCH) {
                    parseCatch(state);
                } else if (tokenType == myTypes.SWITCH) {
                    parseSwitch(state);
                } else if (tokenType == myTypes.TAG_NAME) {
                    parseTagName(state);
                } else if (tokenType == myTypes.LIDENT) {
                    parseLIdent(state);
                } else if (tokenType == myTypes.UIDENT) {
                    parseUIdent(state);
                } else if (tokenType == myTypes.POLY_VARIANT) {
                    parsePolyVariant(state);
                } else if (tokenType == myTypes.ARROBASE) {
                    parseArrobase(state);
                } else if (tokenType == myTypes.PERCENT) {
                    parsePercent(state);
                } else if (tokenType == myTypes.COLON) {
                    parseColon(state);
                } else if (tokenType == myTypes.STRING_VALUE) {
                    parseStringValue(state);
                } else if (tokenType == myTypes.PIPE) {
                    parsePipe(state);
                } else if (tokenType == myTypes.COMMA) {
                    parseComma(state);
                } else if (tokenType == myTypes.AND) {
                    parseAnd(state);
                } else if (tokenType == myTypes.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == myTypes.DOTDOTDOT) {
                    parseDotDotDot(state);
                } else if (tokenType == myTypes.QUESTION_MARK) {
                    parseQuestionMark(state);
                } else if (tokenType == myTypes.TILDE) {
                    parseTilde(state);
                } else if (tokenType == myTypes.UNDERSCORE) {
                    parseUnderscore(state);
                } else if (tokenType == myTypes.INT_VALUE || tokenType == myTypes.FLOAT_VALUE) {
                    parseNumeric(state);
                }
                // if ... else
                else if (tokenType == myTypes.IF) {
                    parseIf(state);
                } else if (tokenType == myTypes.ELSE) {
                    parseElse(state);
                }
                // ( ... )
                else if (tokenType == myTypes.LPAREN) {
                    parseLParen(state);
                } else if (tokenType == myTypes.RPAREN) {
                    parseRParen(state);
                }
                // { ... }
                // list{ ... }
                else if (tokenType == myTypes.LBRACE) {
                    parseLBrace(state);
                } else if (tokenType == myTypes.LIST) {
                    parseList(state);
                } else if (tokenType == myTypes.RBRACE) {
                    parseRBrace(state);
                }
                // [ ... ]
                else if (tokenType == myTypes.LBRACKET) {
                    parseLBracket(state);
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
                // j` ... `
                else if (tokenType == myTypes.JS_STRING_OPEN) {
                    parseTemplateStringOpen(state);
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
                } else if (tokenType == myTypes.LET) {
                    parseLet(state);
                } else if (tokenType == myTypes.EXCEPTION) {
                    parseException(state);
                }
            }

            if (state.dontMove) {
                state.dontMove = false;
            } else {
                builder.advanceLexer();
            }
        }

        state.popEndUntilScope();
    }

    private void parseList(@NotNull ParserState state) {
        if (state.lookAhead(1) == myTypes.LBRACE) {
            // |>list<| { ... }
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE)
                    .advance().advance()
                    .markDummy(myTypes.C_DUMMY_COLLECTION_ITEM); // Needed to rollback to individual item in collection
        }
    }

    private void parseNumeric(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS)) {
            state.mark(myTypes.C_FUN_PARAM);
        }
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS)/* && state.isParent(m_types.C_FUN_EXPR)*/) {
            state.mark(myTypes.C_FUN_PARAM);
        }
    }

    private void parseRef(@NotNull ParserState state) {
        if (state.is(myTypes.C_RECORD_EXPR)) {
            // { |>x<| ...
            state.mark(myTypes.C_RECORD_FIELD).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.strictlyIn(myTypes.C_TAG_START)) {
            state.remapCurrentToken(myTypes.PROPERTY_NAME).mark(myTypes.C_TAG_PROPERTY);
        }
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(myTypes.C_OPTION);
    }

    private void parseIf(@NotNull ParserState state) {
        state.mark(myTypes.C_IF)
                .advance().mark(myTypes.C_BINARY_CONDITION);
    }

    private void parseElse(@NotNull ParserState state) {
        // if ... |>else<| ...
        state.popEndUntil(myTypes.C_IF)
                .advance().mark(myTypes.C_IF_THEN_SCOPE);
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        if (state.previousElementType(1) == myTypes.LBRACE) { // Mixin
            // ... { |>...<| x ...
            state.updateComposite(myTypes.C_RECORD_EXPR)
                    .mark(myTypes.C_MIXIN_FIELD);
        }
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        if (state.strictlyInAny(myTypes.C_TAG_START, myTypes.C_TAG_PROP_VALUE)) {
            // <jsx |>?<|prop ...
            if (state.isFound(myTypes.C_TAG_START)) {
                state.mark(myTypes.C_TAG_PROPERTY)
                        .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state))
                        .advance()
                        .remapCurrentToken(myTypes.PROPERTY_NAME);
            }
        } else if (state.isDone(myTypes.C_BINARY_CONDITION) || state.strictlyIn(myTypes.C_BINARY_CONDITION)) { // a ternary in progress
            // ... |>?<| ...
            state.popEndUntilFoundIndex().end()
                    .advance()
                    .mark(myTypes.C_IF_THEN_SCOPE);
        } else if (!state.inAny(myTypes.C_TERNARY, myTypes.C_NAMED_PARAM)) {
            if (state.inScopeOrAny(myTypes.C_LET_BINDING, myTypes.C_FIELD_VALUE, myTypes.C_FUN_BODY,
                    myTypes.C_FUN_PARAM, myTypes.C_FUNCTOR_PARAM, myTypes.C_DUMMY_COLLECTION_ITEM)) {
                // a new ternary
                state.rollbackToFoundIndex()
                        .mark(myTypes.C_TERNARY)
                        .mark(myTypes.C_BINARY_CONDITION);
            }
        }
    }

    private void parseTilde(@NotNull ParserState state) {
        if (state.is(myTypes.C_PARAMETERS)) {
            state.mark(myTypes.C_FUN_PARAM)
                    .mark(myTypes.C_NAMED_PARAM).advance()
                    .wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.strictlyIn(myTypes.C_SIG_EXPR) && !state.is(myTypes.C_SIG_ITEM)) {
            state.mark(myTypes.C_SIG_ITEM)
                    .mark(myTypes.C_NAMED_PARAM).advance()
                    .wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else {
            state.mark(myTypes.C_NAMED_PARAM).advance()
                    .wrapWith(myTypes.C_LOWER_IDENTIFIER);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(myTypes.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        //noinspection StatementWithEmptyBody
        if (state.is(myTypes.C_CONSTRAINT)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
        } else if (state.strictlyIn(myTypes.C_TYPE_DECLARATION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(myTypes.C_TYPE_DECLARATION);
        } else if (state.strictlyIn(myTypes.C_MODULE_DECLARATION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(myTypes.C_MODULE_DECLARATION);
        } else if (state.strictlyIn(myTypes.C_LET_DECLARATION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(myTypes.C_LET_DECLARATION);
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.strictlyIn(myTypes.C_SIG_EXPR)) {  // remove intermediate signatures
            if (state.isAtIndex(state.getIndex() - 1, myTypes.C_SCOPED_EXPR)) { // a dummy scope
                state.popEndUntilIndex(state.getIndex() - 1);
            } else {
                state.popEndUntilFoundIndex();
            }
            if (state.strictlyIn(myTypes.C_SIG_ITEM)) {
                state.popEndUntilFoundIndex().popEnd();
            }
        }

        if (state.strictlyIn(myTypes.C_SCOPED_EXPR) && state.isAtIndex(state.getIndex() + 1, myTypes.C_LET_DECLARATION)) {
            // It must be a deconstruction ::  let ( a |>,<| b ) = ...
            // We need to do it again because lower symbols must be wrapped with identifiers
            state.rollbackTo(state.getIndex())
                    .markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN).advance();
        }
        // Same priority
        else if (state.inScopeOrAny(
                myTypes.C_FUN_PARAM, myTypes.C_DECONSTRUCTION, myTypes.C_RECORD_FIELD, myTypes.C_MIXIN_FIELD,
                myTypes.C_OBJECT_FIELD, myTypes.C_DUMMY_COLLECTION_ITEM
        )) {

            if (state.isFound(myTypes.C_DUMMY_COLLECTION_ITEM)) {
                state.popEndUntilFoundIndex().popEnd()
                        .advance().markDummy(myTypes.C_DUMMY_COLLECTION_ITEM);
            } else if (state.isFound(myTypes.C_FUN_PARAM)) {
                state.popEndUntilFoundIndex().popEnd().advance();
                if (state.getTokenType() != myTypes.RPAREN) {
                    // not at the end of a list: ie not => (p1, p2<,> )
                    state.mark(myTypes.C_FUN_PARAM);
                }
            } else if (state.isFound(myTypes.C_DECONSTRUCTION)) {
                state.popEndUntilScope();
            } else if (state.isFound(myTypes.C_RECORD_FIELD) || state.isFound(myTypes.C_MIXIN_FIELD) || state.isFound(myTypes.C_OBJECT_FIELD)) {
                state.popEndUntilFoundIndex().popEnd(); //.advance();
            }

        }
    }

    private void parsePipe(@NotNull ParserState state) {
        if (state.is(myTypes.C_TYPE_BINDING)) {
            // type x = |>|<| ...
            state.advance().mark(myTypes.C_VARIANT_DECLARATION);
        } else if (state.is(myTypes.C_TRY_HANDLERS)) { // Start of a try handler
            // try (...) { |>|<| ... }
            state.advance().mark(myTypes.C_TRY_HANDLER);
        } else if (state.is(myTypes.C_SWITCH_BODY)) {
            // switch x { |>|<| ... }
            state.advance().mark(myTypes.C_PATTERN_MATCH_EXPR);
        } else if (state.strictlyIn(myTypes.C_PATTERN_MATCH_BODY)) {
            // can be a switchBody or a 'fun'
            state.popEndUntil(myTypes.C_PATTERN_MATCH_EXPR).popEnd()
                    .advance()
                    .mark(myTypes.C_PATTERN_MATCH_EXPR);
        } else if (state.strictlyIn(myTypes.C_PATTERN_MATCH_EXPR)) { // pattern grouping
            // | X |>|<| Y => ...
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(myTypes.C_PATTERN_MATCH_EXPR);
        } else if (state.strictlyIn(myTypes.C_VARIANT_DECLARATION)) {
            // type t = | X |>|<| Y ...
            state.popEndUntil(myTypes.C_VARIANT_DECLARATION).popEnd()
                    .advance()
                    .mark(myTypes.C_VARIANT_DECLARATION);
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        if (state.is(myTypes.C_JS_OBJECT)) {
            state.mark(myTypes.C_OBJECT_FIELD);
        }
    }

    private void parseTemplateStringOpen(@NotNull ParserState state) {
        // |>`<| ...
        state.markScope(myTypes.C_INTERPOLATION_EXPR, myTypes.JS_STRING_OPEN).advance();
        if (state.getTokenType() != myTypes.DOLLAR) {
            state.mark(myTypes.C_INTERPOLATION_PART);
        }
    }

    private void parseLet(@NotNull ParserState state) {
        if (!state.is(myTypes.C_PATTERN_MATCH_BODY)) {
            state.popEndUntilScope();
        }
        state.mark(myTypes.C_LET_DECLARATION);
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.is(myTypes.C_MACRO_NAME)) {
            state.popEndUntilScope();
            state.mark(myTypes.C_MODULE_DECLARATION);
        }
    }

    private void parseException(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_EXCEPTION_DECLARATION);
    }

    private void parseType(@NotNull ParserState state) {
        if (!state.is(myTypes.C_MODULE_DECLARATION)) {
            state.popEndUntilScope();
            state.mark(myTypes.C_TYPE_DECLARATION);
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_EXTERNAL_DECLARATION);
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
        // |>%<| raw ...
        state.mark(myTypes.C_MACRO_EXPR).mark(myTypes.C_MACRO_NAME).advance();
        if (state.getTokenType() == myTypes.PERCENT) {
            // %|>%<| raw ...
            state.advance();
        }
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.is(myTypes.C_SCOPED_EXPR) && state.isParent(myTypes.C_FIELD_VALUE)) {
            state.rollbackTo(state.getIndex())
                    .markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE)
                    .advance();
        } else if (state.strictlyInAny(
                myTypes.C_MODULE_DECLARATION, myTypes.C_LET_DECLARATION, myTypes.C_EXTERNAL_DECLARATION,
                myTypes.C_FUN_PARAM, myTypes.C_RECORD_FIELD, myTypes.C_OBJECT_FIELD, myTypes.C_NAMED_PARAM,
                myTypes.C_IF_THEN_SCOPE)) {

            if (state.isFound(myTypes.C_FUN_PARAM)) {
                // let x = (y |> :<| ...
                state.advance()
                        .mark(myTypes.C_SIG_EXPR)
                        .mark(myTypes.C_SIG_ITEM);
            } else if (state.isFound(myTypes.C_MODULE_DECLARATION)) {
                // module M |> :<| ...
                state.popEndUntilFoundIndex().advance()
                        .mark(myTypes.C_MODULE_TYPE);
            } else if (state.isFound(myTypes.C_EXTERNAL_DECLARATION) || state.isFound(myTypes.C_LET_DECLARATION)) {
                // external/let x |> :<| ...
                state.popEndUntilFoundIndex().advance().mark(myTypes.C_SIG_EXPR);
                if (state.getTokenType() == myTypes.LPAREN) {
                    // external/let x : |>(<| ...
                    state.markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN).advance();
                    if (state.getTokenType() == myTypes.DOT) {
                        // external/let : ( |>.<| ...
                        state.advance();
                    }
                }
                state.mark(myTypes.C_SIG_ITEM);
            } else if (state.isFound(myTypes.C_RECORD_FIELD) || state.isFound(myTypes.C_OBJECT_FIELD)) {
                state.advance();
                if (state.in(myTypes.C_TYPE_BINDING)) {
                    state.mark(myTypes.C_SIG_EXPR)
                            .mark(myTypes.C_SIG_ITEM);
                } else {
                    state.mark(myTypes.C_FIELD_VALUE);
                }
            } else if (state.isFound(myTypes.C_NAMED_PARAM)) {
                state.advance().mark(myTypes.C_SIG_EXPR)
                        .mark(myTypes.C_SIG_ITEM);
            } else if (state.isFound(myTypes.C_IF_THEN_SCOPE)) {
                state.popEndUntilFoundIndex().popEnd()
                        .advance()
                        .mark(myTypes.C_IF_THEN_SCOPE);
            }

        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_ANNOTATION).mark(myTypes.C_MACRO_NAME);
    }

    private void parseLt(@NotNull ParserState state) {
        if (state.is(myTypes.C_OPTION) || state.is(myTypes.C_SIG_ITEM)) {
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LT);
        } else if (state.strictlyIn(myTypes.C_TYPE_DECLARATION)) { // type parameters
            // type t |> < <| 'a >
            state.markScope(myTypes.C_PARAMETERS, myTypes.LT);
        } else {
            // Can be a symbol or a JSX tag
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == myTypes.LIDENT || nextTokenType == myTypes.UIDENT || nextTokenType == myTypes.OPTION) {
                // Note that option is a keyword but also a JSX keyword !
                state.mark(myTypes.C_TAG)
                        .markScope(myTypes.C_TAG_START, myTypes.LT);
            } else if (nextTokenType == myTypes.GT) {
                // a React fragment start
                state.mark(myTypes.C_TAG)
                        .mark(myTypes.C_TAG_START)
                        .advance()
                        .advance()
                        .popEnd();
            }
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.is(myTypes.C_TAG_PROP_VALUE)) {
            // ?prop=value |> > <| ...
            state.popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
        } else if (state.is(myTypes.C_TAG_PROPERTY)) {
            // ?prop |> > <| ...
            state.popEnd();
        }


        // else
        if (state.is(myTypes.C_SCOPED_EXPR)) {
            if (state.isParent(myTypes.C_OPTION)) {
                // option < ... |> > <| ...
                state.advance().popEnd().popEnd();
            } else if (state.isParent(myTypes.C_SIG_ITEM)) {
                // : ... < ... |> ><| ...
                state.advance().popEnd();
            }
        } else if (state.strictlyIn(myTypes.C_TAG_START)) {
            state.advance()
                    .popEndUntilFoundIndex()
                    .end();
            state.mark(myTypes.C_TAG_BODY);
        } else if (state.strictlyIn(myTypes.C_TAG_CLOSE)) {
            state.advance()
                    .popEndUntil(myTypes.C_TAG)
                    .end();
        } else if (state.strictlyIn(myTypes.C_PARAMETERS)) {
            state.popEndUntilFoundIndex().advance().end();
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        if (state.is(myTypes.C_TAG_PROP_VALUE)) {
            // ?prop=value |> /> <| ...
            state.popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
        } else if (state.is(myTypes.C_TAG_PROPERTY)) {
            // ?prop |> /> <| ...
            state.popEnd();
        }

        if (state.strictlyIn(myTypes.C_TAG_START)) {
            state.popEndUntilFoundIndex()
                    .advance().popEnd()
                    .end();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        if (state.in(myTypes.C_TAG)) {
            if (state.in(myTypes.C_TAG_BODY)) {
                state.popEndUntilFoundIndex().end();
            }
            state.remapCurrentToken(myTypes.TAG_LT_SLASH)
                    .mark(myTypes.C_TAG_CLOSE);
        }
    }

    private void parseTagName(@NotNull ParserState state) {
        // LIdent might have been converted to tagName in a first pass, and we need to convert it back if we know
        // that we are in a signature (second pass)
        if (state.isParent(myTypes.C_SIG_ITEM)) {
            state.remapCurrentToken(myTypes.LIDENT).wrapWith(myTypes.C_LOWER_SYMBOL);
        }
    }

    private void parseLIdent(@NotNull ParserState state) {
        // Must stop annotation if no dot/@ before
        if (state.is(myTypes.C_MACRO_NAME)) {
            IElementType previousElementType = state.previousElementType(1);
            if (previousElementType != myTypes.DOT && previousElementType != myTypes.ARROBASE) {
                state.popEnd().popEnd();
            }
        }

        if (state.is(myTypes.C_EXTERNAL_DECLARATION)) {
            // external |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_LET_DECLARATION)) {
            // let |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_TYPE_DECLARATION)) {
            // type |>x<| ...
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_FUN_EXPR)) {
            state.mark(myTypes.C_PARAMETERS)
                    .mark(myTypes.C_FUN_PARAM).wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_PARAMETERS) && (state.isParent(myTypes.C_FUN_EXPR) || state.isParent(myTypes.C_FUN_CALL))) {
            boolean funDeclaration = state.isParent(myTypes.C_FUN_EXPR);
            state.mark(myTypes.C_FUN_PARAM)
                    .wrapWith(funDeclaration ? myTypes.C_LOWER_IDENTIFIER : myTypes.C_LOWER_SYMBOL);
        } else if (state.is(myTypes.C_RECORD_EXPR)) {
            // { |>x<| ...
            state.mark(myTypes.C_RECORD_FIELD)
                    .wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.is(myTypes.C_TAG_START)) {
            // tag name
            state.remapCurrentToken(myTypes.TAG_NAME)
                    .wrapWith(myTypes.C_LOWER_SYMBOL);
        } else if (state.is(myTypes.C_DECONSTRUCTION)) {
            state.wrapWith(myTypes.C_LOWER_IDENTIFIER);
        } else if (state.strictlyIn(myTypes.C_TAG_START) && !state.isCurrent(myTypes.C_TAG_PROP_VALUE)) { // no scope
            // This is a property
            state.remapCurrentToken(myTypes.PROPERTY_NAME)
                    .mark(myTypes.C_TAG_PROPERTY)
                    .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state));
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.is(myTypes.C_SCOPED_EXPR) && state.isScope(myTypes.LBRACE) && nextElementType == myTypes.COLON) {
                // this is a record usage ::  { |>x<| : ...
                state.updateComposite(myTypes.C_RECORD_EXPR)
                        .mark(myTypes.C_RECORD_FIELD)
                        .wrapWith(myTypes.C_LOWER_IDENTIFIER);
            } else if (nextElementType == myTypes.LPAREN && !state.is(myTypes.C_MACRO_NAME)) { // a function call
                // |>x<| ( ...
                endLikeSemi(state);
                state.mark(myTypes.C_FUN_CALL)
                        .wrapWith(myTypes.C_LOWER_SYMBOL);
            } else if (state.is(myTypes.C_SIG_EXPR) || (state.isScope(myTypes.LPAREN) && state.isParent(myTypes.C_SIG_EXPR))) {
                state.mark(myTypes.C_SIG_ITEM)
                        .wrapWith(myTypes.C_LOWER_SYMBOL).popEnd();
            } else {
                state.wrapWith(myTypes.C_LOWER_SYMBOL).popEnd();
            }
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        if (state.previousElementType(2) == myTypes.UIDENT && state.previousElementType(1) == myTypes.DOT) { // Local open
            // M. |>[ <| ...
            state.markScope(myTypes.C_LOCAL_OPEN, myTypes.LBRACKET);
        } else {
            IElementType nextType = state.rawLookup(1);

            if (nextType == myTypes.GT) { // Lower bound type constraint
                // |> [ <| > ... ]
                state.markScope(myTypes.C_LOWER_BOUND_CONSTRAINT, myTypes.LBRACKET).advance();
            } else if (nextType == myTypes.LT) { // Upper bound type constraint
                // |> [ <| < ... ]
                state.markScope(myTypes.C_UPPER_BOUND_CONSTRAINT, myTypes.LBRACKET).advance();
            } else {
                state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACKET).advance();
                if (state.getTokenType() != myTypes.PIPE && state.getTokenType() != myTypes.POLY_VARIANT) {
                    state.markDummy(myTypes.C_DUMMY_COLLECTION_ITEM); // Needed to rollback to individual item in collection
                }
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

    private void parseLBrace(@NotNull ParserState state) {
        if (state.previousElementType(1) == myTypes.DOT && state.previousElementType(2) == myTypes.UIDENT) { // Local open a js object or a record
            // Xxx.|>{<| ... }
            state.mark(myTypes.C_LOCAL_OPEN);
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == myTypes.LIDENT) {
                state.markScope(myTypes.C_RECORD_EXPR, myTypes.LBRACE);
            } else {
                state.markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE);
            }
        } else if (state.is(myTypes.C_TYPE_BINDING)) {
            boolean isJsObject = state.lookAhead(1) == myTypes.STRING_VALUE;
            state.markScope(isJsObject ? myTypes.C_JS_OBJECT : myTypes.C_RECORD_EXPR, myTypes.LBRACE);
        } else if (state.is(myTypes.C_MODULE_BINDING)) {
            // module M = |>{<| ...
            state.updateScopeToken(myTypes.LBRACE);
        } else if (state.is(myTypes.C_TAG_PROP_VALUE) || state.is(myTypes.C_TAG_BODY)) {
            // A scoped property
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LBRACE);
        } else if (state.is(myTypes.C_MODULE_TYPE)) {
            // module M : |>{<| ...
            state.updateScopeToken(myTypes.LBRACE);
        } else if (state.isDone(myTypes.C_TRY_BODY)) { // A try expression
            // try ... |>{<| ... }
            state.markScope(myTypes.C_TRY_HANDLERS, myTypes.LBRACE);
        } else if (state.isDone(myTypes.C_BINARY_CONDITION) && state.isParent(myTypes.C_IF)) {
            // if x |>{<| ... }
            state.markScope(myTypes.C_IF_THEN_SCOPE, myTypes.LBRACE);
        } else if (state.isDone(myTypes.C_BINARY_CONDITION) && state.isParent(myTypes.C_SWITCH_EXPR)) {
            // switch (x) |>{<| ... }
            state.markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
        } else if (state.strictlyIn(myTypes.C_BINARY_CONDITION)) {
            state.popEndUntilFoundIndex().end();
            if (state.strictlyIn(myTypes.C_SWITCH_EXPR)) {
                // switch x |>{<| ... }
                state.markScope(myTypes.C_SWITCH_BODY, myTypes.LBRACE);
            }
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (nextElement == myTypes.STRING_VALUE || nextElement == myTypes.DOT) {
                boolean hasDot = nextElement == myTypes.DOT;
                // js object detected ::  |>{<| ./"x" ___ }
                state.markScope(myTypes.C_JS_OBJECT, myTypes.LBRACE).advance();
                if (hasDot) {
                    state.advance();
                }
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
            } else if (scope.isCompositeType(myTypes.C_RECORD_EXPR) && state.is(myTypes.C_TYPE_BINDING)) {
                // Record type, end the type itself
                state.popEndUntil(myTypes.C_TYPE_DECLARATION).popEnd();
            }
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.previousElementType(2) == myTypes.UIDENT && state.previousElementType(1) == myTypes.DOT) { // Local open
            // M. |>(<| ... )
            state.markScope(myTypes.C_LOCAL_OPEN, myTypes.LPAREN);
        } else if (state.is(myTypes.C_FUN_EXPR)) { // A function
            // |>(<| .  OR  |>(<| ~
            state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN);
        } else if (state.isCurrent(myTypes.C_FUN_CALL)) {
            // x |>(<| ...
            state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
            if (state.getTokenType() != myTypes.RPAREN) {
                state.mark(myTypes.C_FUN_PARAM);
            }
        } else if (state.is(myTypes.C_PARAMETERS) && !state.hasScopeToken()) {
            state.updateScopeToken(myTypes.LPAREN);
        } else if (state.is(myTypes.C_MACRO_NAME) && state.isParent(myTypes.C_ANNOTATION)) {
            // @ann |>(<| ... )
            state.popEnd()
                    .markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN);
        } else if (state.is(myTypes.C_MACRO_NAME)) {
            // %raw |>(<| ...
            state.popEnd()
                    .markDummyScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                    .advance()
                    .mark(myTypes.C_MACRO_BODY);
        } else if (state.is(myTypes.C_BINARY_CONDITION)) {
            state.updateScopeToken(myTypes.LPAREN);
        } else if (state.is(myTypes.C_DECONSTRUCTION) && state.isParent(myTypes.C_LET_DECLARATION)) {
            // let ((x |>,<| ...
            state.markScope(myTypes.C_DECONSTRUCTION, myTypes.LPAREN);
        }
        //
        else if (state.strictlyInAny(
                myTypes.C_OPEN, myTypes.C_INCLUDE, myTypes.C_VARIANT_DECLARATION, myTypes.C_FUNCTOR_DECLARATION,
                myTypes.C_FUNCTOR_CALL, myTypes.C_FUNCTOR_RESULT
        )) {

            if (state.isFound(myTypes.C_OPEN) || state.isFound(myTypes.C_INCLUDE)) { // Functor call
                // open M |>(<| ...
                state.markBefore(state.getIndex() - 1, myTypes.C_FUNCTOR_CALL)
                        .markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance();
            } else if (state.isFound(myTypes.C_VARIANT_DECLARATION)) { // Variant constructor
                // type t = | Variant |>(<| .. )
                state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN)
                        .advance()
                        .mark(myTypes.C_FUN_PARAM);
            }
            if (state.isFound(myTypes.C_FUNCTOR_DECLARATION) || state.isFound(myTypes.C_FUNCTOR_CALL)) {
                // module M = |>(<| ...
                // module M = ( ... ) : |>(<| ...
                state.markScope(myTypes.C_PARAMETERS, myTypes.LPAREN).advance()
                        .mark(myTypes.C_FUNCTOR_PARAM);
            }
        } else {
            state.markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                    .markDummy(myTypes.C_DUMMY_COLLECTION_ITEM);  // Needed to rollback to individual item in collection
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(myTypes.LPAREN);
        state.advance();

        if (state.is(myTypes.C_BINARY_CONDITION)) {
            state.end();
            if (state.isParent(myTypes.C_IF) && state.getTokenType() != myTypes.LBRACE) {
                // if ( x ) |><| ...
                state.mark(myTypes.C_IF_THEN_SCOPE);
            }
        } else if (scope != null) {
            if (state.is(myTypes.C_PARAMETERS)) {
                state.end();
                if (state.isGrandParent(myTypes.C_FUN_CALL)) {
                    state.popEnd().popEnd().popEnd();
                }
            } else {
                state.popEnd();
                if (state.is(myTypes.C_ANNOTATION)) {
                    state.popEnd();
                } else if (state.strictlyIn(myTypes.C_TAG_PROP_VALUE)) {// && !state.hasScopeToken()) {
                    state.popEndUntil(myTypes.C_TAG_PROPERTY).popEnd();
                }
            }
        }
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.strictlyInAny(
                myTypes.C_TYPE_DECLARATION, myTypes.C_LET_DECLARATION, myTypes.C_MODULE_TYPE, myTypes.C_MODULE_DECLARATION,
                myTypes.C_TAG_PROPERTY, myTypes.C_SIG_EXPR, myTypes.C_NAMED_PARAM
        )) {

            if (state.isFound(myTypes.C_TYPE_DECLARATION)) {
                // type t |> = <| ...
                state.popEndUntilFoundIndex().advance()
                        .mark(myTypes.C_TYPE_BINDING);
            } else if (state.isFound(myTypes.C_LET_DECLARATION)) {
                // let x |> = <| ...
                state.popEndUntilFoundIndex().advance()
                        .mark(myTypes.C_LET_BINDING);
            } else if (state.isFound(myTypes.C_MODULE_TYPE)) {
                // module M : T |> = <| ...
                state.popEndUntilFoundIndex().end()
                        .advance()
                        .mark(myTypes.C_MODULE_BINDING);
            } else if (state.isFound(myTypes.C_MODULE_DECLARATION)) {
                // module M |> = <| ...
                state.advance().mark(myTypes.C_MODULE_BINDING);
            } else if (state.isFound(myTypes.C_TAG_PROPERTY)) {
                // <Comp prop |> = <| ...
                state.advance().mark(myTypes.C_TAG_PROP_VALUE);
            } else if (state.isFound(myTypes.C_SIG_EXPR)) {
                state.popEndUntilFoundIndex().end();
                if (state.isGrandParent(myTypes.C_NAMED_PARAM)) {
                    state.advance().mark(myTypes.C_DEFAULT_VALUE);
                } else if (state.isParent(myTypes.C_LET_DECLARATION)) {
                    // let x : M.t |> =<| ...
                    state.popEndUntilFoundIndex().advance()
                            .mark(myTypes.C_LET_BINDING);
                }
            } else if (state.isFound(myTypes.C_NAMED_PARAM)) {
                // ... => ~x |> =<| ...
                state.advance().mark(myTypes.C_DEFAULT_VALUE);
            }

        }
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.is(myTypes.C_MODULE_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_EXCEPTION_DECLARATION)) {
            // exception |>E<| ...
            state.wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.isCurrent(myTypes.C_TAG_START) || state.isCurrent(myTypes.C_TAG_CLOSE)) {
            // tag name
            state.remapCurrentToken(myTypes.TAG_NAME)
                    .wrapWith(myTypes.C_UPPER_SYMBOL);
        } else if (state.isCurrent(myTypes.C_OPEN)) { // It is a module name/path, or maybe a functor call
            // open |>M<| ...
            state.wrapWith(myTypes.C_UPPER_SYMBOL);
            IElementType nextToken = state.getTokenType();
            if (nextToken != myTypes.LPAREN && nextToken != myTypes.DOT) {
                state.popEndUntil(myTypes.C_OPEN).popEnd();
            }
        } else if (state.isCurrent(myTypes.C_INCLUDE)) { // It is a module name/path, or maybe a functor call
            // include |>M<| ...
            state.wrapWith(myTypes.C_UPPER_SYMBOL);
            IElementType nextToken = state.getTokenType();
            if (nextToken != myTypes.LPAREN && nextToken != myTypes.DOT) {
                state.popEndUntil(myTypes.C_INCLUDE).popEnd();
            }
        } else if (state.is(myTypes.C_VARIANT_DECLARATION)) { // Declaring a variant
            // type t = | |>X<| ..
            state.remapCurrentToken(myTypes.VARIANT_NAME)
                    .wrapWith(myTypes.C_UPPER_IDENTIFIER);
        } else if (state.is(myTypes.C_TYPE_BINDING)) {
            IElementType nextToken = state.lookAhead(1);
            if (nextToken == myTypes.DOT) { // a path
                state.wrapWith(myTypes.C_UPPER_SYMBOL);
            } else { // We are declaring a variant without a pipe before
                // type t = |>X<| | ...
                // type t = |>X<| (...) | ...
                state.remapCurrentToken(myTypes.VARIANT_NAME)
                        .mark(myTypes.C_VARIANT_DECLARATION)
                        .wrapWith(myTypes.C_UPPER_IDENTIFIER);
            }
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.isCurrent(myTypes.C_MODULE_BINDING) && nextElementType == myTypes.LPAREN) {
                // functor call ::  |>X<| ( ...
                // functor call with path :: A.B.|>X<| ( ...
                Marker current = state.getCurrentMarker();
                if (current != null) {
                    current.drop();
                }
                state.mark(myTypes.C_FUNCTOR_CALL);
            } else {
                state.wrapWith(myTypes.C_UPPER_SYMBOL);
            }
        }

    }

    private void parsePolyVariant(@NotNull ParserState state) {
        if (state.isParent(myTypes.C_TYPE_BINDING)) {
            // type t = [ |>#xxx<| ...
            state.mark(myTypes.C_VARIANT_DECLARATION);
        }
    }

    private void parseSwitch(@NotNull ParserState state) {
        state.mark(myTypes.C_SWITCH_EXPR)
                .advance()
                .mark(myTypes.C_BINARY_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(myTypes.C_TRY_EXPR).advance()
                .mark(myTypes.C_TRY_BODY);
    }

    private void parseCatch(@NotNull ParserState state) {
        if (state.strictlyIn(myTypes.C_TRY_BODY)) {
            state.popEndUntilFoundIndex().end();
            //    state.popEnd();
        }
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.isDone(myTypes.C_PARAMETERS) && state.isParent(myTypes.C_FUN_EXPR)) {
            state.advance().mark(myTypes.C_FUN_BODY);
        } else if (state.strictlyIn(myTypes.C_PARAMETERS) && state.isAtIndex(state.getIndex() + 1, myTypes.C_FUN_EXPR)) { // parenless param
            // x |>=><| ...
            state.popEndUntil(myTypes.C_PARAMETERS).end()
                    .advance()
                    .mark(myTypes.C_FUN_BODY);
        } else if (state.inScopeOrAny(
                myTypes.C_LET_BINDING, myTypes.C_PATTERN_MATCH_EXPR, myTypes.C_FIELD_VALUE, myTypes.C_FUN_EXPR,
                myTypes.C_FUN_PARAM, myTypes.C_SIG_ITEM, myTypes.C_SIG_EXPR
        )) {

            if (state.isFound(myTypes.C_LET_BINDING)) {
                // let x = .?. |>=><| ...
                state.rollbackTo(state.getIndex())
                        .mark(myTypes.C_LET_BINDING)
                        .mark(myTypes.C_FUN_EXPR)
                        .mark(myTypes.C_PARAMETERS);
            } else if (state.isFound(myTypes.C_PATTERN_MATCH_EXPR)) {
                // switch ( ... ) { | ... |>=><| ... }
                state.popEndUntilFoundIndex()
                        .advance()
                        .mark(myTypes.C_PATTERN_MATCH_BODY);
            } else if (state.isFound(myTypes.C_FIELD_VALUE)) {
                // { x : .?. |>=><| ...
                state.rollbackTo(state.getIndex())
                        .mark(myTypes.C_FIELD_VALUE)
                        .mark(myTypes.C_FUN_EXPR)
                        .mark(myTypes.C_PARAMETERS);
            } else if (state.isFound(myTypes.C_SIG_ITEM)) {
                state.popEndUntilFoundIndex().popEnd();
                state.advance().mark(myTypes.C_SIG_ITEM);
            } else if (state.isFound(myTypes.C_SIG_EXPR)) {
                state.advance().mark(myTypes.C_SIG_ITEM);
            } else if (state.isFound(myTypes.C_FUN_PARAM)) { // anonymous function
                // x( y |>=><| ... )
                state.rollbackTo(state.getIndex())
                        .mark(myTypes.C_FUN_PARAM)
                        .mark(myTypes.C_FUN_EXPR)
                        .mark(myTypes.C_PARAMETERS);
            }
            // C_FUN_EXPR -> ignore
            else if (state.is(myTypes.C_SCOPED_EXPR) && !state.in(myTypes.C_SIG_ITEM)) {
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

    private void endLikeSemi(@NotNull ParserState state) {
        if (!state.is(myTypes.C_LET_BINDING) && !state.is(myTypes.C_BINARY_CONDITION)) {
            int pos = -1;
            IElementType previousElementType = state.rawLookup(pos);
            while (previousElementType != null && previousElementType == TokenType.WHITE_SPACE) {
                pos--;
                previousElementType = state.rawLookup(pos);
            }

            if (previousElementType != null && previousElementType != myTypes.DOT && previousElementType != myTypes.ARROW) {
                state.popEndUntilScope();
            }
        }
    }
}
