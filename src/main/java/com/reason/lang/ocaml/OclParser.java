package com.reason.lang.ocaml;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.*;

public class OclParser extends CommonParser {

    OclParser() {
        super(OclTypes.INSTANCE);
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
                parseSemi(parserState);
            } else if (tokenType == m_types.IN) {
                parseIn(parserState);
            } else if (tokenType == m_types.END) { // end (like a })
                parseEnd(builder, parserState);
            } else if (tokenType == m_types.PIPE) {
                parsePipe(builder, parserState);
            } else if (tokenType == m_types.EQ) {
                parseEq(builder, parserState);
            } else if (tokenType == m_types.COLON) {
                parseColon(parserState);
            } else if (tokenType == m_types.LIDENT) {
                parseLIdent(builder, parserState);
            } else if (tokenType == m_types.UIDENT) {
                parseUIdent(builder, parserState);
            } else if (tokenType == m_types.SIG) {
                parseSig(builder, parserState);
            } else if (tokenType == m_types.STRUCT) {
                parseStruct(builder, parserState);
            } else if (tokenType == m_types.IF) {
                parseIf(builder, parserState);
            } else if (tokenType == m_types.THEN) {
                parseThen(builder, parserState);
            } else if (tokenType == m_types.MATCH) {
                parseMatch(builder, parserState);
            } else if (tokenType == m_types.TRY) {
                parseTry(builder, parserState);
            } else if (tokenType == m_types.WITH) {
                parseWith(builder, parserState);
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
            // Starts expression
            else if (tokenType == m_types.OPEN) {
                parseOpen(builder, parserState);
            } else if (tokenType == m_types.INCLUDE) {
                parseInclude(builder, parserState);
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
            } else if (tokenType == m_types.EXCEPTION) {
                parseException(builder, parserState);
            }

            if (parserState.dontMove) {
                parserState.dontMove = false;
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "oclFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }

    private void parsePipe(PsiBuilder builder, ParserState parserState) {
        parserState.endUntilScopeExpression(m_types.WITH);
    }

    private void parseMatch(PsiBuilder builder, ParserState parserState) {
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, match, m_types.MATCH, groupExpression, m_types.MATCH);
        parserState.dontMove = advance(builder);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, matchBinaryCondition, m_types.BIN_CONDITION, groupExpression, null);
    }

    private void parseTry(PsiBuilder builder, ParserState parserState) {
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, _try, m_types.TRY, groupExpression, m_types.TRY);
        parserState.dontMove = advance(builder);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, tryBinaryCondition, m_types.BIN_CONDITION, groupExpression, null);
    }

    private void parseWith(PsiBuilder builder, ParserState parserState) {
        parserState.endUntilScopeExpression(parserState.currentScope.resolution == matchBinaryCondition ? m_types.MATCH : m_types.TRY);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, matchWith, m_types.SCOPED_EXPR, groupExpression, m_types.WITH);
    }

    private void parseIf(PsiBuilder builder, ParserState parserState) {
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, _if, m_types.IF, groupExpression, m_types.IF);
        parserState.dontMove = advance(builder);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, binaryCondition, m_types.BIN_CONDITION, groupExpression, null);
    }

    private void parseThen(PsiBuilder builder, ParserState parserState) {
        parserState.endUntilScopeExpression(m_types.IF);
        parserState.currentScope = markCompleteScope(builder, parserState.scopes, ifThenStatement, m_types.SCOPED_EXPR, groupExpression, m_types.THEN);
    }

    private void parseStruct(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(moduleNamedEq) || parserState.isCurrentResolution(moduleNamedSignature)) {
            parserState.end();
        }
        parserState.currentScope = markScope(builder, parserState.scopes, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.STRUCT);
    }

    private void parseSig(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(moduleNamedEq) || parserState.isCurrentResolution(moduleNamedColon)) {
            parserState.end();
            parserState.currentScope.resolution = moduleNamedSignature;
            parserState.currentScope = markScope(builder, parserState.scopes, moduleSignature, m_types.SIG_SCOPE, scopeExpression, m_types.SIG);
        }
    }

    private void parseSemi(ParserState parserState) {
        // A SEMI operator ends the start expression, not the group or scope
        ParserScope scope = parserState.end();
        if (scope != null && scope.scopeType == startExpression) {
            parserState.scopes.pop();
            scope.end();
        }

        parserState.updateCurrentScope();
    }

    private void parseIn(ParserState parserState) {
        // End current start-expression scope
        ParserScope scope = parserState.endUntilStart();
        if (scope != null && scope.scopeType == startExpression) {
            parserState.scopes.pop();
            scope.end();
        }

        parserState.updateCurrentScope();
    }

    private void parseEnd(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(null);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            parserState.scopes.pop().end();
        }

        parserState.updateCurrentScope();
    }

    private void parseColon(ParserState parserState) {
        if (parserState.currentScope.resolution == moduleNamed) {
            parserState.currentScope.resolution = moduleNamedColon;
            parserState.currentScope.complete = true;
        }
    }

    private void parseEq(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(typeNamed)) {
            parserState.currentScope.resolution = typeNamedEq;
        } else if (parserState.isCurrentResolution(letNamed)) {
            parserState.currentScope.resolution = letNamedEq;
            builder.advanceLexer();
            parserState.dontMove = true;
            parserState.currentScope = markScope(builder, parserState.scopes, letNamedEq, m_types.LET_BINDING, groupExpression, m_types.EQ);
            parserState.currentScope.complete = true;
        } else if (parserState.isCurrentResolution(tagProperty)) {
            parserState.currentScope.resolution = tagPropertyEq;
        } else if (parserState.isCurrentResolution(moduleNamed)) {
            parserState.currentScope.resolution = moduleNamedEq;
            parserState.currentScope.complete = true;
        }
    }

    private void parseRParen(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LPAREN);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            parserState.scopes.pop().end();
            parserState.getLatestScope();
        }

        parserState.updateCurrentScope();
    }

    private void parseLParen(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        parserState.currentScope = markScope(builder, parserState.scopes, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN);
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

    private void parseLBrace(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        parserState.currentScope = markScope(builder, parserState.scopes, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
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

        parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
    }

    private void parseLBracket(PsiBuilder builder, ParserState parserState) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            // This is an annotation
            parserState.currentScope = markScope(builder, parserState.scopes, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET);
        } else {
            parserState.currentScope = markScope(builder, parserState.scopes, bracket, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACKET);
        }
    }

    private void parseLIdent(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(type)) {
            builder.remapCurrentToken(m_types.TYPE_CONSTR_NAME);
            parserState.currentScope.resolution = typeNamed;
            parserState.currentScope.complete = true;
        } else if (parserState.isCurrentResolution(external)) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.currentScope.resolution = externalNamed;
            parserState.currentScope.complete = true;
        } else if (parserState.isCurrentResolution(let)) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.currentScope.resolution = letNamed;
            parserState.currentScope.complete = true;
        } else if (parserState.isCurrentResolution(val)) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.currentScope.resolution = valNamed;
            parserState.currentScope.complete = true;
        }
    }

    private void parseUIdent(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(open)) {
            // It is a module name/path
            parserState.currentScope.complete = true;
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.currentScope = markComplete(builder, parserState.scopes, openModulePath, m_types.MODULE_PATH);
            parserState.dontMove = advance(builder, m_types.MODULE_NAME);
        } else if (parserState.isCurrentResolution(include)) {
            // It is a module name/path
            parserState.currentScope.complete = true;
            builder.remapCurrentToken(m_types.MODULE_NAME);
            parserState.currentScope = markComplete(builder, parserState.scopes, openModulePath, m_types.MODULE_PATH);
        } else if (parserState.isCurrentResolution(exception)) {
            parserState.currentScope.complete = true;
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = advance(builder, m_types.EXCEPTION_NAME);
            parserState.currentScope.resolution = exceptionNamed;
        } else if (parserState.isCurrentResolution(module)) {
            // Module definition
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = advance(builder, m_types.MODULE_NAME);
            parserState.currentScope.resolution = moduleNamed;
        }
    }

    private void parseOpen(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.currentScope = markScope(builder, parserState.scopes, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN);
    }

    private void parseInclude(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.currentScope = markScope(builder, parserState.scopes, include, m_types.INCLUDE_EXPRESSION, startExpression, m_types.INCLUDE);
    }

    private void parseExternal(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.currentScope = markScope(builder, parserState.scopes, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL);
    }

    private void parseType(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution != module) {
            endLikeSemi(parserState);
            parserState.currentScope = markScope(builder, parserState.scopes, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE);
        }
    }

    private void parseException(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.currentScope = markScope(builder, parserState.scopes, exception, m_types.EXCEPTION_EXPRESSION, startExpression, m_types.EXCEPTION);
    }

    private void parseVal(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.currentScope = markScope(builder, parserState.scopes, val, m_types.VAL_EXPRESSION, startExpression, m_types.VAL);
    }

    private void parseLet(PsiBuilder builder, ParserState parserState) {
        if (parserState.previousTokenType != m_types.EQ && parserState.previousTokenType != m_types.IN) {
            endLikeSemi(parserState);
        }
        parserState.currentScope = markScope(builder, parserState.scopes, let, m_types.LET_EXPRESSION, startExpression, m_types.LET);
    }

    private void parseModule(PsiBuilder builder, ParserState parserState) {
        if (parserState.currentScope.resolution != annotationName) {
            endLikeSemi(parserState);
            parserState.currentScope = markScope(builder, parserState.scopes, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE);
        }
    }

    private void endLikeSemi(ParserState parserState) {
        ParserScope scope;
        if (parserState.previousTokenType != m_types.IN && parserState.previousTokenType != m_types.SIG && parserState.previousTokenType != m_types.STRUCT) {
            // force completion of scoped expressions
            scope = parserState.endUntilStartForced();
        } else {
            // End current start-expression scope
            scope = parserState.endUntilStart();
        }

        if (scope != null) {
            if (scope.scopeType == startExpression) {
                parserState.scopes.pop();
                scope.end();
            }
        }

        parserState.updateCurrentScope();
    }
}
