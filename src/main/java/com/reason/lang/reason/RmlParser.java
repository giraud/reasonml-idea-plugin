package com.reason.lang.reason;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.*;

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
                parseString(parserState);
            } else if (tokenType == m_types.PIPE) {
                parsePipe(builder, parserState);
            } else if (tokenType == m_types.TILDE) {
                parseTilde(builder, parserState);
            } else if (tokenType == m_types.COMMA) {
                parseComma(builder, parserState);
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
        }
    }

    private void parseString(ParserState state) {
        if (state.isResolution(annotationName) || state.isResolution(macroName)) {
            state.endAny();
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
            scope.complete = true;
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
            scope.complete = true;
            parserState.popEnd();
        }
    }

    private void parseLet(PsiBuilder builder, ParserState parserState) {
        parserState.endAny();
        parserState.add(markScope(builder, let, m_types.LET_EXPRESSION, startExpression, m_types.LET));
    }

    private void parseVal(PsiBuilder builder, ParserState parserState) {
        parserState.endAny();
        parserState.add(markScope(builder, let, m_types.LET_EXPRESSION, startExpression, m_types.VAL));
    }

    private void parseModule(PsiBuilder builder, ParserState parserState) {
        if (parserState.notResolution(annotationName)) {
            parserState.endUntilScopeExpression(null);
            parserState.add(markScope(builder, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE));
        }
    }

    private void parseType(PsiBuilder builder, ParserState state) {
        if (state.notResolution(module)) {
            state.endUntilScopeExpression(null);
            state.add(markScope(builder, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE));
        }
    }

    private void parseExternal(PsiBuilder builder, ParserState parserState) {
        parserState.endAny();
        parserState.add(markScope(builder, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL));
    }

    private void parseOpen(PsiBuilder builder, ParserState parserState) {
        parserState.endAny();
        parserState.add(markScope(builder, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN));
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
                scope.complete = true;
            }

            scope = parserState.pop();
            if (scope != null) {
                scope.end();
            }
        }

        parserState.updateCurrentScope();
    }

    private void parseLBrace(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(typeNamedEq)) {
            parserState.add(markScope(builder, objectBinding, m_types.OBJECT_EXPR, scopeExpression, m_types.LBRACE));
        } else if (parserState.isResolution(moduleNamedEq) || parserState.isResolution(moduleNamedSignature)) {
            parserState.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE));
        } else if (parserState.isResolution(letNamedEqParameters)) {
            parserState.add(markScope(builder, letFunBody, m_types.LET_BINDING, scopeExpression, m_types.LBRACE));
        } else {
            if (parserState.isResolution(switchBinaryCondition)) {
                parserState.endUntilScopeExpression(m_types.SWITCH);
            } else {
                parserState.endAny();
            }
            parserState.add(markScope(builder, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE));
        }
    }

    private void parseRBrace(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACE);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            scope = parserState.pop();
            if (scope != null) {
                scope.end();
            }
        }

        parserState.updateCurrentScope();
    }

    private void parseRParen(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LPAREN);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            ParserScope poppedScope = parserState.pop();
            if (poppedScope != null) {
                poppedScope.end();
            }
            scope = parserState.getLatestScope();
            if (scope != null && scope.resolution == letNamedEq) {
                scope.resolution = letNamedEqParameters;
            }
        }

        parserState.updateCurrentScope();
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

    private void parseEq(ParserState parserState) {
        if (parserState.isResolution(typeNamed)) {
            parserState.setResolution(typeNamedEq);
        } else if (parserState.isResolution(letNamed)) {
            parserState.setResolution(letNamedEq);
        } else if (parserState.isResolution(tagProperty)) {
            parserState.setResolution(tagPropertyEq);
        } else if (parserState.isResolution(moduleNamed)) {
            parserState.setResolution(moduleNamedEq);
            parserState.setComplete();
        } else if (parserState.isResolution(externalNamedSignature)) {
            parserState.setComplete();
            parserState.endUntilStart();
        }
    }

    private void parseSemi(PsiBuilder builder, ParserState parserState) {
        // End current start-expression scope
        ParserScope scope = parserState.endUntilStart();
        if (scope != null && (scope.scopeType == startExpression || scope.tokenType == m_types.LET_BINDING)) {
            builder.advanceLexer();
            parserState.dontMove = true;
            parserState.pop();
            scope.end();
        }
    }

    private void parseUIdent(PsiBuilder builder, ParserState state) {
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

    private void parseSwitch(PsiBuilder builder, ParserState parserState) {
        parserState.add(markCompleteScope(builder, switch_, m_types.SWITCH, groupExpression, m_types.SWITCH));
        parserState.dontMove = advance(builder);
        parserState.add(markCompleteScope(builder, switchBinaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseTry(PsiBuilder builder, ParserState parserState) {
        parserState.add(markCompleteScope(builder, try_, m_types.TRY, groupExpression, m_types.TRY));
        parserState.dontMove = advance(builder);
        parserState.add(markCompleteScope(builder, tryBinaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseArrow(PsiBuilder builder, ParserState parserState) {
        parserState.dontMove = advance(builder);
        IElementType nextTokenType = builder.getTokenType();

        if (nextTokenType != m_types.LBRACE && parserState.isResolution(letNamedEqParameters)) {
            // let x = ($ANY) => <EXPR>
            parserState.add(markCompleteScope(builder, letFunBody, m_types.LET_BINDING, scopeExpression, null));
        }
    }

}
