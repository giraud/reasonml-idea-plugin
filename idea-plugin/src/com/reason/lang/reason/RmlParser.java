package com.reason.lang.reason;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.reason.lang.ParserScope.*;
import static com.reason.lang.ParserScopeEnum.*;

public class RmlParser extends CommonParser<RmlTypes> {

    RmlParser() {
        super(RmlTypes.INSTANCE);
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
            tokenType = state.getTokenType();
            if (tokenType == null) {
                break;
            }

            // special analyse when inside an interpolation string
            if (state.isCurrentContextRml(interpolationString)) {
                if (tokenType == m_types.JS_STRING_CLOSE) {
                    parseJsStringClose(state);
                } else if (tokenType == m_types.DOLLAR) {
                    if (state.isCurrentResolution(interpolationPart)) {
                        state.popEnd();
                        state.updateCurrentResolution(interpolationReference);
                    }
                } else if (state.isCurrentResolution(interpolationReference)) {
                    state.wrapWith(m_types.C_INTERPOLATION_REF);
                    state.updateCurrentResolution(interpolationString);
                } else if (state.currentResolution() != interpolationPart) {
                    state.add(mark(builder, interpolationString, interpolationPart, m_types.C_INTERPOLATION_PART).complete());
                }
            } else {

                // special keywords that can be used as lower identifier in records
                if (tokenType == m_types.REF && state.isCurrentResolution(recordBinding)) {
                    parseLIdent(builder, state);
                } else if (tokenType == m_types.METHOD && state.isCurrentResolution(recordBinding)) {
                    parseLIdent(builder, state);
                }
                //
                else if (tokenType == m_types.SEMI) {
                    parseSemi(state);
                } else if (tokenType == m_types.EQ) {
                    parseEq(builder, state);
                } else if (tokenType == m_types.UNDERSCORE) {
                    parseUnderscore(state);
                } else if (tokenType == m_types.ARROW) {
                    parseArrow(builder, state);
                } else if (tokenType == m_types.OPTION) {
                    parseOption(state);
                } else if (tokenType == m_types.SOME) {
                    parseSome(state);
                } else if (tokenType == m_types.NONE) {
                    parseNone(state);
                } else if (tokenType == m_types.TRY) {
                    parseTry(state);
                } else if (tokenType == m_types.SWITCH) {
                    parseSwitch(builder, state);
                } else if (tokenType == m_types.LIDENT) {
                    parseLIdent(builder, state);
                } else if (tokenType == m_types.UIDENT) {
                    parseUIdent(builder, state);
                } else if (tokenType == m_types.ARROBASE) {
                    parseArrobase(state);
                } else if (tokenType == m_types.PERCENT) {
                    parsePercent(state);
                } else if (tokenType == m_types.COLON) {
                    parseColon(builder, state);
                } else if (tokenType == m_types.RAW) {
                    parseRaw(state);
                } else if (tokenType == m_types.STRING_VALUE) {
                    parseStringValue(builder, state);
                } else if (tokenType == m_types.PIPE) {
                    parsePipe(builder, state);
                } else if (tokenType == m_types.COMMA) {
                    parseComma(builder, state);
                } else if (tokenType == m_types.AND) {
                    parseAnd(state);
                } else if (tokenType == m_types.FUN) {
                    parseFun(builder, state);
                } else if (tokenType == m_types.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == m_types.IF) {
                    parseIf(state);
                } else if (tokenType == m_types.DOT) {
                    parseDot(state);
                } else if (tokenType == m_types.DOTDOTDOT) {
                    parseDotDotDot(state);
                } else if (tokenType == m_types.WITH) {
                    parseWith(state);
                }
                // ( ... )
                else if (tokenType == m_types.LPAREN) {
                    parseLParen(builder, state);
                } else if (tokenType == m_types.RPAREN) {
                    parseRParen(builder, state);
                }
                // { ... }
                else if (tokenType == m_types.LBRACE) {
                    parseLBrace(builder, state);
                } else if (tokenType == m_types.RBRACE) {
                    parseRBrace(state);
                }
                // [ ... ]
                // [> ... ]
                else if (tokenType == m_types.LBRACKET) {
                    parseLBracket(builder, state);
                } else if (tokenType == m_types.BRACKET_GT) {
                    parseBracketGt(state);
                } else if (tokenType == m_types.RBRACKET) {
                    parseRBracket(builder, state);
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
                    parseMlStringOpen(builder, state);
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
                    parseType(builder, state);
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
            } else {
                builder.advanceLexer();
            }
        }
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
            // switch (c) { | |>Some<| .. }
            state.remapCurrentToken(m_types.VARIANT_NAME).
                    wrapWith(m_types.C_VARIANT).
                    updateCurrentResolution(patternMatchVariant);
        }
    }

    private void parseRaw(@NotNull ParserState state) {
        if (state.isCurrentResolution(macroName)) {
            state.advance().complete().popEnd().updateCurrentContextRml(macroRaw).updateCurrentResolution(macroRawNamed);
        }
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
            state.complete();
        }
    }

    private void parseIf(@NotNull ParserState state) {
        state.mark(ifThenStatement, m_types.C_IF_STMT);
    }

    private void parseDot(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LBRACE && state.isCurrentResolution(jsObject)) {
            // Js object definition
            // ... { |>.<| ... }
            state.advance().
                    mark(objectField, m_types.C_OBJECT_FIELD);
        }
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LBRACE) {
            // Mixin:
            // { |>...<| x ...
            state.updateCurrentContextRml(recordUsage).
                    updateCurrentResolution(recordBinding).
                    updateCurrentCompositeElementType(m_types.C_RECORD_EXPR).
                    mark(mixin, m_types.C_MIXIN_FIELD);
        }
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorNamedColonResult)) {
            // module M (X) : ( S |>with<| ... ) = ...
            state.complete().
                    popEnd().
                    mark(functorConstraints, m_types.C_CONSTRAINTS);
        } else if (state.isCurrentResolution(include)) {
            state.mark(functorConstraints, m_types.C_CONSTRAINTS);
        }
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(assert_, m_types.C_ASSERT_STMT).
                advance();
    }

    private void parseFun(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(letNamedEq)) {
            // let x = |>fun<| | ..
            // fun keyword is equivalent to a switch body
            state.add(mark(builder, funPattern, switchBody, m_types.C_FUN_EXPR).complete());
        }
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorConstraint)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
            state.complete().popEnd();
        } else {
            ParserScope latestScope = state.popEndUntilScope();

            if (isTypeResolution(latestScope)) {
                state.advance().
                        mark(type, m_types.C_EXPR_TYPE).
                        mark(typeConstrName, m_types.C_TYPE_CONSTR_NAME);
            } else if (isLetResolution(latestScope)) {
                state.advance().
                        mark(let, m_types.C_EXPR_LET);
            } else if (isModuleResolution(latestScope)) {
                state.advance().
                        mark(module, m_types.C_EXPR_MODULE);
            }
        }
    }

    private void parseComma(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(functionBody)) {
            // a function is part of something else, close it first
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(functionParameterNamedSignatureItem)) {
            state.complete().
                    popEndUntilResolution(functionParameterNamed).updateCurrentResolution(functionParameterNamedSignature);
        }

        if ((state.isCurrentResolution(signatureItem) || state.isCurrentResolution(signatureItemEq)) && !state.isCurrentContextRml(recordSignature)) {
            state.popEnd();
            state.advance();
            state.add(mark(builder, state.currentContextRml(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentContextRml(signature)) {
            state.popEnd();
        }

        if (state.isCurrentContextRml(recordSignature)) {
            state.complete().
                    popEndUntilResolution(recordField).popEnd().
                    advance().
                    add(mark(builder, state.currentContextRml(), recordField, m_types.C_RECORD_FIELD));
        } else if (state.isCurrentResolution(recordField)) {
            state.popEnd().advance().add(mark(builder, state.currentContextRml(), recordField, m_types.C_RECORD_FIELD));
        } else if (state.isCurrentResolution(objectFieldNamed)) {
            state.popEnd().advance().add(mark(builder, state.currentContextRml(), objectField, m_types.C_OBJECT_FIELD));
        } else if (state.isCurrentResolution(mixin)) {
            state.popEnd();
        } else if (state.isCurrentResolution(functionParameter) || state.isCurrentResolution(functionParameterNamed) || state
                .isCurrentResolution(functionParameterNamedSignature) || state.isCurrentResolution(functionParameterNamedBinding)) {
            state.complete();
            if (state.isCurrentResolution(functionParameterNamedBinding)) {
                state.popEndUntilResolution(functionParameters);
            } else {
                state.popEnd();
            }
            state.advance().
                    add(mark(builder, state.currentContextRml(), functionParameter, m_types.C_FUN_PARAM));
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.RPAREN) {
                // not at the end of a list: ie not => (p1, p2<,> )
                state.complete();
            }
        } else if (state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
            // We don't know yet but we need to complete the marker
            state.complete();
            state.popEnd();
        } else if (state.isCurrentContextRml(let) && state.isCurrentResolution(genericExpression)) {
            // It must be a deconstruction
            // let ( a |>,<| b ) = ..
            state.updateCurrentResolution(deconstruction).updateCurrentCompositeElementType(m_types.C_DECONSTRUCTION);
        }
    }

    private void parsePipe(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        // By default, a pattern match
        if (state.isCurrentResolution(patternMatchBody)) {
            state.popEndUntilResolution(switchBody);
        }
        if (state.isCurrentResolution(patternMatchVariant) || state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEnd();
        }

        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(mark(builder, state.currentContextRml(), typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
        } else if (state.isCurrentResolution(tryBodyWith)) {
            // Start of a try handler
            //   try (..) { |>|<| .. }
            state.add(mark(builder, state.currentContextRml(), tryBodyWithHandler, m_types.C_TRY_HANDLER).complete());
        } else if (state.isCurrentResolution(variant) && state.isCurrentContextRml(typeBinding)) {
            state.popEndUntilResolution(typeNamedEqVariant).
                    add(mark(builder, state.currentContextRml(), variant, m_types.C_VARIANT_DECL).complete());
        } else {
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.add(mark(builder, state.currentContextRml(), patternMatch, m_types.C_PATTERN_MATCH_EXPR).complete());
        }
    }

    private void parseStringValue(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentContextRml(macroRaw)) {
            state.wrapWith(m_types.C_MACRO_RAW_BODY);
        } else if (state.isCurrentContextRml(raw)) {
            state.complete().add(mark(builder, rawBody, m_types.C_MACRO_RAW_BODY).complete()).advance().popEnd();
        } else if (state.isCurrentResolution(annotationName)) {
            state.popEndUntilScope();
        } else if (state.isCurrentResolution(maybeRecordUsage)) {
            IElementType nextToken = state.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.updateCurrentContextRml(object).updateCurrentResolution(object).updateCurrentCompositeElementType(m_types.C_JS_OBJECT);
                state.add(markScope(builder, object, objectField, m_types.C_OBJECT_FIELD, m_types.STRING_VALUE));
            }
        } else if (state.isCurrentResolution(object)) {
            state.add(markScope(builder, object, objectField, m_types.C_OBJECT_FIELD, m_types.STRING_VALUE));
        }
    }

    private void parseMlStringOpen(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
            state.endAny();
        }

        if (state.isCurrentContextRml(macroRaw)) {
            state.add(mark(builder, state.currentContextRml(), macroRawBody, m_types.C_MACRO_RAW_BODY).complete());
        }

        state.add(markScope(builder, state.currentContextRml(), multilineStart, m_types.C_ML_INTERPOLATOR, m_types.ML_STRING_OPEN));
    }

    private void parseMlStringClose(@NotNull ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.ML_STRING_OPEN);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseJsStringOpen(@NotNull ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) { // use space notifier like in tag ?
            state.endAny();
        }

        state.markScope(interpolationString, m_types.C_INTERPOLATION_EXPR, m_types.JS_STRING_OPEN);
    }

    private void parseJsStringClose(@NotNull ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.JS_STRING_OPEN);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseLet(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(let, m_types.C_EXPR_LET);
    }

    private void parseVal(@NotNull ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            state.popEndUntilScope();
            if (state.isCurrentResolution(clazzBody)) {
                state.mark(clazzField, m_types.C_CLASS_FIELD);
            } else {
                state.mark(let, m_types.C_EXPR_LET);
            }
        }
    }

    private void parsePub(@NotNull ParserState state) {
        state.popEndUntilScope();
        if (state.isCurrentResolution(clazzBody)) {
            state.mark(clazzMethod, m_types.C_CLASS_METHOD);
        }
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            state.popEndUntilScope();
            state.mark(module, m_types.C_EXPR_MODULE);
        }
    }

    private void parseException(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(exception, m_types.C_EXPR_EXCEPTION);
    }

    private void parseClass(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(clazz, m_types.C_CLASS_STMT);
    }

    private void parseType(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(functorConstraints)) {
            // module M = (X) : ( S with |>type<| ... ) = ...
            state.add(mark(builder, functorConstraints, functorConstraint, m_types.C_CONSTRAINT));
        } else if (state.isCurrentResolution(includeConstraints)) {
            // include M with |>type<| ...
            state.add(mark(builder, includeConstraints, includeConstraint, m_types.C_CONSTRAINT));
        } else if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz)) {
            if (!state.isCurrentResolution(letNamedSignature)) {
                state.popEndUntilScope();
            }
            state.mark(type, m_types.C_EXPR_TYPE).
                    advance().
                    mark(typeConstrName, m_types.C_TYPE_CONSTR_NAME);
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(external, m_types.C_EXPR_EXTERNAL);
    }

    private void parseOpen(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(open, m_types.C_OPEN);
    }

    private void parseInclude(@NotNull ParserState state) {
        state.popEndUntilScope();
        state.mark(include, m_types.C_INCLUDE);
    }

    private void parsePercent(@NotNull ParserState state) {
        if (state.isCurrentResolution(macro)) {
            state.mark(macroName, m_types.C_MACRO_NAME);
        } else if (state.isCurrentResolution(letNamed)) {
            // let name|>%<|private = ...
            state.mark(letNamedAttribute, m_types.C_LET_ATTR);
        } else {
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == m_types.RAW) {
                // |>%<| raw ...
                state.mark(raw, m_types.C_RAW);
            }
        }
    }

    private void parseColon(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentContextRml(maybeRecord) && state.isCurrentCompositeElementType(m_types.C_SCOPED_EXPR)) {
            // yes it is a record, remove the maybe
            //ParserScope fieldState = state.pop();
            state.popCancel();
            ParserScope latestScope = state.getLatestScope();
            state.pop();
            latestScope.rollbackTo();

            state.add(mark(builder, latestScope.getContext(), latestScope.getResolution(), latestScope.getCompositeType())).complete();
            state.add(mark(builder, recordUsage, record, m_types.C_RECORD_EXPR)).complete();
            state.advance();
            state.add(mark(builder, recordUsage, recordField, m_types.C_RECORD_FIELD));
            return;
        }

        if (state.isCurrentResolution(externalNamed)) {
            state.updateCurrentResolution(externalNamedSignature).
                    advance().
                    add(mark(builder, signature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(letNamed)) {
            state.advance();
            state.add(mark(builder, signature, letNamedSignature, m_types.C_SIG_EXPR).complete());
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.LPAREN) {
                state.add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
            }
        } else if (state.isCurrentResolution(moduleNamed)) {
            // module M |> :<| ...
            state.updateCurrentResolution(moduleNamedSignature).
                    complete();
        } else if (state.isCurrentResolution(functorNamedEq)) {
            // module M = (X:Y) |> :<| ...
            state.updateCurrentResolution(functorNamedEqColon).advance();
            IElementType tokenType = state.getTokenType();
            if (tokenType == m_types.LPAREN) {
                // module M = (X:Y) : |>(<| S ... ) = ...
                state.add(markScope(builder, state.currentContextRml(), scope, m_types.C_SCOPED_EXPR, m_types.LPAREN)).advance();
            }
            state.add(mark(builder, state.currentContextRml(), functorNamedColonResult, m_types.C_FUNCTOR_RESULT));
        } else if (state.isCurrentResolution(functorParam)) {
            state.updateCurrentResolution(functorParamColon);
        } else if (state.isCurrentResolution(recordField)) {
            state.complete().advance();
            if (!state.isCurrentContextRml(recordUsage)) {
                state.add(mark(builder, recordSignature, signature, m_types.C_SIG_EXPR).complete());
                state.add(mark(builder, recordSignature, signatureItem, m_types.C_SIG_ITEM).complete());
            }
        } else if (state.isCurrentResolution(objectField)) {
            state.complete();
            state.updateCurrentResolution(objectFieldNamed);
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateCurrentResolution(functionParameterNamed).
                    advance().
                    add(mark(builder, signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, functionParameterNamedSignatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(paren) && state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
            state.complete().
                    updateCurrentContextRml(functionParameter).updateCurrentResolution(functionParameterNamed).
                    advance().
                    add(mark(builder, signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signatureItem, functionParameterNamedSignatureItem, m_types.C_SIG_ITEM).complete());
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        if (state.isCurrentResolution(annotation)) {
            state.mark(annotationName, m_types.C_MACRO_NAME);
        }
    }

    private void parseLt(@NotNull ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT
                || nextTokenType == m_types.OPTION) { // Note that option is a ReasonML keyword but also a JSX keyword !
            // Surely a tag
            state.remapCurrentToken(m_types.TAG_LT).
                    mark(jsxTag, m_types.C_TAG).
                    markScope(jsxStartTag, m_types.C_TAG_START, m_types.TAG_LT).
                    advance().
                    remapCurrentToken(m_types.TAG_NAME).
                    wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // A closing tag
            if (state.isCurrentContextRml(jsxTagBody)) {
                state.popEnd();
            }

            state.remapCurrentToken(m_types.TAG_LT);
            state.mark(jsxTagClose, m_types.C_TAG_CLOSE);

            state.advance();

            state.remapCurrentToken(m_types.TAG_NAME);
            state.wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(jsxStartTag)) {
            state.wrapWith(m_types.C_TAG_GT).popEnd().mark(jsxTagBody, m_types.C_TAG_BODY);
        } else if (state.isCurrentResolution(jsxTagClose)) {
            state.wrapWith(m_types.C_TAG_GT).popEnd().popEnd();
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            state.popEnd().popEnd();
        }

        state.advance().popEnd().popEnd();
    }

    private void parseLIdent(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(maybeRecord)) {
            // Maybe a record, we must check
            IElementType nextTokenType = state.lookAhead(1);
            if (nextTokenType == m_types.COLON) {
                // Yes, this is a record binding
                state.updateCurrentResolution(recordBinding);
                state.updateCurrentCompositeElementType(m_types.C_RECORD_EXPR);
            }
        }

        if (state.isCurrentResolution(typeConstrName)) {
            // type |>x<| ..
            state.updateCurrentResolution(typeNamed);
            state.complete();
            state.setPreviousCompleteOLD();
        } else if (state.isCurrentResolution(functionParameters)) {
            state.mark(functionParameter, m_types.C_FUN_PARAM);
        } else if (state.isCurrentResolution(external)) {
            // external |>x<| ..
            state.updateCurrentResolution(externalNamed);
            state.complete();
        } else if (state.isCurrentResolution(let)) {
            // let |>x<| ..
            state.updateCurrentResolution(letNamed).complete();
        } else if (state.isCurrentResolution(letNamedEq)) {
            if (state.previousElementType1 == m_types.EQ) {
                // let x = |>c<| => ...
                IElementType nextElementType = state.lookAhead(1);
                if (nextElementType == m_types.ARROW) {
                    // Single (paren less) function parameters
                    state.add(mark(builder, function, letNamedBindingFunction, m_types.C_FUN_EXPR).complete());
                    state.add(mark(builder, function, functionParameters, m_types.C_FUN_PARAMS).complete());
                    state.add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
                }
            }
        } else if (state.isCurrentResolution(macroName)) {
            state.complete();
            boolean isRaw = "raw".equals(state.getTokenText());
            if (isRaw) {
                state.advance();
            }
            state.popEnd();
            if (isRaw) {
                state.updateCurrentContextRml(macroRaw);
            }
            state.updateCurrentResolution(macroNamed);
            return;
        } else if (state.isCurrentResolution(clazz)) {
            // class |>x<| ...
            state.updateCurrentResolution(clazzNamed);
            state.complete();
        } else if (state.isCurrentResolution(clazzField)) {
            // class c ... val |>x<| ...
            state.updateCurrentResolution(clazzFieldNamed);
            state.complete();
        } else if (state.isCurrentResolution(clazzMethod)) {
            // method |>x<| ...
            state.updateCurrentResolution(clazzMethodNamed);
            state.complete();
        } else if (state.isCurrentResolution(jsxStartTag)) {
            // This is a property
            state.popEndUntilScope();
            state.remapCurrentToken(m_types.PROPERTY_NAME);
            state.mark(jsxTagProperty, m_types.C_TAG_PROPERTY);
            state.setWhitespaceSkippedCallback((type, start, end) -> {
                if (state.isCurrentResolution(jsxTagProperty) || (state.isCurrentResolution(jsxTagPropertyEqValue) && state.notInScopeExpression())) {
                    if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
                        state.popEnd();
                    }
                    state.popEnd();
                    state.setWhitespaceSkippedCallback(null);
                }
            });
        } else if (state.isCurrentResolution(recordBinding)) {
            state.add(mark(builder, state.currentContextRml(), recordField, m_types.C_RECORD_FIELD));
        } else if (state.isCurrentResolution(record)) {
            state.add(mark(builder, recordUsage, recordField, m_types.C_RECORD_FIELD));
        } else if (state.isCurrentResolution(jsObject)) {
            state.add(mark(builder, state.currentContextRml(), objectField, m_types.C_OBJECT_FIELD));
        } else if (state.isCurrentResolution(mixin)) {
            state.complete();
        } else if (shouldStartExpression(state)) {
            // ?
        } else if (!state.isCurrentContextRml(signature)) {
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == m_types.ARROW) {
                // Single (paren less) function parameters
                // <c> => ...
                state.add(mark(builder, function, function, m_types.C_FUN_EXPR).complete());
                state.add(mark(builder, function, functionParameters, m_types.C_FUN_PARAMS).complete());
                state.add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
            }
        }

        if (!state.isCurrentResolution(jsxTagProperty)) {
            state.wrapWith(m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseLBracket(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            if (state.isCurrentResolution(recordField)) {
                state.popEnd();
                state.add(markScope(builder, recordFieldAnnotation, annotation, m_types.C_ANNOTATION, m_types.LBRACKET));
            } else {
                state.markScope(annotation, m_types.C_ANNOTATION, m_types.LBRACKET);
            }
        } else if (nextTokenType == m_types.PERCENT) {
            state.markScope(macro, m_types.C_MACRO_EXPR, m_types.LBRACKET);
        } else {
            if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
                // Local open
                // M.|>[<| ... ]
                state.markScope(localOpen, m_types.C_LOCAL_OPEN, m_types.LBRACKET);
            } else {
                state.markScope(bracket, m_types.C_SCOPED_EXPR, m_types.LBRACKET);
            }
        }
    }

    private void parseRBracket(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LBRACKET);
        state.advance();

        if (scope != null) {
            if (!scope.isResolution(annotation)) {
                scope.complete();
            }
            state.popEnd();
            if (scope.isContext(recordFieldAnnotation)) {
                state.add(mark(builder, state.currentContextRml(), recordField, m_types.C_RECORD_FIELD));
            }
        }
    }

    private void parseBracketGt(@NotNull ParserState state) {
        state.markScope(bracketGt, m_types.C_SCOPED_EXPR, m_types.LBRACKET);
    }

    private void parseLBrace(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.previousElementType1 == m_types.DOT && state.previousElementType2 == m_types.UIDENT) {
            // Local open a js object
            // Xxx.|>{<| .. }
            state.add(markScope(builder, localOpen, localObjectOpen, m_types.C_LOCAL_OPEN, m_types.LPAREN).complete());
        }

        if (state.isCurrentResolution(typeNamedEq)) {
            boolean isJsObject = state.lookAhead(1) == m_types.DOT;
            state.add(markScope(builder, isJsObject ? jsObject : recordBinding, isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(tryBody)) {
            // A try expression
            //   try (..) |>{<| .. }
            state.updateCurrentResolution(tryBodyWith).
                    add(markScope(builder, state.currentContextRml(), tryBodyWith, m_types.C_TRY_HANDLERS, m_types.LBRACE));
        } else if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignature)) {
            state.markScope(moduleBinding, m_types.C_SCOPED_EXPR, m_types.LBRACE);
        } else if (state.isCurrentResolution(letNamedEq)) {
            state.add(markScope(builder, maybeRecord, maybeRecordUsage, m_types.C_SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(ifThenStatement)) {
            state.add(markScope(builder, scope, brace, m_types.C_SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(clazzNamedEq)) {
            state.markScope(clazzBody, m_types.C_SCOPED_EXPR, m_types.LBRACE);
        } else if (state.isCurrentResolution(switchBinaryCondition)) {
            boolean isSwitch = state.popEndUntilContextRml(switch_).isCurrentResolution(switch_);
            state.add(markScope(builder, isSwitch ? switchBody : brace, m_types.C_SCOPED_EXPR, isSwitch ? m_types.SWITCH : m_types.LBRACE));
        } else if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            // A scoped property
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.isCurrentResolution(functorParamColon)) {
            state.updateCurrentResolution(functorParamColonSignature).
                    add(markScope(builder, state.currentContextRml(), functorParamColonSignature, m_types.C_SIG_EXPR, m_types.LBRACE).complete());
        } else if (state.isCurrentContextRml(functorDeclaration) && state.previousElementType1 == m_types.ARROW) {
            // Functor implementation
            //    module M = (..) => |>{<| .. }
            state.add(markScope(builder, functorBinding, functorBinding, m_types.C_FUNCTOR_BINDING, m_types.LBRACE).complete());
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (state.isCurrentResolution(signatureItem) && nextElement == m_types.DOT) {
                // js object detected (in definition)
                // let x: |>{<|. ___ }
                state.add(markScope(builder, state.currentContextRml(), jsObject, m_types.C_JS_OBJECT, m_types.LBRACE));
            } else if (nextElement == m_types.STRING_VALUE || nextElement == m_types.DOT) {
                // js object detected (in usage)
                // |>{<| "x" ___ }
                state.add(markScope(builder, state.currentContextRml(), jsObject, m_types.C_JS_OBJECT, m_types.LBRACE)).
                        advance().
                        add(mark(builder, state.currentContextRml(), objectField, m_types.C_OBJECT_FIELD));
            } else {
                state.add(markScope(builder, scope, brace, m_types.C_SCOPED_EXPR, m_types.LBRACE));
            }
        }
    }

    private void parseRBrace(@NotNull ParserState state) {
        if (state.isCurrentResolution(recordField)) {
            state.popEnd();
        }

        ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACE, m_types.RECORD, m_types.SWITCH);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }

        if (state.isCurrentResolution(jsxTagPropertyEq) || state.isCurrentResolution(localObjectOpen)) {
            state.popEnd();
        }
    }

    private void parseLParen(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(switchBinaryCondition)) {
            state.add(markScope(builder, state.currentContextRml(), paren, m_types.C_SCOPED_EXPR, m_types.LPAREN));
            return;
        }

        if (state.isCurrentResolution(option)) {
            state.complete().
                    add(markScope(builder, state.currentContextRml(), optionParameter, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(try_)) {
            // Valid try expression
            //   try |>(<| .. ) with ..
            state.updateCurrentResolution(tryBody).
                    complete().
                    add(markScope(builder, state.currentContextRml(), tryBody, m_types.C_TRY_BODY, m_types.LPAREN));
        } else if (state.isCurrentResolution(signatureItem) && state.previousElementType1 == m_types.COLON) {
            // A ReasonML signature is written like a function, but it's not
            //   (x, y) => z  alias x => y => z
            state.popCancel().
                    add(markScope(builder, signatureParams, signature, m_types.C_SCOPED_EXPR, m_types.LPAREN).dummy()).
                    advance().
                    add(mark(builder, state.currentContextRml(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(letNamedSignature)) {
            // A signature on a let definition
            //   let x : |>(<| .. )
            state.markScope(signatureScope, m_types.C_SCOPED_EXPR, m_types.LPAREN).
                    advance().
                    add(mark(builder, state.currentContextRml(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(moduleNamedEq) && state.previousElementType1 != m_types.UIDENT) {
            // This is a functor
            //   module M = |>(<| .. )
            state.updateCurrentContextRml(functorDeclaration).
                    updateCurrentResolution(functorNamedEq).
                    updateCurrentCompositeElementType(m_types.C_FUNCTOR).
                    add(markScope(builder, functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN)).
                    advance().
                    add(mark(builder, state.currentContextRml(), functorParam, m_types.C_FUNCTOR_PARAM).complete());
        } else if (state.isCurrentResolution(functorNamedEqColon)) {
            // Functor constraint :: module M = (..) : |>(<| .. ) =
            state.markScope(functorConstraints, m_types.C_CONSTRAINTS, m_types.LPAREN);
        } else if (state.isCurrentResolution(maybeFunctorCall)) {
            // We know now that it is really a functor call
            // module M = X |>(<| ... )
            state.updateCurrentResolution(functorCall).complete();
            state.add(markScope(builder, functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN)).
                    advance().
                    add(mark(builder, state.currentContextRml(), functorParam, m_types.C_FUNCTOR_PARAM).complete());
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Variant params
            // type t = | Variant |>(<| .. )
            state.markScope(variantConstructor, m_types.C_FUN_PARAMS, m_types.LPAREN).
                    advance().
                    add(mark(builder, variantConstructor, functionParameter, m_types.C_FUN_PARAM));
        } else if (state.isCurrentResolution(patternMatchVariant)) {
            // It's a constructor
            // | Variant |>(<| .. ) => ..
            state.add(markScope(builder, state.currentContextRml(), patternMatchVariantConstructor, m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN));
        } else if (state.isCurrentResolution(variant)) {
            // It's a variant constructor (not in a pattern match)
            // Variant |>(<| .. )
            state.add(markScope(builder, state.currentContextRml(), variantConstructor, m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN));
        } else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Local open
            // M.|>(<| ...
            state.markScope(localOpen, m_types.C_LOCAL_OPEN, m_types.LPAREN);
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.add(markScope(builder, state.currentContextRml(), scope, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamedParameters)) {
            state.add(markScope(builder, state.currentContextRml(), clazzConstructor, m_types.C_CLASS_CONSTR, m_types.LPAREN));
        } else if (state.isCurrentResolution(ifThenStatement)) {
            state.complete();
            state.markScope(binaryCondition, m_types.C_BIN_CONDITION, m_types.LPAREN);
        } else if (state.isCurrentContextRml(typeConstrName)) {
            // type parameter
            // type x = t |>(<| 'a )
            state.add(markScope(builder, state.currentContextRml(), typeConstrNameParameters, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        } else if (state.previousElementType1 == m_types.LIDENT) {
            // calling a function
            state.
                    add(markScope(builder, functionCall, functionCallParams, m_types.C_FUN_CALL_PARAMS, m_types.LPAREN)).
                    advance().
                    add(mark(builder, functionCall, functionParameter, m_types.C_FUN_PARAM));
        } else if (state.isCurrentResolution(external)) {
            // overloading an operator
            state.updateCurrentResolution(externalNamed);
            state.complete();
        } else if (state.isCurrentResolution(let)) {
            // Overloading operator OR deconstructing a term
            //  let |>(<| + ) =
            //  let |>(<| a, b ) =
            state.add(markScope(builder, let, genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        } else {
            IElementType nextTokenType = state.lookAhead(1);

            if (nextTokenType == m_types.DOT || nextTokenType == m_types.TILDE) {
                // |>(<| .  OR  |>(<| ~
                state.mark(function, m_types.C_FUN_EXPR).
                        markScope(functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                        advance();
                if (nextTokenType == m_types.DOT) {
                    state.advance();
                }
                state.add(mark(builder, functionParameters, functionParameter, m_types.C_FUN_PARAM).complete());
            } else if (nextTokenType == m_types.RPAREN) {
                IElementType nexNextTokenType = state.lookAhead(2);
                if (nexNextTokenType == m_types.ARROW) {
                    // Function in parameter
                    state.add(mark(builder, function, m_types.C_FUN_EXPR).complete()).
                            add(mark(builder, functionParameters, m_types.C_FUN_PARAMS).complete()).
                            add(mark(builder, functionParameter, m_types.C_FUN_PARAM).complete()).
                            add(mark(builder, function, functionParameter, m_types.C_UNIT).complete()).
                            advance().
                            advance().
                            popEnd().
                            popEnd().
                            popEnd();
                } else {
                    // unit
                    state.add(mark(builder, function, functionParameter, m_types.C_UNIT).complete()).advance().advance().popEnd();
                }
            } else {
                if (state.previousElementType1 == m_types.LIDENT) {
                    state.add(markScope(builder, paren, functionCallParams, m_types.C_FUN_CALL_PARAMS, m_types.LPAREN));
                } else {
                    state.add(markScope(builder, genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN));
                }
            }
        }
    }

    private void parseRParen(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
            IElementType aheadType = state.lookAhead(1);
            if (aheadType == m_types.ARROW) {
                // if current resolution is UNKNOWN and next item is an arrow, it means we are processing a function definition,
                // we must rollback to the start of the scope and start the parsing again, but this time with exact information!
                ParserScope startScope = state.popEndUntilOneOfElementType(m_types.LPAREN);
                if (startScope != null) {
                    startScope.rollbackTo();
                    state.pop();
                    state.mark(function, m_types.C_FUN_EXPR).
                            markScope(functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                            advance().
                            mark(functionParameter, m_types.C_FUN_PARAM);
                    return;
                }
            }
        }

        // pop intermediate constructions
        if (state.isCurrentResolution(signatureItem)) {
            state.popEnd();
        }
        if (state.isCurrentResolution(signatureScope)) {
            state.popEnd();
        }
        if (state.isCurrentResolution(functorConstraint)) {
            state.complete().popEnd();
        }

        // Stopping early when structure is well known
        if (state.isCurrentResolution(letNamedSignature)) {
            return;
        } else if (state.isCurrentResolution(switchBinaryCondition)) {
            state.popEnd();
            return;
        } else if (state.isCurrentResolution(variantConstructor)) {
            state.complete();
            state.endUntilResolution(variant);
            state.popEnd();
            return;
        } else if (state.isCurrentResolution(functorConstraints)) {
            state.complete().endUntilScopeToken(m_types.LPAREN);
            state.popEnd();
            return;
        }

        if (!state.isScopeTokenElementType(m_types.LPAREN) && !state.isCurrentEmpty()) {
            state.complete();
        }

        ParserScope parenScope = state.endUntilScopeToken(m_types.LPAREN);
        state.advance();
        IElementType nextTokenType = state.getTokenType();

        if (parenScope != null) {
            // Remove the scope from the stack, we want to test its parent
            state.pop();

            if (nextTokenType == m_types.ARROW && !state.isCurrentResolution(function)) {
                if (state.isCurrentResolution(functorNamedEq)) {
                    state.updateCurrentResolution(functorNamedEqParamsArrow);
                } else if (state.isCurrentResolution(patternMatchVariant)) {
                    state.updateCurrentResolution(patternMatchVariantConstructor);
                } else if (!state.isCurrentResolution(patternMatch) && !state.isCurrentContextRml(signature) && !state
                        .isCurrentContextRml(functorDeclaration)) {
                    // we are processing a function definition,
                    // we must rollback to the start of the scope and start the parsing again, but this time with exact information!
                    parenScope.rollbackTo();
                    state.mark(function, m_types.C_FUN_EXPR)
                            .add(markScope(builder, function, functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).complete()).advance()
                            .add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM));
                    return;
                }
            } else if (nextTokenType == m_types.LPAREN) {
                if (state.isCurrentResolution(clazzNamed)) {
                    // First parens found, it must be a class parameter
                    parenScope.updateCompositeElementType(m_types.C_CLASS_PARAMS);
                    state.updateCurrentResolution(clazzNamedParameters);
                }
            } else if (nextTokenType == m_types.EQ) {
                if (state.isCurrentResolution(clazzNamed)) {
                    parenScope.updateCompositeElementType(m_types.C_CLASS_CONSTR);
                    state.updateCurrentResolution(clazzNamedConstructor);
                } else if (parenScope.isResolution(clazzConstructor)) {
                    state.updateCurrentResolution(clazzConstructor);
                }
            } else if (nextTokenType == m_types.COLON) {
                if (state.isCurrentResolution(let)) {
                    // let (op|>)<|: ...
                    //parenScope.updateCompositeElementType(m_types.C_OPERATOR);
                    state.updateCurrentResolution(letNamed).complete();
                }
            }

            parenScope.complete();
            parenScope.end();

            if (state.isCurrentResolution(option)) {
                state.popEnd();
            }

            ParserScope scope = state.getLatestScope();
            if (scope != null && ((scope.isResolution(jsxTagPropertyEqValue) && !scope.isScope()))) {
                if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
                    state.popEnd().popEnd();
                }
            }
        }
    }

    private void parseEq(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentContextRml(signature)) {
            state.popEndWhileContextOLD(signature);
        }

        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd().
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    add(mark(builder, typeBinding, typeNamedEq, m_types.C_TYPE_BINDING).complete());
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedAttribute) || state.isCurrentResolution(letNamedSignature)) {
            if (state.isCurrentResolution(letNamedSignature)) {
                state.popEnd();
            } else if (state.isCurrentResolution(letNamedAttribute)) {
                state.complete().popEnd();
            }
            state.updateCurrentResolution(letNamedEq).advance().add(mark(builder, letBinding, letNamedEq, m_types.C_LET_BINDING).complete());
        } else if (state.isCurrentResolution(jsxTagProperty)) {
            state.updateCurrentResolution(jsxTagPropertyEq).complete().advance()
                    .add(mark(builder, state.currentContextRml(), jsxTagPropertyEqValue, m_types.C_TAG_PROP_VALUE).complete());
        } else if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedEq).complete();
        } else if (state.isCurrentResolution(externalNamedSignature)) {
            state.complete().
                    updateCurrentResolution(externalNamedSignatureEq);
        } else if (state.isCurrentResolution(clazzNamed) || state.isCurrentResolution(clazzConstructor)) {
            state.updateCurrentResolution(clazzNamedEq);
        } else if (state.isCurrentResolution(signatureItem)) {
            state.updateCurrentResolution(signatureItemEq);
        } else if (state.isCurrentResolution(functionParameterNamed)) {
            // call(~x |> =<| .. )
            state.add(mark(builder, state.currentContextRml(), functionParameterNamedBinding, m_types.C_FUN_PARAM_BINDING).complete());
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        // Special case for the `fun` keyword that must be seen like a switch
        if (state.isCurrentContextRml(funPattern)) {
            state.popEndUntilResolution(switchBody);
        }

        if (!state.isCurrentResolution(patternMatchBody)) {
            state.popEndUntilScope();
        }
    }

    private void parseUIdent(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.isCurrentResolution(open)) {
            // It is a module name/path, or maybe a functor call
            //   open |>M<| ...
            state.complete();
            state.add(mark(builder, state.currentContextRml(), maybeFunctorCall, m_types.C_FUNCTOR_CALL));
        } else if (state.isCurrentResolution(include)) {
            // It is a module name/path, or maybe a functor call
            //   include |>M<| ...
            state.complete();
            state.add(mark(builder, state.currentContextRml(), maybeFunctorCall, m_types.C_FUNCTOR_CALL));
        } else if (state.isCurrentResolution(module)) {
            state.updateCurrentResolution(moduleNamed);
        } else if (state.isCurrentResolution(moduleNamedEq)) {
            // it might be a module functor call, like: module M = |>X<| ( ... )
            state.add(mark(builder, state.currentContextRml(), maybeFunctorCall, m_types.C_FUNCTOR_CALL));
        } else if ((state.isCurrentResolution(jsxStartTag) || state.isCurrentResolution(jsxTagClose)) && state.previousElementType1 == m_types.DOT) {
            // a namespaced custom component
            state.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Declaring a variant
            // type t = | |>X<| ..
            state.remapCurrentToken(m_types.VARIANT_NAME);
            state.wrapWith(m_types.C_VARIANT);
            return;
        } else if (state.isCurrentResolution(exception)) {
            // Declaring an exception
            //   exception |>Ex<| ..
            state.complete().updateCurrentResolution(exceptionNamed);
            state.remapCurrentToken(m_types.EXCEPTION_NAME);
        } else if (state.isCurrentResolution(patternMatch)) {
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType != m_types.DOT) {
                // Defining a pattern match
                // switch (c) { | |>X<| .. }
                state.remapCurrentToken(m_types.VARIANT_NAME);
                state.wrapWith(m_types.C_VARIANT).updateCurrentResolution(patternMatchVariant);

                return;
            }
        } else {
            IElementType nextElementType = state.lookAhead(1);
            if (!state.isCurrentResolution(moduleNamedEq) && !state.isCurrentResolution(maybeFunctorCall) && nextElementType == m_types.LPAREN) {
                state.remapCurrentToken(m_types.VARIANT_NAME);
                // A variant with a constructor
                if (state.isCurrentResolution(typeNamedEq)) {
                    state.add(mark(builder, state.currentContextRml(), typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
                }
                state.wrapWith(m_types.C_VARIANT);
                return;
            } else if (state.isCurrentResolution(typeNamedEq) && nextElementType == m_types.PIPE) {
                // We are declaring a variant without a pipe before
                // type t = |>X<| | ..
                state.remapCurrentToken(m_types.VARIANT_NAME);
                state.add(mark(builder, state.currentContextRml(), typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
                state.wrapWith(m_types.C_VARIANT);
                return;
            } else if (!state.isCurrentResolution(moduleNamedEq) && !state.isCurrentResolution(maybeFunctorCall) && nextElementType != m_types.DOT) {
                // Must be a variant call
                state.remapCurrentToken(m_types.VARIANT_NAME);
                state.wrapWith(m_types.C_VARIANT);
                return;
            }
        }

        state.wrapWith(m_types.C_UPPER_SYMBOL);
    }

    private void parseSwitch(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        boolean inScope = state.isScopeTokenElementType(m_types.LBRACE);
        state.add(mark(builder, switch_, m_types.C_SWITCH_EXPR).complete().setIsStart(inScope)).
                advance().
                mark(switchBinaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        state.mark(try_, m_types.C_TRY_EXPR);
    }

    private void parseArrow(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentContextRml(signature)) {
            state.popEndUnlessFirstContextRml(signature).
                    advance().
                    add(mark(builder, state.currentContextRml(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentContextRml(typeConstrName)) {
            state.advance().popEndUntilContextRml(type).popEnd();
        } else if (state.isCurrentResolution(functionParameter)) {
            state.popEndUnlessFirstContextRml(function).advance().add(mark(builder, function, functionBody, m_types.C_FUN_BODY).complete());
        } else if (state.isCurrentResolution(function)) {
            // let x = ( .. ) |>=><| ..
            state.complete().advance().add(mark(builder, state.currentContextRml(), functionBody, m_types.C_FUN_BODY).complete());
        } else if (state.isCurrentResolution(functorNamedEqColon) || state.isCurrentResolution(functorNamedColonResult)) {
            // module Make = (M) : R |>=><| ..
            if (state.isCurrentResolution(functorNamedColonResult)) {
                state.complete().popEnd();
            }
            state.advance().add(mark(builder, functorBinding, functorBinding, m_types.C_FUNCTOR_BINDING).complete());
        } else if (state.isCurrentResolution(patternMatchVariant) || state.isCurrentResolution(patternMatchVariantConstructor)) {
            // switch ( .. ) { | .. |>=><| .. }
            state.advance().add(mark(builder, state.currentContextRml(), patternMatchBody, m_types.C_PATTERN_MATCH_BODY).complete())/*.setStart()*/;
        }
    }

    private boolean shouldStartExpression(@NotNull ParserState state) {
        return state.isInScopeExpression() && state.isScopeTokenElementType(m_types.LBRACE);
    }
}
