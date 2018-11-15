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
import static com.reason.lang.ParserScope.mark;
import static com.reason.lang.ParserScope.markScope;
import static com.reason.lang.ParserScopeEnum.*;

public class OclParser extends CommonParser<OclTypes> {

    public OclParser() {
        super(OclTypes.INSTANCE);
    }

    @Override
    protected void parseFile(PsiBuilder builder, ParserState state) {
        IElementType tokenType = null;

        int c = current_position_(builder);
        while (true) {
            state.previousTokenElementType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == m_types.SEMI) {
                parseSemi(builder, state);
            } else if (tokenType == m_types.IN) {
                parseIn(state);
            } else if (tokenType == m_types.END) { // end (like a })
                parseEnd(state);
            } else if (tokenType == m_types.UNDERSCORE) {
                parseUnderscore(state);
            } else if (tokenType == m_types.RIGHT_ARROW) {
                parseRightArrow(builder, state);
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
            } else if (tokenType == m_types.BEGIN) {
                parseBegin(builder, state);
            } else if (tokenType == m_types.OBJECT) {
                parseObject(builder, state);
            } else if (tokenType == m_types.IF) {
                parseIf(builder, state);
            } else if (tokenType == m_types.THEN) {
                parseThen(builder, state);
            } else if (tokenType == m_types.ELSE) {
                parseElse(builder, state);
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
            } else if (tokenType == m_types.FUNCTION || tokenType == m_types.FUN) {
                parseFun(builder, state);
            } else if (tokenType == m_types.ASSERT) {
                parseAssert(builder, state);
            }
            // do ... done
            else if (tokenType == m_types.DO) {
                parseDo(builder, state);
            } else if (tokenType == m_types.DONE) {
                parseDone(state);
            }
            // ( ... )
            else if (tokenType == m_types.LPAREN) {
                parseLParen(builder, state);
            } else if (tokenType == m_types.RPAREN) {
                parseRParen(state);
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
                parseRBracket(state);
            }
            // [| ... |]
            else if (tokenType == m_types.LARRAY) {
                parseLArray(builder, state);
            } else if (tokenType == m_types.RARRAY) {
                parseRArray(state);
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
            } else if (tokenType == m_types.CLASS) {
                parseClass(builder, state);
            } else if (tokenType == m_types.LET) {
                parseLet(builder, state);
            } else if (tokenType == m_types.VAL) {
                parseVal(builder, state);
            } else if (tokenType == m_types.METHOD) {
                parseMethod(builder, state);
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

    private void parseLArray(PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, array, m_types.SCOPED_EXPR, m_types.LARRAY));
    }

    private void parseRArray(ParserState state) {
        state.popEndUntilContext(array);
        if (state.isCurrentResolution(array)) {
            state.popEnd();
        }
    }

    private void parseDo(PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, doLoop, m_types.SCOPED_EXPR, m_types.DO));
    }

    private void parseDone(ParserState state) {
        state.popEndUntilStartScope();
        if (state.isCurrentResolution(doLoop)) {
            state.popEnd();
        }
    }

    private void parseRightArrow(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(signatureItem)) {
            state.popEnd().
                    advance().
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(patternMatch)) {
            state.add(mark(builder, patternMatch, patternMatchBody, m_types.SCOPED_EXPR));
        } else if (state.isCurrentResolution(matchWith)) {
            state.advance()
                    .add(mark(builder, matchException, m_types.SCOPED_EXPR));
        } else if (state.isCurrentContext(typeConstrName)) {
            state.popEndUntilContext(type).popEnd();
        } else if (state.isCurrentResolution(maybeFunctionParameters)) {
            state.complete().popEnd()
                    .advance().
                    add(mark(builder, functionBody, m_types.C_FUN_BODY).complete());
        }
    }

    private void parseUnderscore(ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
            state.complete();
        }
    }

    private void parseAssert(PsiBuilder builder, ParserState state) {
        state.add(mark(builder, assert_, m_types.ASSERT_STMT).complete());
    }

    private void parseAnd(PsiBuilder builder, ParserState state) {
        // pop scopes until a known context is found
        ParserScopeEnum context = endUntilStartExpression(state);

        if (context == type) {
            state.popEnd();
            state.advance();
            state.add(mark(builder, type, m_types.EXP_TYPE));
            state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
        } else if (context == let) {
            state.popEnd();
            state.advance();
            state.add(mark(builder, let, m_types.LET_STMT));
        } else if (context == moduleDeclaration) {
            state.popEnd();
            state.advance();
            parseModule(builder, state);
        }
    }

    private ParserScopeEnum endUntilStartExpression(ParserState state) {
        ParserScopeEnum context = state.currentContext();
        while (context != let && context != type && context != moduleDeclaration && context != assert_) {
            if (context == file || state.isInScopeExpression()) {
                break;
            }
            state.popEnd();
            context = state.currentContext();
        }
        return context;
    }


    private void parseString(ParserState state) {
        if (state.isCurrentResolution(annotationName)) {
            state.endAny();
        }
    }

    private void parsePipe(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(mark(builder, typeNamedEqVariant, m_types.VARIANT_EXP).complete());
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEnd();
            state.add(mark(builder, typeNamedEqVariant, m_types.VARIANT_EXP).complete());
        } else {
            // By default, a pattern match
            if (state.isCurrentResolution(patternMatchBody)) {
                state.popEnd();
            }
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.add(mark(builder, state.currentContext(), patternMatch, m_types.PATTERN_MATCH_EXPR).complete());
        }
    }

    private void parseMatch(PsiBuilder builder, ParserState state) {
        state.add(mark(builder, match, m_types.MATCH_EXPR).complete())
                .advance()
                .add(mark(builder, matchBinaryCondition, m_types.BIN_CONDITION).complete());
    }

    private void parseTry(PsiBuilder builder, ParserState state) {
        state.add(mark(builder, try_, m_types.TRY_EXPR).complete())
                .add(mark(builder, try_, tryScope, m_types.SCOPED_EXPR).complete());
    }

    private void parseWith(PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(moduleInstanciation)) {
            // this is incorrect, it might comes from:
            // module Constraint : Set.S with type elt = univ_constraint
            state.popEnd();
        }

        if (!state.isCurrentResolution(moduleNamedColon)) {
            if (state.isCurrentContext(try_)) {
                state.endUntilResolution(try_);
                state.updateCurrentResolution(tryWith);
                state.advance();
                state.add(mark(builder, try_, tryWithScope, m_types.SCOPED_EXPR).complete());
            } else if (state.isCurrentContext(matchBinaryCondition)) {
                state.popEndUntilContext(match);
                state.updateCurrentResolution(matchWith);
                state.advance();
            }
        }
        //else {
        // A functor like:
        // module Make (M : Input) : S with type input = M.t
        //}
    }

    private void parseIf(PsiBuilder builder, ParserState state) {
        state.add(mark(builder, if_, m_types.IF_STMT).complete())
                .advance()
                .add(mark(builder, binaryCondition, m_types.BIN_CONDITION).complete());
    }

    private void parseThen(PsiBuilder builder, ParserState state) {
        state.popEndUntilContext(if_);
        state.advance()
                .add(mark(builder, ifThenStatement, m_types.SCOPED_EXPR).complete());
    }

    private void parseElse(PsiBuilder builder, ParserState state) {
        state.popEndUntilContext(if_);
        state.advance()
                .add(mark(builder, ifElseStatement, m_types.SCOPED_EXPR).complete());
    }

    private void parseStruct(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignatureEq)) {
            state.popEndUntilContext(moduleDeclaration);
            state.add(markScope(builder, moduleBinding, m_types.SCOPED_EXPR, m_types.STRUCT));
        } else {
            state.add(markScope(builder, struct, m_types.STRUCT_EXPR, m_types.STRUCT).complete());
        }
    }

    private void parseSig(PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(moduleDeclaration)) {
            if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedColon)) {
                state.popEndUntilContext(moduleDeclaration);
                state.updateCurrentResolution(moduleNamedSignature);
                state.add(markScope(builder, state.currentContext(), moduleSignature, m_types.C_SIG_EXPR, m_types.SIG));
            }
        }
    }

    private void parseSemi(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(recordField)) {
            // SEMI ends the field, and starts a new one
            state.complete();
            state.popEndUntilContext(recordField);
            state.popEnd();
            state.advance();
            state.add(mark(builder, recordField, m_types.RECORD_FIELD));
        } else {
            // A SEMI operator ends the previous expression
            if (!state.isInScopeExpression()) {
                state.popEnd();
            }
        }
    }

    private void parseIn(ParserState state) {
        endUntilStartExpression(state);
        state.popEnd();
    }

    private void parseBegin(PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, beginScope, m_types.SCOPED_EXPR, m_types.BEGIN));
    }

    private void parseObject(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(clazzNamedEq)) {
            state.add(markScope(builder, clazzBodyScope, m_types.SCOPED_EXPR, m_types.OBJECT));
        } else {
            state.add(markScope(builder, objectScope, m_types.SCOPED_EXPR, m_types.OBJECT));
        }
    }

    private void parseEnd(ParserState state) {
        ParserScope scope = state.popEndUntilOneOfElementType(m_types.BEGIN, m_types.SIG, m_types.STRUCT, m_types.OBJECT);
        state.advance();

        if (scope != null && scope.isScopeStart()) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseColon(PsiBuilder builder, ParserState state) { // :
        if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedColon).complete();
        } else if (state.isCurrentResolution(externalNamed)) {
            state.advance().
                    add(mark(builder, signature, externalNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(valNamed)) {
            // val x <:> ...
            state.advance().
                    add(mark(builder, signature, valNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateCurrentResolution(functionParameterNamed).
                    advance().
                    add(mark(builder, signature, functionParameterNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(letNamed)) {
            state.advance().
                    add(mark(builder, signature, letNamedSignature, m_types.C_SIG_EXPR).complete()).
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        }
    }

    private void parseFun(PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(letBinding)) {
            state.add(markScope(builder, function, m_types.C_FUN_EXPR, m_types.FUN).complete());
            state.advance();
            state.add(mark(builder, maybeFunctionParameters, m_types.C_FUN_PARAMS));
        }
    }

    private void parseEq(PsiBuilder builder, ParserState state) { // =
        if (state.isCurrentContext(signature)) {
            state.popEndWhileContext(signature);
        }

        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd();
            state.updateCurrentResolution(typeNamedEq);
            state.advance();
            state.add(mark(builder, typeNamedEq, state.currentContext(), m_types.TYPE_BINDING).complete());
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedSignature)) {
            state.popEndUntilContext(let);
            state.updateCurrentResolution(letNamedEq);
            state.advance();
            state.add(mark(builder, letBinding, letNamedBinding, m_types.LET_BINDING).complete());
        } else if (state.isCurrentResolution(jsxTagProperty)) {
            state.updateCurrentResolution(jsxTagPropertyEq);
        } else if (state.isCurrentContext(moduleDeclaration)) {
            if (state.isCurrentResolution(moduleNamed)) {
                state.updateCurrentResolution(moduleNamedEq);
                state.complete();
            } else if (state.isCurrentResolution(moduleNamedSignature)) {
                state.updateCurrentResolution(moduleNamedSignatureEq);
                state.complete();
            }
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.updateCurrentResolution(clazzNamedEq);
        } else if (state.isCurrentResolution(externalNamedSignature)) {
            state.complete();
            state.popEnd();
            state.updateCurrentResolution(externalNamedSignatureEq);
        } else if (state.isCurrentResolution(maybeFunctionParameters)) {
            ParserScope innerScope = state.pop();
            if (innerScope != null) {
                // This is a function definition, change the scopes
                innerScope.resolution(functionParameters).compositeElementType(m_types.C_FUN_PARAMS).complete().end();
                state.updateCurrentContext(function).
                        updateCurrentResolution(function).
                        updateCurrentCompositeElementType(m_types.C_FUN_EXPR).
                        complete();
                state.advance();
                state.add(mark(builder, functionBody, m_types.C_FUN_BODY).complete());
            }
        } else if (state.isCurrentResolution(functionParameter)) {
            state.complete().
                    popEndUntilResolution(function).
                    advance().
                    add(mark(builder, functionBody, m_types.C_FUN_BODY).complete());
        }
    }

    private void parseArrobase(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotation)) {
            state.complete();
            state.add(mark(builder, annotationName, m_types.MACRO_NAME).complete());
        }
    }

    private void parseLParen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(modulePath) && state.previousTokenElementType == m_types.DOT) {
            // Detecting a local open : M1.M2(...)
            state.updateCurrentResolution(localOpen);
            state.updateCurrentCompositeElementType(m_types.LOCAL_OPEN);
            state.complete();
            state.add(markScope(builder, localOpenScope, m_types.SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(external)) {
            // Overloading an operator: external (...) = ...
            state.updateCurrentResolution(externalNamed).complete();
        } else if (state.isCurrentResolution(val)) {
            // Overloading an operator: val (...) = ...
            state.updateCurrentResolution(valNamed).complete();
            state.add(markScope(builder, valNamedSymbol, m_types.SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.add(markScope(builder, state.currentContext(), clazzConstructor, m_types.CLASS_CONSTR, m_types.LPAREN));
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateScopeToken(m_types.LPAREN);
        } else if (state.isCurrentResolution(functionParameters)) {
            state.add(markScope(builder, functionParameters, functionParameter, m_types.C_FUN_PARAM, m_types.LPAREN));
        } else {
            state.add(markScope(builder, scope, paren, m_types.SCOPED_EXPR, m_types.LPAREN));
        }
    }

    private void parseRParen(ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LPAREN);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseLBrace(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(functionParameters)) {
            state.add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
        }

        state.add(markScope(builder, recordBinding, m_types.RECORD_EXPR, m_types.LBRACE));
        state.advance();
        state.add(mark(builder, recordField, m_types.RECORD_FIELD));
    }

    private void parseRBrace(ParserState state) {
        if (state.isCurrentResolution(recordField) && state.previousTokenElementType != m_types.SEMI) {
            state.complete();
        }

        ParserScope scope = state.endUntilScopeToken(m_types.LBRACE);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseLBracket(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(clazz)) {
            state.add(markScope(builder, clazzDeclaration, bracket, m_types.CLASS_PARAMS, m_types.LBRACKET));
        } else {
            IElementType nextTokenType = builder.rawLookup(1);
            if (nextTokenType == m_types.ARROBASE) {
                // This is an annotation
                state.popEndUntilStartScope();
                state.add(markScope(builder, annotation, m_types.ANNOTATION_EXPR, m_types.LBRACKET));
            } else {
                state.add(markScope(builder, bracket, m_types.SCOPED_EXPR, m_types.LBRACKET));
            }
        }
    }

    private void parseBracketGt(PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, bracketGt, m_types.SCOPED_EXPR, m_types.LBRACKET));
    }

    private void parseRBracket(ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LBRACKET);
        state.advance();

        if (scope != null) {
            if (!scope.isResolution(annotation)) {
                scope.complete();
            }
            state.popEnd();
        }
    }

    private void parseLIdent(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(modulePath)) {
            if (state.isCurrentContext(genericExpression)) {
                state.updateCurrentResolution(genericExpression);
            } else {
                state.popEnd();
            }
        }

        if (state.isCurrentResolution(typeConstrName)) {
            state.updateCurrentResolution(typeNamed);
            state.complete();
            state.setPreviousComplete();
        } else if (state.isCurrentResolution(external)) {
            state.updateCurrentResolution(externalNamed);
            state.complete();
        } else if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed).complete();
            state.dontMove = wrapWith(m_types.C_LET_NAME, builder);
            IElementType nextTokenType = builder.getTokenType();
            if (nextTokenType != m_types.EQ && nextTokenType != m_types.COLON) {
                state.add(mark(builder, letBinding, letNamedBinding, m_types.LET_BINDING).complete())
                        .add(mark(builder, function, m_types.C_FUN_EXPR).complete())
                        .add(mark(builder, function, functionParameters, m_types.C_FUN_PARAMS).complete());
//                        .add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
            }
            return;
        } else if (state.isCurrentResolution(val)) {
            state.updateCurrentResolution(valNamed);
            state.complete();
        } else if (state.isCurrentResolution(clazz)) {
            state.updateCurrentResolution(clazzNamed);
            state.complete();
        } else if (state.isCurrentResolution(functionParameters)) {
            state.add(mark(builder, functionParameters, functionParameter, m_types.C_FUN_PARAM));
        } else if (state.isCurrentResolution(functionParameter) && !state.isInScopeExpression()) {
            state.complete().popEnd().
                    add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM));
        }

        state.dontMove = wrapWith(m_types.LOWER_SYMBOL, builder);
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
        } else if (state.isCurrentResolution(exception)) {
            state.complete();
            state.updateCurrentResolution(exceptionNamed);
            builder.remapCurrentToken(m_types.EXCEPTION_NAME);
        } else if (state.isCurrentResolution(module)) {
            state.updateCurrentResolution(moduleNamed);
        } else if (state.previousTokenElementType == m_types.PIPE) {
            builder.remapCurrentToken(m_types.VARIANT_NAME);
        } else {
            if (!state.isCurrentResolution(modulePath)) {
                IElementType nextElementType = builder.lookAhead(1);
                if (nextElementType == m_types.DOT) {
                    if (state.isCurrentContext(moduleDeclaration)) {
                        // module X = <|>Path.Functor(...)
                        state.add(mark(builder, moduleInstanciation, modulePath, m_types.MODULE_PATH/*?*/));
                    } else {
                        // We are parsing a module path
                        ParserScopeEnum newContext = state.currentContext() == functionBody ? genericExpression : state.currentContext();
                        state.add(mark(builder, newContext, modulePath, m_types.MODULE_PATH));
                    }
                }
            }
        }

        state.dontMove = wrapWith(m_types.UPPER_SYMBOL, builder);
    }

    private void parseOpen(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            // let open X (coq/indtypes.ml)
            state.updateCurrentResolution(open);
            state.updateCurrentCompositeElementType(m_types.OPEN_STMT);
        } else {
            endLikeSemi(state);
            state.add(mark(builder, open, m_types.OPEN_STMT));
        }
    }

    private void parseInclude(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, include, m_types.INCLUDE_STMT));
    }

    private void parseExternal(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, external, m_types.EXTERNAL_STMT));
    }

    private void parseType(PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz)) {
            if (state.isCurrentResolution(moduleNamedColon) || state.isCurrentResolution(moduleNamedColonWith)) {
                state.updateCurrentResolution(moduleNamedWithType);
            } else {
                endLikeSemi(state);
                state.add(mark(builder, type, m_types.EXP_TYPE));
                state.advance();
                state.add(mark(builder, typeConstrName, m_types.TYPE_CONSTR_NAME));
            }
        }
    }

    private void parseException(PsiBuilder builder, ParserState state) {
        if (state.previousTokenElementType != m_types.PIPE) {
            endLikeSemi(state);
            state.add(mark(builder, exception, m_types.EXCEPTION_EXPR));
        }
    }

    private void parseVal(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, val, state.isCurrentContext(clazzBodyScope) ? m_types.CLASS_FIELD : m_types.VAL_EXPR));
    }

    private void parseMethod(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, val, m_types.CLASS_METHOD));
    }

    private void parseLet(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, let, m_types.LET_STMT));
    }

    private void parseModule(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentContext(moduleDeclaration).
                    updateCurrentResolution(module).
                    updateCurrentCompositeElementType(m_types.MODULE_STMT);
        } else if (!state.isCurrentResolution(annotationName)) {
            endLikeSemi(state);
            state.add(mark(builder, moduleDeclaration, module, m_types.MODULE_STMT));
        }
    }

    private void parseClass(PsiBuilder builder, ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, clazzDeclaration, clazz, m_types.CLASS_STMT));
    }

    private void endLikeSemi(ParserState state) {
        if (state.previousTokenElementType != m_types.EQ && state.previousTokenElementType != m_types.RIGHT_ARROW &&
                state.previousTokenElementType != m_types.TRY && state.previousTokenElementType != m_types.SEMI &&
                state.previousTokenElementType != m_types.THEN && state.previousTokenElementType != m_types.ELSE &&
                state.previousTokenElementType != m_types.IN && state.previousTokenElementType != m_types.LPAREN &&
                state.previousTokenElementType != m_types.DO && state.previousTokenElementType != m_types.STRUCT &&
                state.previousTokenElementType != m_types.SIG && state.previousTokenElementType != m_types.COLON) {
            ParserScope parserScope = state.popEndUntilStartScope();
            while (parserScope.isContext(function)) {
                state.popEnd();
                parserScope = state.popEndUntilStartScope();
            }
        }
    }
}
