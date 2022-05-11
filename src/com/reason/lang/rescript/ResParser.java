package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;

public class ResParser extends CommonParser<ResTypes> {

    ResParser() {
        super(ResTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;
        state.previousElementType1 = null;

        // long parseStart = System.currentTimeMillis();

        while (true) {
            // long parseTime = System.currentTimeMillis();
            // if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            // break;
            // }

            state.previousElementType2 = state.previousElementType1;
            state.previousElementType1 = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // Special analyse when inside an interpolation string
            if (state.is(m_types.C_INTERPOLATION_EXPR)
                    || state.is(m_types.C_INTERPOLATION_PART)
                    || state.is(m_types.C_INTERPOLATION_REF)) {
                if (tokenType == m_types.JS_STRING_OPEN) {
                    state.popEndUntil(m_types.C_INTERPOLATION_EXPR).advance().popEnd();
                } else if (tokenType == m_types.DOLLAR) {
                    if (state.is(m_types.C_INTERPOLATION_PART)) {
                        state.popEnd();
                    }
                    state.advance();
                    IElementType nextElement = state.getTokenType();
                    if (nextElement == m_types.LBRACE) {
                        state.advance().markScope(m_types.C_INTERPOLATION_REF, m_types.LBRACE);
                    }
                } else if (state.is(m_types.C_INTERPOLATION_REF) && tokenType == m_types.RBRACE) {
                    state.popEnd().advance();
                    if (state.getTokenType() != m_types.JS_STRING_OPEN) {
                        state.mark(m_types.C_INTERPOLATION_PART);
                    }
                }
            } else {
                // EOL is a statement separator
                if (tokenType == m_types.EOL) {
                    // We identify it at the lexer level, because We try to detect current construction that can't have
                    // EOL in it ; but we don't want to have it in the parsed tree, we change it to a whitespace
                    builder.remapCurrentToken(TokenType.WHITE_SPACE);

                    if (state.is(m_types.C_FUNCTOR_CALL/*PsiOpen is potential functor call*/) && state.isParent(m_types.C_OPEN)) {
                        state.popEnd().popEnd();
                    } else if (state.isParent(m_types.C_ANNOTATION) && !state.is(m_types.C_SCOPED_EXPR)) {
                        // inside an annotation
                        state.popEnd().popEnd();
                    } else if (state.is(m_types.C_LET_BINDING)) {
                        // simple let with no scope
                        state.popEnd().popEnd();
                    }
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
                } else if (tokenType == m_types.CATCH) {
                    parseCatch(state);
                } else if (tokenType == m_types.SWITCH) {
                    parseSwitch(state);
                } else if (tokenType == m_types.TAG_NAME) {
                    parseTagName(state);
                } else if (tokenType == m_types.LIDENT) {
                    parseLIdent(state);
                } else if (tokenType == m_types.UIDENT) {
                    parseUIdent(state);
                } else if (tokenType == m_types.POLY_VARIANT) {
                    parsePolyVariant(state);
                } else if (tokenType == m_types.ARROBASE) {
                    parseArrobase(state);
                } else if (tokenType == m_types.PERCENT) {
                    parsePercent(state);
                } else if (tokenType == m_types.COLON) {
                    parseColon(state);
                } else if (tokenType == m_types.STRING_VALUE) {
                    parseStringValue(state);
                } else if (tokenType == m_types.PIPE) {
                    parsePipe(state);
                } else if (tokenType == m_types.COMMA) {
                    parseComma(state);
                } else if (tokenType == m_types.AND) {
                    parseAnd(state);
                } else if (tokenType == m_types.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == m_types.IF) {
                    parseIf(state);
                } else if (tokenType == m_types.DOTDOTDOT) {
                    parseDotDotDot(state);
                } else if (tokenType == m_types.QUESTION_MARK) {
                    parseQuestionMark(state);
                } else if (tokenType == m_types.TILDE) {
                    parseTilde(state);
                } else if (tokenType == m_types.UNDERSCORE) {
                    parseUnderscore(state);
                } else if (tokenType == m_types.INT_VALUE || tokenType == m_types.FLOAT_VALUE) {
                    parseNumeric(state);
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
                } else if (tokenType == m_types.TAG_LT_SLASH) {
                    parseLtSlash(state);
                } else if (tokenType == m_types.GT) {
                    parseGt(state);
                } else if (tokenType == m_types.TAG_AUTO_CLOSE) {
                    parseGtAutoClose(state);
                }
                // j` ... `
                else if (tokenType == m_types.JS_STRING_OPEN) {
                    parseTemplateStringOpen(state);
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
                } else if (tokenType == m_types.LET) {
                    parseLet(state);
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
        }

        state.popEndUntilScope();
    }

    private void parseNumeric(@NotNull ParserState state) {
        if (state.is(m_types.C_PARAMETERS)) {
            state.mark(m_types.C_FUN_PARAM);
        }
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.is(m_types.C_PARAMETERS)/* && state.isParent(m_types.C_FUN_EXPR)*/) {
            state.mark(m_types.C_FUN_PARAM);
        }
    }

    private void parseRef(@NotNull ParserState state) {
        if (state.is(m_types.C_RECORD_EXPR)) {
            // { |>x<| ...
            state.mark(m_types.C_RECORD_FIELD).wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.strictlyIn(m_types.C_TAG_START)) {
            state.remapCurrentToken(m_types.PROPERTY_NAME).mark(m_types.C_TAG_PROPERTY);
        }
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(m_types.C_OPTION);
    }

    private void parseIf(@NotNull ParserState state) {
        state.mark(m_types.C_IF)
                .advance().mark(m_types.C_BINARY_CONDITION);
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LBRACE) { // Mixin
            // ... { |>...<| x ...
            state.updateComposite(m_types.C_RECORD_EXPR)
                    .mark(m_types.C_MIXIN_FIELD);
        }
    }

    private void parseQuestionMark(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.EQ) {
            // x=|>?<| ...
            return;
        }

        if (state.strictlyIn(m_types.C_TAG_START)) {
            // <jsx |>?<|prop ...
            state.mark(m_types.C_TAG_PROPERTY)
                    .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state))
                    .advance()
                    .remapCurrentToken(m_types.PROPERTY_NAME);
        } else if (state.isDone(m_types.C_BINARY_CONDITION) || state.strictlyIn(m_types.C_BINARY_CONDITION)) { // a ternary in progress
            // ... |>?<| ...
            state.popEndUntilFoundIndex().end()
                    .advance()
                    .mark(m_types.C_IF_THEN_SCOPE);
        } else if (state.strictlyIn(m_types.C_LET_BINDING)) { // a new ternary
            // let x = ... |>?<| ...
            state.rollbackTo(state.getIndex())
                    .mark(m_types.C_LET_BINDING)
                    .mark(m_types.C_TERNARY)
                    .mark(m_types.C_BINARY_CONDITION);
        }
    }

    private void parseTilde(@NotNull ParserState state) {
        if (state.is(m_types.C_PARAMETERS)) {
            state.mark(m_types.C_FUN_PARAM)
                    .mark(m_types.C_NAMED_PARAM).advance()
                    .wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.strictlyIn(m_types.C_SIG_EXPR) && !state.is(m_types.C_SIG_ITEM)) {
            state.mark(m_types.C_SIG_ITEM)
                    .mark(m_types.C_NAMED_PARAM).advance()
                    .wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else {
            state.mark(m_types.C_NAMED_PARAM).advance()
                    .wrapWith(m_types.C_LOWER_IDENTIFIER);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(m_types.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        //noinspection StatementWithEmptyBody
        if (state.is(m_types.C_CONSTRAINT)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
        } else if (state.strictlyIn(m_types.C_TYPE_DECLARATION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(m_types.C_TYPE_DECLARATION);
        } else if (state.strictlyIn(m_types.C_MODULE_DECLARATION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(m_types.C_MODULE_DECLARATION);
        } else if (state.strictlyIn(m_types.C_LET_DECLARATION)) {
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(m_types.C_LET_DECLARATION);
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.strictlyIn(m_types.C_SIG_EXPR)) {  // remove intermediate signatures
            if (state.isAtIndex(state.getIndex() - 1, m_types.C_SCOPED_EXPR)) { // a dummy scope
                state.popEndUntilIndex(state.getIndex() - 1);
            } else {
                state.popEndUntilFoundIndex();
            }
            if (state.strictlyIn(m_types.C_SIG_ITEM)) {
                state.popEndUntilFoundIndex().popEnd();
            }
        }

        if (state.strictlyIn(m_types.C_SCOPED_EXPR) && state.isAtIndex(state.getIndex() + 1, m_types.C_LET_DECLARATION)) {
            // It must be a deconstruction ::  let ( a |>,<| b ) = ...
            // We need to do it again because lower symbols must be wrapped with identifiers
            state.rollbackTo(state.getIndex())
                    .markScope(m_types.C_DECONSTRUCTION, m_types.LPAREN).advance();
        }
        // Same priority
        else if (state.strictlyInAny(
                m_types.C_FUN_PARAM, m_types.C_DECONSTRUCTION, m_types.C_RECORD_FIELD, m_types.C_MIXIN_FIELD,
                m_types.C_OBJECT_FIELD
        )) {

            if (state.isFound(m_types.C_FUN_PARAM)) {
                state.popEndUntilFoundIndex().popEnd().advance();
                if (state.getTokenType() != m_types.RPAREN) {
                    // not at the end of a list: ie not => (p1, p2<,> )
                    state.mark(m_types.C_FUN_PARAM);
                }
            } else if (state.isFound(m_types.C_DECONSTRUCTION)) {
                state.popEndUntilScope();
            } else if (state.isFound(m_types.C_RECORD_FIELD) || state.isFound(m_types.C_MIXIN_FIELD) || state.isFound(m_types.C_OBJECT_FIELD)) {
                state.popEndUntilFoundIndex().popEnd(); //.advance();
            }

        }
    }

    private void parsePipe(@NotNull ParserState state) {
        if (state.is(m_types.C_TYPE_BINDING)) {
            // type x = |>|<| ...
            state.advance().mark(m_types.C_VARIANT_DECLARATION);
        } else if (state.is(m_types.C_TRY_HANDLERS)) { // Start of a try handler
            // try (...) { |>|<| ... }
            state.advance().mark(m_types.C_TRY_HANDLER);
        } else if (state.is(m_types.C_SWITCH_BODY)) {
            // switch x { |>|<| ... }
            state.advance().mark(m_types.C_PATTERN_MATCH_EXPR);
        } else if (state.strictlyIn(m_types.C_PATTERN_MATCH_BODY)) {
            // can be a switchBody or a 'fun'
            state.popEndUntil(m_types.C_PATTERN_MATCH_EXPR).popEnd()
                    .advance()
                    .mark(m_types.C_PATTERN_MATCH_EXPR);
        } else if (state.strictlyIn(m_types.C_PATTERN_MATCH_EXPR)) { // pattern grouping
            // | X |>|<| Y => ...
            state.popEndUntilFoundIndex().popEnd()
                    .advance()
                    .mark(m_types.C_PATTERN_MATCH_EXPR);
        } else if (state.strictlyIn(m_types.C_VARIANT_DECLARATION)) {
            // type t = | X |>|<| Y ...
            state.popEndUntil(m_types.C_VARIANT_DECLARATION).popEnd()
                    .advance()
                    .mark(m_types.C_VARIANT_DECLARATION);
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        if (state.is(m_types.C_JS_OBJECT)) {
            state.mark(m_types.C_OBJECT_FIELD);
        }
    }

    private void parseTemplateStringOpen(@NotNull ParserState state) {
        // |>`<| ...
        state.markScope(m_types.C_INTERPOLATION_EXPR, m_types.JS_STRING_OPEN).advance();
        if (state.getTokenType() != m_types.DOLLAR) {
            state.mark(m_types.C_INTERPOLATION_PART);
        }
    }

    private void parseLet(@NotNull ParserState state) {
        if (!state.is(m_types.C_PATTERN_MATCH_BODY)) {
            state.popEndUntilScope();
        }
        state.mark(m_types.C_LET_DECLARATION);
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.is(m_types.C_MACRO_NAME)) {
            state.popEndUntilScope();
            state.mark(m_types.C_MODULE_DECLARATION);
        }
    }

    private void parseException(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_EXCEPTION_DECLARATION);
    }

    private void parseType(@NotNull ParserState state) {
        if (!state.is(m_types.C_MODULE_DECLARATION)) {
            state.popEndUntilScope();
            state.mark(m_types.C_TYPE_DECLARATION);
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_EXTERNAL_DECLARATION);
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
        // |>%<| raw ...
        state.mark(m_types.C_MACRO_EXPR).mark(m_types.C_MACRO_NAME).advance();
        if (state.getTokenType() == m_types.PERCENT) {
            // %|>%<| raw ...
            state.advance();
        }
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.is(m_types.C_SCOPED_EXPR) && state.isParent(m_types.C_FIELD_VALUE)) {
            state.rollbackTo(state.getIndex())
                    .markScope(m_types.C_RECORD_EXPR, m_types.LBRACE)
                    .advance();
        } else if (state.strictlyInAny(
                m_types.C_MODULE_DECLARATION, m_types.C_LET_DECLARATION, m_types.C_EXTERNAL_DECLARATION,
                m_types.C_FUN_PARAM, m_types.C_RECORD_FIELD, m_types.C_OBJECT_FIELD, m_types.C_NAMED_PARAM,
                m_types.C_IF_THEN_SCOPE)) {

            if (state.isFound(m_types.C_FUN_PARAM)) {
                // let x = (y |> :<| ...
                state.advance()
                        .mark(m_types.C_SIG_EXPR)
                        .mark(m_types.C_SIG_ITEM);
            } else if (state.isFound(m_types.C_MODULE_DECLARATION)) {
                // module M |> :<| ...
                state.popEndUntilFoundIndex().advance()
                        .mark(m_types.C_MODULE_TYPE);
            } else if (state.isFound(m_types.C_LET_DECLARATION)) {
                // let x |> :<| ...
                state.popEndUntilFoundIndex().advance().mark(m_types.C_SIG_EXPR);
                if (state.getTokenType() == m_types.LPAREN) {
                    state.markDummyScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).advance();
                }
                state.mark(m_types.C_SIG_ITEM);
            } else if (state.isFound(m_types.C_EXTERNAL_DECLARATION)) { // external declaration
                // external x |> :<| ...
                state.popEndUntilFoundIndex().advance().mark(m_types.C_SIG_EXPR);
                if (state.getTokenType() == m_types.LPAREN) {
                    state.markDummyScope(m_types.C_SCOPED_EXPR, m_types.LPAREN).advance();
                }
                state.mark(m_types.C_SIG_ITEM);
            } else if (state.isFound(m_types.C_RECORD_FIELD) || state.isFound(m_types.C_OBJECT_FIELD)) {
                state.advance();
                if (state.in(m_types.C_TYPE_BINDING)) {
                    state.mark(m_types.C_SIG_EXPR)
                            .mark(m_types.C_SIG_ITEM);
                } else {
                    state.mark(m_types.C_FIELD_VALUE);
                }
            } else if (state.isFound(m_types.C_NAMED_PARAM)) {
                state.advance().mark(m_types.C_SIG_EXPR)
                        .mark(m_types.C_SIG_ITEM);
            } else if (state.isFound(m_types.C_IF_THEN_SCOPE)) {
                state.popEndUntilFoundIndex().popEnd()
                        .advance()
                        .mark(m_types.C_IF_THEN_SCOPE);
            }

        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_ANNOTATION).mark(m_types.C_MACRO_NAME);
    }

    private void parseLt(@NotNull ParserState state) {
        if (state.is(m_types.C_OPTION) || state.is(m_types.C_SIG_ITEM)) {
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LT);
        } else if (state.strictlyIn(m_types.C_TYPE_DECLARATION)) { // type parameters
            // type t |> < <| 'a >
            state.markScope(m_types.C_PARAMETERS, m_types.LT);
        } else {
            // Can be a symbol or a JSX tag
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT || nextTokenType == m_types.OPTION) {
                // Note that option is a keyword but also a JSX keyword !
                state.mark(m_types.C_TAG)
                        .markScope(m_types.C_TAG_START, m_types.LT);
            } else if (nextTokenType == m_types.GT) {
                // a React fragment start
                state.mark(m_types.C_TAG)
                        .mark(m_types.C_TAG_START)
                        .advance()
                        .advance()
                        .popEnd();
            }
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.is(m_types.C_TAG_PROP_VALUE)) {
            // ?prop=value |> > <| ...
            state.popEndUntil(m_types.C_TAG_PROPERTY).popEnd();
        } else if (state.is(m_types.C_TAG_PROPERTY)) {
            // ?prop |> > <| ...
            state.popEnd();
        }


        // else
        if (state.is(m_types.C_SCOPED_EXPR)) {
            if (state.isParent(m_types.C_OPTION)) {
                // option < ... |> > <| ...
                state.advance().popEnd().popEnd();
            } else if (state.isParent(m_types.C_SIG_ITEM)) {
                // : ... < ... |> ><| ...
                state.advance().popEnd();
            }
        } else if (state.strictlyIn(m_types.C_TAG_START)) {
            state.advance()
                    .popEndUntilFoundIndex()
                    .end();
            state.mark(m_types.C_TAG_BODY);
        } else if (state.strictlyIn(m_types.C_TAG_CLOSE)) {
            state.advance()
                    .popEndUntil(m_types.C_TAG)
                    .end();
        } else if (state.strictlyIn(m_types.C_PARAMETERS)) {
            state.popEndUntilFoundIndex().advance().end();
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        if (state.is(m_types.C_TAG_PROP_VALUE)) {
            // ?prop=value |> /> <| ...
            state.popEndUntil(m_types.C_TAG_PROPERTY).popEnd();
        } else if (state.is(m_types.C_TAG_PROPERTY)) {
            // ?prop |> /> <| ...
            state.popEnd();
        }

        if (state.strictlyIn(m_types.C_TAG_START)) {
            state.popEndUntilFoundIndex()
                    .advance().popEnd()
                    .end();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        if (state.in(m_types.C_TAG)) {
            if (state.in(m_types.C_TAG_BODY)) {
                state.popEndUntilFoundIndex().end();
            }
            state.remapCurrentToken(m_types.TAG_LT_SLASH)
                    .mark(m_types.C_TAG_CLOSE);
        }
    }

    private void parseTagName(@NotNull ParserState state) {
        // LIdent might have been converted to tagName in a first pass, and we need to convert it back if we know
        // that we are in a signature (second pass)
        if (state.isParent(m_types.C_SIG_ITEM)) {
            state.remapCurrentToken(m_types.LIDENT).wrapWith(m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseLIdent(@NotNull ParserState state) {
        // Must stop annotation if no dot/@ before
        if (state.is(m_types.C_MACRO_NAME)) {
            if (state.previousElementType1 != m_types.DOT && state.previousElementType1 != m_types.ARROBASE) {
                state.popEnd().popEnd();
            }
        }

        if (state.is(m_types.C_EXTERNAL_DECLARATION)) {
            // external |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_LET_DECLARATION)) {
            // let |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_TYPE_DECLARATION)) {
            // type |>x<| ...
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_FUN_EXPR)) {
            state.mark(m_types.C_PARAMETERS)
                    .mark(m_types.C_FUN_PARAM).wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_PARAMETERS) && (state.isParent(m_types.C_FUN_EXPR) || state.isParent(m_types.C_FUN_CALL))) {
            boolean funDeclaration = state.isParent(m_types.C_FUN_EXPR);
            state.mark(m_types.C_FUN_PARAM)
                    .wrapWith(funDeclaration ? m_types.C_LOWER_IDENTIFIER : m_types.C_LOWER_SYMBOL);
        } else if (state.is(m_types.C_RECORD_EXPR)) {
            // { |>x<| ...
            state.mark(m_types.C_RECORD_FIELD)
                    .wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.is(m_types.C_TAG_START)) {
            // tag name
            state.remapCurrentToken(m_types.TAG_NAME)
                    .wrapWith(m_types.C_LOWER_SYMBOL);
        } else if (state.is(m_types.C_DECONSTRUCTION)) {
            state.wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.strictlyIn(m_types.C_TAG_START) && !state.isCurrent(m_types.C_TAG_PROP_VALUE)) { // no scope
            // This is a property
            state.remapCurrentToken(m_types.PROPERTY_NAME)
                    .mark(m_types.C_TAG_PROPERTY)
                    .setWhitespaceSkippedCallback(endJsxPropertyIfWhitespace(state));
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.is(m_types.C_SCOPED_EXPR) && state.isParent(m_types.C_LET_BINDING) && state.isScopeTokenElementType(m_types.LBRACE) && nextElementType == m_types.COLON) {
                // this is a record usage ::  { |>x<| : ...
                state.updateComposite(m_types.C_RECORD_EXPR)
                        .mark(m_types.C_RECORD_FIELD)
                        .wrapWith(m_types.C_LOWER_IDENTIFIER); // ?? zzz symbol
            } else if (nextElementType == m_types.LPAREN && !state.is(m_types.C_MACRO_NAME)) { // a function call
                // |>x<| ( ...
                state.mark(m_types.C_FUN_CALL)
                        .wrapWith(m_types.C_LOWER_SYMBOL);
            } else if (state.is(m_types.C_SIG_EXPR) || (state.isScopeTokenElementType(m_types.LPAREN) && state.isParent(m_types.C_SIG_EXPR))) {
                state.mark(m_types.C_SIG_ITEM)
                        .wrapWith(m_types.C_LOWER_SYMBOL).popEnd();
            } else {
                state.wrapWith(m_types.C_LOWER_SYMBOL).popEnd();
            }
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) { // Local open
            // M. |>[ <| ...
            state.markScope(m_types.C_LOCAL_OPEN, m_types.LBRACKET);
        } else {
            IElementType nextType = state.rawLookup(1);

            if (nextType == m_types.GT) { // Lower bound type constraint
                // |> [ <| > ... ]
                state.markScope(m_types.C_LOWER_BOUND_CONSTRAINT, m_types.LBRACKET).advance();
            } else if (nextType == m_types.LT) { // Upper bound type constraint
                // |> [ <| < ... ]
                state.markScope(m_types.C_UPPER_BOUND_CONSTRAINT, m_types.LBRACKET).advance();
            } else {
                state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACKET);
            }
        }
    }

    private void parseRBracket(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(m_types.LBRACKET);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
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
            boolean isJsObject = state.lookAhead(1) == m_types.STRING_VALUE;
            state.markScope(isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE);
        } else if (state.is(m_types.C_MODULE_BINDING)) {
            // module M = |>{<| ...
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.is(m_types.C_TAG_PROP_VALUE) || state.is(m_types.C_TAG_BODY)) {
            // A scoped property
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LBRACE);
        } else if (state.is(m_types.C_MODULE_TYPE)) {
            // module M : |>{<| ...
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.isDone(m_types.C_TRY_BODY)) { // A try expression
            // try ... |>{<| ... }
            state.markScope(m_types.C_TRY_HANDLERS, m_types.LBRACE);
        } else if (state.isDone(m_types.C_BINARY_CONDITION) && state.isParent(m_types.C_IF)) {
            // if x |>{<| ... }
            state.markScope(m_types.C_IF_THEN_SCOPE, m_types.LBRACE);
        } else if (state.isDone(m_types.C_BINARY_CONDITION) && state.isParent(m_types.C_SWITCH_EXPR)) {
            // switch (x) |>{<| ... }
            state.markScope(m_types.C_SWITCH_BODY, m_types.LBRACE);
        } else if (state.strictlyIn(m_types.C_BINARY_CONDITION)) {
            state.popEndUntilFoundIndex().end();
            if (state.strictlyIn(m_types.C_SWITCH_EXPR)) {
                // switch x |>{<| ... }
                state.markScope(m_types.C_SWITCH_BODY, m_types.LBRACE);
            }
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (nextElement == m_types.STRING_VALUE || nextElement == m_types.DOT) {
                boolean hasDot = nextElement == m_types.DOT;
                // js object detected ::  |>{<| ./"x" ___ }
                state.markScope(m_types.C_JS_OBJECT, m_types.LBRACE).advance();
                if (hasDot) {
                    state.advance();
                }
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
        } else if (scope != null && scope.isCompositeType(m_types.C_RECORD_EXPR) && state.is(m_types.C_TYPE_BINDING)) {
            // Record type, end the type itself
            state.popEndUntil(m_types.C_TYPE_DECLARATION).popEnd();
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) { // Local open
            // M. |>(<| ... )
            state.markScope(m_types.C_LOCAL_OPEN, m_types.LPAREN);
        } else if (state.is(m_types.C_FUN_EXPR)) { // A function
            // |>(<| .  OR  |>(<| ~
            state.markScope(m_types.C_PARAMETERS, m_types.LPAREN);
        } else if (state.isCurrent(m_types.C_FUN_CALL)) {
            // x |>(<| ...
            state.markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance();
            if (state.getTokenType() != m_types.RPAREN) {
                state.mark(m_types.C_FUN_PARAM);
            }
        } else if (state.is(m_types.C_PARAMETERS) && !state.hasScopeToken()) {
            state.updateScopeToken(m_types.LPAREN);
        } else if (state.is(m_types.C_MACRO_NAME) && state.isParent(m_types.C_ANNOTATION)) {
            // @ann |>(<| ... )
            state.popEnd()
                    .markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.is(m_types.C_MACRO_NAME)) {
            // %raw |>(<| ...
            state.popEnd()
                    .markDummyScope(m_types.C_SCOPED_EXPR, m_types.LPAREN)
                    .advance()
                    .mark(m_types.C_MACRO_BODY);
        } else if (state.is(m_types.C_BINARY_CONDITION)) {
            state.updateScopeToken(m_types.LPAREN);
        } else if (state.strictlyInAny(
                m_types.C_OPEN, m_types.C_INCLUDE, m_types.C_VARIANT_DECLARATION, m_types.C_FUNCTOR_DECLARATION,
                m_types.C_FUNCTOR_CALL, m_types.C_FUNCTOR_RESULT
        )) {

            if (state.isFound(m_types.C_OPEN) || state.isFound(m_types.C_INCLUDE)) { // Functor call
                // open M |>(<| ...
                state.markBefore(state.getIndex() - 1, m_types.C_FUNCTOR_CALL)
                        .markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance();
            } else if (state.isFound(m_types.C_VARIANT_DECLARATION)) { // Variant constructor
                // type t = | Variant |>(<| .. )
                state.markScope(m_types.C_PARAMETERS, m_types.LPAREN)
                        .advance()
                        .mark(m_types.C_FUN_PARAM); // zzz !
            }
            if (state.isFound(m_types.C_FUNCTOR_DECLARATION) || state.isFound(m_types.C_FUNCTOR_CALL)) {
                // module M = |>(<| ...
                // module M = ( ... ) : |>(<| ...
                state.markScope(m_types.C_PARAMETERS, m_types.LPAREN).advance()
                        .mark(m_types.C_FUNCTOR_PARAM);
            }
        } else {
            state.markScope(m_types.C_SCOPED_EXPR, m_types.LPAREN);
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        Marker scope = state.popEndUntilScopeToken(m_types.LPAREN);
        state.advance();

        if (scope != null) {
            if (state.is(m_types.C_PARAMETERS)) {
                state.end();
                if (state.isParent(m_types.C_FUN_CALL)) {
                    state.popEnd().popEnd();
                }
            } else if (state.is(m_types.C_BINARY_CONDITION)) {
                state.end();
            } else {
                state.popEnd();
                if (state.is(m_types.C_ANNOTATION)) {
                    state.popEnd();
                } else if (state.strictlyIn(m_types.C_TAG_PROP_VALUE)) {// && !state.hasScopeToken()) {
                    state.popEndUntil(m_types.C_TAG_PROPERTY).popEnd();
                }
            }
        }
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.strictlyInAny(
                m_types.C_TYPE_DECLARATION, m_types.C_LET_DECLARATION, m_types.C_MODULE_TYPE, m_types.C_MODULE_DECLARATION,
                m_types.C_TAG_PROPERTY, m_types.C_SIG_EXPR, m_types.C_NAMED_PARAM
        )) {

            if (state.isFound(m_types.C_TYPE_DECLARATION)) {
                // type t |> = <| ...
                state.popEndUntilFoundIndex().advance()
                        .mark(m_types.C_TYPE_BINDING);
            } else if (state.isFound(m_types.C_LET_DECLARATION)) {
                // let x |> = <| ...
                state.popEndUntilFoundIndex().advance()
                        .mark(m_types.C_LET_BINDING);
            } else if (state.isFound(m_types.C_MODULE_TYPE)) {
                // module M : T |> = <| ...
                state.popEndUntilFoundIndex().end()
                        .advance()
                        .mark(m_types.C_MODULE_BINDING);
            } else if (state.isFound(m_types.C_MODULE_DECLARATION)) {
                // module M |> = <| ...
                state.advance().mark(m_types.C_MODULE_BINDING);
            } else if (state.isFound(m_types.C_TAG_PROPERTY)) {
                // <Comp prop |> = <| ...
                state.advance().mark(m_types.C_TAG_PROP_VALUE);
            } else if (state.isFound(m_types.C_SIG_EXPR)) {
                state.popEndUntilFoundIndex().end();
                if (state.isGrandParent(m_types.C_NAMED_PARAM)) {
                    state.advance().mark(m_types.C_DEFAULT_VALUE);
                } else if (state.isParent(m_types.C_LET_DECLARATION)) {
                    // let x : M.t |> =<| ...
                    state.popEndUntilFoundIndex().advance()
                            .mark(m_types.C_LET_BINDING);
                }
            } else if (state.isFound(m_types.C_NAMED_PARAM)) {
                // ... => ~x |> =<| ...
                state.advance().mark(m_types.C_DEFAULT_VALUE);
            }

        }
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.is(m_types.C_MODULE_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_EXCEPTION_DECLARATION)) {
            // exception |>E<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.isCurrent(m_types.C_TAG_START) || state.isCurrent(m_types.C_TAG_CLOSE)) {
            // tag name
            state.remapCurrentToken(m_types.TAG_NAME)
                    .wrapWith(m_types.C_UPPER_SYMBOL);
        } else if (state.isCurrent(m_types.C_OPEN)) { // It is a module name/path, or maybe a functor call
            // open |>M<| ...
            state.wrapWith(m_types.C_UPPER_SYMBOL);
            IElementType nextToken = state.getTokenType();
            if (nextToken != m_types.LPAREN && nextToken != m_types.DOT) {
                state.popEndUntil(m_types.C_OPEN).popEnd();
            }
        } else if (state.isCurrent(m_types.C_INCLUDE)) { // It is a module name/path, or maybe a functor call
            // include |>M<| ...
            state.wrapWith(m_types.C_UPPER_SYMBOL);
            IElementType nextToken = state.getTokenType();
            if (nextToken != m_types.LPAREN && nextToken != m_types.DOT) {
                state.popEndUntil(m_types.C_INCLUDE).popEnd();
            }
        } else if (state.is(m_types.C_VARIANT_DECLARATION)) { // Declaring a variant
            // type t = | |>X<| ..
            state.remapCurrentToken(m_types.VARIANT_NAME)
                    .wrapWith(m_types.C_UPPER_IDENTIFIER);
        } else if (state.is(m_types.C_TYPE_BINDING)) {
            IElementType nextToken = state.lookAhead(1);
            if (nextToken == m_types.DOT) { // a path
                state.wrapWith(m_types.C_UPPER_SYMBOL);
            } else { // We are declaring a variant without a pipe before
                // type t = |>X<| | ...
                // type t = |>X<| (...) | ...
                state.remapCurrentToken(m_types.VARIANT_NAME)
                        .mark(m_types.C_VARIANT_DECLARATION)
                        .wrapWith(m_types.C_UPPER_IDENTIFIER);
            }
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.isCurrent(m_types.C_MODULE_BINDING) && nextElementType == m_types.LPAREN) {
                // functor call ::  |>X<| ( ...
                // functor call with path :: A.B.|>X<| ( ...
                state.getCurrentMarker().drop();
                state.mark(m_types.C_FUNCTOR_CALL);
            } else {
                state.wrapWith(m_types.C_UPPER_SYMBOL);
            }
        }

    }

    private void parsePolyVariant(@NotNull ParserState state) {
        if (state.isParent(m_types.C_TYPE_BINDING)) {
            // type t = [ |>#xxx<| ...
            state.mark(m_types.C_VARIANT_DECLARATION);
        }
    }

    private void parseSwitch(@NotNull ParserState state) {
        state.mark(m_types.C_SWITCH_EXPR)
                .advance()
                .mark(m_types.C_BINARY_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(m_types.C_TRY_EXPR).advance()
                .mark(m_types.C_TRY_BODY);
    }

    private void parseCatch(@NotNull ParserState state) {
        if (state.strictlyIn(m_types.C_TRY_BODY)) {
            state.popEndUntilFoundIndex().end();
            //    state.popEnd();
        }
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.isDone(m_types.C_PARAMETERS) && state.isParent(m_types.C_FUN_EXPR) /*|| state.is(m_types.C_FUN_PARAM)*/) {
            state.advance().mark(m_types.C_FUN_BODY);
        } else if (state.strictlyIn(m_types.C_PARAMETERS) && state.isAtIndex(state.getIndex() + 1, m_types.C_FUN_EXPR)) { // parenless param
            // x |>=><| ...
            state.popEndUntil(m_types.C_PARAMETERS).end()
                    .advance()
                    .mark(m_types.C_FUN_BODY);
        } else if (state.strictlyInAny(
                m_types.C_LET_BINDING, m_types.C_PATTERN_MATCH_EXPR, m_types.C_FIELD_VALUE, m_types.C_FUN_EXPR,
                m_types.C_FUN_PARAM, m_types.C_SIG_ITEM, m_types.C_SIG_EXPR
        )) {

            if (state.isFound(m_types.C_LET_BINDING)) {
                // let x = .?. |>=><| ...
                state.rollbackTo(state.getIndex())
                        .mark(m_types.C_LET_BINDING)
                        .mark(m_types.C_FUN_EXPR)
                        .mark(m_types.C_PARAMETERS);
            } else if (state.isFound(m_types.C_PATTERN_MATCH_EXPR)) {
                // switch ( ... ) { | ... |>=><| ... }
                state.popEndUntilFoundIndex()
                        .advance()
                        .mark(m_types.C_PATTERN_MATCH_BODY);
            } else if (state.isFound(m_types.C_FIELD_VALUE)) {
                // { x : .?. |>=><| ...
                state.rollbackTo(state.getIndex())
                        .mark(m_types.C_FIELD_VALUE)
                        .mark(m_types.C_FUN_EXPR)
                        .mark(m_types.C_PARAMETERS);
            } else if (state.isFound(m_types.C_SIG_ITEM)) {
                state.popEndUntilFoundIndex().popEnd();
                state.advance().mark(m_types.C_SIG_ITEM);
            } else if (state.isFound(m_types.C_SIG_EXPR)) {
                state.advance().mark(m_types.C_SIG_ITEM);
            } else if (state.isFound(m_types.C_FUN_PARAM)) { // anonymous function
                // x( y |>=><| ... )
                state.rollbackTo(state.getIndex())
                        .mark(m_types.C_FUN_PARAM)
                        .mark(m_types.C_FUN_EXPR)
                        .mark(m_types.C_PARAMETERS);
            }
            // C_FUN_EXPR -> ignore

        }
    }
}
