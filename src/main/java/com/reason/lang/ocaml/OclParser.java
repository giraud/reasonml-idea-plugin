package com.reason.lang.ocaml;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.*;

public class OclParser extends CommonParser {

    OclParser() {
        super(OclLanguage.INSTANCE, OclTypes.INSTANCE);
    }

    @Override
    protected void parseFile(PsiBuilder builder, Stack<ParserScope> scopes, ParserScope fileScope) {
        ParserScope currentScope = fileScope;
        boolean dontMove = false;
        IElementType tokenType = null;
        IElementType previousTokenType = null;

        int c = current_position_(builder);
        while (true) {
            previousTokenType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // ;
            if (tokenType == m_types.SEMI) {
                // A SEMI operator ends the start expression, not the group or scope
                ParserScope scope = end(scopes);
                if (scope != null && scope.scopeType == startExpression) {
                    scopes.pop();
                    scope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // in
            else if (tokenType == m_types.IN) {
                // End current start-expression scope
                ParserScope scope = endUntilStart(scopes);
                if (scope != null && scope.scopeType == startExpression) {
                    scopes.pop();
                    scope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // end (like a })
            else if (tokenType == m_types.END) {
                ParserScope scope = endUntilScopeExpression(scopes, null);

                builder.advanceLexer();
                dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    scopes.pop().end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // =
            else if (tokenType == m_types.EQ) {
                if (currentScope.resolution == typeNamed) {
                    currentScope.resolution = typeNamedEq;
                } else if (currentScope.resolution == letNamed) {
                    currentScope.resolution = letNamedEq;
                    builder.advanceLexer();
                    dontMove = true;
                    currentScope = markScope(builder, scopes, letNamedEq, m_types.LET_BINDING, groupExpression, m_types.EQ);
                    currentScope.complete = true;
                } else if (currentScope.resolution == tagProperty) {
                    currentScope.resolution = tagPropertyEq;
                } else if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedEq;
                    currentScope.complete = true;
                }
            }

            // :
            else if (tokenType == m_types.COLON) {
                if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedColon;
                    currentScope.complete = true;
                }
            }

            // ( ... )
            else if (tokenType == m_types.LPAREN) {
                end(scopes);
                currentScope = markScope(builder, scopes, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN);
            } else if (tokenType == m_types.RPAREN) {
                ParserScope scope = endUntilScopeExpression(scopes, m_types.LPAREN);

                builder.advanceLexer();
                dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    scopes.pop().end();
                    scope = getLatestScope(scopes);
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // { ... }
            else if (tokenType == m_types.LBRACE) {
                end(scopes);
                currentScope = markScope(builder, scopes, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
            } else if (tokenType == m_types.RBRACE) {
                ParserScope scope = endUntilScopeExpression(scopes, m_types.LBRACE);

                builder.advanceLexer();
                dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    scopes.pop().end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // [ ... ]
            else if (tokenType == m_types.LBRACKET) {
                IElementType nextTokenType = builder.rawLookup(1);
                if (nextTokenType == m_types.ARROBASE) {
                    // This is an annotation
                    currentScope = markScope(builder, scopes, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET);
                } else {
                    currentScope = markScope(builder, scopes, bracket, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACKET);
                }
            } else if (tokenType == m_types.RBRACKET) {
                ParserScope scope = endUntilScopeExpression(scopes, m_types.LBRACKET);

                builder.advanceLexer();
                dontMove = true;

                if (scope != null) {
                    if (scope.resolution != annotation) {
                        scope.complete = true;
                    }
                    scopes.pop().end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            //
            //            else if (tokenType == ARROW) {
            //                builder.advanceLexer();
            //                dontMove = true;
            //            }

            //
            //            else if (tokenType == PIPE) {
            //                //    if (currentScope.resolution == typeNamedEq) {
            //                //        currentScope = markScope(builder, scopes, typeNamedEqPatternMatch, PATTERN_MATCH_EXPR, scopeExpression);
            //                //    }
            //            }

            //
            else if (tokenType == m_types.LIDENT) {
                if (currentScope.resolution == type) {
                    builder.remapCurrentToken(m_types.TYPE_CONSTR_NAME);
                    currentScope.resolution = typeNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == external) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    currentScope.resolution = externalNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == let) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    currentScope.resolution = letNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == val) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    currentScope.resolution = valNamed;
                    currentScope.complete = true;
                    //                } else if (currentScope.resolution == startTag) {
                    //                    // This is a property
                    //                    end(scopes);
                    //                    builder.remapCurrentToken(PROPERTY_NAME);
                    //                    currentScope = markScope(builder, scopes, tagProperty, TAG_PROPERTY, groupExpression, LIDENT);
                    //                    currentScope.complete = true;
                }
            } else if (tokenType == m_types.UIDENT) {
                if (currentScope.resolution == open) {
                    // It is a module name/path
                    currentScope.complete = true;
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    currentScope = markComplete(builder, scopes, openModulePath, m_types.MODULE_PATH);
                    dontMove = advance(builder, m_types.MODULE_NAME);
                } else if (currentScope.resolution == include) {
                    // It is a module name/path
                    currentScope.complete = true;
                    builder.remapCurrentToken(m_types.MODULE_NAME);
                    currentScope = markComplete(builder, scopes, openModulePath, m_types.MODULE_PATH);
                } else if (currentScope.resolution == exception) {
                    currentScope.complete = true;
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    dontMove = markToken(builder, m_types.EXCEPTION_NAME);
                    currentScope.resolution = exceptionNamed;
                } else if (currentScope.resolution == module) {
                    // Module definition
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    dontMove = markToken(builder, m_types.MODULE_NAME);
                    currentScope.resolution = moduleNamed;
                }
            }

            // module signature
            else if (tokenType == m_types.SIG) {
                if (currentScope.resolution == moduleNamedEq || currentScope.resolution == moduleNamedColon) {
                    end(scopes);
                    currentScope = markScope(builder, scopes, moduleSignature, m_types.SIG_SCOPE, scopeExpression, m_types.SIG);
                }
            }

            // module body
            else if (tokenType == m_types.STRUCT) {
                if (currentScope.resolution == moduleNamedEq) {
                    end(scopes);
                    currentScope = markScope(builder, scopes, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.STRUCT);
                }
            }

            //
            else if (tokenType == m_types.LT) {
                //                // Can be a symbol or a JSX tag
                //                IElementType nextTokenType = builder.rawLookup(1);
                //                if (nextTokenType == LIDENT || nextTokenType == UIDENT) {
                //                    // Surely a tag
                //                    builder.remapCurrentToken(TAG_LT);
                //                    currentScope = markScope(builder, scopes, startTag, TAG_START, groupExpression, TAG_LT);
                //                    currentScope.complete = true;
                //
                //                    builder.advanceLexer();
                //                    dontMove = true;
                //                    builder.remapCurrentToken(TAG_NAME);
                //                } else if (nextTokenType == SLASH) {
                //                    builder.remapCurrentToken(TAG_LT);
                //                    currentScope = markScope(builder, scopes, closeTag, TAG_CLOSE, any, TAG_LT);
                //                    currentScope.complete = true;
                //                }
            } else if (tokenType == m_types.GT || tokenType == m_types.TAG_AUTO_CLOSE) {
                //                if (currentScope.tokenType == TAG_PROPERTY) {
                //                    currentScope.end();
                //                    scopes.pop();
                //                    currentScope = scopes.empty() ? fileScope : scopes.peek();
                //                }
                //
                //                if (currentScope.resolution == startTag || currentScope.resolution == closeTag) {
                //                    builder.remapCurrentToken(TAG_GT);
                //                    builder.advanceLexer();
                //                    dontMove = true;
                //
                //                    currentScope.end();
                //                    scopes.pop();
                //
                //                    currentScope = scopes.empty() ? fileScope : scopes.peek();
                //                }
            } else if (tokenType == m_types.ARROBASE) {
                //                if (currentScope.resolution == annotation) {
                //                    currentScope.complete = true;
                //                    currentScope = mark(builder, scopes, annotationName, ANNOTATION_NAME, any);
                //                    currentScope.complete = true;
                //                }
            }

            // Starts an open
            else if (tokenType == m_types.OPEN) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN);
            }

            // Starts an include
            else if (tokenType == m_types.INCLUDE) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, include, m_types.INCLUDE_EXPRESSION, startExpression, m_types.INCLUDE);
            }

            // Starts an external
            else if (tokenType == m_types.EXTERNAL) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL);
            }

            // Starts a type
            else if (tokenType == m_types.TYPE) {
                if (currentScope.resolution != module) {
                    endLikeSemi(previousTokenType, scopes, fileScope);
                    currentScope = markScope(builder, scopes, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE);
                }
            }

            // Starts a module
            else if (tokenType == m_types.MODULE) {
                if (currentScope.resolution != annotationName) {
                    endLikeSemi(previousTokenType, scopes, fileScope);
                    currentScope = markScope(builder, scopes, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE);
                }
            }

            // Starts a let
            else if (tokenType == m_types.LET) {
                if (previousTokenType != m_types.EQ) {
                    endLikeSemi(previousTokenType, scopes, fileScope);
                }
                currentScope = markScope(builder, scopes, let, m_types.LET_EXPRESSION, startExpression, m_types.LET);
            }

            // Starts a val
            else if (tokenType == m_types.VAL) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, val, m_types.VAL_EXPRESSION, startExpression, m_types.VAL);
            }

            // Starts an exception
            else if (tokenType == m_types.EXCEPTION) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, exception, m_types.EXCEPTION_EXPRESSION, startExpression, m_types.EXCEPTION);
            }

            if (dontMove) {
                dontMove = false;
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "reasonFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }

    private boolean markToken(PsiBuilder builder, IElementType elementType) {
        PsiBuilder.Marker name = builder.mark();
        boolean dontMove = advance(builder);
        name.done(elementType);
        return dontMove;
    }

    private ParserScope endLikeSemi(IElementType previousTokenType, Stack<ParserScope> scopes, ParserScope fileScope) {
        ParserScope scope;
        if (previousTokenType != m_types.IN && previousTokenType != m_types.SIG && previousTokenType != m_types.STRUCT) {
            // force completion of scoped expressions
            scope = endUntilStartForced(scopes);
        } else {
            // End current start-expression scope
            scope = endUntilStart(scopes);
        }

        if (scope != null) {
            if (scope.scopeType == startExpression) {
                scopes.pop();
                scope.end();
            }
        }

        return scopes.empty() ? fileScope : scopes.peek();
    }
}
