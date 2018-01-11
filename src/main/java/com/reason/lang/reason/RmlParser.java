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
        int c = current_position_(builder);
        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == m_types.SEMI) {
                // End current start-expression scope
                ParserScope scope = parserState.endUntilStart();
                if (scope != null && scope.scopeType == startExpression) {
                    builder.advanceLexer();
                    parserState.dontMove = true;
                    parserState.scopes.pop();
                    scope.end();
                }

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // =
            else if (tokenType == m_types.EQ) {
                if (parserState.currentScope.resolution == typeNamed) {
                    parserState.currentScope.resolution = typeNamedEq;
                } else if (parserState.currentScope.resolution == letNamed) {
                    parserState.currentScope.resolution = letNamedEq;
                } else if (parserState.currentScope.resolution == tagProperty) {
                    parserState.currentScope.resolution = tagPropertyEq;
                } else if (parserState.currentScope.resolution == moduleNamed) {
                    parserState.currentScope.resolution = moduleNamedEq;
                    parserState.currentScope.complete = true;
                }
            }

            // ( ... )
            else if (tokenType == m_types.LPAREN) {
                parserState.end();
                if (parserState.currentScope.resolution == letNamedEq) {
                    // function parameters
                    parserState.currentScope = markScope(builder, parserState.scopes, letParameters, m_types.LET_FUN_PARAMS, scopeExpression, m_types.LPAREN);
                } else {
                    parserState.currentScope = markScope(builder, parserState.scopes, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN);
                }
            } else if (tokenType == m_types.RPAREN) {
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

                parserState.currentScope = parserState.scopes.empty() ? parserState.fileScope : parserState.scopes.peek();
            }

            // { ... }
            else if (tokenType == m_types.LBRACE) {
                if (parserState.currentScope.resolution == typeNamedEq) {
                    parserState.currentScope = markScope(builder, parserState.scopes, objectBinding, m_types.OBJECT_EXPR, scopeExpression, m_types.LBRACE);
                } else if (parserState.currentScope.resolution == moduleNamedEq) {
                    parserState.currentScope = markScope(builder, parserState.scopes, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
                } else if (parserState.currentScope.resolution == letNamedEqParameters) {
                    parserState.currentScope = markScope(builder, parserState.scopes, letFunBody, m_types.LET_BINDING, scopeExpression, m_types.LBRACE);
                } else {
                    parserState.end();
                    parserState.currentScope = markScope(builder, parserState.scopes, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
                }
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
                    parserState.currentScope = markScope(builder, parserState.scopes, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET);
                } else if (nextTokenType == m_types.PERCENT) {
                    parserState.currentScope = markScope(builder, parserState.scopes, macro, m_types.MACRO_EXPRESSION, scopeExpression, m_types.LBRACKET);
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
            else if (tokenType == m_types.ARROW) {
                builder.advanceLexer();
                parserState.dontMove = true;
            }

            //
            else if (tokenType == m_types.PIPE) {
                //    if (currentScope.resolution == typeNamedEq) {
                //        currentScope = markScope(builder, scopes, typeNamedEqPatternMatch, PATTERN_MATCH_EXPR, scopeExpression);
                //    }
            }

            //
            else if (tokenType == m_types.LIDENT) {
                if (parserState.currentScope.resolution == type) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    ParserScope scope = markComplete(builder, parserState.scopes, typeNamed, m_types.TYPE_CONSTR_NAME);
                    parserState.dontMove = advance(builder);
                    scope.end();
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
                } else if (parserState.currentScope.resolution == startTag) {
                    // This is a property
                    parserState.end();
                    builder.remapCurrentToken(m_types.PROPERTY_NAME);
                    parserState.currentScope = markScope(builder, parserState.scopes, tagProperty, m_types.TAG_PROPERTY, groupExpression, m_types.LIDENT);
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
                    PsiBuilder.Marker mark = builder.mark();
                    parserState.dontMove = advance(builder);
                    mark.done(m_types.MODULE_NAME);
                } else if (parserState.currentScope.resolution == module) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    ParserScope scope = markComplete(builder, parserState.scopes, moduleNamed, m_types.MODULE_NAME);
                    parserState.dontMove = advance(builder);
                    scope.end();
                    parserState.currentScope.resolution = moduleNamed;
                } else {
                    // !! variant
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    ParserScope scope = markComplete(builder, parserState.scopes, moduleNamed, m_types.MODULE_NAME);
                    parserState.dontMove = advance(builder);
                    scope.end();
                }
            }

            // < ... >
            else if (tokenType == m_types.LT) {
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
            } else if (tokenType == m_types.GT || tokenType == m_types.TAG_AUTO_CLOSE) {
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

            //
            else if (tokenType == m_types.ARROBASE) {
                if (parserState.currentScope.resolution == annotation) {
                    parserState.currentScope.complete = true;
                    parserState.currentScope = markComplete(builder, parserState.scopes, annotationName, m_types.MACRO_NAME);
                }
            }

            //
            else if (tokenType == m_types.PERCENT) {
                if (parserState.currentScope.resolution == macro) {
                    parserState.currentScope.complete = true;
                    parserState.currentScope = markComplete(builder, parserState.scopes, macroName, m_types.MACRO_NAME);
                    parserState.currentScope.complete = true;
                }
            }

            // Starts an open
            else if (tokenType == m_types.OPEN) {
                parserState.end();
                parserState.currentScope = markScope(builder, parserState.scopes, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN);
            }

            // Starts an external
            else if (tokenType == m_types.EXTERNAL) {
                parserState.end();
                parserState.currentScope = markScope(builder, parserState.scopes, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL);
            }

            // Starts a type
            else if (tokenType == m_types.TYPE) {
                parserState.end();
                parserState.currentScope = markScope(builder, parserState.scopes, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE);
            }

            // Starts a module
            else if (tokenType == m_types.MODULE) {
                if (parserState.currentScope.resolution != annotationName) {
                    parserState.end();
                    parserState.currentScope = markScope(builder, parserState.scopes, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE);
                }
            }

            // Starts a let
            else if (tokenType == m_types.LET) {
                parserState.end();
                parserState.currentScope = markScope(builder, parserState.scopes, let, m_types.LET_EXPRESSION, startExpression, m_types.LET);
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

}
