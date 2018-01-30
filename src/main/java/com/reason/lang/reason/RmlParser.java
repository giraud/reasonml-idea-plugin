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
                parseString(builder, parserState);
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
            } else if (tokenType == m_types.GT || tokenType == m_types.TAG_AUTO_CLOSE) {
                parseGtAutoClose(builder, parserState);
            }
            // {| ... |}
            else if (tokenType == m_types.ML_STRING_OPEN) {
                parseMlStringOpen(builder, parserState);
            } else if (tokenType == m_types.ML_STRING_CLOSE) {
                parseMlStringClose(builder, parserState);
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

    private void parseString(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
            state.end();
        }
    }

    private void parseMlStringOpen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
            state.end();
        }

        state.currentScope = markScope(builder, state.scopes, mlOpen, m_types.SCOPED_EXPR, scopeExpression, m_types.ML_STRING_OPEN);
    }

    private void parseMlStringClose(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.ML_STRING_CLOSE);
        parserState.dontMove = advance(builder);

        if (scope != null) {
            scope.complete = true;
            parserState.scopes.pop().end();
        }

        parserState.updateCurrentScope();
    }

    private void parseLet(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        parserState.currentScope = markScope(builder, parserState.scopes, let, m_types.LET_EXPRESSION, startExpression, m_types.LET);
    }

    private void parseModule(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution != annotationName) {
            parserState.endUntilScopeExpression(null);
            parserState.currentScope = markScope(builder, parserState.scopes, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE);
        }
    }

    private void parseType(PsiBuilder builder, ParserState parserState) {
        if (parserState.notCurrentResolution(module)) {
            parserState.endUntilScopeExpression(null);
            parserState.currentScope = markScope(builder, parserState.scopes, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE);
        }
    }

    private void parseExternal(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        parserState.currentScope = markScope(builder, parserState.scopes, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL);
    }

    private void parseOpen(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        parserState.currentScope = markScope(builder, parserState.scopes, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN);
    }

    private void parsePercent(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution == macro) {
            parserState.currentScope.complete = true;
            parserState.currentScope = markComplete(builder, parserState.scopes, macroName, m_types.MACRO_NAME);
            parserState.currentScope.complete = true;
        }
    }

    private void parseColon(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(externalNamed)) {
            parserState.dontMove = advance(builder);
            parserState.currentScope = markScope(builder, parserState.scopes, externalNamedSignature, m_types.SIG_SCOPE, groupExpression, m_types.SIG);
        }
    }

    private void parseArrobase(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution == annotation) {
            parserState.currentScope.complete = true;
            parserState.currentScope = markComplete(builder, parserState.scopes, annotationName, m_types.MACRO_NAME);
        }
    }

    private void parseGtAutoClose(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.tokenType == m_types.TAG_PROPERTY) {
            parserState.currentScope.end();
            parserState.scopes.pop();
            parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
        }

        if (parserState.currentScope.resolution == startTag || parserState.currentScope.resolution == closeTag) {
            builder.remapCurrentToken(m_types.TAG_GT);
            builder.advanceLexer();
            parserState.dontMove = true;

            parserState.currentScope.end();
            parserState.scopes.pop();

            parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
        }
    }

    private void parseLt(PsiBuilder builder, ParserState parserState) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // Surely a tag
            builder.remapCurrentToken(m_types.TAG_LT);
            parserState.currentScope = markScope(builder, parserState.scopes, startTag, m_types.TAG_START, groupExpression, m_types.TAG_LT);
            parserState.currentScope.complete = true;

            builder.advanceLexer();
            parserState.dontMove = true;
            builder.remapCurrentToken(m_types.TAG_NAME);
        } else if (nextTokenType == m_types.SLASH) {
            builder.remapCurrentToken(m_types.TAG_LT);
            parserState.currentScope = markScope(builder, parserState.scopes, closeTag, m_types.TAG_CLOSE, any, m_types.TAG_LT);
            parserState.currentScope.complete = true;
        }
    }

    private void parseLIdent(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution == type) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = wrapWith(m_types.TYPE_CONSTR_NAME, builder);
            parserState.currentScope.resolution = typeNamed;
            parserState.currentScope.complete = true;
        } else if (parserState.currentScope.resolution == external) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.currentScope.resolution = externalNamed;
            parserState.currentScope.complete = true;
        } else if (parserState.currentScope.resolution == let) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = wrapWith(m_types.VAR_NAME, builder);
            parserState.currentScope.resolution = letNamed;
            parserState.currentScope.complete = true;
        } else if (parserState.currentScope.resolution == startTag) {
            // This is a property
            parserState.end();
            builder.remapCurrentToken(m_types.PROPERTY_NAME);
            parserState.currentScope = markScope(builder, parserState.scopes, tagProperty, m_types.TAG_PROPERTY, groupExpression, m_types.LIDENT);
            parserState.currentScope.complete = true;
        } else {
            if (parserState.notCurrentResolution(annotationName)) {
                builder.remapCurrentToken(m_types.VALUE_NAME);
                parserState.dontMove = wrapWith(m_types.VAR_NAME, builder);
            }
        }
    }

    private void parseLBracket(PsiBuilder builder, ParserState parserState) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            parserState.currentScope = markScope(builder, parserState.scopes, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET);
        } else if (nextTokenType == m_types.PERCENT) {
            parserState.currentScope = markScope(builder, parserState.scopes, macro, m_types.MACRO_EXPRESSION, scopeExpression, m_types.LBRACKET);
        } else {
            parserState.currentScope = markScope(builder, parserState.scopes, bracket, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACKET);
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
            parserState.scopes.pop().end();
        }

        parserState.updateCurrentScope();
    }

    private void parseLBrace(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution == typeNamedEq) {
            parserState.currentScope = markScope(builder, parserState.scopes, objectBinding, m_types.OBJECT_EXPR, scopeExpression, m_types.LBRACE);
        } else if (parserState.currentScope.resolution == moduleNamedEq) {
            parserState.currentScope = markScope(builder, parserState.scopes, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
        } else if (parserState.currentScope.resolution == letNamedEqParameters) {
            parserState.currentScope = markScope(builder, parserState.scopes, letFunBody, m_types.LET_BINDING, scopeExpression, m_types.LBRACE);
        } else {
            if (parserState.isCurrentResolution(switchBinaryCondition)) {
                parserState.endUntilScopeExpression(m_types.SWITCH);
            } else {
                parserState.end();
            }
            parserState.currentScope = markScope(builder, parserState.scopes, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
        }
    }

    private void parseRBrace(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACE);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            parserState.scopes.pop().end();
        }

        parserState.updateCurrentScope();
    }

    private void parseRParen(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LPAREN);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            parserState.scopes.pop().end();
            scope = parserState.getLatestScope();
            if (scope != null && scope.resolution == letNamedEq) {
                scope.resolution = letNamedEqParameters;
            }
        }

        parserState.updateCurrentScope();
    }

    private void parseLParen(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        if (parserState.currentScope.resolution == letNamedEq) {
            // function parameters
            parserState.currentScope = markScope(builder, parserState.scopes, letParameters, m_types.LET_FUN_PARAMS, scopeExpression, m_types.LPAREN);
        } else {
            parserState.currentScope = markScope(builder, parserState.scopes, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN);
        }
    }

    private void parseEq(ParserState parserState) {
        if (parserState.isCurrentResolution(typeNamed)) {
            parserState.currentScope.resolution = typeNamedEq;
        } else if (parserState.isCurrentResolution(letNamed)) {
            parserState.currentScope.resolution = letNamedEq;
        } else if (parserState.isCurrentResolution(tagProperty)) {
            parserState.currentScope.resolution = tagPropertyEq;
        } else if (parserState.isCurrentResolution(moduleNamed)) {
            parserState.currentScope.resolution = moduleNamedEq;
            parserState.currentScope.complete = true;
        } else if (parserState.isCurrentResolution(externalNamedSignature)) {
            parserState.currentScope.complete = true;
            parserState.endUntilStart();
            parserState.updateCurrentScope();
        }
    }

    private void parseSemi(PsiBuilder builder, ParserState parserState) {
        // End current start-expression scope
        ParserScope scope = parserState.endUntilStart();
        if (scope != null && scope.scopeType == startExpression) {
            builder.advanceLexer();
            parserState.dontMove = true;
            parserState.scopes.pop();
            scope.end();
        }

        parserState.updateCurrentScope();
    }

    private void parseUIdent(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution == open) {
            // It is a module name/path
            parserState.currentScope.complete = true;
            builder.remapCurrentToken(m_types.VALUE_NAME);
            //parserState.currentScope = markComplete(builder, parserState.scopes, openModulePath, m_types.MODULE_PATH);
            parserState.dontMove = wrapWith(m_types.MODULE_NAME, builder);
        } else if (parserState.currentScope.resolution == module) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            ParserScope scope = markComplete(builder, parserState.scopes, moduleNamed, m_types.MODULE_NAME);
            parserState.dontMove = advance(builder);
            scope.end();
            parserState.currentScope.resolution = moduleNamed;
        } else {
            if (parserState.previousTokenType == m_types.PIPE) {
                builder.remapCurrentToken(m_types.TYPE_CONSTR_NAME);
            } else {
                builder.remapCurrentToken(m_types.VALUE_NAME);
                ParserScope scope = markComplete(builder, parserState.scopes, moduleNamed, m_types.MODULE_NAME);
                parserState.dontMove = advance(builder);
                scope.end();
            }
        }
    }

    private void parseSwitch(PsiBuilder builder, ParserState parserState) {
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, switch_, m_types.SWITCH, groupExpression, m_types.SWITCH);
        parserState.dontMove = advance(builder);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, switchBinaryCondition, m_types.BIN_CONDITION, groupExpression, null);
    }

    private void parseTry(PsiBuilder builder, ParserState parserState) {
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, try_, m_types.TRY, groupExpression, m_types.TRY);
        parserState.dontMove = advance(builder);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, tryBinaryCondition, m_types.BIN_CONDITION, groupExpression, null);
    }

    private void parseArrow(PsiBuilder builder, ParserState parserState) {
        parserState.dontMove = advance(builder);
        IElementType nextTokenType = builder.getTokenType();

        if (nextTokenType != m_types.LBRACE && parserState.isCurrentResolution(letNamedEqParameters)) {
            // let x = ($ANY) => <EXPR>
            parserState.currentScope = markCompleteScope(builder, parserState.scopes, letFunBody, m_types.LET_BINDING, scopeExpression, null);
        }
    }

}
