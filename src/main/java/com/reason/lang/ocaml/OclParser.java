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

            // ;
            if (tokenType == m_types.SEMI) {
                // A SEMI operator ends the start expression, not the group or scope
                ParserScope scope = parserState.end();
                if (scope != null && scope.scopeType == startExpression) {
                    parserState.scopes.pop();
                    scope.end();
                }

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // in
            else if (tokenType == m_types.IN) {
                // End current start-expression scope
                ParserScope scope = parserState.endUntilStart();
                if (scope != null && scope.scopeType == startExpression) {
                    parserState.scopes.pop();
                    scope.end();
                }

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // end (like a })
            else if (tokenType == m_types.END) {
                ParserScope scope = parserState.endUntilScopeExpression(null);

                builder.advanceLexer();
                parserState.dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    parserState.scopes.pop().end();
                }

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // =
            else if (tokenType == m_types.EQ) {
                if (parserState.currentScope.resolution == typeNamed) {
                    parserState.currentScope.resolution = typeNamedEq;
                } else if (parserState.currentScope.resolution == letNamed) {
                    parserState.currentScope.resolution = letNamedEq;
                    builder.advanceLexer();
                    parserState.dontMove = true;
                    parserState.currentScope = markScope(builder, parserState.scopes, letNamedEq, m_types.LET_BINDING, groupExpression, m_types.EQ);
                    parserState.currentScope.complete = true;
                } else if (parserState.currentScope.resolution == tagProperty) {
                    parserState.currentScope.resolution = tagPropertyEq;
                } else if (parserState.currentScope.resolution == moduleNamed) {
                    parserState.currentScope.resolution = moduleNamedEq;
                    parserState.currentScope.complete = true;
                }
            }

            // :
            else if (tokenType == m_types.COLON) {
                if (parserState.currentScope.resolution == moduleNamed) {
                    parserState.currentScope.resolution = moduleNamedColon;
                    parserState.currentScope.complete = true;
                }
            }

            // ( ... )
            else if (tokenType == m_types.LPAREN) {
                parserState.end();
                parserState.currentScope = markScope(builder, parserState.scopes, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN);
            } else if (tokenType == m_types.RPAREN) {
                ParserScope scope = parserState.endUntilScopeExpression(m_types.LPAREN);

                builder.advanceLexer();
                parserState.dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    parserState.scopes.pop().end();
                    parserState.getLatestScope();
                }

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // { ... }
            else if (tokenType == m_types.LBRACE) {
                parserState.end();
                parserState.currentScope = markScope(builder, parserState.scopes, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
            } else if (tokenType == m_types.RBRACE) {
                ParserScope scope = parserState.endUntilScopeExpression(m_types.LBRACE);

                builder.advanceLexer();
                parserState.dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    parserState.scopes.pop().end();
                }

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // [ ... ]
            else if (tokenType == m_types.LBRACKET) {
                IElementType nextTokenType = builder.rawLookup(1);
                if (nextTokenType == m_types.ARROBASE) {
                    // This is an annotation
                    parserState.currentScope = markScope(builder, parserState.scopes, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET);
                } else {
                    parserState.currentScope = markScope(builder, parserState.scopes, bracket, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACKET);
                }
            } else if (tokenType == m_types.RBRACKET) {
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

            //
            else if (tokenType == m_types.LIDENT) {
                if (parserState.currentScope.resolution == type) {
                    builder.remapCurrentToken(m_types.TYPE_CONSTR_NAME);
                    parserState.currentScope.resolution = typeNamed;
                    parserState.currentScope.complete = true;
                } else if (parserState.currentScope.resolution == external) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    parserState.currentScope.resolution = externalNamed;
                    parserState.currentScope.complete = true;
                } else if (parserState.currentScope.resolution == let) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    parserState.currentScope.resolution = letNamed;
                    parserState.currentScope.complete = true;
                } else if (parserState.currentScope.resolution == val) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    parserState.currentScope.resolution = valNamed;
                    parserState.currentScope.complete = true;
                }
            }

            //
            else if (tokenType == m_types.UIDENT) {
                if (parserState.currentScope.resolution == open) {
                    // It is a module name/path
                    parserState.currentScope.complete = true;
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    parserState.currentScope = markComplete(builder, parserState.scopes, openModulePath, m_types.MODULE_PATH);
                    parserState.dontMove = advance(builder, m_types.MODULE_NAME);
                } else if (parserState.currentScope.resolution == include) {
                    // It is a module name/path
                    parserState.currentScope.complete = true;
                    builder.remapCurrentToken(m_types.MODULE_NAME);
                    parserState.currentScope = markComplete(builder, parserState.scopes, openModulePath, m_types.MODULE_PATH);
                } else if (parserState.currentScope.resolution == exception) {
                    parserState.currentScope.complete = true;
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    parserState.dontMove = advance(builder, m_types.EXCEPTION_NAME);
                    parserState.currentScope.resolution = exceptionNamed;
                } else if (parserState.currentScope.resolution == module) {
                    // Module definition
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    parserState.dontMove = advance(builder, m_types.MODULE_NAME);
                    parserState.currentScope.resolution = moduleNamed;
                }
            }

            //else if (tokenType == m_types.MATCH) {
            //    currentScope = markScope(builder, scopes, match, m_types.PATTERN_MATCH_EXPR, startExpression, m_types.MATCH);
            //    currentScope.complete = true;
            //}
            //
            //else if (tokenType == m_types.WITH) {
            //    currentScope = markScope(builder, scopes, matchWith, m_types.SCOPED_EXPR, scopeExpression, m_types.WITH);
            //}

            // module signature
            else if (tokenType == m_types.SIG) {
                if (parserState.currentScope.resolution == moduleNamedEq || parserState.currentScope.resolution == moduleNamedColon) {
                    parserState.end();
                    parserState.currentScope.resolution = moduleNamedSignature;
                    parserState.currentScope = markScope(builder, parserState.scopes, moduleSignature, m_types.SIG_SCOPE, scopeExpression, m_types.SIG);
                }
            }

            // module body
            else if (tokenType == m_types.STRUCT) {
                if (parserState.currentScope.resolution == moduleNamedEq || parserState.currentScope.resolution == moduleNamedSignature) {
                    parserState.end();
                }
                parserState.currentScope = markScope(builder, parserState.scopes, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.STRUCT);
            }

            // Starts an open
            else if (tokenType == m_types.OPEN) {
                endLikeSemi(parserState);
                parserState.currentScope = markScope(builder, parserState.scopes, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN);
            }

            // Starts an include
            else if (tokenType == m_types.INCLUDE) {
                endLikeSemi(parserState);
                parserState.currentScope = markScope(builder, parserState.scopes, include, m_types.INCLUDE_EXPRESSION, startExpression, m_types.INCLUDE);
            }

            // Starts an external
            else if (tokenType == m_types.EXTERNAL) {
                endLikeSemi(parserState);
                parserState.currentScope = markScope(builder, parserState.scopes, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL);
            }

            // Starts a type
            else if (tokenType == m_types.TYPE) {
                if (parserState.currentScope.resolution != module) {
                    endLikeSemi(parserState);
                    parserState.currentScope = markScope(builder, parserState.scopes, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE);
                }
            }

            // Starts a module
            else if (tokenType == m_types.MODULE) {
                parseModule(builder, parserState);
            }

            // Starts a let
            else if (tokenType == m_types.LET) {
                parseLet(builder, parserState);
            }

            // Starts a val
            else if (tokenType == m_types.VAL) {
                parseVal(builder, parserState);
            }

            // Starts an exception
            else if (tokenType == m_types.EXCEPTION) {
                endLikeSemi(parserState);
                parserState.currentScope = markScope(builder, parserState.scopes, exception, m_types.EXCEPTION_EXPRESSION, startExpression, m_types.EXCEPTION);
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

    private ParserScope endLikeSemi(ParserState parserState) {
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

        return parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
    }
}
