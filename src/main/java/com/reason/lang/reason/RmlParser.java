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

public class RmlParser extends CommonParser {

    RmlParser() {
        super(RmlTypes.INSTANCE);
    }

    @Override
    protected void parseFile(PsiBuilder builder, ParserState state) {
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
            } else if (tokenType == m_types.PIPE) {
                parsePipe(builder, state);
            } else if (tokenType == m_types.TILDE) {
                parseTilde(builder, state);
            } else if (tokenType == m_types.COMMA) {
                parseComma(state);
            } else if (tokenType == m_types.AND) {
                parseAnd(builder, state);
            } else if (tokenType == m_types.FUN) {
                parseFun(builder, state);
            } else if (tokenType == m_types.ASSERT) {
                parseAssert(builder, state);
            } else if (tokenType == m_types.IF) {
                parseIf(builder, state);
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
                parseRBrace(builder, state);
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
            } else if (tokenType == m_types.GT || tokenType == m_types.TAG_AUTO_CLOSE) {
                parseGtAutoClose(builder, state);
            }
            // {| ... |}
            else if (tokenType == m_types.ML_STRING_OPEN) {
                parseMlStringOpen(builder, state);
            } else if (tokenType == m_types.ML_STRING_CLOSE) {
                parseMlStringClose(builder, state);
            }
            // {j| ... |j}
            else if (tokenType == m_types.JS_STRING_OPEN) {
                parseJsStringOpen(builder, state);
            } else if (tokenType == m_types.JS_STRING_CLOSE) {
                parseJsStringClose(builder, state);
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

    private void parseUnderscore(ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
            state.complete();
        }
    }

    private void parseIf(PsiBuilder builder, ParserState state) {
        state.add(markComplete(builder, ifThenStatement, m_types.IF_STMT));
    }

    private void parseDotDotDot(PsiBuilder builder, ParserState state) {
        if (state.previousTokenElementType == m_types.LBRACE) {
            // Mixin:  ... LBRACE <DOTDOTDOT> LIDENT ...
            state.updateCurrentResolution(recordBinding);
            state.updateCurrentContext(record);
            state.updateCurrentCompositeElementType(m_types.RECORD_EXPR);
            state.add(mark(builder, recordBinding, mixin, m_types.MIXIN_FIELD));
        }
    }

    private void parseAssert(PsiBuilder builder, ParserState state) {
        state.add(markComplete(builder, assert_, m_types.ASSERT_STMT));
        state.dontMove = advance(builder);
    }

    private void parseFun(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(letNamedEq)) {
            state.add(mark(builder, function, m_types.FUN_EXPR));
            state.add(mark(builder, functionBody, m_types.FUN_BODY));
        }
    }

    private void parseAnd(PsiBuilder builder, ParserState state) {
        ParserScope latestScope = state.endUntilStartScope();

        if (isTypeResolution(latestScope)) {
            state.dontMove = advance(builder);
            state.add(mark(builder, type, m_types.EXP_TYPE));
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        } else if (isLetResolution(latestScope)) {
            state.dontMove = advance(builder);
            state.add(mark(builder, let, m_types.LET_STMT));
        } else if (isModuleResolution(latestScope)) {
            state.dontMove = advance(builder);
            state.add(mark(builder, module, m_types.MODULE_STMT));
        }
    }

    private void parseComma(ParserState state) {
        if (state.isCurrentResolution(namedSymbolSignature)) {
            state.complete();
            state.popEnd();
        } else if (state.isCurrentContext(recordSignature)) {
            state.complete();
            state.endUntilContext(recordField);
            state.popEnd();
        } else if (state.isCurrentResolution(mixin)) {
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
        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(mark(builder, typeNamedEqVariant, m_types.VARIANT_EXP));
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEnd();
            state.add(markComplete(builder, typeNamedEqVariant, m_types.VARIANT_EXP));
        } else {
            // By default, a pattern match
            if (state.isCurrentResolution(patternMatchBody)) {
                state.popEnd();
            }
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.add(markComplete(builder, patternMatch, m_types.PATTERN_MATCH_EXPR));
        }
    }

    private void parseString(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
            state.endAny();
        } else if (state.isCurrentResolution(brace)) {
            IElementType nextToken = builder.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.updateCurrentResolution(jsObject);
                state.setTokenElementType(m_types.RECORD);
                state.dontMove = wrapWith(m_types.RECORD_FIELD, builder);
            }
        } else if (state.isCurrentResolution(jsObject)) {
            IElementType nextToken = builder.lookAhead(1);
            if (m_types.COLON.equals(nextToken)) {
                state.dontMove = wrapWith(m_types.RECORD_FIELD, builder);
            }
        }
    }

    private void parseMlStringOpen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) {
            state.endAny();
        }

        state.add(markScope(builder, multilineStart, m_types.SCOPED_EXPR, m_types.ML_STRING_OPEN));
    }

    private void parseMlStringClose(PsiBuilder builder, ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.ML_STRING_OPEN);
        state.dontMove = advance(builder);

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }

        state.updateCurrentScope();
    }

    private void parseJsStringOpen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotationName) || state.isCurrentResolution(macroName)) { // use space notifier like in tag ?
            state.endAny();
        }

        state.add(markScope(builder, interpolationStart, m_types.SCOPED_EXPR, m_types.JS_STRING_OPEN));
        state.dontMove = advance(builder);
        state.add(markComplete(builder, interpolationString, m_types.INTERPOLATION_EXPR));
    }

    private void parseJsStringClose(PsiBuilder builder, ParserState parserState) {
        ParserScope scope = parserState.endUntilScopeToken(m_types.JS_STRING_OPEN);
        parserState.dontMove = advance(builder);

        if (scope != null) {
            scope.complete();
            parserState.popEnd();
        }
    }

    private void parseLet(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        state.add(mark(builder, let, m_types.LET_STMT));
    }

    private void parseVal(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        if (state.isCurrentResolution(clazzBodyScope)) {
            state.add(mark(builder, val, clazzField, m_types.CLASS_FIELD));
        } else {
            state.add(mark(builder, let, m_types.LET_STMT));
        }
    }

    private void parsePub(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        if (state.isCurrentResolution(clazzBodyScope)) {
            state.add(mark(builder, clazzMethod, m_types.CLASS_METHOD));
        }
    }

    private void parseModule(PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            state.endUntilStartScope();
            state.add(mark(builder, module, m_types.MODULE_STMT));
        }
    }

    private void parseClass(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        state.add(mark(builder, clazzDeclaration, clazz, m_types.CLASS_STMT));
    }

    private void parseType(PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz)) {
            if (!state.isCurrentResolution(letNamedSignature)) {
                state.endUntilStartScope();
            }
            state.add(mark(builder, type, m_types.EXP_TYPE));
            state.dontMove = advance(builder);
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        }
    }

    private void parseExternal(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        state.add(mark(builder, external, m_types.EXTERNAL_STMT));
    }

    private void parseOpen(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        state.add(mark(builder, open, m_types.OPEN_STMT));
    }

    private void parseInclude(PsiBuilder builder, ParserState state) {
        state.endUntilStartScope();
        state.add(mark(builder, include, m_types.INCLUDE_STMT));
    }

    private void parsePercent(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(macro)) {
            parserState.complete();
            parserState.add(markComplete(builder, macroName, m_types.MACRO_NAME));
            parserState.complete();
        }
    }

    private void parseColon(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(externalNamed)) {
            state.dontMove = advance(builder);
            state.add(markComplete(builder, externalNamedSignature, m_types.SIG_SCOPE));
        } else if (state.isCurrentResolution(letNamed)) {
            state.dontMove = advance(builder);
            state.add(markComplete(builder, state.currentContext(), letNamedSignature, m_types.SIG_SCOPE));
        } else if (state.isCurrentResolution(moduleNamed)) {
            // Module signature
            //   MODULE UIDENT COLON ...
            state.updateCurrentResolution(moduleNamedSignature);
            state.complete();
        } else if (state.isCurrentResolution(namedSymbol)) {
            state.complete();
            state.popEnd();

            state.dontMove = advance(builder);
            state.add(mark(builder, namedSymbolSignature, m_types.SIG_SCOPE));
        } else if (state.isCurrentResolution(recordField)) {
            state.complete();
            state.dontMove = advance(builder);
            state.add(mark(builder, recordSignature, m_types.SIG_SCOPE));
        }
    }

    private void parseArrobase(PsiBuilder builder, ParserState parserState) {
        if (parserState.isCurrentResolution(annotation)) {
            parserState.complete();
            parserState.add(markComplete(builder, annotationName, m_types.MACRO_NAME));
        }
    }

    private void parseLt(PsiBuilder builder, ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT || nextTokenType == m_types.OPTION) {
            // Surely a tag
            // Note that option is a ReasonML keyword but also a JSX keyword !
            builder.remapCurrentToken(m_types.TAG_LT);
            ParserScope tagScope = markCompleteScope(builder, startTag, m_types.TAG_START, m_types.TAG_LT);
            state.add(tagScope);

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
            state.add(markComplete(builder, closeTag, m_types.TAG_CLOSE));

            state.dontMove = advance(builder);

            builder.remapCurrentToken(m_types.TAG_NAME);
            state.dontMove = wrapWith(nextTokenType == m_types.UIDENT ? m_types.UPPER_SYMBOL : m_types.LOWER_SYMBOL, builder);
        }
    }

    private void parseGtAutoClose(PsiBuilder builder, ParserState state) {
        if (state.isCurrentCompositeElementType(m_types.TAG_PROPERTY)) {
            state.popEnd();
        }

        if (state.isCurrentResolution(startTag) || state.isCurrentResolution(closeTag)) {
            builder.remapCurrentToken(m_types.TAG_GT);
            state.dontMove = advance(builder);
            state.popEnd();
        }
    }

    private void parseLIdent(PsiBuilder builder, ParserState state) {
        boolean processSingleParam = false;

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
        } else if (state.isCurrentResolution(external)) {
            // EXTERNAL <LIDENT> ...
            state.updateCurrentResolution(externalNamed);
            state.complete();
        } else if (state.isCurrentResolution(let)) {
            // LET <LIDENT> ...
            state.updateCurrentResolution(letNamed);
            state.complete();
        } else if (state.isCurrentResolution(letNamedEq)) {
            if (state.previousTokenElementType == m_types.EQ) {
                // LET LIDENT EQ <LIDENT> ...
                IElementType nextElementType = builder.lookAhead(1);
                if (nextElementType == m_types.ARROW) {
                    // Single (paren less) function parameters
                    state.add(markComplete(builder, state.currentContext(), function, m_types.FUN_EXPR));
                    state.add(markComplete(builder, state.currentContext(), parameters, m_types.FUN_PARAMS));
                    processSingleParam = true;
                }
            }
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
        } else if (state.isCurrentResolution(startTag)) {
            // This is a property
            state.endUntilStartScope();
            builder.remapCurrentToken(m_types.PROPERTY_NAME);
            state.add(markComplete(builder, tagProperty, m_types.TAG_PROPERTY));
            builder.setWhitespaceSkippedCallback((type, start, end) -> {
                if (state.isCurrentResolution(tagProperty) || (state.isCurrentResolution(tagPropertyEq) && state.notInScopeExpression())) {
                    state.popEnd();
                    builder.setWhitespaceSkippedCallback(null);
                }
            });
        } else if (state.isCurrentResolution(recordBinding)) {
            state.add(mark(builder, recordField, m_types.RECORD_FIELD));
        } else if (state.isCurrentResolution(mixin)) {
            state.complete();
        } else if (shouldStartExpression(state)) {
            state.add(mark(builder, genericExpression, builder.getTokenType()));
        } else {
            IElementType nextElementType = builder.lookAhead(1);
            if (nextElementType == m_types.ARROW) {
                // Single (paren less) function parameters
                // <LIDENT> ARROW ...
                state.add(markComplete(builder, state.currentContext(), function, m_types.FUN_EXPR));
                state.add(markComplete(builder, state.currentContext(), parameters, m_types.FUN_PARAMS));
                processSingleParam = true;
            }
        }

        state.dontMove = wrapWith(m_types.LOWER_SYMBOL, builder);

        if (processSingleParam) {
            state.popEnd();
        }
    }

    private void parseLBracket(PsiBuilder builder, ParserState parserState) {
        IElementType nextTokenType = builder.rawLookup(1);
        if (nextTokenType == m_types.ARROBASE) {
            parserState.add(markScope(builder, annotation, m_types.ANNOTATION_EXPR, m_types.LBRACKET));
        } else if (nextTokenType == m_types.PERCENT) {
            parserState.add(markScope(builder, macro, m_types.MACRO_EXPR, m_types.LBRACKET));
        } else {
            parserState.add(markScope(builder, bracket, m_types.SCOPED_EXPR, m_types.LBRACKET));
        }
    }

    private void parseBracketGt(PsiBuilder builder, ParserState parserState) {
        parserState.add(markScope(builder, bracketGt, m_types.SCOPED_EXPR, m_types.LBRACKET));
    }

    private void parseRBracket(PsiBuilder builder, ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LBRACKET);
        state.dontMove = advance(builder);

        if (scope != null) {
            if (!scope.isResolution(annotation)) {
                scope.complete();
            }
            state.popEnd();
        }
    }

    private void parseLBrace(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(markScope(builder, recordBinding, m_types.RECORD_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignature)) {
            state.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(letNamedEq)) {
            state.add(markScope(builder, maybeRecord, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(ifThenStatement)) {
            state.add(markScope(builder, scope, brace, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(clazzNamedEq)) {
            state.add(markScope(builder, clazzBodyScope, m_types.SCOPED_EXPR, m_types.LBRACE));
        } else if (state.isCurrentResolution(switchBinaryCondition)) {
            ParserScope switchScope = state.endUntilContext(switch_);
            boolean isSwitch = switchScope != null && switchScope.isResolution(switch_);
            state.add(markScope(builder, isSwitch ? switchBody : brace, m_types.SCOPED_EXPR, isSwitch ? m_types.SWITCH : m_types.LBRACE));
        } else {
            state.add(markScope(builder, scope, brace, m_types.SCOPED_EXPR, m_types.LBRACE));
        }
    }

    private void parseRBrace(PsiBuilder builder, ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LBRACE);
        state.dontMove = advance(builder);

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseLParen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(modulePath) && state.previousTokenElementType == m_types.DOT) {
            state.updateCurrentResolution(localOpen);
            state.updateCurrentCompositeElementType(m_types.LOCAL_OPEN);
            state.complete();
            state.add(markScope(builder, paren, m_types.SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.add(markScope(builder, state.currentContext(), scope, m_types.SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamedParameters)) {
            state.add(markScope(builder, state.currentContext(), clazzConstructor, m_types.CLASS_CONSTR, m_types.LPAREN));
        } else if (state.isCurrentResolution(ifThenStatement)) {
            state.complete();
            state.add(markCompleteScope(builder, binaryCondition, m_types.BIN_CONDITION, m_types.LPAREN));
        } else if (state.isCurrentResolution(letNamedSignature)) {
            //
        } else {
            if (state.isCurrentResolution(external)) {
                // overloading an operator
                state.updateCurrentResolution(externalNamed);
                state.complete();
            }

            if (!state.isCurrentResolution(patternMatch) && !state.isCurrentResolution(recordSignature) &&
                    !state.isCurrentResolution(letNamedSignature) && !state.isCurrentResolution(tagPropertyEq) &&
                    !state.isCurrentContext(typeConstrName)) {
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

    private void parseRParen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(letNamedSignature)) {
            return;
        }

        ParserScope parenScope = state.endUntilScopeToken(m_types.LPAREN);
        state.dontMove = advance(builder);
        IElementType nextTokenType = builder.getTokenType();

        if (parenScope != null) {
            // Remove the scope from the stack, we want to test its parent
            state.pop();

            if (nextTokenType == m_types.ARROW) {
                if (!state.isCurrentResolution(patternMatch) && !state.isCurrentTokenType(m_types.SIG)) {
                    parenScope.resolution(parameters);
                    parenScope.compositeElementType(m_types.FUN_PARAMS);
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
            if (parenScope.isResolution(parameters) && nextTokenType == m_types.ARROW) {
                // Transform the generic scope to a function scope
                state.updateCurrentResolution(function);
                state.updateCurrentCompositeElementType(m_types.FUN_EXPR);
                state.complete();
            } else if (state.isCurrentResolution(genericExpression)) {
                state.popEnd();
            }

            ParserScope scope = state.getLatestScope();
            if (scope != null && (scope.isResolution(localOpen) || scope.isResolution(tagPropertyEq))) {
                state.popEnd();
            }
        }
    }

    private void parseEq(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd();
            state.updateCurrentResolution(typeNamedEq);
            state.dontMove = advance(builder);
            state.add(markComplete(builder, typeNamedEq, m_types.TYPE_BINDING));
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedSignature)) {
            if (state.isCurrentResolution(letNamedSignature)) {
                state.popEnd();
            }
            state.updateCurrentResolution(letNamedEq);
            state.dontMove = advance(builder);
            state.add(markComplete(builder, letBinding, letNamedEq, m_types.LET_BINDING));
        } else if (state.isCurrentResolution(tagProperty)) {
            state.updateCurrentResolution(tagPropertyEq);
        } else if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedEq);
            state.complete();
        } else if (state.isCurrentResolution(externalNamedSignature)) {
            state.complete();
            state.endUntilStartScope();
        } else if (state.isCurrentResolution(clazzNamed) || state.isCurrentResolution(clazzConstructor)) {
            state.updateCurrentResolution(clazzNamedEq);
        }
    }

    private void parseSemi(ParserState state) {
        // Don't pop the scopes
        state.endUntilStartScope();
    }

    private void parseUIdent(PsiBuilder builder, ParserState state) {
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
        } else if ((state.isCurrentResolution(startTag) || state.isCurrentResolution(closeTag)) && state.previousTokenElementType == m_types.DOT) {
            // a namespaced custom component
            builder.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.previousTokenElementType == m_types.PIPE) {
            builder.remapCurrentToken(m_types.VARIANT_NAME);
        } else {
            if (shouldStartExpression(state)) {
                state.add(mark(builder, genericExpression, builder.getTokenType()));
            }

            if (!state.isCurrentResolution(modulePath)) {
                IElementType nextElementType = builder.lookAhead(1);
                if (nextElementType == m_types.DOT) {
                    // We are parsing a module path
                    state.add(mark(builder, modulePath, m_types.UPPER_SYMBOL));
                }
            }
        }

        state.dontMove = wrapWith(m_types.UPPER_SYMBOL, builder);
    }

    private void parseSwitch(PsiBuilder builder, ParserState state) {
        boolean inScope = state.isScopeTokenElementType(m_types.LBRACE);
        ParserScope scope = markComplete(builder, switch_, m_types.SWITCH_EXPR);
        state.add(scope, inScope);
        state.dontMove = advance(builder);
        state.add(markComplete(builder, switchBinaryCondition, m_types.BIN_CONDITION));
    }

    private void parseTry(PsiBuilder builder, ParserState parserState) {
        parserState.add(markComplete(builder, try_, m_types.TRY_EXPR));
        parserState.dontMove = advance(builder);
        parserState.add(markComplete(builder, tryScope, m_types.SCOPED_EXPR));
    }

    private void parseArrow(PsiBuilder builder, ParserState state) {
        state.dontMove = advance(builder);
        IElementType nextTokenType = builder.getTokenType();

        if (state.isCurrentContext(typeConstrName)) {
            ParserScope scope = state.endUntilContext(type);
            if (scope != null) {
                state.popEnd();
            }
        } else if (state.isCurrentResolution(function)) {
            // let x = ($ANY) => <EXPR>
            state.add(markComplete(builder, state.currentContext(), functionBody, m_types.FUN_BODY));
        } else if (nextTokenType != m_types.LBRACE) {
            if (state.isCurrentResolution(patternMatch)) {
                state.add(mark(builder, state.currentContext(), patternMatchBody, m_types.SCOPED_EXPR));
            }
        }
    }

    private boolean shouldStartExpression(ParserState state) {
        return state.isInScopeExpression() && state.isScopeTokenElementType(m_types.LBRACE);
    }
}
