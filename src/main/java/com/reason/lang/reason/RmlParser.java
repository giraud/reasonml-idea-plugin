package com.reason.lang.reason;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.groupExpression;
import static com.reason.lang.ParserScopeType.scopeExpression;

public class RmlParser extends CommonParser {

    RmlParser() {
        super(RmlTypes.INSTANCE);
    }

    @Override
    protected void parseFile(PsiBuilder builder, ParserState parserState) {
        IElementType tokenType = null;

        int c = current_position_(builder);
        while (true) {
            parserState.previousTokenType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // Anything can be a new expression
            // A new element inside a { block starts an expression
            //if (parserState.isInScopeExpression() && parserState.isScopeElementType(m_types.LBRACE)) {
            //    parserState.add(markScope(builder, genericExpression, tokenType, startExpression, tokenType));
            //}

            if (tokenType == m_types.SEMI) {
                parseSemi(builder, parserState);
            } else if (tokenType == m_types.EQ) {
                parseEq(parserState);
            } else if (tokenType == m_types.ARROW) {
                parseArrow(builder, parserState);
            } else if (tokenType == m_types.TRY) {
                parseTry(builder, parserState);
            } else if (tokenType == m_types.SWITCH) {
                parseSwitch(builder, parserState);
            } else if (tokenType == m_types.LIDENT) {
                parseLIdent(builder, parserState);
            } else if (tokenType == m_types.UIDENT) {
                parseUIdent(builder, parserState);
            } else if (tokenType == m_types.ARROBASE) {
                parseArrobase(builder, parserState);
            } else if (tokenType == m_types.PERCENT) {
                parsePercent(builder, parserState);
            } else if (tokenType == m_types.COLON) {
                parseColon(builder, parserState);
            } else if (tokenType == m_types.STRING) {
                parseString(builder, parserState);
            } else if (tokenType == m_types.PIPE) {
                parsePipe(builder, parserState);
            } else if (tokenType == m_types.TILDE) {
                parseTilde(builder, parserState);
            } else if (tokenType == m_types.COMMA) {
                parseComma(builder, parserState);
            } else if (tokenType == m_types.AND) {
                parseAnd(builder, parserState);
            } else if (tokenType == m_types.FUN) {
                parseFun(builder, parserState);
            }
            // ( ... )
            else if (tokenType == m_types.LPAREN) {
                parseLParen(builder, parserState);
            } else if (tokenType == m_types.RPAREN) {
                parseRParen(builder, parserState);
            }
            // { ... }
            else if (tokenType == m_types.LBRACE) {
                parseLBrace(builder, parserState);
            } else if (tokenType == m_types.RBRACE) {
                parseRBrace(builder, parserState);
            }
            // [ ... ]
            else if (tokenType == m_types.LBRACKET) {
                parseLBracket(builder, parserState);
            } else if (tokenType == m_types.RBRACKET) {
                parseRBracket(builder, parserState);
            }
            // < ... >
            else if (tokenType == m_types.LT) {
                parseLt(builder, parserState);
            } else if (tokenType == m_types.TAG_LT_SLASH) {
                parseLtSlash(builder, parserState);
            } else if (tokenType == m_types.GT || tokenType == m_types.TAG_AUTO_CLOSE) {
                parseGtAutoClose(builder, parserState);
            }
            // {| ... |}
            else if (tokenType == m_types.ML_STRING_OPEN) {
                parseMlStringOpen(builder, parserState);
            } else if (tokenType == m_types.ML_STRING_CLOSE) {
                parseMlStringClose(builder, parserState);
            }
            // {j| ... |j}
            else if (tokenType == m_types.JS_STRING_OPEN) {
                parseJsStringOpen(builder, parserState);
            } else if (tokenType == m_types.JS_STRING_CLOSE) {
                parseJsStringClose(builder, parserState);
            }
            // Starts an expression
            else if (tokenType == m_types.OPEN) {
                parseOpen(builder, parserState);
            } else if (tokenType == m_types.EXTERNAL) {
                parseExternal(builder, parserState);
            } else if (tokenType == m_types.TYPE) {
                parseType(builder, parserState);
            } else if (tokenType == m_types.MODULE) {
                parseModule(builder, parserState);
            } else if (tokenType == m_types.LET) {
                parseLet(builder, parserState);
            } else if (tokenType == m_types.VAL) {
                parseVal(builder, parserState);
            }
            //else {
            //     if local scope, starts a new expression
            //if (parserState.isInScopeExpression() && parserState.isScopeElementType(m_types.LBRACE)) {
            //    parserState.add(markScope(builder, genericExpression, tokenType, startExpression, tokenType));
            //}
            //}

            if (parserState.dontMove) {
                parserState.dontMove = false;
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "reasonFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }

    private void parseFun(PsiBuilder builder, ParserState state) {
        if (state.isResolution(letNamedEq)) {
            state.add(markScope(builder, letFunBody, m_types.LET_BINDING, groupExpression, m_types.FUN));
        }
    }

    private void parseAnd(PsiBuilder builder, ParserState state) {
        if (state.isResolution(typeNamed) || state.isResolution(typeNamedEq)) {
            state.endUntilScopeExpression(null);
            state.dontMove = advance(builder);
            state.addStart(mark(builder, type, m_types.TYPE_EXPRESSION));
        } else if (state.isResolution(letNamedEq)) {
            state.endUntilScopeExpression(null);
            state.dontMove = advance(builder);
            state.addStart(mark(builder, let, m_types.LET_EXPRESSION));
        }
    }

    private void parseComma(PsiBuilder builder, ParserState state) {
        if (state.isResolution(namedSymbolSignature)) {
            state.setComplete();
            state.popEnd();
        }
    }

    private void parseTilde(PsiBuilder builder, ParserState state) {
        IElementType nextToken = builder.rawLookup(1);
        if (m_types.LIDENT == nextToken) {
            state.add(mark(builder, namedSymbol, m_types.NAMED_SYMBOL));
        }
    }

    private void parsePipe(PsiBuilder builder, ParserState state) {
        if (state.isResolution(typeNamedEq)) {
            state.add(markCompleteScope(builder, typeNamedEqVariant, m_types.VARIANT, groupExpression, m_types.PIPE));
        } else if (state.isResolution(typeNamedEqVariant)) {
            state.popEnd();
            state.add(markCompleteScope(builder, typeNamedEqVariant, m_types.VARIANT, groupExpression, m_types.PIPE));
        } else if (state.isInScopeExpression()) {
            // By default, a pattern match
            if (state.isResolution(patternMatchBody)) {
                state.popEnd();
            }
            if (state.isResolution(patternMatch)) {
                state.popEnd();
            }
            state.add(markCompleteScope(builder, patternMatch, m_types.PATTERN_MATCH_EXPR, groupExpression, null));
        }
    }

    private void parseString(PsiBuilder builder, ParserState state) {
        if (state.isResolution(annotationName) || state.isResolution(macroName)) {
            state.endAny();
        } else if (state.isResolution(brace)) {
            IElementType nextToken = builder.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.setResolution(jsObject);
                state.setTokenType(m_types.OBJECT);
                state.dontMove = wrapWith(m_types.OBJECT_FIELD, builder);
            }
        } else if (state.isResolution(jsObject)) {
            IElementType nextToken = builder.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.dontMove = wrapWith(m_types.OBJECT_FIELD, builder);
            }
        }
    }

    private void parseMlStringOpen(PsiBuilder builder, ParserState state) {
        if (state.isResolution(annotationName) || state.isResolution(macroName)) {
            state.endAny();
        }

        state.add(markScope(builder, multilineStart, m_types.SCOPED_EXPR, scopeExpression, m_types.ML_STRING_OPEN));
    }

    private void parseMlStringClose(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.ML_STRING_OPEN);
        parserState.dontMove = advance(builder);

        if (scope != null) {
            scope.complete();
            scope = parserState.pop();
            if (scope != null) {
                scope.end();
            }
        }

        parserState.updateCurrentScope();
    }

    private void parseJsStringOpen(PsiBuilder builder, ParserState state) {
        if (state.isResolution(annotationName) || state.isResolution(macroName)) { // use space notifier like in tag ?
            state.endAny();
        }

        state.add(markScope(builder, interpolationStart, m_types.SCOPED_EXPR, scopeExpression, m_types.JS_STRING_OPEN));
        state.dontMove = advance(builder);
        state.add(markComplete(builder, interpolationString, m_types.INTERPOLATION));
    }

    private void parseJsStringClose(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.JS_STRING_OPEN);
        parserState.dontMove = advance(builder);

        if (scope != null) {
            scope.complete();
            parserState.popEnd();
        }
    }

    private void parseLet(PsiBuilder builder, ParserState state) {
        state.endAny();
        state.addStart(mark(builder, let, m_types.LET_EXPRESSION));
    }

    private void parseVal(PsiBuilder builder, ParserState state) {
        state.endAny();
        state.addStart(mark(builder, let, m_types.LET_EXPRESSION));
    }

    private void parseModule(PsiBuilder builder, ParserState state) {
        if (state.notResolution(annotationName)) {
            state.endUntilScopeExpression(null);
            state.addStart(mark(builder, module, m_types.MODULE_EXPRESSION));
        }
    }

    private void parseType(PsiBuilder builder, ParserState state) {
        if (state.notResolution(module)) {
            state.endUntilScopeExpression(null);
            state.addStart(mark(builder, type, m_types.TYPE_EXPRESSION));
        }
    }

    private void parseExternal(PsiBuilder builder, ParserState state) {
        state.endAny();
        state.addStart(mark(builder, external, m_types.EXTERNAL_EXPRESSION));
    }

    private void parseOpen(PsiBuilder builder, ParserState state) {
        state.endAny();
        state.addStart(mark(builder, open, m_types.OPEN_EXPRESSION));
    }

    private void parsePercent(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(macro)) {
            parserState.setComplete();
            parserState.add(markComplete(builder, macroName, m_types.MACRO_NAME));
            parserState.setComplete();
        }
    }

    private void parseColon(PsiBuilder builder, ParserState state) {
        if (state.isResolution(externalNamed)) {
            state.dontMove = advance(builder);
            state.add(markScope(builder, externalNamedSignature, m_types.SIG_SCOPE, groupExpression, m_types.SIG));
        } else if (state.isResolution(letNamed)) {
            state.dontMove = advance(builder);
            state.add(markCompleteScope(builder, letNamedSignature, m_types.SIG_SCOPE, groupExpression, m_types.SIG));
        } else if (state.isResolution(moduleNamed)) {
            // Module signature
            //   MODULE UIDENT COLON ...
            state.setResolution(moduleNamedSignature);
            state.setComplete();
        } else if (state.isResolution(namedSymbol)) {
            state.setComplete();
            state.popEnd();

            state.dontMove = advance(builder);
            state.add(markScope(builder, namedSymbolSignature, m_types.SIG_SCOPE, groupExpression, null));
        }
    }

    private void parseArrobase(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(annotation)) {
            parserState.setComplete();
            parserState.add(markComplete(builder, annotationName, m_types.MACRO_NAME));
        }
    }

    private void parseLt(PsiBuilder builder, ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // Surely a tag
            builder.remapCurrentToken(m_types.TAG_LT);
            state.add(markCompleteScope(builder, startTag, m_types.TAG_START, groupExpression, m_types.TAG_LT));
            state.dontMove = advance(builder);

            builder.remapCurrentToken(m_types.TAG_NAME);
            state.dontMove = wrapWith(nextTokenType == m_types.UIDENT ? m_types.UPPER_SYMBOL : m_types.LOWER_SYMBOL, builder);
        }
    }

    private void parseLtSlash(PsiBuilder builder, ParserState state) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // A closing tag
            builder.remapCurrentToken(m_types.TAG_LT);
            state.add(markCompleteScope(builder, closeTag, m_types.TAG_CLOSE, groupExpression, m_types.TAG_LT));
            state.dontMove = advance(builder);
            builder.remapCurrentToken(m_types.TAG_NAME);
        }
    }

    private void parseGtAutoClose(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentTokenType(m_types.TAG_PROPERTY)) {
            parserState.popEnd();
        }

        if (parserState.isResolution(startTag) || parserState.isResolution(closeTag)) {
            builder.remapCurrentToken(m_types.TAG_GT);
            parserState.dontMove = advance(builder);
            parserState.popEnd();
        }
    }

    private void parseLIdent(PsiBuilder builder, ParserState state) {
        if (state.isResolution(type)) {
            // TYPE LIDENT ...
            builder.remapCurrentToken(m_types.TYPE_CONSTR_NAME);
            state.setResolution(typeNamed);
            state.setComplete();
        } else if (state.isResolution(external)) {
            state.setResolution(externalNamed);
            state.setComplete();
        } else if (state.isResolution(let)) {
            state.setResolution(letNamed);
            state.setComplete();
        } else if (state.isResolution(startTag)) {
            // This is a property
            state.endAny();
            builder.remapCurrentToken(m_types.PROPERTY_NAME);
            state.add(markComplete(builder, tagProperty, m_types.TAG_PROPERTY));
            builder.setWhitespaceSkippedCallback((type, start, end) -> {
                if (state.isResolution(tagProperty) || (state.isResolution(tagPropertyEq) && state.notInScopeExpression())) {
                    state.popEnd();
                    builder.setWhitespaceSkippedCallback(null);
                }
            });
        }

        state.dontMove = wrapWith(m_types.LOWER_SYMBOL, builder);
    }

    private void parseLBracket(PsiBuilder builder, ParserState parserState) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            parserState.add(markScope(builder, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET));
        } else if (nextTokenType == m_types.PERCENT) {
            parserState.add(markScope(builder, macro, m_types.MACRO_EXPRESSION, scopeExpression, m_types.LBRACKET));
        } else {
            parserState.add(markScope(builder, bracket, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACKET));
        }
    }

    private void parseRBracket(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACKET);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            if (scope.resolution != annotation) {
                scope.complete();
            }

            scope = parserState.pop();
            if (scope != null) {
                scope.end();
            }
        }

        parserState.updateCurrentScope();
    }

    private void parseLBrace(PsiBuilder builder, ParserState state) {
        if (state.isResolution(typeNamedEq)) {
            state.add(markScope(builder, objectBinding, m_types.OBJECT, scopeExpression, m_types.LBRACE));
        } else if (state.isResolution(moduleNamedEq) || state.isResolution(moduleNamedSignature)) {
            state.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE));
        } else if (state.isResolution(letNamedEqParameters)) {
            state.add(markScope(builder, letFunBody, m_types.LET_BINDING, scopeExpression, m_types.LBRACE));
        } else {
            ParserScope scope;
            if (state.isResolution(switchBinaryCondition)) {
                scope = state.endUntilScopeExpression(m_types.SWITCH);
            } else {
                scope = state.endAny();
            }
            state.add(markScope(builder, brace, m_types.SCOPED_EXPR, scopeExpression, scope != null && scope.resolution == switch_ ? m_types.SWITCH : m_types.LBRACE));
        }
    }

    private void parseRBrace(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACE);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete();
            parserState.popEnd();
        }
    }

    private void parseRParen(PsiBuilder builder, ParserState state) {
        ParserScope scope = state.endUntilScopeExpression(m_types.LPAREN);
        state.dontMove = advance(builder);

        if (scope != null) {
            scope.complete();
            state.popEnd();
            scope = state.getLatestScope();
            if (scope != null && scope.resolution == letNamedEq) {
                scope.resolution = letNamedEqParameters;
            }
        }
    }

    private void parseLParen(PsiBuilder builder, ParserState state) {
        if (state.isResolution(external)) {
            // overloading an operator
            state.setResolution(externalNamed);
            state.setComplete();
        }

        state.endAny();

        if (state.isResolution(letNamedEq)) {
            // function parameters
            state.add(markScope(builder, letParameters, m_types.LET_FUN_PARAMS, scopeExpression, m_types.LPAREN));
        } else {
            state.add(markScope(builder, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN));
        }
    }

    private void parseEq(ParserState state) {
        if (state.isResolution(typeNamed)) {
            state.setResolution(typeNamedEq);
        } else if (state.isResolution(letNamed)) {
            state.setResolution(letNamedEq);
        } else if (state.isResolution(tagProperty)) {
            state.setResolution(tagPropertyEq);
        } else if (state.isResolution(moduleNamed)) {
            state.setResolution(moduleNamedEq);
            state.setComplete();
        } else if (state.isResolution(externalNamedSignature)) {
            state.setComplete();
            state.endUntilStart();
        }
    }

    private void parseSemi(PsiBuilder builder, ParserState state) {
        if (state.isResolution(patternMatchBody)) {
            state.endAny();
        } else {
            // End current start-expression scope
            ParserScope scope = state.endUntilStart();
            if (scope != null && state.isStart(scope)) {
                state.dontMove = advance(builder);
                state.popEnd();
            }
        }
    }

    private void parseUIdent(PsiBuilder builder, ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(builder.getTokenText())) {
            return;
        }

        if (state.isResolution(open)) {
            // It is a module name/path
            state.setComplete();
        } else if (state.isResolution(module)) {
            state.setResolution(moduleNamed);
        } else if (state.isResolution(startTag) && state.previousTokenType == m_types.DOT) {
            // a namespaced custom component
            builder.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.isResolution(typeNamedEqVariant) && state.previousTokenType == m_types.PIPE) {
            builder.remapCurrentToken(m_types.VARIANT_NAME);
        }

        state.dontMove = wrapWith(m_types.UPPER_SYMBOL, builder);
    }

    private void parseSwitch(PsiBuilder builder, ParserState state) {
        boolean inScope = state.isScopeElementType(m_types.LBRACE);
        ParserScope scope = markCompleteScope(builder, switch_, m_types.SWITCH, groupExpression, m_types.SWITCH);
        state.add(scope, inScope);
        state.dontMove = advance(builder);
        state.add(markCompleteScope(builder, switchBinaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseTry(PsiBuilder builder, ParserState parserState) {
        parserState.add(markCompleteScope(builder, try_, m_types.TRY, groupExpression, m_types.TRY));
        parserState.dontMove = advance(builder);
        parserState.add(markCompleteScope(builder, tryBinaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseArrow(PsiBuilder builder, ParserState state) {
        state.dontMove = advance(builder);
        IElementType nextTokenType = builder.getTokenType();

        if (nextTokenType != m_types.LBRACE) {
            if (state.isResolution(patternMatch)) {
                state.add(markScope(builder, patternMatchBody, m_types.SCOPED_EXPR, groupExpression, null));
            } else if (state.isResolution(letNamedEqParameters)) { // let x = ($ANY) => <EXPR>
                state.add(markCompleteScope(builder, letFunBody, m_types.LET_BINDING, scopeExpression, null));
            }
        }
    }
}
