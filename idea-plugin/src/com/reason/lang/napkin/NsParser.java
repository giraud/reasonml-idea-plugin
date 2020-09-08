package com.reason.lang.napkin;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.reason.lang.ParserScopeEnum.*;

public class NsParser extends CommonParser<NsTypes> {

    NsParser() {
        super(NsTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;
        state.previousElementType1 = null;

        //long parseStart = System.currentTimeMillis();

        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //break;
            //}

            state.previousElementType2 = state.previousElementType1;
            state.previousElementType1 = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // Special analyse when inside an interpolation string
            if (state.isCurrentResolution(interpolationString) || state.isCurrentResolution(interpolationPart) || state
                    .isCurrentResolution(interpolationReference)) {
                if (tokenType == m_types.ML_STRING_VALUE/*!*/) {
                    state.popEndUntilResolution(interpolationString).
                            advance().
                            popEnd();
                } else if (tokenType == m_types.DOLLAR && state.isCurrentResolution(interpolationPart)) {
                    state.popEnd().advance();
                    IElementType nextElement = state.getTokenType();
                    if (nextElement == m_types.LBRACE) {
                        state.advance().
                                markScope(interpolationReference, m_types.C_INTERPOLATION_REF, m_types.LBRACE);
                    }
                } else if (state.isCurrentResolution(interpolationReference) && tokenType == m_types.RBRACE) {
                    state.popEnd().
                            advance().
                            mark(interpolationPart, m_types.C_INTERPOLATION_PART);
                }
            } else {
                if (tokenType == m_types.SEMI) {
                    parseSemi(state);
                } else if (tokenType == m_types.EQ) {
                    parseEq(state);
                } else if (tokenType == m_types.ARROW) {
                    parseArrow(state);
                } else if (tokenType == m_types.OPTION) {
                    parseOption(state);
                } else if (tokenType == m_types.SOME) {
                    parseSome(state);
                } else if (tokenType == m_types.NONE) {
                    parseNone(state);
                } else if (tokenType == m_types.TRY) {
                    parseTry(state);
                } else if (tokenType == m_types.CATCH) {
                    parseCatch(state);
                } else if (tokenType == m_types.SWITCH) {
                    parseSwitch(state);
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
                } else if (tokenType == m_types.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == m_types.IF) {
                    parseIf(state);
                } else if (tokenType == m_types.DOTDOTDOT) {
                    parseDotDotDot(state);
                } else if (tokenType == m_types.WITH) {
                    parseWith(state);
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
                //// [> ... ]
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
            } else {
                builder.advanceLexer();
            }
        }

        endLikeSemi(state);
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(option, m_types.C_OPTION);
    }

    private void parseSome(@NotNull ParserState state) {
        if (state.isCurrentResolution(patternMatch)) {
            // Defining a pattern match
            // switch (c) { | |>Some<| .. }
            state.remapCurrentToken(m_types.VARIANT_NAME).
                    wrapWith(m_types.C_VARIANT).
                    updateCurrentResolution(patternMatchVariant);
        }
    }

    private void parseNone(@NotNull ParserState state) {
        if (state.isCurrentResolution(patternMatch)) {
            // Defining a pattern match
            // switch (c) { | |>None<| .. }
            state.remapCurrentToken(m_types.VARIANT_NAME).
                    wrapWith(m_types.C_VARIANT).
                    updateCurrentResolution(patternMatchVariant);
        }
    }

    private void parseRaw(@NotNull ParserState state) {
        if (state.isCurrentResolution(macroName)) {
            state.updateCurrentResolution(macroRawNamed).
                    mark(macroRawNamed, m_types.C_MACRO_NAME);
        }
    }

    private void parseIf(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(if_, m_types.C_IF_STMT).
                advance().
                mark(binaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LBRACE) {
            // Mixin
            // ... { |>...<| x ...
            state.updateCurrentResolution(recordUsage).
                    updateCurrentCompositeElementType(m_types.C_RECORD_EXPR).
                    mark(mixin, m_types.C_MIXIN_FIELD);
        }
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorResult)) {
            // module M (X) : ( S |>with<| ... ) = ...
            state.popEnd().
                    mark(functorConstraints, m_types.C_CONSTRAINTS);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        // |>assert<| ...
        state.mark(assert_, m_types.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorConstraint)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
            state.popEnd();
        } else {
            state.popEndUntilStart();
            ParserScope latestScope = state.getLatestScope();
            state.popEnd().advance();

            if (latestScope != null) {
                if (isTypeResolution(latestScope)) {
                    state.markStart(type, m_types.C_EXPR_TYPE);
                } else if (isLetResolution(latestScope)) {
                    state.markStart(let, m_types.C_EXPR_LET);
                } else if (isModuleResolution(latestScope)) {
                    state.markStart(module, m_types.C_MODULE_DECLARATION);
                }
            }
        }
    }

    private void parseComma(@NotNull ParserState state) {
        // Intermediate structures
        if (state.is(m_types.C_FUN_BODY)) {
            // a function is part of something else, close it first
            state.popEnd().popEnd();
        }
        if (state.is(m_types.C_SIG_ITEM)) {
            state.popEnd();
            if (!state.isCurrentResolution(signatureScope)) {
                state.popEnd();
            }
        }

        if (state.isCurrentResolution(recordField) || state.isCurrentResolution(mixin)) {
            state.popEnd().
                    advance();
            IElementType nextToken = state.getTokenType();
            if (nextToken != m_types.RBRACE) {
                state.mark(recordField, m_types.C_RECORD_FIELD);
            }
        } else if (state.is(m_types.C_OBJECT_FIELD) || state.isCurrentResolution(fieldNamed)) {
            boolean isJsObject = state.isCurrentCompositeElementType(m_types.C_OBJECT_FIELD);
            state.popEnd().
                    advance();
            IElementType nextToken = state.getTokenType();
            if (nextToken != m_types.RBRACE) {
                state.mark(field, isJsObject ? m_types.C_OBJECT_FIELD : m_types.C_RECORD_FIELD);
            }
        } else if (state.isCurrentResolution(signatureScope)) {
            state.advance().mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(functionParameter)) {
            state.popEnd();
            state.advance();
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.RPAREN) {
                // not at the end of a list: ie not => (p1, p2<,> )
                state.mark(functionParameter, m_types.C_FUN_PARAM);
            }
        }
    }

    private void parsePipe(@NotNull ParserState state) {
        // Remove intermediate constructions
        if (state.isCurrentResolution(functionBody)) {
            // a function is part of something else, close it first
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(variantDeclaration)) {
            state.popEnd();
        } else if (state.isCurrentResolution(functionParameter) && state.isPreviousResolution(variantConstructor)) {
            state.popEndUntilResolution(typeBinding);
        } else if (state.isCurrentResolution(patternMatchBody)) {
            state.popEndUntilResolution(switchBody);
        }

        if (state.isCurrentResolution(typeBinding)) {
            // type x = |>|<| ...
            state.advance().
                    mark(variantDeclaration, m_types.C_VARIANT_DECL);
        } else if (state.isCurrentResolution(switchBody)) {
            // switch x { |>|<| ... }
            state.advance().
                    mark(patternMatch, m_types.C_PATTERN_MATCH_EXPR);
        } else if (state.isCurrentResolution(tryBodyWith)) {
            // Start of a try handler
            //   try (...) { |>|<| ... }
            state.advance().
                    mark(tryBodyWithHandler, m_types.C_TRY_HANDLER);
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        if (state.isCurrentResolution(maybeRecord)) {
            IElementType nextToken = state.lookAhead(1);
            if (nextToken == m_types.COLON) {
                state.updateCurrentResolution(jsObject).
                        updateCurrentCompositeElementType(m_types.C_JS_OBJECT).
                        mark(field, m_types.C_OBJECT_FIELD);
            }
        }
    }

    private void parseTemplateStringOpen(@NotNull ParserState state) {
        // |>j`<| ...
        state.markScope(interpolationString, m_types.C_INTERPOLATION_EXPR, m_types.JS_STRING_OPEN).
                advance().
                mark(interpolationPart, m_types.C_INTERPOLATION_PART);
    }

    private void parseLet(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(let, m_types.C_EXPR_LET);
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            endLikeSemi(state);
            state.markStart(module, m_types.C_MODULE_DECLARATION);
        }
    }

    private void parseException(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(exception, m_types.C_EXCEPTION_DECLARATION);
    }

    private void parseType(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorConstraints)) {
            // module M = (X) : ( S with |>type<| ... ) = ...
            state.mark(functorConstraint, m_types.C_CONSTRAINT);
        } else if (!state.isCurrentResolution(module)) {
            endLikeSemi(state);
            state.markStart(type, m_types.C_EXPR_TYPE);
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(external, m_types.C_EXPR_EXTERNAL);
    }

    private void parseOpen(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(open, m_types.C_OPEN);
    }

    private void parseInclude(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(include, m_types.C_INCLUDE);
    }

    private void parsePercent(@NotNull ParserState state) {
        state.mark(macroName, m_types.C_MACRO_EXPR);
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.isCurrentResolution(maybeRecord)) {
            // yes it is a record, rollback and remove the maybe
            ParserScope startScope = state.getLatestScope();
            if (startScope != null) {
                startScope.rollbackTo();
                state.pop();
                state.mark(recordUsage, m_types.C_RECORD_EXPR).
                        advance().
                        mark(recordField, m_types.C_RECORD_FIELD);
            }
            return;
        }

        if (state.isCurrentResolution(externalNamed)) {
            // external x |> :<| ...
            state.updateCurrentResolution(externalNamedSignature).
                    advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(letNamed)) {
            // let x |> :<| ...
            state.advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(functorNamedEq)) {
            // module M = (X:Y) |> :<| ...
            state.updateCurrentResolution(functorNamedEqColon).advance();
            IElementType tokenType = state.getTokenType();
            if (tokenType == m_types.LPAREN) {
                // module M = (X:Y) : |>(<| S ... ) = ...
                state.markScope(scope, m_types.C_SCOPED_EXPR, m_types.LPAREN).dummy().advance();
            }
            state.mark(functorResult, m_types.C_FUNCTOR_RESULT);
        } else if (state.isCurrentResolution(recordField) || state.is(m_types.C_OBJECT_FIELD)) {
            state.complete().advance();
            if (state.isInContext(typeBinding)) {
                state.mark(signature, m_types.C_SIG_EXPR).
                        mark(signatureItem, m_types.C_SIG_ITEM);
            }
        } else if (state.isCurrentResolution(field)) {
            state.updateCurrentResolution(fieldNamed);
        } else if (state.isCurrentResolution(functionParameter)) {
            state.advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signatureItem, m_types.C_SIG_ITEM);
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        endLikeSemi(state);
        state.markStart(annotation, m_types.C_ANNOTATION).
                mark(annotationName, m_types.C_MACRO_NAME);
    }

    private void parseLt(@NotNull ParserState state) {
        if (state.isCurrentResolution(option)) {
            state.markScope(optionParameter, m_types.C_SCOPED_EXPR, m_types.LT);
        } else if (state.isCurrentResolution(typeConstrName)) {
            // type parameter
            // type x = t |> < <| 'a >
            state.markScope(typeConstrNameParameters, m_types.C_SCOPED_EXPR, m_types.LT);
        } else if (!state.isCurrentResolution(signatureItem)) {
            // Can be a symbol or a JSX tag
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT || nextTokenType == m_types.OPTION) {
                // Note that option is a ReasonML keyword but also a JSX keyword !
                // Surely a tag
                state.remapCurrentToken(m_types.TAG_LT).
                        mark(jsxTag, m_types.C_TAG).
                        markScope(jsxStartTag, m_types.C_TAG_START, m_types.TAG_LT).
                        advance().
                        remapCurrentToken(m_types.TAG_NAME).
                        wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
            }
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyValue)) {
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(jsxStartTag)) {
            state.wrapWith(m_types.C_TAG_GT).popEnd().mark(jsxTagBody, m_types.C_TAG_BODY);
        } else if (state.isCurrentResolution(jsxTagClose)) {
            state.wrapWith(m_types.C_TAG_GT).popEnd().popEnd();
        } else if (state.isCurrentResolution(optionParameter)) {
            state.advance().popEnd().popEnd();
        } else if (state.isCurrentResolution(typeConstrNameParameters)) {
            state.advance().popEnd();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // A closing tag
            if (state.isCurrentResolution(jsxTagBody)) {
                state.popEnd();
            }

            state.remapCurrentToken(m_types.TAG_LT).
                    mark(jsxTagClose, m_types.C_TAG_CLOSE).
                    advance().
                    remapCurrentToken(m_types.TAG_NAME).
                    wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyValue)) {
            state.popEnd().popEnd();
        }

        state.advance().popEnd().popEnd();
    }

    private void parseLIdent(@NotNull ParserState state) {
        if (state.isCurrentResolution(annotationName)) {
            // Must stop annotation if no dot/@ before
            if (state.previousElementType1 != m_types.DOT && state.previousElementType1 != m_types.ARROBASE) {
                state.popEnd().popEnd();
            }
        }

        if (state.isCurrentResolution(external)) {
            // external |>x<| ...
            state.updateCurrentResolution(externalNamed).
                    wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.isCurrentResolution(let)) {
            // let |>x<| ...
            state.updateCurrentResolution(letNamed).
                    wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else if (state.isCurrentResolution(type)) {
            // type |>x<| ...
            state.updateCurrentResolution(typeNamed).
                    mark(typeConstrName, m_types.C_TYPE_CONSTR_NAME).
                    wrapWith(m_types.C_LOWER_IDENTIFIER);
        } else {
            if (state.isCurrentResolution(jsxStartTag)) {
                // This is a property
                state.remapCurrentToken(m_types.PROPERTY_NAME).
                        mark(jsxTagProperty, m_types.C_TAG_PROPERTY).
                        setWhitespaceSkippedCallback((type, start, end) -> {
                            if (state.isCurrentResolution(jsxTagProperty) || (state.isCurrentResolution(jsxTagPropertyValue) && state.notInScopeExpression())) {
                                if (state.isCurrentResolution(jsxTagPropertyValue)) {
                                    state.popEnd();
                                }
                                state.popEnd();
                                state.setWhitespaceSkippedCallback(null);
                            }
                        });
            } else if (state.isCurrentResolution(recordBinding)) {
                state.mark(recordField, m_types.C_RECORD_FIELD);
            } else {
                IElementType nextElementType = state.lookAhead(1);
                if (!state.isCurrentResolution(signatureItem) && nextElementType == m_types.ARROW) {
                    // Single (paren less) function parameters
                    // |>x<| => ...
                    state.mark(function, m_types.C_FUN_EXPR).
                            mark(functionParameters, m_types.C_FUN_PARAMS).
                            mark(functionParameter, m_types.C_FUN_PARAM);
                }
            }

            if (!state.isCurrentResolution(jsxTagProperty)) {
                state.wrapWith(m_types.C_LOWER_SYMBOL);
            }
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Local open
            // M.|>[<| ... ]
            state.markScope(localOpen, m_types.C_LOCAL_OPEN, m_types.LBRACKET);
        } else {
            state.markScope(bracket, m_types.C_SCOPED_EXPR, m_types.LBRACKET);
        }
    }

    private void parseBracketGt(@NotNull ParserState state) {
        state.markScope(bracketGt, m_types.C_SCOPED_EXPR, m_types.LBRACKET);
    }

    private void parseRBracket(@NotNull ParserState state) {
        ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACKET);
        state.advance();

        if (scope != null) {
            state.popEnd();
        }
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.DOT && state.previousElementType2 == m_types.UIDENT) {
            // Local open a js object
            // Xxx.|>{<| "y" : ... }
            state.mark(localObjectOpen, m_types.C_LOCAL_OPEN);
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == m_types.LIDENT) {
                state.markScope(record, m_types.C_RECORD_EXPR, m_types.LBRACE).
                        advance().
                        mark(field, m_types.C_RECORD_FIELD);
            } else {
                state.markScope(jsObject, m_types.C_JS_OBJECT, m_types.LBRACE).
                        advance().
                        mark(field, m_types.C_OBJECT_FIELD);
            }
        } else if (state.isCurrentResolution(typeBinding)) {
            boolean isJsObject = state.lookAhead(1) == m_types.DOT;
            state.markScope(isJsObject ? jsObject : recordBinding, isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE);
            if (isJsObject) {
                state.advance().
                        advance().
                        mark(field, m_types.C_OBJECT_FIELD);
            }
        } else if (state.isCurrentResolution(tryBodyWith)) {
            // A try expression
            //   try ... |>{<| ... }
            state.markScope(tryBodyWith, m_types.C_TRY_HANDLERS, m_types.LBRACE);
        } else if (state.isCurrentResolution(moduleNamedEq) /*|| state.isCurrentResolution(moduleNamedSignature)*/) {
            state.markScope(moduleBinding, m_types.C_SCOPED_EXPR, m_types.LBRACE);
        } else if (state.isCurrentResolution(letBinding)) {
            state.markScope(maybeRecord, m_types.C_SCOPED_EXPR, m_types.LBRACE);
        } else if (state.isCurrentResolution(binaryCondition)) {
            state.popEnd();
            if (state.isCurrentResolution(if_)) {
                // if x |>{<| ... }
                state.markScope(ifThenStatement, m_types.C_SCOPED_EXPR, m_types.LBRACE);
            } else if (state.isCurrentResolution(switch_)) {
                // switch x |>{<| ... }
                state.markScope(switchBody, m_types.C_SCOPED_EXPR, m_types.LBRACE);
            }
        } else if (state.isCurrentResolution(jsxTagPropertyValue)) {
            // A scoped property
            state.updateScopeToken(m_types.LBRACE);
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (nextElement == m_types.STRING_VALUE || nextElement == m_types.DOT) {
                // js object detected
                // |>{<| ./"x" ___ }
                state.markScope(jsObject, m_types.C_JS_OBJECT, m_types.LBRACE).
                        advance().
                        advance().
                        mark(field, m_types.C_OBJECT_FIELD);
            } else {
                state.markScope(scope, m_types.C_SCOPED_EXPR, m_types.LBRACE);
            }
        }
    }

    private void parseRBrace(@NotNull ParserState state) {
        ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACE);
        state.advance();
        if (scope != null) {
            state.popEnd();
        }

        if (state.isCurrentResolution(jsxTagPropertyEq) || state.isCurrentResolution(localObjectOpen)) {
            state.popEnd();
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.isCurrentResolution(annotationName)) {
            // @ann |>(<| ... )
            state.popEnd().
                    markScope(annotationParameter, m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.isCurrentResolution(signatureItem) && state.previousElementType1 != m_types.LIDENT) {
            if (state.isPreviousResolution(signature)) {
                state.updateCurrentResolution(signatureScope).
                        updateCurrentCompositeElementType(m_types.C_SCOPED_EXPR).
                        updateScopeToken(m_types.LPAREN).
                        advance().
                        mark(signatureItem, m_types.C_SIG_ITEM);
            } else {
                state.markScope(signatureScope, m_types.C_SCOPED_EXPR, m_types.LPAREN);
            }
        } else if (state.isCurrentResolution(macroRawNamed)) {
            state.popEnd().
                    markScope(rawBody, m_types.C_MACRO_RAW_BODY, m_types.LPAREN);
        } else if (state.isCurrentResolution(moduleBinding) && state.previousElementType1 != m_types.UIDENT) {
            // This is a functor
            //  module M = |>(<| ... )
            state.popCancel(). // remove previous module binding
                    updateCurrentResolution(functorNamedEq).
                    updateCurrentCompositeElementType(m_types.C_FUNCTOR).
                    markScope(functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN).
                    advance().
                    mark(functorParam, m_types.C_FUNCTOR_PARAM);
        } else if (state.isCurrentResolution(maybeFunctorCall)) {
            // We know now that it is really a functor call
            //  module M = X |>(<| ... )
            //  open X |>(<| ... )
            state.updateCurrentResolution(functorCall).complete().
                    markScope(functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                    advance().
                    mark(functionParameter, m_types.C_FUN_PARAM);
        } else if (state.isCurrentResolution(variantDeclaration)) {
            // Variant params
            // type t = | Variant |>(<| .. )
            state.markScope(variantConstructor, m_types.C_FUN_PARAMS, m_types.LPAREN).
                    advance().
                    mark(functionParameter, m_types.C_FUN_PARAM);
        } else if (state.isCurrentResolution(patternMatchVariant)) {
            // It's a constructor in a pattern match
            // switch x { | Variant |>(<| ... ) => ... }
            state.markScope(patternMatchVariantConstructor, m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN);
        } else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Local open
            // M. |>(<| ... )
            state.markScope(localOpen, m_types.C_LOCAL_OPEN, m_types.LPAREN);
        } else if (state.isCurrentResolution(let)) {
            // Deconstructing a term
            //  let |>(<| a, b ) =
            state.updateCurrentResolution(letNamed).
                    markScope(deconstruction, m_types.C_DECONSTRUCTION, m_types.LPAREN);
        } else if (state.previousElementType1 == m_types.LIDENT) {
            // Calling a function
            state.markScope(functionCallParams, m_types.C_FUN_CALL_PARAMS, m_types.LPAREN).
                    advance();
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.RPAREN) {
                state.mark(functionParameter, m_types.C_FUN_PARAM);
            }
        } else {
            IElementType nextTokenType = state.lookAhead(1);

            if (nextTokenType == m_types.DOT || nextTokenType == m_types.TILDE) {
                // A function
                // |>(<| .  OR  |>(<| ~
                state.mark(function, m_types.C_FUN_EXPR).
                        markScope(functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                        advance();
                if (nextTokenType == m_types.DOT) {
                    state.advance();
                }
                state.mark(functionParameter, m_types.C_FUN_PARAM);
            } else {
                state.markScope(genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN);
            }
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        ParserScope startScope = state.popEndUntilOneOfElementType(m_types.LPAREN);
        if (startScope != null && startScope.isResolution(genericExpression)) {
            IElementType aheadType = state.lookAhead(1);
            if (aheadType == m_types.ARROW) {
                // if current resolution is UNKNOWN and next item is an arrow, it means we are processing a function definition,
                // we must rollback to the start of the scope and start the parsing again, but this time with exact information!
                startScope.rollbackTo();
                state.pop();
                state.mark(function, m_types.C_FUN_EXPR).
                        markScope(functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                        advance().
                        mark(functionParameter, m_types.C_FUN_PARAM);
                return;
            }
        }

        state.advance().popEnd();

        if (state.isCurrentResolution(variantDeclaration)) {
            state.popEndUntilResolution(typeBinding);
        } else if (state.isCurrentResolution(annotation)) {
            state.popEnd();
        }
    }

    private void parseEq(@NotNull ParserState state) {
        // Intermediate constructions
        if (state.isCurrentResolution(signatureItem)) {
            state.popEndUntilResolution(signature).popEnd();
        }

        if (state.isCurrentResolution(typeConstrName)) {
            // type t |> = <| ...
            state.popEnd().
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    mark(typeBinding, m_types.C_TYPE_BINDING);
        } else if (state.isCurrentResolution(let) || state.isCurrentResolution(letNamed)/* || state.isCurrentResolution(letNamedAttribute)*/ || state
                .isCurrentResolution(letNamedSignature)) {
            state.updateCurrentResolution(letNamedEq).
                    advance().
                    mark(letBinding, m_types.C_LET_BINDING);
        } else if (state.isCurrentResolution(module)) {
            // module M |> = <| ...
            state.advance().
                    markDummy(moduleBinding, m_types.C_UNKNOWN_EXPR/*C_DUMMY*/);
        } else if (state.isCurrentResolution(jsxTagProperty)) {
            state.updateCurrentResolution(jsxTagPropertyEq).
                    advance().
                    mark(jsxTagPropertyValue, m_types.C_TAG_PROP_VALUE);
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        // Don't pop the scopes
        state.popEndUntilStart();
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }
        if (state.is(m_types.C_MODULE_DECLARATION)) {
            // module |>M<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
            return;
        }
        if (state.is(m_types.C_EXCEPTION_DECLARATION)) {
            // exception |>E<| ...
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
            return;
        }

        if (state.isCurrentResolution(open)) {
            // It is a module name/path, or maybe a functor call
            // open |>M<| ...
            state.markOptional(maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if (state.isCurrentResolution(include)) {
            // It is a module name/path, or maybe a functor call
            // include |>M<| ...
            state.markOptional(maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if (state.isCurrentResolution(moduleBinding)) {
            // it might be a module functor call
            // module M = |>X<| ( ... )
            state.markOptional(maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if ((state.isCurrentResolution(jsxStartTag) || state.isCurrentResolution(jsxTagClose)) && state.previousElementType1 == m_types.DOT) {
            // a namespaced custom component
            state.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.isCurrentResolution(variantDeclaration)) {
            // Declaring a variant
            // type t = | |>X<| ..
            state.wrapWith(m_types.C_UPPER_IDENTIFIER);
            return;
        } else if (state.isCurrentResolution(patternMatch)) {
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType != m_types.DOT) {
                // Defining a pattern match
                // switch (c) { | |>X<| .. }
                state.remapCurrentToken(m_types.VARIANT_NAME).
                        wrapWith(m_types.C_VARIANT).
                        updateCurrentResolution(patternMatchVariant);

                return;
            }
        } else {
            IElementType nextElementType = state.lookAhead(1);

            if (state.isCurrentResolution(typeBinding) && (nextElementType == m_types.PIPE || nextElementType == m_types.LPAREN)) {
                // We are declaring a variant without a pipe before
                // type t = |>X<| | ...
                // type t = |>X<| (...) | ...
                state.remapCurrentToken(m_types.VARIANT_NAME).
                        mark(variantDeclaration, m_types.C_VARIANT_DECL).
                        wrapWith(m_types.C_UPPER_IDENTIFIER);
                return;
            } else if (!state.isCurrentResolution(moduleNamedEq) && !state.isCurrentResolution(maybeFunctorCall)) {
                if (nextElementType == m_types.LPAREN) {
                    state.remapCurrentToken(m_types.VARIANT_NAME);
                    // A variant with a constructor
                    if (state.isCurrentResolution(typeNamedEq)) {
                        state.mark(typeNamedEqVariant, m_types.C_VARIANT_DECL);
                    }
                    state.wrapWith(m_types.C_VARIANT);
                    return;
                } else if (nextElementType != m_types.DOT) {
                    // Must be a variant call
                    state.remapCurrentToken(m_types.VARIANT_NAME).
                            wrapWith(m_types.C_VARIANT);
                    return;
                }
            }
        }

        state.wrapWith(m_types.C_UPPER_SYMBOL);
    }

    private void parsePolyVariant(@NotNull ParserState state) {
        if (state.isCurrentResolution(patternMatch)) {
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == m_types.LPAREN) {
                state.wrapWith(m_types.C_VARIANT);
                state.updateCurrentResolution(patternMatchVariant);
            }
        }
    }

    private void parseSwitch(@NotNull ParserState state) {
        boolean inScope = state.isScopeTokenElementType(m_types.LBRACE);
        state.mark(switch_, m_types.C_SWITCH_EXPR).
                setStart(inScope).
                advance().
                mark(binaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(try_, m_types.C_TRY_EXPR).
                advance().
                mark(tryBody, m_types.C_TRY_BODY);
    }

    private void parseCatch(@NotNull ParserState state) {
        if (state.isCurrentResolution(tryBody)) {
            state.popEnd().
                    updateCurrentResolution(tryBodyWith);
        }
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.isCurrentResolution(function) || state.isCurrentResolution(functionParameter)) {
            // param(s) |>=><| body
            state.popEndUntilResolution(function).
                    advance().
                    mark(functionBody, m_types.C_FUN_BODY);
        } else if (state.isCurrentResolution(signature)) {
            state.advance().
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(signatureItem)) {
            state.popEnd();
            if (!state.isCurrentResolution(signatureScope)) {
                state.popEndUntilResolution(signature);
            }
            state.advance().
                    mark(signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(functorNamedEq) || state.isCurrentResolution(functorNamedEqColon) || state.isCurrentResolution(functorResult)) {
            // module Make = (M) : R |>=><| ...
            if (state.isCurrentResolution(functorResult)) {
                state.popEnd();
            }
            state.advance().
                    mark(functorBinding, m_types.C_FUNCTOR_BINDING);
        } else if (state.isCurrentResolution(patternMatchVariant) || state.isCurrentResolution(patternMatchVariantConstructor)) {
            // switch ( ... ) { | ... |>=><| ... }
            state.advance().
                    mark(patternMatchBody, m_types.C_PATTERN_MATCH_BODY).setStart();
        }
    }

    private void endLikeSemi(@NotNull ParserState state) {
        state.popEndUntilScope();
    }
}
