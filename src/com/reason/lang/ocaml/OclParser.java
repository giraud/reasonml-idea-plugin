package com.reason.lang.ocaml;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ILazyParseableElementType;
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

public class OclParser extends CommonParser<OclTypes> {

    public OclParser() {
        super(OclTypes.INSTANCE);
    }

    public static ASTNode parseOcamlNode(ILazyParseableElementType root, ASTNode chameleon) {
        PsiElement parentElement = chameleon.getTreeParent().getPsi();
        Project project = parentElement.getProject();

        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new OclLexer(), root.getLanguage(), chameleon.getText());
        OclParser parser = new OclParser();

        return parser.parse(root, builder).getFirstChildNode();
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;
        state.previousElementType1 = null;

        int c = current_position_(builder);
        while (true) {
            state.previousElementType2 = state.previousElementType1;
            state.previousElementType1 = tokenType;
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
            } else if (tokenType == m_types.OF) {
                parseOf(builder, state);
            } else if (tokenType == m_types.STAR) {
                parseStar(builder, state);
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
            } else if (tokenType == m_types.AND) {
                parseAnd(builder, state);
            } else if (tokenType == m_types.FUNCTION || tokenType == m_types.FUN) {
                parseFun(builder, state);
            } else if (tokenType == m_types.ASSERT) {
                parseAssert(builder, state);
            } else if (tokenType == m_types.RAISE) {
                parseRaise(builder, state);
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

    private void parseRaise(PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(external)) {
            builder.remapCurrentToken(m_types.LIDENT);
            state.wrapWith(m_types.C_LOWER_SYMBOL).updateCurrentResolution(externalNamed).complete();
        }
    }

    private void parseLArray(@NotNull PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, array, m_types.C_SCOPED_EXPR, m_types.LARRAY));
    }

    private void parseRArray(ParserState state) {
        state.popEndUntilContext(array);
        if (state.isCurrentResolution(array)) {
            state.popEnd();
        }
    }

    private void parseDo(@NotNull PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, doLoop, m_types.C_SCOPED_EXPR, m_types.DO));
    }

    private void parseDone(ParserState state) {
        state.popEndUntilStartScope();
        if (state.isCurrentResolution(doLoop)) {
            state.popEnd();
        }
    }

    private void parseRightArrow(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(signatureItem)) {
            state.popEnd().
                    advance().
                    add(mark(builder, signature, signatureItem, m_types.C_SIG_ITEM).complete());
        } else if (state.isCurrentResolution(patternMatch)) {
            state.advance().
                    add(mark(builder, state.currentContext(), patternMatchBody, m_types.C_PATTERN_MATCH_BODY).complete());
        } else if (state.isCurrentResolution(matchWith)) {
            state.advance()
                    .add(mark(builder, matchException, m_types.C_SCOPED_EXPR));
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

    private void parseAssert(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, assert_, m_types.C_ASSERT_STMT).complete());
    }

    private void parseAnd(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        // pop scopes until a known context is found
        endUntilStartExpression(state);
        if (state.isCurrentResolution(function)) {
            state.popEnd();
        }

        if (state.isCurrentContext(type)) {
            state.popEnd().popEnd().
                    advance().
                    add(mark(builder, type, m_types.C_EXP_TYPE)).
                    add(mark(builder, typeConstrName, m_types.C_TYPE_CONSTR_NAME));
        } else if (state.isCurrentContext(let) || state.isCurrentContext(letBinding)) {
            if (state.isCurrentContext(letBinding)) {
                state.popEnd();
            }
            state.popEnd().
                    advance().
                    add(mark(builder, let, m_types.C_LET_STMT));
        } else if (state.isCurrentContext(moduleDeclaration)) {
            state.popEnd().
                    advance();
            parseModule(builder, state);
        }
    }

    private void endUntilStartExpression(ParserState state) {
        ParserScopeEnum context = state.currentContext();
        while (context != let && context != type && context != moduleDeclaration && context != assert_) {
            if (context == file || state.isInScopeExpression()) {
                break;
            }
            state.popEnd();
            context = state.currentContext();
        }
    }

    private void parsePipe(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(typeNamedEq)) {
            state.add(mark(builder, state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEnd();
            state.add(mark(builder, state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
        } else if (state.isCurrentContext(variantConstructor)) {
            state.popEndWhileContext(variantConstructor);
            state.popEnd().add(mark(builder, typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
        } else {
            // By default, a pattern match
            if (state.isCurrentResolution(patternMatchBody)) {
                state.popEnd();
            }
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.add(mark(builder, state.currentContext(), patternMatch, m_types.C_PATTERN_MATCH_EXPR).complete());
        }
    }

    private void parseMatch(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, match, m_types.C_MATCH_EXPR).complete())
                .advance()
                .add(mark(builder, matchBinaryCondition, m_types.C_BIN_CONDITION).complete());
    }

    private void parseTry(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, try_, m_types.C_TRY_EXPR).complete())
                .add(mark(builder, try_, tryScope, m_types.C_SCOPED_EXPR).complete());
    }

    private void parseWith(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(moduleInstanciation)) {
            // this is incorrect, it might comes from:
            // module Constraint : Set.S with type elt = univ_constraint
            state.popEnd();
        }

        if (state.isCurrentResolution(functorNamedColon)) {
            // A functor like:
            // module Make (M : Input) : S <with> type input = M.t
            state.add(markScope(builder, functorConstraints, m_types.C_FUNCTOR_CONSTRAINTS, m_types.WITH));
        } else if (!state.isCurrentResolution(moduleNamedColon)) {
            if (state.isCurrentContext(try_)) {
                state.endUntilResolution(try_);
                state.updateCurrentResolution(tryWith);
                state.advance();
                state.add(mark(builder, try_, tryWithScope, m_types.C_SCOPED_EXPR).complete());
            } else if (state.isCurrentContext(matchBinaryCondition)) {
                state.popEndUntilContext(match);
                state.updateCurrentResolution(matchWith).setStart();
                state.advance();
            }
        }
    }

    private void parseIf(@NotNull PsiBuilder builder, ParserState state) {
        state.add(mark(builder, if_, m_types.C_IF_STMT).complete())
                .advance()
                .add(mark(builder, binaryCondition, m_types.C_BIN_CONDITION).complete());
    }

    private void parseThen(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilContext(if_);
        state.advance()
                .add(mark(builder, ifThenStatement, m_types.C_SCOPED_EXPR).complete());
    }

    private void parseElse(@NotNull PsiBuilder builder, ParserState state) {
        state.popEndUntilContext(if_);
        state.advance()
                .add(mark(builder, ifElseStatement, m_types.C_SCOPED_EXPR).complete());
    }

    private void parseStruct(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedSignatureEq)) {
            state.popEndUntilContext(moduleDeclaration);
            state.add(markScope(builder, moduleBinding, m_types.C_SCOPED_EXPR, m_types.STRUCT));
        } else if (state.isCurrentResolution(functorNamedEq)) {
            state.popEndUntilContext(functorDeclaration);
            state.add(markScope(builder, functorBinding, m_types.C_FUNCTOR_BINDING, m_types.STRUCT));
        } else {
            state.add(markScope(builder, struct, m_types.C_STRUCT_EXPR, m_types.STRUCT).complete());
        }
    }

    private void parseSig(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(moduleDeclaration)) {
            if (state.isCurrentResolution(moduleNamedEq) || state.isCurrentResolution(moduleNamedColon)) {
                state.popEndUntilContext(moduleDeclaration);
                state.updateCurrentResolution(moduleNamedSignature);
                state.add(markScope(builder, state.currentContext(), moduleSignature, m_types.C_SIG_EXPR, m_types.SIG));
            }
        }
    }

    private void parseSemi(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(recordField)) {
            // SEMI ends the field, and starts a new one
            state.complete();
            state.popEndUntilContext(recordField);
            state.popEnd();
            state.advance();
            state.add(mark(builder, recordField, m_types.C_RECORD_FIELD));
        } else {
            boolean isImplicitScope = state.isCurrentContext(functionBody);

            // A SEMI operator ends the previous expression
            if (!isImplicitScope && !state.isInScopeExpression()) {
                state.popEnd();
            }
        }
    }

    private void parseIn(@NotNull ParserState state) {
        endUntilStartExpression(state);
        state.popEnd();
    }

    private void parseBegin(@NotNull PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, beginScope, m_types.C_SCOPED_EXPR, m_types.BEGIN));
    }

    private void parseObject(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(clazzNamedEq)) {
            state.add(markScope(builder, clazzBodyScope, m_types.C_SCOPED_EXPR, m_types.OBJECT));
        } else {
            state.add(markScope(builder, objectScope, m_types.C_SCOPED_EXPR, m_types.OBJECT));
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

    private void parseColon(@NotNull PsiBuilder builder, ParserState state) { // :
        if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedColon).complete();
        } else if (state.isCurrentResolution(functorNamed)) {
            state.updateCurrentResolution(functorNamedColon);
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

    private void parseFun(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(letBinding)) {
            state.add(markScope(builder, function, m_types.C_FUN_EXPR, m_types.FUN).complete());
            state.advance();
            state.add(mark(builder, maybeFunctionParameters, m_types.C_FUN_PARAMS));
        }
    }

    private void parseEq(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndWhileContext(signature);
        } else if (state.isCurrentResolution(typeNamedEq)) {
            // Functor constraints
            state.popEndUntilStartScope().complete();
            state.popEnd();
        }

        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd().
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    add(mark(builder, typeBinding, typeNamedEq, m_types.C_TYPE_BINDING).complete());
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedSignature)) {
            state.popEndUntilContext(let);
            state.updateCurrentResolution(letNamedEq);
            state.advance();
            state.add(mark(builder, letBinding, letNamedBinding, m_types.C_LET_BINDING).complete());
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
        } else if (state.isCurrentContext(functorDeclaration)) {
            if (state.isCurrentResolution(functorNamed) || state.isCurrentResolution(functorNamedColon)) {
                state.updateCurrentResolution(functorNamedEq).
                        complete();
            }
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.updateCurrentResolution(clazzNamedEq);
        } else if (state.isCurrentResolution(externalNamed) && state.previousElementType1 == m_types.LPAREN) {
            // external ( «=» ) = ...
            builder.remapCurrentToken(m_types.LIDENT);
            state.wrapWith(m_types.C_LOWER_SYMBOL);
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

    private void parseOf(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Variant params :: type t = | Variant «of» ..
            state.add(mark(builder, variantConstructor, variantConstructorParameters, m_types.C_FUN_PARAMS).complete()).
                    advance().
                    add(mark(builder, variantConstructor, variantConstructorParameter, m_types.C_FUN_PARAM).complete());
        }
    }

    private void parseStar(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(variantConstructorParameter)) {
            // Variant params :: type t = | Variant of x <*> y .. )
            state.popEnd().
                    advance().
                    add(mark(builder, variantConstructor, variantConstructorParameter, m_types.C_FUN_PARAM).complete());
        }
    }

    private void parseArrobase(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(annotation)) {
            state.complete();
            state.add(mark(builder, annotationName, m_types.C_MACRO_NAME).complete());
        }
    }

    private void parseLParen(@NotNull PsiBuilder builder, ParserState state) {
        if (state.previousElementType2 == m_types.UIDENT && state.previousElementType1 == m_types.DOT) {
            // Detecting a local open : M1.M2.<(> .. )
            state.add(markScope(builder, localOpenScope, m_types.C_LOCAL_OPEN, m_types.LPAREN).complete());
        } else if (state.isCurrentResolution(external)) {
            // Overloading an operator: external (...) = ...
            state.updateCurrentResolution(externalNamed).complete();
            state.add(markScope(builder, localOpenScope, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(val)) {
            // Overloading an operator: val (...) = ...
            state.updateCurrentResolution(valNamed).complete();
            state.add(markScope(builder, valNamedSymbol, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        } else if (state.isCurrentResolution(clazzNamed)) {
            state.add(markScope(builder, state.currentContext(), clazzConstructor, m_types.C_CLASS_CONSTR, m_types.LPAREN));
        } else if (state.isCurrentResolution(functionParameter)) {
            state.updateScopeToken(m_types.LPAREN);
        } else if (state.isCurrentResolution(functionParameters)) {
            state.add(markScope(builder, functionParameters, functionParameter, m_types.C_FUN_PARAM, m_types.LPAREN));

            IElementType nextTokenType = builder.rawLookup(1);
            if (nextTokenType == m_types.RPAREN) {
                // unit parameter
                state.wrapWith(m_types.C_UNIT).advance();
            }
        } else if (state.isCurrentResolution(moduleNamed)) {
            // This is a functor: module Make <(> ... )
            state.updateCurrentContext(functorDeclaration).
                    updateCurrentResolution(functorNamed).
                    updateCurrentCompositeElementType(m_types.C_FUNCTOR).
                    add(markScope(builder, functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN));
        } else {
            state.add(markScope(builder, scope, paren, m_types.C_SCOPED_EXPR, m_types.LPAREN));
        }
    }

    private void parseRParen(ParserState state) {
        ParserScope scope = state.endUntilScopeToken(m_types.LPAREN);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }

        if (state.isCurrentResolution(let)) {
            // we are processing an infix operator or a desconstruction (tuple) : let (..<)>
            state.updateCurrentResolution(letNamed).complete();
        }
    }

    private void parseLBrace(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(functionParameters)) {
            state.add(mark(builder, function, functionParameter, m_types.C_FUN_PARAM).complete());
        }

        state.add(markScope(builder, recordBinding, m_types.C_RECORD_EXPR, m_types.LBRACE));
        state.advance();
        state.add(mark(builder, recordField, m_types.C_RECORD_FIELD));
    }

    private void parseRBrace(ParserState state) {
        if (state.isCurrentResolution(recordField) && state.previousElementType1 != m_types.SEMI) {
            state.complete();
        }

        ParserScope scope = state.endUntilScopeToken(m_types.LBRACE);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }
    }

    private void parseLBracket(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(clazz)) {
            state.add(markScope(builder, clazzDeclaration, bracket, m_types.C_CLASS_PARAMS, m_types.LBRACKET));
        } else {
            IElementType nextTokenType = builder.rawLookup(1);
            if (nextTokenType == m_types.ARROBASE) {
                // This is an annotation
                state.popEndUntilStartScope();
                state.add(markScope(builder, annotation, m_types.C_ANNOTATION_EXPR, m_types.LBRACKET));
            } else {
                state.add(markScope(builder, bracket, m_types.C_SCOPED_EXPR, m_types.LBRACKET));
            }
        }
    }

    private void parseBracketGt(@NotNull PsiBuilder builder, ParserState state) {
        state.add(markScope(builder, bracketGt, m_types.C_SCOPED_EXPR, m_types.LBRACKET));
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

    private void parseLIdent(@NotNull PsiBuilder builder, ParserState state) {
        /*
        (state.isCurrentResolution(modulePath)) {
            if (state.isCurrentContext(genericExpression)) {
                state.updateCurrentResolution(genericExpression);
            } else {
                state.popEnd();
            }
        }
*/
        if (state.isCurrentResolution(typeConstrName)) {
            state.updateCurrentResolution(typeNamed);
            state.complete();
            state.setPreviousComplete();
        } else if (state.isCurrentResolution(external)) {
            state.updateCurrentResolution(externalNamed);
            state.complete();
        } else if (state.isCurrentResolution(let)) {
            transitionToLetNamed(builder, state);
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

        state.wrapWith(m_types.C_LOWER_SYMBOL);
    }

    private void transitionToLetNamed(PsiBuilder builder, ParserState state) {
        state.updateCurrentResolution(letNamed).complete().
                wrapWith(m_types.C_LOWER_SYMBOL);
        IElementType tokenType = builder.getTokenType();
        if (tokenType != m_types.EQ && tokenType != m_types.COLON) {
            state.add(mark(builder, letBinding, letNamedBinding, m_types.C_LET_BINDING).complete())
                    .add(mark(builder, function, m_types.C_FUN_EXPR).complete())
                    .add(mark(builder, function, functionParameters, m_types.C_FUN_PARAMS).complete());
        }

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
        } else if (state.isCurrentResolution(exception)) {
            state.complete();
            state.updateCurrentResolution(exceptionNamed);
            builder.remapCurrentToken(m_types.EXCEPTION_NAME);
        } else if (state.isCurrentResolution(module)) {
            state.updateCurrentResolution(moduleNamed);
        } else if (state.isCurrentResolution(typeNamedEq)) {
            // Might be a variant without a pipe
            IElementType nextTokenType = builder.lookAhead(1);
            if (nextTokenType == m_types.OF || nextTokenType == m_types.PIPE) {
                // type t = «X» | ..   or   type t = «X» of ..
                state.add(mark(builder, state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL).complete());
                state.wrapWith(m_types.C_VARIANT_CONSTRUCTOR);
                return;
            }
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Declaring a variant
            // type t = | «X» ..
            state.wrapWith(m_types.C_VARIANT_CONSTRUCTOR);
            return;
        } else if (state.isCurrentResolution(patternMatch)) {
            IElementType nextTokenType = builder.lookAhead(1);
            if (nextTokenType != m_types.DOT) {
                // Pattern matching a variant
                // match c with | «X» ..
                builder.remapCurrentToken(m_types.VARIANT_NAME);
                state.wrapWith(m_types.C_VARIANT_CONSTRUCTOR);
                return;
            }
        }

        state.wrapWith(m_types.C_UPPER_SYMBOL);
    }

    private void parseOpen(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            // let open X (coq/indtypes.ml)
            state.updateCurrentResolution(open);
            state.updateCurrentCompositeElementType(m_types.C_OPEN);
        } else {
            endLikeSemi(state);
            state.add(mark(builder, open, m_types.C_OPEN));
        }
    }

    private void parseInclude(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, include, m_types.C_INCLUDE));
    }

    private void parseExternal(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, external, m_types.C_EXTERNAL_STMT));
    }

    private void parseType(@NotNull PsiBuilder builder, ParserState state) {
        if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz)) {
            if (state.isCurrentResolution(moduleNamedColon) || state.isCurrentResolution(moduleNamedColonWith)) {
                state.updateCurrentResolution(moduleNamedWithType);
            } else {
                endLikeSemi(state);
                state.add(mark(builder, type, m_types.C_EXP_TYPE));
                state.advance();
                state.add(mark(builder, typeConstrName, m_types.C_TYPE_CONSTR_NAME));
            }
        }
    }

    private void parseException(@NotNull PsiBuilder builder, ParserState state) {
        if (state.previousElementType1 != m_types.PIPE) {
            endLikeSemi(state);
            state.add(mark(builder, exception, m_types.C_EXCEPTION_EXPR));
        }
    }

    private void parseVal(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, val, state.isCurrentContext(clazzBodyScope) ? m_types.C_CLASS_FIELD : m_types.C_VAL_EXPR));
    }

    private void parseMethod(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, val, m_types.C_CLASS_METHOD));
    }

    private void parseLet(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, let, m_types.C_LET_STMT));
    }

    private void parseModule(@NotNull PsiBuilder builder, ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentContext(moduleDeclaration).
                    updateCurrentResolution(module).
                    updateCurrentCompositeElementType(m_types.C_MODULE_STMT);
        } else if (!state.isCurrentResolution(annotationName)) {
            endLikeSemi(state);
            state.add(mark(builder, moduleDeclaration, module, m_types.C_MODULE_STMT));
        }
    }

    private void parseClass(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        endLikeSemi(state);
        state.add(mark(builder, clazzDeclaration, clazz, m_types.C_CLASS_STMT));
    }

    private void endLikeSemi(ParserState state) {
        if (state.previousElementType1 != m_types.EQ && state.previousElementType1 != m_types.RIGHT_ARROW &&
                state.previousElementType1 != m_types.TRY && state.previousElementType1 != m_types.SEMI &&
                state.previousElementType1 != m_types.THEN && state.previousElementType1 != m_types.ELSE &&
                state.previousElementType1 != m_types.IN && state.previousElementType1 != m_types.LPAREN &&
                state.previousElementType1 != m_types.DO && state.previousElementType1 != m_types.STRUCT &&
                state.previousElementType1 != m_types.SIG && state.previousElementType1 != m_types.COLON) {
            ParserScope parserScope = state.popEndUntilStartScope();
            while (parserScope.isContext(function) || parserScope.isContext(match)) {
                state.popEnd();
                parserScope = state.popEndUntilStartScope();
            }
        }
    }
}
