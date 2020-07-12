package com.reason.lang.napkin;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserScopeEnum;
import com.reason.lang.ParserState;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
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

        int c = current_position_(builder);
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
            if (state.isCurrentContext(interpolationString)) {
                if (tokenType == m_types.BACKTICK) {
                    parseJsStringClose(state);
                } else if (tokenType == m_types.DOLLAR && state.isCurrentResolution(interpolationString)) {
                    IElementType nextElementType = state.rawLookup(1);
                    if (nextElementType == m_types.LBRACE) {
                        state.updateCurrentResolution(interpolationReference);
                    }
                } else if (state.isCurrentResolution(interpolationReference) && tokenType == m_types.LBRACE) {
                    state.advance().
                            markScope(interpolationString, interpolationVar, m_types.C_INTERPOLATION_REF, m_types.LBRACE);
                } else if (tokenType == m_types.RBRACE && state.isCurrentResolution(interpolationVar)) {
                    state.popEnd().updateCurrentResolution(interpolationString);
                }
            } else {
                // special keywords that can be used as lower identifier in records
                if (tokenType == m_types.REF && state.isCurrentResolution(recordBinding)) {
                    parseLIdent(state);
                } else if (tokenType == m_types.METHOD && state.isCurrentResolution(recordBinding)) {
                    parseLIdent(state);
                }
                //
                else if (tokenType == m_types.SEMI) {
                    parseSemi(state);
                } else if (tokenType == m_types.EQ) {
                    parseEq(state);
                } else if (tokenType == m_types.UNDERSCORE) {
                    parseUnderscore(state);
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
                } else if (tokenType == m_types.DOT) {
                    parseDot(state);
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
                //else if (tokenType == m_types.ML_STRING_OPEN) {
                //    parseMlStringOpen(builder, state);
                //} else if (tokenType == m_types.ML_STRING_CLOSE) {
                //    parseMlStringClose(state);
                //}
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

            if (!empty_element_parsed_guard_(builder, "reasonFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }

        endLikeSemi(state);
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(state.currentContext(), option, m_types.C_OPTION);
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
            state./*advance().complete().popEnd().*/updateCurrentContext(macroRaw).
                    updateCurrentResolution(macroRawNamed).
                    mark(state.currentContext(), macroRawNamed, m_types.C_MACRO_NAME);
        }
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
        }
    }

    private void parseIf(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(if_, m_types.C_IF_STMT).
                advance().
                mark(state.currentContext(), binaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseDot(@NotNull ParserState state) {
        //if (state.previousElementType1 == m_types.LBRACE && state.isCurrentResolution(jsObject)) {
        // Js object definition
        // ... { |>.<| ... }
        //state.advance().
        //        add(mark(builder, jsObject, objectField, m_types.C_OBJECT_FIELD));
        //}
    }

    private void parseDotDotDot(@NotNull ParserState state) {
        //if (state.previousElementType1 == m_types.LBRACE) {
        // Mixin:  ... { <...> x ...
        //state.updateCurrentContext(recordUsage).updateCurrentResolution(recordBinding).updateCurrentCompositeElementType(m_types.C_RECORD_EXPR)
        //        .add(mark(builder, recordUsage, mixin, m_types.C_MIXIN_FIELD));
        //}
    }

    private void parseWith(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorNamedColonResult)) {
            // module M (X) : ( S |>with<| ... ) = ...
            state.complete().
                    popEnd().
                    mark(functorConstraints, m_types.C_CONSTRAINTS);
        }
        // else if (state.isCurrentResolution(include)) {
        //    state.add(mark(builder, functorConstraints, m_types.C_CONSTRAINTS));
        //}
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(assert_, m_types.C_ASSERT_STMT);
    }

    private void parseAnd(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorConstraint)) {
            // module M = (X) : ( S with ... |>and<| ... ) = ...
            state.popEnd();
        } else {
            ParserScope latestScope = state.popEndUntilStartScope();
            state.advance();

            if (isTypeResolution(latestScope)) {
                state.mark(type, m_types.C_EXP_TYPE).
                        mark(typeConstrName, m_types.C_TYPE_CONSTR_NAME);
            } else if (isLetResolution(latestScope)) {
                state.mark(let, m_types.C_LET_STMT);
            } else if (isModuleResolution(latestScope)) {
                state.mark(module, m_types.C_MODULE_STMT);
            }
        }
    }

    private void parseComma(@NotNull ParserState state) {
        if (state.isCurrentResolution(functionBody)) {
            // a function is part of something else, close it first
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(functionParameterNamedSignatureItem)) {
            state.popEndUntilResolution(functionParameterNamed).
                    updateCurrentResolution(functionParameterNamedSignature);
        }

        if ((state.isCurrentResolution(signatureItem) || state.isCurrentResolution(signatureItemEq)) && !state.isCurrentContext(recordSignature)) {
            state.popEnd().
                    advance().
                    mark(state.currentContext(), signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentContext(signature)) {
            state.popEnd();
        }

        if (state.isCurrentContext(recordSignature)) {
            state.complete().
                    popEndUntilResolution(recordField).
                    popEnd().
                    advance().
                    mark(state.currentContext(), recordField, m_types.C_RECORD_FIELD);
        } else if (state.isCurrentResolution(recordField)) {
            state.popEnd().
                    advance().
                    mark(state.currentContext(), recordField, m_types.C_RECORD_FIELD);
        } else if (state.isCurrentResolution(objectFieldNamed)) {
            state.popEnd().
                    advance().
                    mark(state.currentContext(), objectField, m_types.C_OBJECT_FIELD);
        }
        // else if (state.isCurrentResolution(mixin)) {
        //    state.popEnd();
        //}
        else if (state.isCurrentResolution(functionParameter) || state.isCurrentResolution(functionParameterNamed) || state
                .isCurrentResolution(functionParameterNamedSignature) || state.isCurrentResolution(functionParameterNamedBinding)) {
            state.complete();
            if (state.isCurrentResolution(functionParameterNamedBinding)) {
                state.popEndUntilResolution(functionParameters);
            } else {
                state.popEnd();
            }
            state.advance().
                    mark(state.currentContext(), functionParameter, state.currentContext() == functionCall ? m_types.C_FUN_CALL_PARAM : m_types.C_FUN_PARAM);
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.RPAREN) {
                // not at the end of a list: ie not => (p1, p2<,> )
                state.complete();
            }
        }
        // else if (state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
        // We don't know yet but we need to complete the marker
        //state.complete();
        //state.popEnd();
        //}
        else if (state.isCurrentContext(let) && state.isCurrentResolution(genericExpression)) {
            // It must be a deconstruction
            // let ( a |>,<| b ) = ..
            state.updateCurrentResolution(deconstruction).updateCurrentCompositeElementType(m_types.C_DECONSTRUCTION);
        }
    }

    private void parsePipe(@NotNull ParserState state) {
        // By default, a pattern match
        if (state.isCurrentResolution(patternMatchBody)) {
            state.popEndUntilResolution(switchBody);
        }
        if (state.isCurrentResolution(patternMatchVariant) || state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEnd();
        }

        if (state.isCurrentResolution(typeNamedEq)) {
            // type x = |> | <| ...
            state.mark(state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL);
        } else if (state.isCurrentResolution(tryBodyWith)) {
            // Start of a try handler
            //   try (..) { |>|<| .. }
            state.mark(state.currentContext(), tryBodyWithHandler, m_types.C_TRY_HANDLER);
        } else if (state.isCurrentResolution(variant) && state.isCurrentContext(typeBinding)) {
            state.popEndUntilResolution(typeNamedEqVariant).
                    mark(state.currentContext(), variant, m_types.C_VARIANT_DECL);
        } else {
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.mark(state.currentContext(), patternMatch, m_types.C_PATTERN_MATCH_EXPR);
        }
    }

    private void parseStringValue(@NotNull ParserState state) {
        //if (state.isCurrentContext(macroRaw)) {
        //    state.markScope(state.currentContext(), rawBody, m_types.C_MACRO_RAW_BODY, m_types.LPAREN);
        //state.wrapWith(m_types.C_MACRO_RAW_BODY);
        //}
        // else if (state.isCurrentContext(raw)) {
        //    state.complete().add(mark(builder, rawBody, m_types.C_MACRO_RAW_BODY).complete()).advance().popEnd();
        //} else if (state.isCurrentResolution(annotationName)) {
        //    state.popEndUntilStartScope();
        //}
        //else
        if (state.isCurrentResolution(maybeRecord/*maybeRecordUsage*/)) {
            IElementType nextToken = state.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.updateCurrentContext(object).
                        updateCurrentResolution(object).
                        updateCurrentCompositeElementType(m_types.C_JS_OBJECT).
                        markScope(object, objectField, m_types.C_OBJECT_FIELD, m_types.STRING_VALUE);
            }
        }
        // else if (state.isCurrentResolution(object)) {
        //    state.add(markScope(builder, object, objectField, m_types.C_OBJECT_FIELD, m_types.STRING_VALUE));
        //}
    }

    private void parseMlStringOpen(@NotNull ParserState state) {
        //if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
        //    state.endAny();
        //}
        //
        //if (state.isCurrentContext(macroRaw)) {
        //    state.add(mark(builder, state.currentContext(), macroRawBody, m_types.C_MACRO_RAW_BODY).complete());
        //}
        //
        //state.add(markScope(builder, state.currentContext(), multilineStart, m_types.C_ML_INTERPOLATOR, m_types.ML_STRING_OPEN));
    }

    private void parseMlStringClose(@NotNull ParserState state) {
        //ParserScope scope = state.endUntilScopeToken(m_types.ML_STRING_OPEN);
        //state.advance();
        //
        //if (scope != null) {
        //    scope.complete();
        //    state.popEnd();
        //}
    }

    private void parseTemplateStringOpen(@NotNull ParserState state) {
        //if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) { // use space notifier like in tag ?
        //    state.endAny();
        //}
        state.markScope(interpolationString, interpolationString, m_types.C_INTERPOLATION_EXPR, m_types.JS_STRING_OPEN);
        //mark(interpolationString, interpolationPart, m_types.C_INTERPOLATION_PART);
    }

    private void parseJsStringClose(@NotNull ParserState state) {
        //ParserScope scope = state.endUntilScopeToken(m_types.JS_STRING_OPEN);
        //state.advance();
        //
        //if (scope != null) {
        //    scope.complete();
        //    state.popEnd();
        //}
    }

    private void parseLet(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(let, m_types.C_LET_STMT);
    }

    private void parseVal(@NotNull ParserState state) {
        //if (!state.isCurrentResolution(annotationName)) {
        //    state.popEndUntilStartScope();
        //    if (state.isCurrentResolution(clazzBodyScope)) {
        //        state.add(mark(builder, val, clazzField, m_types.C_CLASS_FIELD));
        //    } else {
        //        state.add(mark(builder, let, m_types.C_LET_STMT));
        //    }
        //}
    }

    private void parsePub(@NotNull ParserState state) {
        //state.popEndUntilStartScope();
        //if (state.isCurrentResolution(clazzBodyScope)) {
        //    state.add(mark(builder, clazzMethod, m_types.C_CLASS_METHOD));
        //}
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            endLikeSemi(state);
            state.mark(module, m_types.C_MODULE_STMT);
        }
    }

    private void parseException(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(exception, m_types.C_EXCEPTION_EXPR);
    }

    private void parseClass(@NotNull ParserState state) {
        //state.popEndUntilStartScope();
        //state.add(mark(builder, clazzDeclaration, clazz, m_types.C_CLASS_STMT));
    }

    private void parseType(@NotNull ParserState state) {
        if (state.isCurrentResolution(functorConstraints)) {
            // module M = (X) : ( S with |>type<| ... ) = ...
            state.mark(functorConstraints, functorConstraint, m_types.C_CONSTRAINT);
        } else if (state.isCurrentResolution(includeConstraints)) {
            // include M with |>type<| ...
            state.mark(includeConstraints, includeConstraint, m_types.C_CONSTRAINT);
        } else if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz)) {
            //    if (!state.isCurrentResolution(letNamedSignature)) {
            //        state.popEndUntilStartScope();
            //    }
            endLikeSemi(state);
            state.mark(type, m_types.C_EXP_TYPE).
                    advance().
                    mark(type, typeConstrName, m_types.C_TYPE_CONSTR_NAME);
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(external, m_types.C_EXTERNAL_STMT);
    }

    private void parseOpen(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(open, m_types.C_OPEN);
    }

    private void parseInclude(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(include, m_types.C_INCLUDE);
    }

    private void parsePercent(@NotNull ParserState state) {
        //endLikeSemi(state);
        state.mark(macro, macroName, m_types.C_MACRO_EXPR);
        //if (state.isCurrentResolution(macro)) {
        //    state.complete();
        //    state.add(mark(builder, macro, macroName, m_types.C_MACRO_NAME));
        //} else if (state.isCurrentResolution(letNamed)) {
        // let name|>%<|private = ...
        //state.add(mark(builder, state.currentContext(), letNamedAttribute, m_types.C_LET_ATTR));
        //} else {
        //    IElementType nextTokenType = builder.rawLookup(1);
        //    if (nextTokenType == m_types.RAW) {
        //        state.add(mark(builder, raw, m_types.C_RAW));
        //    }
        //}
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.isCurrentContext(maybeRecord) /*&& state.isCurrentResolution(genericExpression)*/) {
            // yes it is a record, remove the maybe
            ParserScope fieldState = state.pop();

            state.updateCurrentContext(recordUsage).updateCurrentCompositeElementType(m_types.C_RECORD_EXPR);
            if (state.isCurrentResolution(maybeRecordUsage)) {
                state.updateCurrentResolution(record);
            }

            if (fieldState != null) {
                fieldState.context(recordUsage);
                fieldState.resolution(recordField);
                fieldState.updateCompositeElementType(m_types.C_RECORD_FIELD);
                state.add(fieldState);
            }
        }

        if (state.isCurrentResolution(externalNamed)) {
            state.updateCurrentResolution(externalNamedSignature).
                    advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signature, signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(letNamed)) {
            // let x |> :<| ...
            state.advance().mark(signature, letNamedSignature, m_types.C_SIG_EXPR);
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.LPAREN) {
                state.mark(signature, signatureItem, m_types.C_SIG_ITEM);
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
                state.markScope(state.currentContext(), scope, m_types.C_SCOPED_EXPR, m_types.LPAREN).dummy().advance();
            }
            state.mark(state.currentContext(), functorNamedColonResult, m_types.C_FUNCTOR_RESULT);
        } else if (state.isCurrentResolution(functorParam)) {
            state.updateCurrentResolution(functorParamColon);
        } else if (state.isCurrentResolution(recordField)) {
            state.complete().advance();
            if (!state.isCurrentContext(recordUsage)) {
                state.mark(recordSignature, signature, m_types.C_SIG_EXPR).
                        mark(recordSignature, signatureItem, m_types.C_SIG_ITEM);
            }
        } else if (state.isCurrentResolution(objectField)) {
            state.updateCurrentResolution(objectFieldNamed);
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateCurrentResolution(functionParameterNamed).
                    advance().
                    mark(signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).
                    mark(signature, functionParameterNamedSignatureItem, m_types.C_SIG_ITEM);
        }
        // else if (state.isCurrentResolution(paren) && state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
        //    state.complete().
        //            updateCurrentContext(functionParameter).updateCurrentResolution(functionParameterNamed).
        //            advance().
        //            add(mark(builder, signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).complete()).
        //            add(mark(builder, signatureItem, functionParameterNamedSignatureItem, m_types.C_SIG_ITEM).complete());
        //}
    }

    private void parseArrobase(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(annotation, m_types.C_ANNOTATION_EXPR).
                mark(annotation, annotationName, m_types.C_MACRO_NAME);
    }

    private void parseLt(@NotNull ParserState state) {
        if (state.isCurrentResolution(option)) {
            state.markScope(state.currentContext(), optionParameter, m_types.C_SCOPED_EXPR, m_types.LT);
        }
        // else if (state.isCurrentContext(typeConstrName)) {
        // type parameter
        // type x = t |> < <| 'a >
        //state.add(markScope(builder, state.currentContext(), typeConstrNameParameters, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        //}
        else if (!state.isCurrentContext(signature) && !state.isCurrentResolution(signatureItem)) {
            // Can be a symbol or a JSX tag
            IElementType nextTokenType = state.rawLookup(1);
            if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT
                    || nextTokenType == m_types.OPTION) { // Note that option is a ReasonML keyword but also a JSX keyword !
                // Surely a tag
                state.remapCurrentToken(m_types.TAG_LT).
                        mark(jsxTag, m_types.C_TAG).
                        markScope(jsxStartTag, jsxStartTag, m_types.C_TAG_START, m_types.TAG_LT).
                        advance().
                        remapCurrentToken(m_types.TAG_NAME).
                        wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
            }
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
        } else if (state.isCurrentResolution(optionParameter)) {
            state.advance().popEnd().popEnd();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // A closing tag
            if (state.isCurrentContext(jsxTagBody)) {
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
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
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

        if (state.isCurrentResolution(maybeRecord)) {
            // Maybe a record, we must check
            IElementType nextTokenType = state.lookAhead(1);
            if (nextTokenType == m_types.COLON) {
                // Yes, this is a record binding
                state.updateCurrentResolution(recordBinding).
                        updateCurrentCompositeElementType(m_types.C_RECORD_EXPR);
            }
        }

        if (state.isCurrentResolution(typeConstrName)) {
            // type |>x<| ...
            state.updateCurrentResolution(typeNamed);
        }
        // else if (state.isCurrentResolution(functionParameters)) {
        //    state.add(mark(builder, functionParameter, m_types.C_FUN_PARAM).complete());
        //}
        else if (state.isCurrentResolution(external)) {
            // external |>x<| ...
            state.updateCurrentResolution(externalNamed);
        } else if (state.isCurrentResolution(let)) {
            // let |>x<| ..
            state.updateCurrentResolution(letNamed);
        } else if (state.isCurrentResolution(letNamedEq)) {
            if (state.previousElementType1 == m_types.EQ) {
                // let x = |>c<| => ...
                IElementType nextElementType = state.lookAhead(1);
                if (nextElementType == m_types.ARROW) {
                    // Single (paren less) function parameters
                    state.mark(function, letNamedBindingFunction, m_types.C_FUN_EXPR).
                            mark(function, functionParameters, m_types.C_FUN_PARAMS).
                            mark(function, functionParameter, m_types.C_FUN_PARAM);
                }
            }
            //} else if (state.isCurrentResolution(macroName)) {
            //    state.complete();
            //    boolean isRaw = "raw".equals(builder.getTokenText());
            //    if (isRaw) {
            //        state.advance();
            //    }
            //    state.popEnd();
            //    if (isRaw) {
            //        state.updateCurrentContext(macroRaw);
            //    }
            //    state.updateCurrentResolution(macroNamed);
            //    return;
        }
        // else if (state.isCurrentResolution(clazz)) {
        // class |>x<| ...
        //state.updateCurrentResolution(clazzNamed);
        //state.complete();
        //} else if (state.isCurrentResolution(clazzField)) {
        // class c ... val |>x<| ...
        //state.updateCurrentResolution(clazzFieldNamed);
        //state.complete();
        //} else if (state.isCurrentResolution(clazzMethod)) {
        // method |>x<| ...
        //state.updateCurrentResolution(clazzMethodNamed);
        //state.complete();
        //}
        else if (state.isCurrentResolution(jsxStartTag)) {
            // This is a property
            state.popEndUntilStartScope();
            state.remapCurrentToken(m_types.PROPERTY_NAME).
                    mark(jsxTagProperty, m_types.C_TAG_PROPERTY).
                    setWhitespaceSkippedCallback((type, start, end) -> {
                        if (state.isCurrentResolution(jsxTagProperty) || (state.isCurrentResolution(jsxTagPropertyEqValue) && state.notInScopeExpression())) {
                            if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
                                state.popEnd();
                            }
                            state.popEnd();
                            state.setWhitespaceSkippedCallback(null);
                        }
                    });
        } else if (state.isCurrentResolution(recordBinding)) {
            state.mark(state.currentContext(), recordField, m_types.C_RECORD_FIELD);
        } else if (state.isCurrentResolution(record)) {
            state.mark(recordUsage, recordField, m_types.C_RECORD_FIELD);
        }
        // else if (state.isCurrentResolution(jsObject)) {
        //    state.add(mark(builder, state.currentContext(), objectField, m_types.C_OBJECT_FIELD));
        //} else if (state.isCurrentResolution(mixin)) {
        //    state.complete();
        //} else if (shouldStartExpression(state)) {
        //    IElementType tokenType = builder.getTokenType();
        //    if (tokenType != null) {
        //        state.add(mark(builder, state.currentContext(), genericExpression, tokenType));
        //    }
        //}
        else if (!state.isCurrentContext(signature)) {
            IElementType nextElementType = state.lookAhead(1);
            if (nextElementType == m_types.ARROW) {
                // Single (paren less) function parameters
                // |>c<| => ...
                state.mark(function, function, m_types.C_FUN_EXPR).
                        mark(function, functionParameters, m_types.C_FUN_PARAMS).
                        mark(function, functionParameter, m_types.C_FUN_PARAM);
            }
        }

        if (!state.isCurrentResolution(jsxTagProperty)) {
            state.wrapWith(m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseLBracket(@NotNull ParserState state) {
        //IElementType nextTokenType = builder.rawLookup(1);
        //if (nextTokenType == m_types.ARROBASE) {
        //    if (state.isCurrentResolution(recordField)) {
        //        state.popEnd();
        //        state.add(markScope(builder, recordFieldAnnotation, annotation, m_types.C_ANNOTATION_EXPR, m_types.LBRACKET));
        //    } else {
        //        state.add(markScope(builder, annotation, m_types.C_ANNOTATION_EXPR, m_types.LBRACKET));
        //    }
        //} else if (nextTokenType == m_types.PERCENT) {
        //    state.add(markScope(builder, macro, m_types.C_MACRO_EXPR, m_types.LBRACKET));
        //} else {
        if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Local open
            // M.|>[<| ... ]
            state.markScope(localOpen, localOpen, m_types.C_LOCAL_OPEN, m_types.LBRACKET);
        } else {
            state.markScope(bracket, bracket, m_types.C_SCOPED_EXPR, m_types.LBRACKET);
        }
        //}
    }

    private void parseRBracket(@NotNull ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LBRACKET);
        state.advance();

        if (scope != null) {
            //    if (!scope.isResolution(annotation)) {
            //        scope.complete();
            //    }
            state.popEnd();
            //    if (scope.isContext(recordFieldAnnotation)) {
            //        state.add(mark(builder, state.currentContext(), recordField, m_types.C_RECORD_FIELD));
            //    }
        }
    }

    private void parseBracketGt(@NotNull ParserState state) {
        //state.add(markScope(builder, bracketGt, m_types.C_SCOPED_EXPR, m_types.LBRACKET));
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.previousElementType1 == m_types.DOT && state.previousElementType2 == m_types.UIDENT) {
            // Local open a js object
            // Xxx.|>{<| "y" : ... }
            state.markScope(localOpen, localObjectOpen, m_types.C_LOCAL_OPEN, m_types.LPAREN);
        }

        if (state.isCurrentResolution(typeNamedEq)) {
            boolean isJsObject = state.lookAhead(1) == m_types.DOT;
            ParserScopeEnum scope = isJsObject ? jsObject : recordBinding;
            state.markScope(scope, scope, isJsObject ? m_types.C_JS_OBJECT : m_types.C_RECORD_EXPR, m_types.LBRACE);
        } else if (state.isCurrentResolution(tryBodyWith)) {
            // A try expression
            //   try ... |>{<| ... }
            state.markScope(state.currentContext(), tryBodyWith, m_types.C_TRY_HANDLERS, m_types.LBRACE);
        }
        // else if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignature)) {
        //    state.add(markScope(builder, moduleBinding, m_types.C_SCOPED_EXPR, m_types.LBRACE));
        //}
        else if (state.isCurrentResolution(letNamedEq)) {
            state.markScope(maybeRecord, maybeRecord, m_types.C_SCOPED_EXPR, m_types.LBRACE);
        } else if (state.isCurrentContext(if_) && state.isCurrentResolution(binaryCondition)) {
            // if x |>{<| ... }
            state.popEnd().
                    markScope(state.currentContext(), ifThenStatement, m_types.C_SCOPED_EXPR, m_types.LBRACE);
        }
        // else if (state.isCurrentResolution(clazzNamedEq)) {
        //    state.add(markScope(builder, clazzBodyScope, m_types.C_SCOPED_EXPR, m_types.LBRACE));
        //}
        else if (state.isCurrentResolution(switchBinaryCondition)) {
            state.popEndUntilContext(switch_).
                    markScope(state.currentContext(), switchBody, m_types.C_SCOPED_EXPR, m_types.LBRACE);
            //boolean isSwitch = state.popEndUntilContext(switch_).isCurrentResolution(switch_);
            //    state.add(markScope(builder, isSwitch ? switchBody : brace, m_types.C_SCOPED_EXPR, isSwitch ? m_types.SWITCH : m_types.LBRACE));
        } else if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            // A scoped property
            state.updateScopeToken(m_types.LBRACE);
        } else if (state.isCurrentResolution(functorParamColon)) {
            state.updateCurrentResolution(functorParamColonSignature).
                    markScope(state.currentContext(), functorParamColonSignature, m_types.C_SIG_EXPR, m_types.LBRACE);
        } else if (state.isCurrentContext(functorDeclaration) && state.previousElementType1 == m_types.ARROW) {
            // Functor implementation
            //    module M = (..) => |>{<| .. }
            state.markScope(functorBinding, functorBinding, m_types.C_FUNCTOR_BINDING, m_types.LBRACE);
        } else {
            // it might be a js object
            IElementType nextElement = state.lookAhead(1);
            if (state.isCurrentResolution(signatureItem) && nextElement == m_types.DOT) {
                // js object detected (in definition)
                // let x: |>{<| . ___ }
                state.markScope(state.currentContext(), jsObject, m_types.C_JS_OBJECT, m_types.LBRACE);
            } else if (nextElement == m_types.STRING_VALUE || nextElement == m_types.DOT) {
                // js object detected (in usage)
                // |>{<| "x" ___ }
                state.markScope(state.currentContext(), jsObject, m_types.C_JS_OBJECT, m_types.LBRACE).
                        advance().
                        mark(state.currentContext(), objectField, m_types.C_OBJECT_FIELD);
            } else {
                state.markScope(scope, brace, m_types.C_SCOPED_EXPR, m_types.LBRACE);
            }
        }
    }

    private void parseRBrace(@NotNull ParserState state) {
        if (state.isCurrentResolution(recordField)) {
            if (state.isCurrentEmpty()) {
                state.popCancel();
            } else {
                state.popEnd();
            }
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

    private void parseLParen(@NotNull ParserState state) {
        //if (state.isCurrentResolution(switchBinaryCondition)) {
        //    state.add(markScope(builder, state.currentContext(), paren, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        //    return;
        //}
        //
        if (state.isCurrentResolution(annotationName)) {
            // @ann |>(<| ... )
            state.popEnd();
        }

        if (state.isCurrentResolution(letNamedSignature)) {
            // A signature on a let definition
            //   let x : |>(<| ... )
            state.markScope(signatureScope, signatureScope, m_types.C_SCOPED_EXPR, m_types.LPAREN).
                    advance().
                    mark(state.currentContext(), signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(annotation)) {
            // Annotation parameters
            state.markScope(annotationParameter, annotationParameter, m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else if (state.isCurrentResolution(macroRawNamed)) {
            state.popEnd().
                    markScope(state.currentContext(), rawBody, m_types.C_MACRO_RAW_BODY, m_types.LPAREN);
        }
        // else if (state.isCurrentResolution(try_)) {
        // Valid try expression
        //   try |>(<| .. ) with ..
        //state.updateCurrentResolution(tryBody).
        //        complete().
        //        add(markScope(builder, state.currentContext(), tryBody, m_types.C_TRY_BODY, m_types.LPAREN));
        //}
        else if (state.isCurrentResolution(signatureItem) && state.previousElementType1 == m_types.COLON) {
            // A ReasonML signature is written like a function, but it's not
            //   (x, y) => z  alias x => y => z
            state.popCancel().
                    markScope(signatureParams, signature, m_types.C_SCOPED_EXPR, m_types.LPAREN).dummy().
                    advance().
                    mark(state.currentContext(), signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(moduleNamedEq) && state.previousElementType1 != m_types.UIDENT) {
            // This is a functor
            //  module M = |>(<| .. )
            state.updateCurrentContext(functorDeclaration).
                    updateCurrentResolution(functorNamedEq).
                    updateCurrentCompositeElementType(m_types.C_FUNCTOR).
                    markScope(functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN).
                    advance().
                    mark(state.currentContext(), functorParam, m_types.C_FUNCTOR_PARAM);
        } else if (state.isCurrentResolution(functorNamedEqColon)) {
            // Functor constraint :: module M = (..) : |>(<| .. ) =
            state.markScope(functorConstraints, functorConstraints, m_types.C_CONSTRAINTS, m_types.LPAREN);
        } else if (state.isCurrentResolution(maybeFunctorCall)) {
            // We know now that it is really a functor call
            //  module M = X |>(<| ... )
            //  open X |>(<| ... )
            state.updateCurrentResolution(functorCall).complete();
            state.markScope(functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN).
                    advance().
                    mark(state.currentContext(), functorParam, m_types.C_FUNCTOR_PARAM);
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Variant params
            // type t = | Variant |>(<| .. )
            state.markScope(variantConstructor, variantConstructor, m_types.C_FUN_PARAMS, m_types.LPAREN).
                    advance().
                    mark(variantConstructor, functionParameter, m_types.C_FUN_PARAM);
        } else if (state.isCurrentResolution(patternMatchVariant)) {
            // It's a constructor in a pattern match
            // | Variant |>(<| .. ) => ..
            state.markScope(state.currentContext(), patternMatchVariantConstructor, m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN);
        }
        // else if (state.isCurrentResolution(variant)) {
        // It's a variant constructor (not in a pattern match)
        // Variant |>(<| .. )
        //state.add(markScope(builder, state.currentContext(), variantConstructor, m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN));
        //}
        else if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Local open
            // M. |>(<| ... )
            state.markScope(localOpen, localOpen, m_types.C_LOCAL_OPEN, m_types.LPAREN);
        }
        // else if (state.isCurrentResolution(clazzNamed)) {
        //    state.add(markScope(builder, state.currentContext(), scope, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        //} else if (state.isCurrentResolution(clazzNamedParameters)) {
        //    state.add(markScope(builder, state.currentContext(), clazzConstructor, m_types.C_CLASS_CONSTR, m_types.LPAREN));
        //}
        // else if (state.isCurrentResolution(ifThenStatement)) {
        //    state.complete();
        //    state.add(markScope(builder, binaryCondition, m_types.C_BIN_CONDITION, m_types.LPAREN).complete());
        //}
        else if (state.previousElementType1 == m_types.LIDENT) {
            // calling a function
            state.markScope(functionCall, functionCallParams, m_types.C_FUN_CALL_PARAMS, m_types.LPAREN).
                    advance().
                    mark(functionCall, functionParameter, m_types.C_FUN_CALL_PARAM);
        }
        // else if (state.isCurrentResolution(external)) {
        // overloading an operator
        //state.updateCurrentResolution(externalNamed);
        //state.complete();
        //}
        else if (state.isCurrentResolution(let)) {
            // Overloading operator OR deconstructing a term
            //  let |>(<| + ) =
            //  let |>(<| a, b ) =
            state.markScope(state.currentContext(), genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN);
        } else {
            IElementType nextTokenType = state.lookAhead(1);

            if (nextTokenType == m_types.DOT || nextTokenType == m_types.TILDE) {
                // A function
                // |>(<| .  OR  |>(<| ~
                state.mark(function, m_types.C_FUN_EXPR).
                        markScope(functionParameters, functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                        advance();
                if (nextTokenType == m_types.DOT) {
                    state.advance();
                }
                state.mark(functionParameters, functionParameter, m_types.C_FUN_PARAM);
            } else if (nextTokenType == m_types.RPAREN) {
                IElementType nexNextTokenType = state.lookAhead(2);
                if (nexNextTokenType == m_types.ARROW) {
                    // Function in parameter
                    state.mark(function, m_types.C_FUN_EXPR).
                            mark(functionParameters, m_types.C_FUN_PARAMS).
                            mark(functionParameter, m_types.C_FUN_PARAM).
                            mark(function, functionParameter, m_types.C_UNIT).
                            advance().
                            advance().
                            popEnd().
                            popEnd().
                            popEnd();
                } else {
                    // unit
                    state.mark(function, functionParameter, m_types.C_UNIT).advance().advance().popEnd();
                }
            } else {
                if (state.previousElementType1 == m_types.LIDENT) {
                    state.markScope(paren, functionCallParams, m_types.C_FUN_CALL_PARAMS, m_types.LPAREN);
                } else {
                    state.markScope(genericExpression, genericExpression, m_types.C_SCOPED_EXPR, m_types.LPAREN);
                }
            }
        }
    }

    private void parseRParen(@NotNull ParserState state) {
        if (state.isCurrentResolution(functionParameter)) {
            ParserScope latestScope = state.getLatestScope();
            if (latestScope != null && latestScope.isEmpty()) {
                // maybe a final comma with nothing after
                state.popCancel();
            }
        }

        if (state.isCurrentResolution(genericExpression)) {
            IElementType aheadType = state.lookAhead(1);
            if (aheadType == m_types.ARROW) {
                // if current resolution is UNKNOWN and next item is an arrow, it means we are processing a function definition,
                // we must rollback to the start of the scope and start the parsing again, but this time with exact information!
                ParserScope startScope = state.popEndUntilOneOfElementType(m_types.LPAREN);
                if (startScope != null) {
                    startScope.rollbackTo();
                    state.pop();
                    state.mark(function, m_types.C_FUN_EXPR).
                            markScope(functionParameters, functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).
                            advance().
                            mark(functionParameters, functionParameter, m_types.C_FUN_PARAM);
                    return;
                }
            }
        }

        state.popEndUntilStartScope();
        state.advance().popEnd();

        // pop intermediate constructions
        if (state.isCurrentResolution(annotation)) {
            state.popEnd();
        }
        //if (state.isCurrentResolution(signatureItem)) {
        //    state.popEnd();
        //}
        //if (state.isCurrentResolution(signatureScope)) {
        //    state.popEnd();
        //}
        //if (state.isCurrentResolution(functorConstraint)) {
        //    state.complete().popEnd();
        //}

        // Stopping early when structure is well known
        //if (state.isCurrentResolution(letNamedSignature)) {
        //    return;
        //}
        // else if (state.isCurrentResolution(switchBinaryCondition)) {
        //    state.popEnd();
        //    return;
        //} else if (state.isCurrentResolution(variantConstructor)) {
        //    state.complete();
        //    state.endUntilResolution(variant);
        //    state.popEnd();
        //    return;
        //} else if (state.isCurrentResolution(functorConstraints)) {
        //    state.complete().endUntilScopeToken(m_types.LPAREN);
        //    state.popEnd();
        //    return;
        //}
        //
        //if (!state.isScopeTokenElementType(m_types.LPAREN) && !state.isCurrentEmpty()) {
        //    state.complete();
        //}
        //
        //ParserScope parenScope = state.endUntilScopeToken(m_types.LPAREN);
        //state.advance();
        //IElementType nextTokenType = builder.getTokenType();

        //if (parenScope != null) {
        // Remove the scope from the stack, we want to test its parent
        //state.pop();
        //
        //if (nextTokenType == m_types.ARROW && !state.isCurrentResolution(function)) {
        //    if (state.isCurrentResolution(functorNamedEq)) {
        //        state.updateCurrentResolution(functorNamedEqParamsArrow);
        //    } else if (state.isCurrentResolution(patternMatchVariant)) {
        //        state.updateCurrentResolution(patternMatchVariantConstructor);
        //    } else if (!state.isCurrentResolution(patternMatch) && !state.isCurrentContext(signature) && !state.isCurrentContext(functorDeclaration)) {
        // we are processing a function definition,
        // we must rollback to the start of the scope and start the parsing again, but this time with exact information!
        //parenScope.rollbackTo();
        //state.add(mark(builder, function, m_types.C_FUN_EXPR).complete())
        //        .add(markScope(builder, function, functionParameters, m_types.C_FUN_PARAMS, m_types.LPAREN).complete()).advance()
        //        .add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM));
        //return;
        //}
        //} else if (nextTokenType == m_types.LPAREN) {
        //    if (state.isCurrentResolution(clazzNamed)) {
        // First parens found, it must be a class parameter
        //parenScope.updateCompositeElementType(m_types.C_CLASS_PARAMS);
        //state.updateCurrentResolution(clazzNamedParameters);
        //}
        //} else if (nextTokenType == m_types.EQ) {
        //    if (state.isCurrentResolution(clazzNamed)) {
        //        parenScope.updateCompositeElementType(m_types.C_CLASS_CONSTR);
        //        state.updateCurrentResolution(clazzNamedConstructor);
        //    } else if (parenScope.isResolution(clazzConstructor)) {
        //        state.updateCurrentResolution(clazzConstructor);
        //    }
        //} else if (nextTokenType == m_types.COLON) {
        //    if (state.isCurrentResolution(let)) {
        // let (op|>)<|: ...
        //parenScope.updateCompositeElementType(m_types.C_OPERATOR);
        //state.updateCurrentResolution(letNamed).complete();
        //}
        //}
        //
        //parenScope.complete();
        //parenScope.end();
        //
        //if (state.isCurrentResolution(option)) {
        //    state.popEnd();
        //}
        //
        //ParserScope scope = state.getLatestScope();
        //if (scope != null && ((scope.isResolution(jsxTagPropertyEqValue) && !scope.isScope()))) {
        //    if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
        //        state.popEnd().popEnd();
        //    }
        //}
        //}
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndWhileContext(signature);
        }

        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd().
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    mark(typeBinding, typeNamedEq, m_types.C_TYPE_BINDING);
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedAttribute) || state.isCurrentResolution(letNamedSignature)) {
            if (state.isCurrentResolution(letNamedSignature)) {
                state.popEnd();
            } else if (state.isCurrentResolution(letNamedAttribute)) {
                state.complete().popEnd();
            }
            state.updateCurrentResolution(letNamedEq).advance().mark(letBinding, letNamedEq, m_types.C_LET_BINDING);
        } else if (state.isCurrentResolution(jsxTagProperty)) {
            state.updateCurrentResolution(jsxTagPropertyEq).
                    advance().
                    mark(state.currentContext(), jsxTagPropertyEqValue, m_types.C_TAG_PROP_VALUE);
        } else if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedEq);
        } else if (state.isCurrentResolution(externalNamedSignature)) {
            state.updateCurrentResolution(externalNamedSignatureEq);
        }
        // else if (state.isCurrentResolution(clazzNamed) || state.isCurrentResolution(clazzConstructor)) {
        //    state.updateCurrentResolution(clazzNamedEq);
        //} else if (state.isCurrentResolution(signatureItem)) {
        //    state.updateCurrentResolution(signatureItemEq);
        //}
        else if (state.isCurrentResolution(functionParameterNamed)) {
            // fn(~x |> =<| .. )
            state.mark(state.currentContext(), functionParameterNamedBinding, m_types.C_FUN_PARAM_BINDING);
        }
    }

    private void parseSemi(@NotNull ParserState state) {
        // Don't pop the scopes
        state.popEndUntilStartScope();
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.isCurrentResolution(open)) {
            // It is a module name/path, or maybe a functor call
            // open |>M<| ...
            state.markOptional(state.currentContext(), maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if (state.isCurrentResolution(include)) {
            // It is a module name/path, or maybe a functor call
            // include |>M<| ...
            state.markOptional(state.currentContext(), maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if (state.isCurrentResolution(module)) {
            // module |>M<| ...
            state.updateCurrentResolution(moduleNamed);
        } else if (state.isCurrentResolution(moduleNamedEq)) {
            // it might be a module functor call
            // module M = |>X<| ( ... )
            state.markOptional(state.currentContext(), maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if ((state.isCurrentResolution(jsxStartTag) || state.isCurrentResolution(jsxTagClose)) && state.previousElementType1 == m_types.DOT) {
            // a namespaced custom component
            state.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Declaring a variant
            // type t = | |>X<| ..
            state.remapCurrentToken(m_types.VARIANT_NAME).wrapWith(m_types.C_VARIANT);
            return;
        }
        // else if (state.isCurrentResolution(exception)) {
        // Declaring an exception
        //   exception |>Ex<| ..
        //state.complete().updateCurrentResolution(exceptionNamed);
        //builder.remapCurrentToken(m_types.EXCEPTION_NAME);
        //}
        else if (state.isCurrentResolution(patternMatch)) {
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
            //    if (shouldStartExpression(state)) {
            //        IElementType tokenType = builder.getTokenType();
            //        if (tokenType != null) {
            //            state.add(mark(builder, genericExpression, tokenType));
            //        }
            //    }

            IElementType nextElementType = state.lookAhead(1);

            if (!state.isCurrentResolution(moduleNamedEq) && !state.isCurrentResolution(maybeFunctorCall) && nextElementType == m_types.LPAREN) {
                state.remapCurrentToken(m_types.VARIANT_NAME);
                // A variant with a constructor
                if (state.isCurrentResolution(typeNamedEq)) {
                    state.mark(state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL);
                }
                state.wrapWith(m_types.C_VARIANT);
                return;
            } else if (state.isCurrentResolution(typeNamedEq) && nextElementType == m_types.PIPE) {
                // We are declaring a variant without a pipe before
                // type t = |>X<| | ..
                state.remapCurrentToken(m_types.VARIANT_NAME).
                        mark(state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL).
                        wrapWith(m_types.C_VARIANT);
                return;
            } else if (!state.isCurrentResolution(moduleNamedEq) && !state.isCurrentResolution(maybeFunctorCall) && nextElementType != m_types.DOT) {
                // Must be a variant call
                state.remapCurrentToken(m_types.VARIANT_NAME).
                        wrapWith(m_types.C_VARIANT);
                return;
            }
        }

        state.wrapWith(m_types.C_UPPER_SYMBOL);
    }

    private void parseSwitch(@NotNull ParserState state) {
        boolean inScope = state.isScopeTokenElementType(m_types.LBRACE);
        state.mark(switch_, m_types.C_SWITCH_EXPR).
                setStart(inScope).
                advance().
                mark(switchBinaryCondition, m_types.C_BIN_CONDITION);
    }

    private void parseTry(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(try_, m_types.C_TRY_EXPR).
                advance().
                mark(try_, tryBody, m_types.C_TRY_BODY);
    }

    private void parseCatch(@NotNull ParserState state) {
        if (state.isCurrentResolution(tryBody)) {
            state.popEnd().
                    updateCurrentResolution(tryBodyWith);
        }
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndUnlessFirstContext(signature).
                    advance().
                    mark(state.currentContext(), signatureItem, m_types.C_SIG_ITEM);
        }
        // else if (state.isCurrentContext(typeConstrName)) {
        //    state.advance().popEndUntilContext(type).popEnd();
        //}
        else if (state.isCurrentResolution(functionParameter)) {
            state.popEndUnlessFirstContext(function).
                    advance().
                    mark(function, functionBody, m_types.C_FUN_BODY);
        } else if (state.isCurrentResolution(function)) {
            // let x = ( ... ) |>=><| ..
            state.advance().
                    mark(state.currentContext(), functionBody, m_types.C_FUN_BODY);
        } else if (state.isCurrentResolution(functorNamedEqColon) || state.isCurrentResolution(functorNamedColonResult)) {
            // module Make = (M) : R |>=><| ..
            if (state.isCurrentResolution(functorNamedColonResult)) {
                state.popEnd();
            }
            state.advance().
                    mark(functorBinding, functorBinding, m_types.C_FUNCTOR_BINDING);
        } else if (state.isCurrentResolution(patternMatchVariant) || state.isCurrentResolution(patternMatchVariantConstructor)) {
            // switch ( .. ) { | .. |>=><| .. }
            state.advance().
                    mark(state.currentContext(), patternMatchBody, m_types.C_PATTERN_MATCH_BODY).setStart();
        }
    }

    private boolean shouldStartExpression(@NotNull ParserState state) {
        return state.isInScopeExpression() && state.isScopeTokenElementType(m_types.LBRACE);
    }

    private void endLikeSemi(@NotNull ParserState state) {
        //if (state.isCurrentResolution(includeConstraint)) {
        //    state.complete().popEnd().popEnd();
        //}
        //
        if (!state.isCurrentResolution(functionBody))
        //if (state.previousElementType1 != m_types.EQ && state.previousElementType1 != m_types.RIGHT_ARROW && state.previousElementType1 != m_types.TRY
        //        && state.previousElementType1 != m_types.SEMI && state.previousElementType1 != m_types.THEN && state.previousElementType1 != m_types.ELSE
        //        && state.previousElementType1 != m_types.IN && state.previousElementType1 != m_types.LPAREN && state.previousElementType1 != m_types.DO
        //        && state.previousElementType1 != m_types.STRUCT && state.previousElementType1 != m_types.SIG && state.previousElementType1 != m_types.COLON) {
        {
            state.popEndUntilStartScope();
        }
        //ParserScope parserScope = state.getLatestScope();
        //while (parserScope != null && (parserScope.isContext(function) || parserScope.isContext(match))) {
        //    state.popEnd();
        //    state.popEndUntilStartScope();
        //    parserScope = state.getLatestScope();
        //}
        //}
    }
}
