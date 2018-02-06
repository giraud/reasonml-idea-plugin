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
                parsePipe(parserState);
            } else if (tokenType == m_types.EQ) {
                parseEq(builder, parserState);
            } else if (tokenType == m_types.COLON) {
                parseColon(builder, parserState);
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
            } else if (tokenType == m_types.ARROBASE) {
                parseArrobase(builder, parserState);
            } else if (tokenType == m_types.STRING) {
                parseString(parserState);
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

    private void parseString(ParserState state) {
        if (state.isResolution(annotationName)) {
            state.end();
        }
    }

    private void parsePipe(ParserState state) {
        state.endUntilScopeExpression(m_types.WITH);
    }

    private void parseMatch(PsiBuilder builder, ParserState parserState) {
        parserState.add(markCompleteScope(builder, match, m_types.MATCH, groupExpression, m_types.MATCH));
        parserState.dontMove = advance(builder);
        parserState.add(markCompleteScope(builder, matchBinaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseTry(PsiBuilder builder, ParserState parserState) {
        parserState.add(markCompleteScope(builder, try_, m_types.TRY, groupExpression, m_types.TRY));
        parserState.dontMove = advance(builder);
        parserState.add(markCompleteScope(builder, tryBinaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseWith(PsiBuilder builder, ParserState parserState) {
        parserState.endUntilScopeExpression(parserState.isResolution(matchBinaryCondition) ? m_types.MATCH : m_types.TRY);
        parserState.add(markCompleteScope(builder, matchWith, m_types.SCOPED_EXPR, groupExpression, m_types.WITH));
    }

    private void parseIf(PsiBuilder builder, ParserState parserState) {
        parserState.add(markCompleteScope(builder, if_, m_types.IF, groupExpression, m_types.IF));
        parserState.dontMove = advance(builder);
        parserState.add(markCompleteScope(builder, binaryCondition, m_types.BIN_CONDITION, groupExpression, null));
    }

    private void parseThen(PsiBuilder builder, ParserState parserState) {
        parserState.endUntilScopeExpression(m_types.IF);
        parserState.add(markCompleteScope(builder, ifThenStatement, m_types.SCOPED_EXPR, groupExpression, m_types.THEN));
    }

    private void parseStruct(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(moduleNamedEq) || parserState.isResolution(moduleNamedSignature)) {
            parserState.end();
        }
        parserState.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.STRUCT));
    }

    private void parseSig(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(moduleNamedEq) || parserState.isResolution(moduleNamedColon)) {
            parserState.end();
            parserState.setResolution(moduleNamedSignature);
            parserState.add(markScope(builder, moduleSignature, m_types.SIG_SCOPE, scopeExpression, m_types.SIG));
        }
    }

    private void parseSemi(ParserState parserState) {
        // A SEMI operator ends the start expression, not the group or scope
        ParserScope scope = parserState.end();
        if (scope != null && scope.scopeType == startExpression) {
            parserState.pop();
            scope.end();
        }

        parserState.updateCurrentScope();
    }

    private void parseIn(ParserState parserState) {
        // End current start-expression scope
        ParserScope scope = parserState.endUntilStart();
        if (scope != null && scope.scopeType == startExpression) {
            parserState.pop();
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
            parserState.popEnd();
        }

        parserState.updateCurrentScope();
    }

    private void parseColon(PsiBuilder builder, ParserState state) { // :
        if (state.isResolution(moduleNamed)) {
            state.setResolution(moduleNamedColon);
            state.setComplete();
        } else if (state.isResolution(externalNamed)) {
            state.dontMove = advance(builder);
            state.add(markScope(builder, externalNamedSignature, m_types.SIG_SCOPE, groupExpression, m_types.SIG));
        } else if (state.isResolution(valNamed)) {
            state.dontMove = advance(builder);
            state.add(markCompleteScope(builder, valNamedSignature, m_types.SIG_SCOPE, groupExpression, m_types.SIG));
        }
    }

    private void parseEq(PsiBuilder builder, ParserState parserState) { // =
        if (parserState.isResolution(typeNamed)) {
            parserState.setResolution(typeNamedEq);
        } else if (parserState.isResolution(letNamed)) {
            parserState.setResolution(letNamedEq);
            builder.advanceLexer();
            parserState.dontMove = true;
            parserState.add(markScope(builder, letNamedEq, m_types.LET_BINDING, groupExpression, m_types.EQ));
            parserState.setComplete();
        } else if (parserState.isResolution(tagProperty)) {
            parserState.setResolution(tagPropertyEq);
        } else if (parserState.isResolution(moduleNamed)) {
            parserState.setResolution(moduleNamedEq);
            parserState.setComplete();
        } else if (parserState.isResolution(externalNamedSignature)) {
            parserState.setComplete();
            parserState.endUntilStart();
            parserState.updateCurrentScope();
        }
    }

    private void parseArrobase(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(annotation)) {
            parserState.complete();
            parserState.add(markComplete(builder, annotationName, m_types.MACRO_NAME));
        }
    }

    private void parseLParen(PsiBuilder builder, ParserState state) {
        if (state.isResolution(external)) {
            // overloading an operator
            state.setResolution(externalNamed);
            state.setComplete();
        }

        state.end();
        state.add(markScope(builder, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN));
    }

    private void parseRParen(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LPAREN);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            parserState.popEnd();
        }

        parserState.updateCurrentScope();
    }

    private void parseLBrace(PsiBuilder builder, ParserState parserState) {
        parserState.end();
        parserState.add(markScope(builder, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE));
    }

    private void parseRBrace(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACE);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete = true;
            parserState.popEnd();
        }

        parserState.updateCurrentScope();
    }

    private void parseLBracket(PsiBuilder builder, ParserState parserState) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            // This is an annotation
            parserState.add(markScope(builder, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET));
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
            parserState.popEnd();
        }

        parserState.updateCurrentScope();
    }

    private void parseLIdent(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(type)) {
            builder.remapCurrentToken(m_types.TYPE_CONSTR_NAME);
            parserState.setResolution(typeNamed);
            parserState.setComplete();
        } else if (parserState.isResolution(external)) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.setResolution(externalNamed);
            parserState.setComplete();
        } else if (parserState.isResolution(let)) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = wrapWith(m_types.VAR_NAME, builder);
            parserState.setResolution(letNamed);
            parserState.setComplete();
        } else if (parserState.isResolution(val)) {
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.setResolution(valNamed);
            parserState.setComplete();
        } else {
            if (parserState.notResolution(annotationName)) {
                builder.remapCurrentToken(m_types.VALUE_NAME);
                parserState.dontMove = wrapWith(m_types.VAR_NAME, builder);
            }
        }
    }

    private void parseUIdent(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(open)) {
            // It is a module name/path
            parserState.setComplete();
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = wrapWith(m_types.MODULE_NAME, builder);
        } else if (parserState.isResolution(include)) {
            // It is a module name/path
            parserState.setComplete();
            builder.remapCurrentToken(m_types.MODULE_NAME);
        } else if (parserState.isResolution(exception)) {
            parserState.setComplete();
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = wrapWith(m_types.EXCEPTION_NAME, builder);
            parserState.setResolution(exceptionNamed);
        } else if (parserState.isResolution(module)) {
            // Module definition
            builder.remapCurrentToken(m_types.VALUE_NAME);
            parserState.dontMove = wrapWith(m_types.MODULE_NAME, builder);
            parserState.setResolution(moduleNamed);
        }
    }

    private void parseOpen(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.add(markScope(builder, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN));
    }

    private void parseInclude(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.add(markScope(builder, include, m_types.INCLUDE_EXPRESSION, startExpression, m_types.INCLUDE));
    }

    private void parseExternal(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.add(markScope(builder, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL));
    }

    private void parseType(PsiBuilder builder, ParserState parserState) {
        if (parserState.notResolution(module)) {
            endLikeSemi(parserState);
            parserState.add(markScope(builder, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE));
        }
    }

    private void parseException(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.add(markScope(builder, exception, m_types.EXCEPTION_EXPRESSION, startExpression, m_types.EXCEPTION));
    }

    private void parseVal(PsiBuilder builder, ParserState parserState) {
        endLikeSemi(parserState);
        parserState.add(markScope(builder, val, m_types.VAL_EXPRESSION, startExpression, m_types.VAL));
    }

    private void parseLet(PsiBuilder builder, ParserState parserState) {
        if (parserState.previousTokenType != m_types.EQ && parserState.previousTokenType != m_types.IN) {
            endLikeSemi(parserState);
        }
        parserState.add(markScope(builder, let, m_types.LET_EXPRESSION, startExpression, m_types.LET));
    }

    private void parseModule(PsiBuilder builder, ParserState parserState) {
        if (parserState.notResolution(annotationName)) {
            endLikeSemi(parserState);
            parserState.add(markScope(builder, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE));
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
                parserState.pop();
                scope.end();
            }
        }

        parserState.updateCurrentScope();
    }
}
