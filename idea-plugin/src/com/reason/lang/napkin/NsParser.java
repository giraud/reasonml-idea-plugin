package com.reason.lang.napkin;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.ParserState;

import static com.intellij.codeInsight.completion.CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;

public class NsParser extends CommonParser<NsTypes> {

    NsParser() {
        super(NsTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;
        state.previousElementType1 = null;

        //long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //break;
            //}

            state.previousElementType2 = state.previousElementType1;
            state.previousElementType1 = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // special analyse when inside an interpolation string
            if (state.isCurrentContext(interpolationString)) {
                if (tokenType == m_types.DOLLAR) {
                    if (state.isCurrentResolution(interpolationPart)) {
                        state.popEnd();
                        state.updateCurrentResolution(interpolationReference);
                    }
                } else if (state.isCurrentResolution(interpolationReference)) {
                    state.wrapWith(m_types.C_INTERPOLATION_REF);
                    state.updateCurrentResolution(interpolationString);
                } else if (state.currentResolution() != interpolationPart) {
                    state.mark(interpolationString, interpolationPart, m_types.C_INTERPOLATION_PART);
                }
            } else {

                // special keywords that can be used as lower identifier in records
                if (tokenType == m_types.REF && state.isCurrentResolution(recordBinding)) {
                    parseLIdent(state);
                } else if (tokenType == m_types.METHOD && state.isCurrentResolution(recordBinding)) {
                    parseLIdent(state);
                }
                //
                else if (tokenType == m_types.SEMI) {
                    parseSemi(state);
                } else if (tokenType == m_types.EQ) {
                    parseEq(state);
                } else if (tokenType == m_types.UNDERSCORE) {
                    parseUnderscore(state);
                } else if (tokenType == m_types.ARROW) {
                    parseArrow(state);
                } else if (tokenType == m_types.OPTION) {
                    parseOption(state);
                } else if (tokenType == m_types.TRY) {
                    parseTry(state);
                } else if (tokenType == m_types.LIDENT) {
                    parseLIdent(state);
                } else if (tokenType == m_types.UIDENT) {
                    parseUIdent(state);
                } else if (tokenType == m_types.ARROBASE) {
                    parseArrobase(state);
                } else if (tokenType == m_types.COLON) {
                    parseColon(state);
                } else if (tokenType == m_types.PIPE) {
                    parsePipe(state);
                } else if (tokenType == m_types.ASSERT) {
                    parseAssert(state);
                } else if (tokenType == m_types.IF) {
                    parseIf(state);
                }
                // ( ... )
                else if (tokenType == m_types.LPAREN) {
                    parseLParen(state);
                }
                // { ... }
                else if (tokenType == m_types.LBRACE) {
                    parseLBrace(state);
                } else if (tokenType == m_types.RBRACE) {
                    parseRBrace(state);
                }
                // < ... >
                else if (tokenType == m_types.LT) {
                    parseLt(state);
                } else if (tokenType == m_types.TAG_LT_SLASH) {
                    parseLtSlash(state);
                } else if (tokenType == m_types.GT) {
                    parseGt(state);
                } else if (tokenType == m_types.TAG_AUTO_CLOSE) {
                    parseGtAutoClose(state);
                }
                // Starts an expression
                else if (tokenType == m_types.OPEN) {
                    parseOpen(state);
                } else if (tokenType == m_types.INCLUDE) {
                    parseInclude(state);
                } else if (tokenType == m_types.EXTERNAL) {
                    parseExternal(state);
                } else if (tokenType == m_types.TYPE) {
                    parseType(state);
                } else if (tokenType == m_types.MODULE) {
                    parseModule(state);
                } else if (tokenType == m_types.LET) {
                    parseLet(state);
                } else if (tokenType == m_types.EXCEPTION) {
                    parseException(state);
                }
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

        endLikeSemi(state);
    }

    private void parseOption(@NotNull ParserState state) {
        state.mark(option, m_types.C_OPTION);
    }

    private void parseUnderscore(@NotNull ParserState state) {
        if (state.isCurrentResolution(let)) {
            state.updateCurrentResolution(letNamed);
        }
    }

    private void parseIf(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(ifThenStatement, m_types.C_IF_STMT);
    }

    private void parseAssert(@NotNull ParserState state) {
        state.mark(assert_, m_types.C_ASSERT_STMT);
    }

    private void parsePipe(@NotNull ParserState state) {
        // By default, a pattern match
        if (state.isCurrentResolution(patternMatchBody)) {
            state.popEndUntilResolution(switchBody);
        }
        if (state.isCurrentResolution(patternMatchVariant) || state.isCurrentResolution(typeNamedEqVariant)) {
            state.popEnd();
        }

        if (state.isCurrentResolution(typeNamedEq)) {
            // type x = |> | <| ...
            state.mark(state.currentContext(), typeNamedEqVariant, m_types.C_VARIANT_DECL);
        } else if (state.isCurrentResolution(variant) && state.isCurrentContext(typeBinding)) {
            state.popEndUntilResolution(typeNamedEqVariant).
                    mark(state.currentContext(), variant, m_types.C_VARIANT_DECL);
        } else {
            if (state.isCurrentResolution(patternMatch)) {
                state.popEnd();
            }
            state.mark(state.currentContext(), patternMatch, m_types.C_PATTERN_MATCH_EXPR);
        }
    }

    private void parseLet(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(let, m_types.C_LET_STMT);
    }

    private void parseModule(@NotNull ParserState state) {
        if (!state.isCurrentResolution(annotationName)) {
            endLikeSemi(state);
            state.mark(module, m_types.C_MODULE_STMT);
        }
    }

    private void parseException(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(exception, m_types.C_EXCEPTION_EXPR);
    }

    private void parseType(@NotNull ParserState state) {
        if (!state.isCurrentResolution(module) && !state.isCurrentResolution(clazz)) {
            endLikeSemi(state);
            state.mark(type, m_types.C_EXP_TYPE).
                    advance().
                    mark(type, typeConstrName, m_types.C_TYPE_CONSTR_NAME);
        }
    }

    private void parseExternal(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(external, m_types.C_EXTERNAL_STMT);
    }

    private void parseOpen(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(open, m_types.C_OPEN);
    }

    private void parseInclude(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(include, m_types.C_INCLUDE);
    }

    private void parseColon(@NotNull ParserState state) {
        if (state.isCurrentResolution(externalNamed)) {
            state.updateCurrentResolution(externalNamedSignature).
                    advance().
                    mark(signature, m_types.C_SIG_EXPR).
                    mark(signature, signatureItem, m_types.C_SIG_ITEM);
        } else if (state.isCurrentResolution(letNamed)) {
            // let x |> :<| ...
            state.advance().mark(signature, letNamedSignature, m_types.C_SIG_EXPR);
            IElementType nextTokenType = state.getTokenType();
            if (nextTokenType != m_types.LPAREN) {
                state.mark(signature, signatureItem, m_types.C_SIG_ITEM);
            }
        } else if (state.isCurrentResolution(moduleNamed)) {
            // module M |> :<| ...
            state.updateCurrentResolution(moduleNamedSignature).
                    complete();
        }
    }

    private void parseArrobase(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(annotation, m_types.C_ANNOTATION_EXPR).
                mark(annotation, annotationName, m_types.C_MACRO_NAME);
    }

    private void parseLt(@NotNull ParserState state) {
        // Can be a symbol or a JSX tag
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT
                || nextTokenType == m_types.OPTION) { // Note that option is a ReasonML keyword but also a JSX keyword !
            // Surely a tag
            state.remapCurrentToken(m_types.TAG_LT).
                    mark(jsxTag, m_types.C_TAG).
                    markScope(jsxStartTag, jsxStartTag, m_types.C_TAG_START, m_types.TAG_LT).
                    advance().
                    remapCurrentToken(m_types.TAG_NAME).
                    wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseGt(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            state.popEnd().popEnd();
        }

        if (state.isCurrentResolution(jsxStartTag)) {
            state.wrapWith(m_types.TAG_GT).popEnd().mark(jsxTagBody, m_types.C_TAG_BODY);
        } else if (state.isCurrentResolution(jsxTagClose)) {
            state.wrapWith(m_types.TAG_GT).popEnd().popEnd();
        }
    }

    private void parseLtSlash(@NotNull ParserState state) {
        IElementType nextTokenType = state.rawLookup(1);
        if (nextTokenType == m_types.LIDENT || nextTokenType == m_types.UIDENT) {
            // A closing tag
            if (state.isCurrentContext(jsxTagBody)) {
                state.popEnd();
            }

            state.remapCurrentToken(m_types.TAG_LT).
                    mark(jsxTagClose, m_types.C_TAG_CLOSE).
                    advance().
                    remapCurrentToken(m_types.TAG_NAME).
                    wrapWith(nextTokenType == m_types.UIDENT ? m_types.C_UPPER_SYMBOL : m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseGtAutoClose(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            state.popEnd().popEnd();
        }

        state.advance().popEnd().popEnd();
    }

    private void parseLIdent(@NotNull ParserState state) {
        if (state.isCurrentResolution(typeConstrName)) {
            // type |>x<| ...
            state.updateCurrentResolution(typeNamed);
        } else if (state.isCurrentResolution(external)) {
            // external |>x<| ...
            state.updateCurrentResolution(externalNamed);
        } else if (state.isCurrentResolution(let)) {
            // let |>x<| ..
            state.updateCurrentResolution(letNamed);
        } else if (state.isCurrentResolution(jsxStartTag)) {
            // This is a property
            state.popEndUntilStartScope();
            state.remapCurrentToken(m_types.PROPERTY_NAME).
                    mark(jsxTagProperty, m_types.C_TAG_PROPERTY).
                    setWhitespaceSkippedCallback((type, start, end) -> {
                        if (state.isCurrentResolution(jsxTagProperty) || (state.isCurrentResolution(jsxTagPropertyEqValue) && state.notInScopeExpression())) {
                            if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
                                state.popEnd();
                            }
                            state.popEnd();
                            state.setWhitespaceSkippedCallback(null);
                        }
                    });
        }

        if (!state.isCurrentResolution(jsxTagProperty)) {
            state.wrapWith(m_types.C_LOWER_SYMBOL);
        }
    }

    private void parseLBrace(@NotNull ParserState state) {
        if (state.isCurrentResolution(jsxTagPropertyEqValue)) {
            // A scoped property
            state.updateScopeToken(m_types.LBRACE);
        }

        state.markScope(scope, brace, m_types.C_SCOPED_EXPR, m_types.LBRACE);
    }

    private void parseRBrace(@NotNull ParserState state) {
        ParserScope scope = state.popEndUntilOneOfElementType(m_types.LBRACE, m_types.RECORD, m_types.SWITCH);
        state.advance();

        if (scope != null) {
            scope.complete();
            state.popEnd();
        }

        if (state.isCurrentResolution(jsxTagPropertyEq) || state.isCurrentResolution(localObjectOpen)) {
            state.popEnd();
        }
    }

    private void parseLParen(@NotNull ParserState state) {
        if (state.isCurrentResolution(annotationName)) {
            // @ann |>(<| ... )
            state.popEnd();
        } else if (state.isCurrentResolution(maybeFunctorCall)) {
            // We know now that it is really a functor call
            // module M = X |>(<| ... )
            // open X |>(<| ... )
            state.updateCurrentResolution(functorCall).complete();
            state.markScope(functorDeclarationParams, functorParams, m_types.C_FUNCTOR_PARAMS, m_types.LPAREN).
                    advance().
                    mark(state.currentContext(), functorParam, m_types.C_FUNCTOR_PARAM);
        }
    }

    private void parseEq(@NotNull ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndWhileContext(signature);
        }

        if (state.isCurrentResolution(typeNamed)) {
            state.popEnd().
                    updateCurrentResolution(typeNamedEq).
                    advance().
                    mark(typeBinding, typeNamedEq, m_types.C_TYPE_BINDING);
        } else if (state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedAttribute) || state.isCurrentResolution(letNamedSignature)) {
            if (state.isCurrentResolution(letNamedSignature)) {
                state.popEnd();
            } else if (state.isCurrentResolution(letNamedAttribute)) {
                state.complete().popEnd();
            }
            state.updateCurrentResolution(letNamedEq).advance().mark(letBinding, letNamedEq, m_types.C_LET_BINDING);
        } else if (state.isCurrentResolution(jsxTagProperty)) {
            state.updateCurrentResolution(jsxTagPropertyEq).
                    advance().
                    mark(state.currentContext(), jsxTagPropertyEqValue, m_types.C_TAG_PROP_VALUE);
        } else if (state.isCurrentResolution(moduleNamed)) {
            state.updateCurrentResolution(moduleNamedEq).complete();
        }
        // else if (state.isCurrentResolution(externalNamedSignature)) {
        //    state.complete().
        //            updateCurrentResolution(externalNamedSignatureEq);
        //} else if (state.isCurrentResolution(clazzNamed) || state.isCurrentResolution(clazzConstructor)) {
        //    state.updateCurrentResolution(clazzNamedEq);
        //} else if (state.isCurrentResolution(signatureItem)) {
        //    state.updateCurrentResolution(signatureItemEq);
        //} else if (state.isCurrentResolution(functionParameterNamed)) {
        // call(~x |> =<| .. )
        //state.add(mark(builder, state.currentContext(), functionParameterNamedBinding, m_types.C_FUN_PARAM_BINDING).complete());
        //}
    }

    private void parseSemi(@NotNull ParserState state) {
        // Don't pop the scopes
        state.popEndUntilStartScope();
    }

    private void parseUIdent(@NotNull ParserState state) {
        if (DUMMY_IDENTIFIER_TRIMMED.equals(state.getTokenText())) {
            return;
        }

        if (state.isCurrentResolution(open)) {
            // It is a module name/path, or maybe a functor call
            // open |>M<| ...
            state.markOptional(state.currentContext(), maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if (state.isCurrentResolution(include)) {
            // It is a module name/path, or maybe a functor call
            // include |>M<| ...
            state.markOptional(state.currentContext(), maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if (state.isCurrentResolution(module)) {
            // module |>M<| ...
            state.updateCurrentResolution(moduleNamed);
        } else if (state.isCurrentResolution(moduleNamedEq)) {
            // it might be a module functor call
            // module M = |>X<| ( ... )
            state.markOptional(state.currentContext(), maybeFunctorCall, m_types.C_FUNCTOR_CALL);
        } else if ((state.isCurrentResolution(jsxStartTag) || state.isCurrentResolution(jsxTagClose)) && state.previousElementType1 == m_types.DOT) {
            // a namespaced custom component
            state.remapCurrentToken(m_types.TAG_NAME);
        } else if (state.isCurrentResolution(typeNamedEqVariant)) {
            // Declaring a variant
            // type t = | |>X<| ..
            state.remapCurrentToken(m_types.VARIANT_NAME).wrapWith(m_types.C_VARIANT);
            return;
        }

        state.wrapWith(m_types.C_UPPER_SYMBOL);
    }

    private void parseTry(@NotNull ParserState state) {
        endLikeSemi(state);
        state.mark(try_, m_types.C_TRY_EXPR);
    }

    private void parseArrow(@NotNull ParserState state) {
        if (state.isCurrentContext(signature)) {
            state.popEndUnlessFirstContext(signature).
                    advance().
                    mark(state.currentContext(), signatureItem, m_types.C_SIG_ITEM);
        }
    }

    private void endLikeSemi(@NotNull ParserState state) {
        state.popEndUntilStartScope();
    }
}
