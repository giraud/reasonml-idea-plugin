package com.reason.lang.reason;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserScopeEnum;
import com.reason.lang.ParserState;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScope.mark;
import static com.reason.lang.ParserScope.markScope;
import static com.reason.lang.ParserScopeEnum.*;

public class RmlParser extends CommonParser<RmlTypes> {

    RmlParser() {
        super(RmlTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;

        //long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //break;
            //}

            state.previousTokenElementType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // special keywords that can be used as lower identifier in records
            if (tokenType == m_types.REF && state.isCurrentResolution(recordBinding)) {
                parseLIdent(builder, state);
            } else if (tokenType == m_types.LIST && state.isCurrentResolution(recordBinding)) {
                parseLIdent(builder, state);
            } else if (tokenType == m_types.METHOD && state.isCurrentResolution(recordBinding)) {
                parseLIdent(builder, state);
            } else if (tokenType == m_types.STRING && state.isCurrentResolution(recordBinding)) {
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
            } else if (tokenType == m_types.TRY) {
                parseTry(builder, state);
            } else if (tokenType == m_types.SWITCH) {
                parseSwitch(builder, state);
            } else if (tokenType == m_types.LIDENT) {
                parseLIdent(builder, state);
            } else if (tokenType == m_types.UIDENT) {
                parseUIdent(builder, state);
            } else if (tokenType == m_types.ARROBASE) {
                parseArrobase(builder, state);
            } else if (tokenType == m_types.PERCENT) {
                parsePercent(builder, state);
            } else if (tokenType == m_types.COLON) {
                parseColon(builder, state);
            } else if (tokenType == m_types.STRING) {
                parseString(builder, state);
            } else if (tokenType == m_types.RAW) {
                parseRaw(builder, state);
            } else if (tokenType == m_types.INT) {
                parseInt(builder, state);
            } else if (tokenType == m_types.BOOL) {
                parseBool(builder, state);
            } else if (tokenType == m_types.ARRAY) {
                parseArray(builder, state);
            } else if (tokenType == m_types.STRING_VALUE) {
                parseStringValue(builder, state);
            } else if (tokenType == m_types.PIPE) {
                parsePipe(builder, state);
            } else if (tokenType == m_types.TILDE) {
                parseTilde(builder, state);
            } else if (tokenType == m_types.COMMA) {
                parseComma(builder, state);
            } else if (tokenType == m_types.AND) {
                parseAnd(builder, state);
            } else if (tokenType == m_types.FUN) {
                parseFun(builder, state);
            } else if (tokenType == m_types.ASSERT) {
                parseAssert(builder, state);
            } else if (tokenType == m_types.IF) {
                parseIf(builder, state);
            } else if (tokenType == m_types.DOT) {
                parseDot(builder, state);
            } else if (tokenType == m_types.DOTDOTDOT) {
                parseDotDotDot(builder, state);
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
                parseBracketGt(builder, state);
            } else if (tokenType == m_types.RBRACKET) {
                parseRBracket(builder, state);
            }
            // < ... >
            else if (tokenType == m_types.LT) {
                parseLt(builder, state);
            } else if (tokenType == m_types.TAG_LT_SLASH) {
                parseLtSlash(builder, state);
            } else if (tokenType == m_types.GT) {
                parseGt(builder, state);
            } else if (tokenType == m_types.TAG_AUTO_CLOSE) {
                parseGtAutoClose(builder, state);
            }
            // {| ... |}
            else if (tokenType == m_types.ML_STRING_OPEN) {
                parseMlStringOpen(builder, state);
            } else if (tokenType == m_types.ML_STRING_CLOSE) {
                parseMlStringClose(state);
            }
            // {j| ... |j}
            else if (tokenType == m_types.JS_STRING_OPEN) {
                parseJsStringOpen(builder, state);
            } else if (tokenType == m_types.JS_STRING_CLOSE) {
                parseJsStringClose(state);
            }
            // Starts an expression
            else if (tokenType == m_types.OPEN) {
                parseOpen(builder, state);
            } else if (tokenType == m_types.INCLUDE) {
                parseInclude(builder, state);
            } else if (tokenType == m_types.EXTERNAL) {
                parseExternal(builder, state);
            } else if (tokenType == m_types.TYPE) {
                parseType(builder, state);
            } else if (tokenType == m_types.MODULE) {
                parseModule(builder, state);
            } else if (tokenType == m_types.CLASS) {
                parseClass(builder, state);
            } else if (tokenType == m_types.LET) {
                parseLet(builder, state);
            } else if (tokenType == m_types.VAL) {
                parseVal(builder, state);
            } else if (tokenType == m_types.PUB) {
                parsePub(builder, state);
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

    private void parseRaw(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(macroName)) {
            state.advance().complete().popEnd()
                    .updateCurrentContext(macroRaw)
                    .updateCurrentResolution(macroRawNamed);
        }
    }

    private void parseUnderscore(ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
            state.complete();
        }
    }

    private void parseIf(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, ifThenStatement, m_types.IF_STMT).complete());
    }

    private void parseDot(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(maybeFunctionParameter) && state.previousTokenElementType == m_types.LPAREN) {
            // yes it should be function
            state.popEnd(); // discard
            ParserScope paramsScope = state.pop();
            ParserScope functionScope = state.pop();
            state.add(functionScope.context(function).resolution(function).compositeElementType(m_types.C_FUN_EXPR))
                    .add(paramsScope.context(function).resolution(functionParameters).compositeElementType(m_types.C_FUN_PARAMS))
                    .advance()
                    .add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM));
        }
    }

    private void parseDotDotDot(@NotNull PsiBuilder builder, ParserState state) {
        if (state.previousTokenElementType == m_types.LBRACE) {
            // Mixin:  ... { <...> x ...
            state.updateCurrentContext(recordUsage).updateCurrentResolution(recordBinding).updateCurrentCompositeElementType(m_types.RECORD_EXPR)
                    .add(mark(builder, recordUsage, mixin, m_types.MIXIN_FIELD));
        }
    }

    private void parseAssert(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, assert_, m_types.ASSERT_STMT).complete());
        state.advance();
    }

    private void parseFun(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(letNamedEq)) {
            state.add(mark(builder, function, m_types.C_FUN_EXPR));
            state.add(mark(builder, functionBody, m_types.C_FUN_BODY));
        }
    }

    private void parseAnd(@NotNull PsiBuilder builder, ParserState state) {
        ParserScope latestScope = state.popEndUntilStartScope();

        if (isTypeResolution(latestScope)) {
            state.advance();
            state.add(mark(builder, type, m_types.EXP_TYPE));
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        } else if (isLetResolution(latestScope)) {
            state.advance();
            state.add(mark(builder, let, m_types.LET_STMT));
        } else if (isModuleResolution(latestScope)) {
            state.advance();
            state.add(mark(builder, module, m_types.MODULE_STMT));
        }
    }

    private void parseComma(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(functionBody)) {
            // a function is part of something else, close it first
            state.popEnd().popEnd();
        } else if (state.isCurrentResolution(modulePath)) {
            state.complete().popEnd();
        }

        if (state.isCurrentResolution(functionParameterNamedSignatureItem)) {
            state.complete().
                    popEndUntilResolution(functionParameterNamed).popEnd().
                    advance().
                    add(mark(builder, functionParameter, m_types.C_FUN_PARAM));
        }

        if (state.isCurrentResolution(signatureItem) && !state.isCurrentContext(recordSignature)) {
            state.popEnd();
            state.advance();
            state.add(mark(builder, state.currentContext(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentContext(signature)) {
            state.popEnd();
        }

        if (state.isCurrentContext(recordSignature)) {
            state.complete().
                    popEndUntilResolution(recordField).popEnd().
                    advance().
                    add(mark(builder, state.currentContext(), recordField, m_types.RECORD_FIELD));
        } else if (state.isCurrentResolution(recordField)) {
            state.popEnd()
                    .advance()
                    .add(mark(builder, state.currentContext(), recordField, m_types.RECORD_FIELD));
        } else if (state.isCurrentResolution(jsObjectFieldNamed)) {
            state.popEnd();
        } else if (state.isCurrentResolution(mixin)) {
            state.popEnd();
        } else if (state.isCurrentContext(functionParameter) || state.isCurrentContext(maybeFunction)) {
            state.popEndUntilStartScope();
        } else if (state.isCurrentResolution(functionParameter)) {
            state.complete().popEnd().
                    advance().
                    add(mark(builder, state.currentContext(), functionParameter, m_types.C_FUN_PARAM));
            IElementType nextTokenType = builder.getTokenType();
            if (nextTokenType != m_types.RPAREN) {
                // not at the end of a list: ie not => (p1, p2<,> )
                state.complete();
            }
        } else if (state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
            // We don't know yet but we need to complete the marker
            state.complete();
            state.popEnd();
        }
    }

    private void parseTilde(PsiBuilder builder, @NotNull ParserState state) {
        IElementType nextToken = builder.rawLookup(1);
        if (m_types.LIDENT == nextToken) {
            if (state.isCurrentContext(paren)) {
                state.updateCurrentContext(functionParameters);
                state.updateCurrentResolution(functionParameters);
            }

            if (state.isCurrentContext(functionParameters)) {
                state.add(mark(builder, functionParameter, m_types.C_FUN_PARAM).complete());
            }
        }
    }

    private void parsePipe(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(mark(builder, typeNamedEqVariant, m_types.C_VARIANT_EXP).complete());
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEndUntilContext(typeBinding);
            state.add(mark(builder, typeNamedEqVariant, m_types.C_VARIANT_EXP).complete());
        } else {
            // By default, a pattern match
            if (state.isCurrentResolution(patternMatchBody)) {
                state.popEnd();
            }
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.add(mark(builder, patternMatch, m_types.PATTERN_MATCH_EXPR).complete());
        }
    }

    private void parseString(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            transitionTo(builder, state, letNamed, m_types.C_LET_NAME);
        } else if (state.isCurrentResolution(external)) {
            transitionTo(builder, state, externalNamed, m_types.LOWER_SYMBOL);
        }
    }

    private void parseInt(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            transitionTo(builder, state, letNamed, m_types.C_LET_NAME);
        } else if (state.isCurrentResolution(external)) {
            transitionTo(builder, state, externalNamed, m_types.LOWER_SYMBOL);
        }
    }

    private void parseBool(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            transitionTo(builder, state, letNamed, m_types.C_LET_NAME);
        } else if (state.isCurrentResolution(external)) {
            transitionTo(builder, state, externalNamed, m_types.LOWER_SYMBOL);
        }
    }

    private void parseArray(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            transitionTo(builder, state, letNamed, m_types.C_LET_NAME);
        } else if (state.isCurrentResolution(external)) {
            transitionTo(builder, state, externalNamed, m_types.LOWER_SYMBOL);
        }
    }

    private void transitionTo(PsiBuilder builder, ParserState state, ParserScopeEnum resolution, IElementType wrapper) {
        builder.remapCurrentToken(m_types.LIDENT);
        state.updateCurrentResolution(resolution).complete();
        state.wrapWith(wrapper);
    }

    private void parseStringValue(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(macroRaw)) {
            state.wrapWith(m_types.C_MACRO_RAW_BODY);
        } else if (state.isCurrentContext(raw)) {
            state.complete()
                    .add(mark(builder, rawBody, m_types.C_MACRO_RAW_BODY).complete())
                    .advance()
                    .popEnd();
        } else if (state.isCurrentResolution(annotationName)) {
            state.popEndUntilStartScope();
        } else if (state.isCurrentResolution(maybeRecordUsage)) {
            IElementType nextToken = builder.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.updateCurrentContext(jsObject).updateCurrentResolution(jsObject).updateCurrentCompositeElementType(m_types.C_JS_OBJECT);
                state.add(markScope(builder, jsObject, jsObjectField, m_types.C_JS_OBJECT_FIELD, m_types.STRING_VALUE));
            }
        } else if (state.isCurrentResolution(jsObject)) {
            state.add(markScope(builder, jsObject, jsObjectField, m_types.C_JS_OBJECT_FIELD, m_types.STRING_VALUE));
        }
    }

    private void parseMlStringOpen(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
            state.endAny();
        }

        if (state.isCurrentContext(macroRaw)) {
            state.add(mark(builder, state.currentContext(), macroRawBody, m_types.C_MACRO_RAW_BODY).complete());
        }

        state.add(markScope(builder, state.currentContext(), multilineStart, m_types.C_ML_INTERPOLATOR, m_types.ML_STRING_OPEN));
    }

    private void parseMlStringClose(ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.ML_STRING_OPEN);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseJsStringOpen(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) { // use space notifier like in tag ?
            state.endAny();
        }

        state.add(markScope(builder, interpolationStart, m_types.SCOPED_EXPR, m_types.JS_STRING_OPEN));
        state.advance();
        state.add(mark(builder, interpolationString, m_types.INTERPOLATION_EXPR).complete());
    }

    private void parseJsStringClose(ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.JS_STRING_OPEN);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseLet(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilStartScope();
        state.add(mark(builder, let, m_types.LET_STMT));
    }

    private void parseVal(@NotNull PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            state.popEndUntilStartScope();
            if (state.isCurrentResolution(clazzBodyScope)) {
                state.add(mark(builder, val, clazzField, m_types.CLASS_FIELD));
            } else {
                state.add(mark(builder, let, m_types.LET_STMT));
            }
        }
    }

    private void parsePub(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilStartScope();
        if (state.isCurrentResolution(clazzBodyScope)) {
            state.add(mark(builder, clazzMethod, m_types.CLASS_METHOD));
        }
    }

    private void parseModule(@NotNull PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            state.popEndUntilStartScope();
            state.add(mark(builder, module, m_types.MODULE_STMT));
        }
    }

    private void parseClass(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilStartScope();
        state.add(mark(builder, clazzDeclaration, clazz, m_types.CLASS_STMT));
    }

    private void parseType(@NotNull PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz) && !state.isCurrentResolution(functorConstraints)) {
            if (!state.isCurrentResolution(letNamedSignature)) {
                state.popEndUntilStartScope();
            }
            state.add(mark(builder, type, m_types.EXP_TYPE));
            state.advance();
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        }
    }

    private void parseExternal(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilStartScope();
        state.add(mark(builder, external, m_types.EXTERNAL_STMT));
    }

    private void parseOpen(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilStartScope();
        state.add(mark(builder, open, m_types.OPEN_STMT));
    }

    private void parseInclude(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilStartScope();
        state.add(mark(builder, include, m_types.INCLUDE_STMT));
    }

    private void parsePercent(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(macro)) {
            state.complete();
            state.add(mark(builder, macro, macroName, m_types.MACRO_NAME));
        } else {
            IElementType nextTokenType = builder.rawLookup(1);
            if (nextTokenType == m_types.RAW) {
                state.add(mark(builder, raw, m_types.C_RAW));
            }
        }
    }

    private void parseColon(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(maybeRecord) && state.isCurrentResolution(genericExpression)) {
            // yes it is a record, remove the maybe
            ParserScope fieldState = state.pop();

            state.updateCurrentContext(recordUsage).updateCurrentCompositeElementType(m_types.RECORD_EXPR);
            if (state.isCurrentResolution(maybeRecordUsage)) {
                state.updateCurrentResolution(record);
            }

            fieldState.context(recordUsage);
            fieldState.resolution(recordField);
            fieldState.compositeElementType(m_types.RECORD_FIELD);
            state.add(fieldState);
        }

        if (state.isCurrentResolution(externalNamed)) {
            state.updateCurrentResolution(externalNamedSignature).
                    advance().
                    add(mark(builder, signature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(letNamed)) {
            state.advance();
            state.add(mark(builder, signature, letNamedSignature, m_types.C_SIG_EXPR).complete());
            IElementType nextTokenType = builder.getTokenType();
            if (nextTokenType != m_types.LPAREN) {
                state.add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
            }
        } else if (state.isCurrentResolution(moduleNamed)) {
            // Module signature
            //   MODULE UIDENT COLON ...
            state.updateCurrentResolution(moduleNamedSignature);
            state.complete();
        } else if (state.isCurrentResolution(functorNamedEq)) {
            state.updateCurrentResolution(functorNamedEqColon);
        } else if (state.isCurrentResolution(recordField)) {
            state.complete();
            state.advance();
            if (!state.isCurrentContext(recordUsage)) {
                state.add(mark(builder, recordSignature, signature, m_types.C_SIG_EXPR).complete());
                state.add(mark(builder, recordSignature, signatureItem, m_types.C_SIG_ITEM).complete());
            }
        } else if (state.isCurrentResolution(jsObjectField)) {
            state.complete();
            state.updateCurrentResolution(jsObjectFieldNamed);
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateCurrentResolution(functionParameterNamed).
                    advance().
                    add(mark(builder, signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, functionParameterNamedSignatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(paren) && state.isCurrentCompositeElementType(m_types.C_UNKNOWN_EXPR)) {
            state.complete().
                    updateCurrentContext(functionParameter).updateCurrentResolution(functionParameterNamed).
                    advance().
                    add(mark(builder, signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signatureItem, functionParameterNamedSignatureItem, m_types.C_SIG_ITEM).complete());

        }
    }

    private void parseArrobase(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotation)) {
            state.complete();
            state.add(mark(builder, annotation, annotationName, m_types.MACRO_NAME).complete());
        }
    }

    private void parseLt(PsiBuilder builder, @NotNull ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT || nextTokenType == m_types.OPTION) { // Note that option is a ReasonML keyword but also a JSX keyword !
            // Surely a tag
            builder.remapCurrentToken(m_types.TAG_LT);
            state.add(mark(builder, jsxTag, m_types.C_TAG).complete())
                    .add(markScope(builder, jsxStartTag, jsxStartTag, m_types.TAG_START, m_types.TAG_LT).complete());

            state.advance();

            builder.remapCurrentToken(m_types.TAG_NAME);
            state.wrapWith(nextTokenType == m_types.UIDENT ? m_types.UPPER_SYMBOL : m_types.LOWER_SYMBOL);
        }
    }

    private void parseLtSlash(PsiBuilder builder, @NotNull ParserState state) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // A closing tag
            if (state.isCurrentContext(jsxTagBody)) {
                state.popEnd();
            }

            builder.remapCurrentToken(m_types.TAG_LT);
            state.add(mark(builder, jsxTagClose, m_types.TAG_CLOSE).complete());

            state.advance();

            builder.remapCurrentToken(m_types.TAG_NAME);
            state.wrapWith(nextTokenType == m_types.UIDENT ? m_types.UPPER_SYMBOL : m_types.LOWER_SYMBOL);
        }
    }

    private void parseGt(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(jsxStartTag)) {
            state.wrapWith(m_types.TAG_GT)
                    .popEnd()
                    .add(mark(builder, jsxTagBody, m_types.C_TAG_BODY).complete());
        } else if (state.isCurrentResolution(jsxTagClose)) {
            state.wrapWith(m_types.TAG_GT).popEnd().popEnd();
        }
    }

    private void parseGtAutoClose(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            state.popEnd().popEnd();
        }

        state.advance().popEnd().popEnd();
    }

    private void parseLIdent(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(modulePath)) {
            state.popEnd();
        }

        if (state.isCurrentResolution(maybeRecord)) {
            // Maybe a record, we must check
            IElementType nextTokenType = builder.lookAhead(1);
            if (nextTokenType == m_types.COLON) {
                // Yes, this is a record binding
                state.updateCurrentResolution(recordBinding);
                state.updateCurrentCompositeElementType(m_types.RECORD_EXPR);
            }
        }

        if (state.isCurrentResolution(typeConstrName)) {
            // TYPE <LIDENT> ...
            state.updateCurrentResolution(typeNamed);
            state.complete();
            state.setPreviousComplete();
        } else if (state.isCurrentResolution(functionParameters)) {
            state.add(mark(builder, functionParameter, m_types.C_FUN_PARAM).complete());
        } else if (state.isCurrentResolution(external)) {
            // EXTERNAL <LIDENT> ...
            state.updateCurrentResolution(externalNamed);
            state.complete();
        } else if (state.isCurrentResolution(let)) {
            // LET <LIDENT> ...
            state.updateCurrentResolution(letNamed).complete();
            state.wrapWith(m_types.C_LET_NAME);
            return;
        } else if (state.isCurrentResolution(letNamedEq)) {
            if (state.previousTokenElementType == m_types.EQ) {
                // let x = <c> => ...
                IElementType nextElementType = builder.lookAhead(1);
                if (nextElementType == m_types.ARROW) {
                    // Single (paren less) function parameters
                    state.add(mark(builder, function, letNamedBindingFunction, m_types.C_FUN_EXPR).complete());
                    state.add(mark(builder, function, functionParameters, m_types.C_FUN_PARAMS).complete());
                    state.add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
                }
            }
        } else if (state.isCurrentResolution(macroName)) {
            state.complete();
            boolean isRaw = "raw".equals(builder.getTokenText());
            if (isRaw) {
                state.advance();
            }
            state.popEnd();
            if (isRaw) {
                state.updateCurrentContext(macroRaw);
            }
            state.updateCurrentResolution(macroNamed);
            return;
        } else if (state.isCurrentResolution(clazz)) {
            // CLASS <LIDENT> ...
            state.updateCurrentResolution(clazzNamed);
            state.complete();
        } else if (state.isCurrentResolution(clazzField)) {
            // [CLASS LIDENT ...] VAL <LIDENT> ...
            state.updateCurrentResolution(clazzFieldNamed);
            state.complete();
        } else if (state.isCurrentResolution(clazzMethod)) {
            // METHOD <LIDENT> ...
            state.updateCurrentResolution(clazzMethodNamed);
            state.complete();
        } else if (state.isCurrentResolution(jsxStartTag)) {
            // This is a property
            state.popEndUntilStartScope();
            builder.remapCurrentToken(m_types.PROPERTY_NAME);
            state.add(mark(builder, jsxTagProperty, m_types.TAG_PROPERTY).complete());
            builder.setWhitespaceSkippedCallback((type, start, end) -> {
                if (state.isCurrentResolution(jsxTagProperty) || (state.isCurrentResolution(jsxTagPropertyEqValue) && state.notInScopeExpression())) {
                    if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
                        state.popEnd();
                    }
                    state.popEnd();
                    builder.setWhitespaceSkippedCallback(null);
                }
            });
        } else if (state.isCurrentResolution(recordBinding)) {
            state.add(mark(builder, state.currentContext(), recordField, m_types.RECORD_FIELD));
        } else if (state.isCurrentResolution(record)) {
            state.add(mark(builder, recordUsage, recordField, m_types.RECORD_FIELD));
        } else if (state.isCurrentResolution(mixin)) {
            state.complete();
        } else if (shouldStartExpression(state)) {
            state.add(mark(builder, state.currentContext(), genericExpression, builder.getTokenType()));
        } else if (!state.isCurrentContext(signature)) {
            IElementType nextElementType = builder.lookAhead(1);
            if (nextElementType == m_types.ARROW) {
                // Single (paren less) function parameters
                // <c> => ...
                state.add(mark(builder, function, function, m_types.C_FUN_EXPR).complete());
                state.add(mark(builder, function, functionParameters, m_types.C_FUN_PARAMS).complete());
                state.add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
            } else {
                // Add a generic wrapper in case it's a parameter
                // It is complete only if we find a comma in the scope
                if (state.isInScopeExpression() && !state.isCurrentContext(jsObject) && !state.isCurrentContext(functorConstraints) &&
                        !state.isCurrentContext(maybeFunction) && !state.isCurrentResolution(patternMatchConstructor) &&
                        state.previousTokenElementType != m_types.DOT) {
                    state.add(mark(builder, state.currentContext(), state.currentResolution(), m_types.C_UNKNOWN_EXPR));
                }
            }
        }

        if (!state.isCurrentResolution(jsxTagProperty)) {
            state.wrapWith(m_types.LOWER_SYMBOL);
        }
    }

    private void parseLBracket(PsiBuilder builder, @NotNull ParserState state) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            if (state.isCurrentResolution(recordField)) {
                state.popEnd();
                state.add(markScope(builder, recordFieldAnnotation, annotation, m_types.ANNOTATION_EXPR, m_types.LBRACKET));
            } else {
                state.add(markScope(builder, annotation, m_types.ANNOTATION_EXPR, m_types.LBRACKET));
            }
        } else if (nextTokenType == m_types.PERCENT) {
            state.add(markScope(builder, macro, m_types.MACRO_EXPR, m_types.LBRACKET));
        } else {
            state.add(markScope(builder, bracket, m_types.SCOPED_EXPR, m_types.LBRACKET));
        }
    }

    private void parseBracketGt(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        state.add(markScope(builder, bracketGt, m_types.SCOPED_EXPR, m_types.LBRACKET));
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
                state.add(mark(builder, state.currentContext(), recordField, m_types.RECORD_FIELD));
            }
        }
    }

    private void parseLBrace(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(modulePath) && state.previousTokenElementType == m_types.DOT) {
            state.updateCurrentResolution(localOpen).updateCurrentCompositeElementType(m_types.LOCAL_OPEN);
            state.complete();
        }

        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(markScope(builder, recordBinding, m_types.RECORD_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignature)) {
            state.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(letNamedEq)) {
            state.add(markScope(builder, maybeRecord, maybeRecordUsage, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(ifThenStatement)) {
            state.add(markScope(builder, scope, brace, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(clazzNamedEq)) {
            state.add(markScope(builder, clazzBodyScope, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(switchBinaryCondition)) {
            boolean isSwitch = state.popEndUntilContext(switch_).isCurrentResolution(switch_);
            state.add(markScope(builder, isSwitch ? switchBody : brace, m_types.SCOPED_EXPR, isSwitch ? m_types.SWITCH : m_types.LBRACE));
        } else if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            // A scoped property
            state.updateScopeToken(m_types.LBRACE);
        } else {
            // it might be a js object
            IElementType nextElement = builder.lookAhead(1);
            if (nextElement == m_types.STRING_VALUE) {
                // js object detected
                state.add(markScope(builder, jsObject, m_types.C_JS_OBJECT, m_types.LBRACE));
            } else {
                state.add(markScope(builder, scope, brace, m_types.SCOPED_EXPR, m_types.LBRACE));
            }
        }
    }

    private void parseRBrace(ParserState state) {
        if (state.isCurrentResolution(recordField)) {
            state.popEnd();
        }

        ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACE, m_types.RECORD, m_types.SWITCH);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }

        if (state.isCurrentResolution(jsxTagPropertyEq)) {
            state.popEnd();
        }
    }

    private void parseLParen(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(switchBinaryCondition)) {
            state.add(markScope(builder, state.currentContext(), paren, m_types.SCOPED_EXPR, m_types.LPAREN));
            return;
        }

        if (state.isCurrentResolution(letNamedSignature)) {
            state.add(markScope(builder, state.currentContext(), signatureScope, m_types.LPAREN, m_types.LPAREN)).
                    advance().
                    add(mark(builder, state.currentContext(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(moduleNamedEq) && state.previousTokenElementType != m_types.UIDENT) {
            // This is a functor :: module M = <(> .. )
            state.updateCurrentContext(functorDeclaration).
                    updateCurrentResolution(functorNamedEq).
                    updateCurrentCompositeElementType(m_types.C_FUNCTOR).
                    add(markScope(builder, functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN));
        } else if (state.isCurrentResolution(functorNamedEqColon)) {
            // Functor constraint :: module M = (..) : <(> .. ) =
            state.add(markScope(builder, functorConstraints, m_types.C_FUNCTOR_CONSTRAINTS, m_types.LPAREN));
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Variant params :: type t = | Variant <(> .. )
            state.add(markScope(builder, variantConstructor, m_types.C_FUN_PARAMS, m_types.LPAREN)).
                    advance().
                    add(mark(builder, variantConstructor, functionParameter, m_types.C_FUN_PARAM));
        } else if (state.isCurrentResolution(modulePath) && state.previousTokenElementType == m_types.DOT) {
            state.updateCurrentResolution(localOpen).updateCurrentCompositeElementType(m_types.LOCAL_OPEN);
            state.complete();
            state.add(markScope(builder, paren, m_types.SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.add(markScope(builder, state.currentContext(), scope, m_types.SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamedParameters)) {
            state.add(markScope(builder, state.currentContext(), clazzConstructor, m_types.CLASS_CONSTR, m_types.LPAREN));
        } else if (state.isCurrentResolution(ifThenStatement)) {
            state.complete();
            state.add(markScope(builder, binaryCondition, m_types.BIN_CONDITION, m_types.LPAREN).complete());
        } else if (state.isCurrentResolution(patternMatch)) {
            // | Some <(> ... ) =>     It's a constructor
            state.add(markScope(builder, state.currentContext(), patternMatchConstructor, m_types.C_VARIANT_CONSTRUCTOR, m_types.LPAREN));
        } else if (state.previousTokenElementType == m_types.LIDENT) {
            // calling a function
            state.
                    add(markScope(builder, functionCall, functionCallParams, m_types.FUN_CALL_PARAMS, m_types.LPAREN)).
                    advance().
                    add(mark(builder, functionCall, functionParameter, m_types.C_FUN_PARAM));
        } else if (state.isCurrentResolution(functionParameter)) {
            // might be a function inside a parameter
            state.add(markScope(builder, maybeFunction, maybeFunction, m_types.C_UNKNOWN_EXPR, m_types.LPAREN))
                    .add(mark(builder, maybeFunction, maybeFunctionParameters, m_types.C_UNKNOWN_EXPR))
                    .add(mark(builder, maybeFunction, maybeFunctionParameter, m_types.C_UNKNOWN_EXPR));
        } else {
            if (state.isCurrentResolution(external)) {
                // overloading an operator
                state.updateCurrentResolution(externalNamed);
                state.complete();
            }

            if (!state.isCurrentResolution(patternMatch) && !state.isCurrentContext(recordSignature) &&
                    !state.isCurrentResolution(letNamedSignature) && !state.isCurrentResolution(jsxTagPropertyEq) &&
                    !state.isCurrentContext(typeConstrName) && !state.isCurrentResolution(signatureItem)) {
                // just a marker that will be used only if it's a function (duplicate the current token type)
                state.add(mark(builder, genericExpression, m_types.LPAREN));
            }

            if (state.previousTokenElementType == m_types.LIDENT) {
                state.add(markScope(builder, paren, functionCallParams, m_types.FUN_CALL_PARAMS, m_types.LPAREN));
            } else {
                state.add(markScope(builder, paren, m_types.SCOPED_EXPR, m_types.LPAREN));
            }
        }
    }

    private void parseRParen(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(signatureItem)) {
            state.popEnd();
        }
        if (state.isCurrentResolution(signatureScope)) {
            state.popEnd();
        }

        if (state.isCurrentResolution(letNamedSignature)) {
            return;
        } else if (state.isCurrentResolution(switchBinaryCondition)) {
            state.popEnd();
            return;
        } else if (state.isCurrentContext(maybeFunction) && state.isCurrentResolution(maybeFunctionParameter)) {
            IElementType nextElementType = builder.lookAhead(1);
            if (nextElementType == m_types.ARROW) {
                // Time to decide it's a function
                state.updateCurrentCompositeElementType(m_types.C_FUN_PARAM).complete().popEnd()
                        .updateCurrentCompositeElementType(m_types.C_FUN_PARAMS).complete().popEnd()
                        .updateCurrentContext(function).updateCurrentResolution(function).updateCurrentCompositeElementType(m_types.C_FUN_EXPR).complete();
                return;
            }
        }

        if (!state.isScopeTokenElementType(m_types.LPAREN) && !state.isCurrentEmpty()) {
            state.complete();
        }

        ParserScope parenScope = state.endUntilScopeToken(m_types.LPAREN);
        state.advance();
        IElementType nextTokenType = builder.getTokenType();

        if (parenScope != null) {
            // Remove the scope from the stack, we want to test its parent
            state.pop();

            if (nextTokenType == m_types.ARROW) {
                if (!state.isCurrentResolution(patternMatch) && !state.isCurrentContext(signature) && !state.isCurrentContext(functorDeclaration)) {
                    parenScope.resolution(functionParameters);
                    parenScope.compositeElementType(m_types.C_FUN_PARAMS);
                }
            } else if (nextTokenType == m_types.LPAREN) {
                if (state.isCurrentResolution(clazzNamed)) {
                    // First parens found, it must be a class parameter
                    parenScope.compositeElementType(m_types.CLASS_PARAMS);
                    state.updateCurrentResolution(clazzNamedParameters);
                }
            } else if (nextTokenType == m_types.EQ) {
                if (state.isCurrentResolution(clazzNamed)) {
                    parenScope.compositeElementType(m_types.CLASS_CONSTR);
                    state.updateCurrentResolution(clazzNamedConstructor);
                } else if (parenScope.isResolution(clazzConstructor)) {
                    state.updateCurrentResolution(clazzConstructor);
                }
            }

            parenScope.complete();
            parenScope.end();

            // Handle the generic scope
            if (parenScope.isResolution(functionParameters) && nextTokenType == m_types.ARROW) {
                // Transform the generic scope to a function scope
                state.updateCurrentResolution(function);
                state.updateCurrentCompositeElementType(m_types.C_FUN_EXPR);
                state.complete();
            } else if (state.isCurrentResolution(genericExpression)) {
                state.popEnd();
            }

            ParserScope scope = state.getLatestScope();
            if (scope != null && (scope.isResolution(localOpen) || (scope.isResolution(jsxTagPropertyEqValue) && !scope.isScope()))) {
                if (scope.isResolution(jsxTagPropertyEqValue)) {
                    state.popEnd();
                }
                state.popEnd();
            }
        }
    }

    private void parseEq(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndWhileContext(signature);
        }

        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd().
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    add(mark(builder, typeBinding, typeNamedEq, m_types.C_TYPE_BINDING).complete());
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedSignature)) {
            if (state.isCurrentResolution(letNamedSignature)) {
                state.popEnd();
            }
            state.updateCurrentResolution(letNamedEq)
                    .advance()
                    .add(mark(builder, letBinding, letNamedEq, m_types.LET_BINDING).complete());
        } else if (state.isCurrentResolution(jsxTagProperty)) {
            state.updateCurrentResolution(jsxTagPropertyEq)
                    .complete()
                    .advance()
                    .add(mark(builder, state.currentContext(), jsxTagPropertyEqValue, m_types.C_TAG_PROP_VALUE).complete());
        } else if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedEq).complete();
        } else if (state.isCurrentResolution(externalNamedSignature)) {
            state.complete().
                    updateCurrentResolution(externalNamedSignatureEq);
        } else if (state.isCurrentResolution(clazzNamed) || state.isCurrentResolution(clazzConstructor)) {
            state.updateCurrentResolution(clazzNamedEq);
        }
    }

    private void parseSemi(ParserState state) {
        // Don't pop the scopes
        state.popEndUntilStartScope();
    }

    private void parseUIdent(PsiBuilder builder, @NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(builder.getTokenText())) {
            return;
        }

        if (state.isCurrentResolution(open)) {
            // It is a module name/path
            state.complete();
        } else if (state.isCurrentResolution(include)) {
            // It is a module name/path
            state.complete();
        } else if (state.isCurrentResolution(module)) {
            state.updateCurrentResolution(moduleNamed);
        } else if ((state.isCurrentResolution(jsxStartTag) || state.isCurrentResolution(jsxTagClose)) && state.previousTokenElementType == m_types.DOT) {
            // a namespaced custom component
            builder.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.previousTokenElementType == m_types.PIPE) {
            builder.remapCurrentToken(m_types.VARIANT_NAME);
            state.add(mark(builder, typeNamedEqVariant, m_types.C_VARIANT_CONSTRUCTOR).complete());
            return;
        } else {
            if (shouldStartExpression(state)) {
                IElementType tokenType = builder.getTokenType();
                if (tokenType != null) {
                    state.add(mark(builder, genericExpression, tokenType));
                }
            }

            if (!state.isCurrentResolution(modulePath)) {
                IElementType nextElementType = builder.lookAhead(1);
                if (nextElementType == m_types.DOT) {
                    // We are parsing a module path
                    state.add(mark(builder, modulePath, m_types.UPPER_SYMBOL));
                } else if (nextElementType == m_types.PIPE) {
                    // We are parsing a variant
                    builder.remapCurrentToken(m_types.VARIANT_NAME);
                    state.add(mark(builder, typeNamedEqVariant, m_types.C_VARIANT_EXP).complete()).
                            add(mark(builder, typeNamedEqVariant, m_types.C_VARIANT_CONSTRUCTOR).complete());
                    return;
                }
            }
        }

        state.wrapWith(m_types.UPPER_SYMBOL);
    }

    private void parseSwitch(@NotNull PsiBuilder builder, ParserState state) {
        boolean inScope = state.isScopeTokenElementType(m_types.LBRACE);
        state.
                add(mark(builder, switch_, m_types.SWITCH_EXPR).complete().setIsStart(inScope)).
                advance().
                add(mark(builder, switchBinaryCondition, m_types.BIN_CONDITION).complete());
    }

    private void parseTry(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, try_, m_types.TRY_EXPR).complete()).
                advance().
                add(mark(builder, tryScope, m_types.SCOPED_EXPR).complete());
    }

    private void parseArrow(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndUnlessFirstContext(signature).
                    advance().
                    add(mark(builder, state.currentContext(), signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentContext(typeConstrName)) {
            state.advance().popEndUntilContext(type).popEnd();
        } else if (state.isCurrentResolution(functionParameter)) {
            state.popEndUnlessFirstContext(function)
                    .advance()
                    .add(mark(builder, function, functionBody, m_types.C_FUN_BODY).complete());
        } else if (state.isCurrentResolution(function)) {
            // let x = ($ANY) < => > ...
            state.advance().add(mark(builder, state.currentContext(), functionBody, m_types.C_FUN_BODY).complete());
        } else if (state.isCurrentResolution(functorNamedEqColon)) {
            // functor :: module Make = (M) : R < => > ..
            state.advance().add(mark(builder, functorBinding, functorBinding, m_types.C_FUNCTOR_BINDING).complete());
        } else {
            state.advance();
            IElementType nextTokenType = builder.getTokenType();
            if (nextTokenType != m_types.LBRACE) {
                if (state.isCurrentResolution(patternMatch)) {
                    state.add(mark(builder, state.currentContext(), patternMatchBody, m_types.SCOPED_EXPR));
                }
            }
        }
    }

    private boolean shouldStartExpression(ParserState state) {
        return state.isInScopeExpression() && state.isScopeTokenElementType(m_types.LBRACE);
    }
}
