package com.reason.lang.ocaml;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserScopeEnum;
import com.reason.lang.ParserState;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.groupExpression;
import static com.reason.lang.ParserScopeType.scopeExpression;

public class OclParser extends CommonParser {

    OclParser() {
        super(OclTypes.INSTANCE);
    }

    @Override
    protected void parseFile(PsiBuilder builder, ParserState state) {
        IElementType tokenType = null;

        int c = current_position_(builder);
        while (true) {
            state.previousTokenType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == m_types.SEMI) {
                parseSemi(state);
            } else if (tokenType == m_types.IN) {
                parseIn(state);
            } else if (tokenType == m_types.END) { // end (like a })
                parseEnd(builder, state);
            } else if (tokenType == m_types.PIPE) {
                parsePipe(builder, state);
            } else if (tokenType == m_types.EQ) {
                parseEq(builder, state);
            } else if (tokenType == m_types.COLON) {
                parseColon(builder, state);
            } else if (tokenType == m_types.LIDENT) {
                parseLIdent(builder, state);
            } else if (tokenType == m_types.UIDENT) {
                parseUIdent(builder, state);
            } else if (tokenType == m_types.SIG) {
                parseSig(builder, state);
            } else if (tokenType == m_types.STRUCT) {
                parseStruct(builder, state);
            } else if (tokenType == m_types.IF) {
                parseIf(builder, state);
            } else if (tokenType == m_types.THEN) {
                parseThen(builder, state);
            } else if (tokenType == m_types.MATCH) {
                parseMatch(builder, state);
            } else if (tokenType == m_types.TRY) {
                parseTry(builder, state);
            } else if (tokenType == m_types.WITH) {
                parseWith(builder, state);
            } else if (tokenType == m_types.ARROBASE) {
                parseArrobase(builder, state);
            } else if (tokenType == m_types.STRING) {
                parseString(state);
            } else if (tokenType == m_types.AND) {
                parseAnd(builder, state);
            } else if (tokenType == m_types.FUNCTION) {
                parseFun(builder, state);
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
                parseRBrace(builder, state);
            }
            // [ ... ]
            else if (tokenType == m_types.LBRACKET) {
                parseLBracket(builder, state);
            } else if (tokenType == m_types.RBRACKET) {
                parseRBracket(builder, state);
            }
            // Starts expression
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
            } else if (tokenType == m_types.LET) {
                parseLet(builder, state);
            } else if (tokenType == m_types.VAL) {
                parseVal(builder, state);
            } else if (tokenType == m_types.EXCEPTION) {
                parseException(builder, state);
            }

            if (state.dontMove) {
                state.dontMove = false;
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "oclFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }

    private void parseAnd(PsiBuilder builder, ParserState state) {
        if (isTypeResolution(state)) {
            state.endUntilScopeExpression(null);
            state.dontMove = advance(builder);
            state.addStart(mark(builder, type, m_types.TYPE_EXPRESSION));
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        } else if (state.isResolution(letNamedEq)) {
            state.endUntilScopeExpression(null);
            state.dontMove = advance(builder);
            state.addStart(mark(builder, let, m_types.LET_EXPRESSION));
        }
    }

    private void parseString(ParserState state) {
        if (state.isResolution(annotationName)) {
            state.endAny();
        }
    }

    private void parsePipe(PsiBuilder builder, ParserState state) {
        if (state.isResolution(typeNamedEq)) {
            state.add(markCompleteScope(builder, typeNamedEqVariant, m_types.VARIANT, groupExpression, m_types.PIPE));
        } else if (state.isResolution(typeNamedEqVariant)) {
            state.popEnd();
            state.add(markCompleteScope(builder, typeNamedEqVariant, m_types.VARIANT, groupExpression, m_types.PIPE));
        } else {
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
            parserState.endAny();
        }
        parserState.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.STRUCT));
    }

    private void parseSig(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(moduleNamedEq) || parserState.isResolution(moduleNamedColon)) {
            parserState.endAny();
            parserState.setResolution(moduleNamedSignature);
            parserState.add(markScope(builder, moduleSignature, m_types.SIG_SCOPE, scopeExpression, m_types.SIG));
        }
    }

    private void parseSemi(ParserState state) {
        // A SEMI operator ends the start expression, not the group or scope
        ParserScope scope = state.endAny();
        if (scope != null && state.isStart(scope)) {
            state.popEnd();
        }
    }

    private void parseIn(ParserState state) {
        // End current start-expression scope
        ParserScope scope = state.endUntilStart();
        if (scope != null && state.isStart(scope)) {
            state.popEnd();
        }
    }

    private void parseEnd(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(null);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete();
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

    private void parseFun(PsiBuilder builder, ParserState state) {
        if (state.isResolution(letNamedEq)) {
            state.add(markScope(builder, funBody, m_types.LET_BINDING, groupExpression, m_types.FUN));
        }
    }

    private void parseEq(PsiBuilder builder, ParserState state) { // =
        if (state.isResolution(typeNamed)) {
            state.popEnd();
            state.setResolution(typeNamedEq);
            state.dontMove = advance(builder);
            state.add(markCompleteScope(builder, typeNamedEq, m_types.TYPE_BINDING, groupExpression, null));
        } else if (state.isResolution(letNamed)) {
            ParserScopeEnum resolution = letNamedEq;
            state.setResolution(resolution);
            state.dontMove = advance(builder);
            state.add(markScope(builder, resolution, m_types.LET_BINDING, groupExpression, m_types.EQ));
            state.setComplete();
        } else if (state.isResolution(tagProperty)) {
            state.setResolution(tagPropertyEq);
        } else if (state.isResolution(moduleNamed)) {
            state.setResolution(moduleNamedEq);
            state.setComplete();
        } else if (state.isResolution(externalNamedSignature)) {
            state.setComplete();
            state.endUntilStart();
            state.updateCurrentScope();
        } else if (state.isResolution(genericExpression)) {
            ParserScope innerScope = state.pop();
            if (innerScope != null && state.isResolution(genericExpression)) {
                // let's say this is a function definition
                innerScope.resolution = parameters;
                innerScope.tokenType = m_types.FUN_PARAMS;
                innerScope.complete();
                innerScope.end();
                state.setResolution(function);
                state.setTokenType(m_types.FUNCTION);
                state.setComplete();
                state.dontMove = advance(builder);
                state.add(markCompleteScope(builder, funBody, m_types.FUN_BODY, groupExpression, null));
            }
        }
    }

    private void parseArrobase(PsiBuilder builder, ParserState parserState) {
        if (parserState.isResolution(annotation)) {
            parserState.setComplete();
            parserState.add(markComplete(builder, annotationName, m_types.MACRO_NAME));
        }
    }

    private void parseLParen(PsiBuilder builder, ParserState state) {
        if (state.isResolution(modulePath) && state.previousTokenType == m_types.DOT) {
            state.setResolution(localOpen);
            state.setTokenType(m_types.LOCAL_OPEN);
            state.setComplete();
            state.add(markScope(builder, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN));
        } else if (state.isResolution(external)) {
            // overloading an operator
            state.setResolution(externalNamed);
            state.setComplete();
        } else if (state.isResolution(val)) {
            // overloading an operator
            state.setResolution(valNamed);
            state.setComplete();
        }

        state.endAny();
        state.add(markScope(builder, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN));
    }

    private void parseRParen(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LPAREN);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete();
            parserState.popEnd();
        }

        parserState.updateCurrentScope();
    }

    private void parseLBrace(PsiBuilder builder, ParserState parserState) {
        parserState.endAny();
        parserState.add(markScope(builder, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE));
    }

    private void parseRBrace(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACE);

        builder.advanceLexer();
        parserState.dontMove = true;

        if (scope != null) {
            scope.complete();
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
                scope.complete();
            }
            parserState.popEnd();
        }

        parserState.updateCurrentScope();
    }

    private void parseLIdent(PsiBuilder builder, ParserState state) {
        if (state.isResolution(typeConstrName)) {
            state.setResolution(typeNamed);
            state.setComplete();
            state.setPreviousComplete();
        } else if (state.isResolution(external)) {
            state.setResolution(externalNamed);
            state.setComplete();
        } else if (state.isResolution(let)) {
            state.setResolution(letNamed);
            state.setComplete();
        } else if (state.isResolution(val)) {
            state.setResolution(valNamed);
            state.setComplete();
        }

        state.dontMove = wrapWith(m_types.LOWER_SYMBOL, builder);

        if (state.isResolution(letNamed)) {
            IElementType nextTokenType = builder.getTokenType();
            if (nextTokenType != m_types.EQ) {
                // add a generic marker: it may be a function + parameters
                state.add(mark(builder, genericExpression, null));
                state.add(mark(builder, genericExpression, null));
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
        } else if (state.isResolution(include)) {
            // It is a module name/path
            state.setComplete();
        } else if (state.isResolution(exception)) {
            state.setComplete();
            state.setResolution(exceptionNamed);
            builder.remapCurrentToken(m_types.EXCEPTION_NAME);
        } else if (state.isResolution(module)) {
            // Module definition
            state.setResolution(moduleNamed);
        } else if ((state.isResolution(typeNamedEqVariant) && state.previousTokenType == m_types.PIPE) || state.isResolution(typeNamedEq)) {
            builder.remapCurrentToken(m_types.VARIANT_NAME);
        } else {
            if (!state.isResolution(modulePath)) {
                IElementType nextElementType = builder.lookAhead(1);
                if (nextElementType == m_types.DOT) {
                    // We are parsing a module path
                    state.add(mark(builder, modulePath, m_types.UPPER_SYMBOL));
                }
            }
        }

        state.dontMove = wrapWith(m_types.UPPER_SYMBOL, builder);
    }

    private void parseOpen(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.addStart(mark(builder, open, m_types.OPEN_EXPRESSION));
    }

    private void parseInclude(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.addStart(mark(builder, include, m_types.INCLUDE_EXPRESSION));
    }

    private void parseExternal(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.addStart(mark(builder, external, m_types.EXTERNAL_EXPRESSION));
    }

    private void parseType(PsiBuilder builder, ParserState state) {
        if (state.notResolution(module)) {
            endLikeSemi(state);
            state.addStart(mark(builder, type, m_types.TYPE_EXPRESSION));
            state.dontMove = advance(builder);
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        }
    }

    private void parseException(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.addStart(mark(builder, exception, m_types.EXCEPTION_EXPRESSION));
    }

    private void parseVal(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.addStart(mark(builder, val, m_types.VAL_EXPRESSION));
    }

    private void parseLet(PsiBuilder builder, ParserState state) {
        if (state.previousTokenType != m_types.EQ && state.previousTokenType != m_types.IN) {
            endLikeSemi(state);
        }
        state.addStart(mark(builder, let, m_types.LET_EXPRESSION));
    }

    private void parseModule(PsiBuilder builder, ParserState state) {
        if (state.notResolution(annotationName)) {
            endLikeSemi(state);
            state.addStart(mark(builder, module, m_types.MODULE_EXPRESSION));
        }
    }

    private void endLikeSemi(ParserState state) {
        ParserScope scope = state.endUntilScopeExpression(null);
        if (scope != null && state.isStart(scope)) {
            state.popEnd();
        }

        state.updateCurrentScope();
    }
}
