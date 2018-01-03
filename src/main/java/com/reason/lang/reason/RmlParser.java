package com.reason.lang.reason;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScope;
import com.reason.lang.RmlLanguage;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.*;

public class RmlParser extends CommonParser {

    RmlParser() {
        super(RmlLanguage.INSTANCE, RmlTypes.INSTANCE);
    }

    @Override
    protected void parseFile(PsiBuilder builder, Stack<ParserScope> scopes, ParserScope fileScope) {
        ParserScope currentScope = fileScope;
        boolean dontMove = false;

        int c = current_position_(builder);
        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == m_types.SEMI) {
                // End current start-expression scope
                ParserScope scope = endUntilStart(scopes);
                if (scope != null && scope.scopeType == startExpression) {
                    builder.advanceLexer();
                    dontMove = true;
                    scopes.pop();
                    scope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // =
            else if (tokenType == m_types.EQ) {
                if (currentScope.resolution == typeNamed) {
                    currentScope.resolution = typeNamedEq;
                } else if (currentScope.resolution == letNamed) {
                    currentScope.resolution = letNamedEq;
                } else if (currentScope.resolution == tagProperty) {
                    currentScope.resolution = tagPropertyEq;
                } else if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedEq;
                    currentScope.complete = true;
                }
            }

            // ( ... )
            else if (tokenType == m_types.LPAREN) {
                end(scopes);
                if (currentScope.resolution == letNamedEq) {
                    // function parameters
                    currentScope = markScope(builder, scopes, letParameters, m_types.LET_FUN_PARAMS, scopeExpression, m_types.LPAREN);
                } else {
                    currentScope = markScope(builder, scopes, paren, m_types.SCOPED_EXPR, scopeExpression, m_types.LPAREN);
                }
            } else if (tokenType == m_types.RPAREN) {
                ParserScope scope = endUntilScopeExpression(scopes, m_types.LPAREN);

                builder.advanceLexer();
                dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    scopes.pop().end();
                    scope = getLatestScope(scopes);
                    if (scope != null && scope.resolution == letNamedEq) {
                        scope.resolution = letNamedEqParameters;
                    }
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // { ... }
            else if (tokenType == m_types.LBRACE) {
                if (currentScope.resolution == typeNamedEq) {
                    currentScope = markScope(builder, scopes, objectBinding, m_types.OBJECT_EXPR, scopeExpression, m_types.LBRACE);
                } else if (currentScope.resolution == moduleNamedEq) {
                    currentScope = markScope(builder, scopes, moduleBinding, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
                } else if (currentScope.resolution == letNamedEqParameters) {
                    currentScope = markScope(builder, scopes, letFunBody, m_types.LET_BINDING, scopeExpression, m_types.LBRACE);
                } else {
                    end(scopes);
                    currentScope = markScope(builder, scopes, brace, m_types.SCOPED_EXPR, scopeExpression, m_types.LBRACE);
                }
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
                    currentScope = markScope(builder, scopes, annotation, m_types.ANNOTATION_EXPRESSION, scopeExpression, m_types.LBRACKET);
                } else if (nextTokenType == m_types.PERCENT) {
                    currentScope = markScope(builder, scopes, macro, m_types.MACRO_EXPRESSION, scopeExpression, m_types.LBRACKET);
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
            else if (tokenType == m_types.ARROW) {
                builder.advanceLexer();
                dontMove = true;
            }

            //
            else if (tokenType == m_types.PIPE) {
                //    if (currentScope.resolution == typeNamedEq) {
                //        currentScope = markScope(builder, scopes, typeNamedEqPatternMatch, PATTERN_MATCH_EXPR, scopeExpression);
                //    }
            }

            //
            else if (tokenType == m_types.LIDENT) {
                if (currentScope.resolution == type) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    ParserScope scope = markComplete(builder, scopes, typeNamed, m_types.TYPE_CONSTR_NAME);
                    dontMove = advance(builder);
                    scope.end();
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
                } else if (currentScope.resolution == startTag) {
                    // This is a property
                    end(scopes);
                    builder.remapCurrentToken(m_types.PROPERTY_NAME);
                    currentScope = markScope(builder, scopes, tagProperty, m_types.TAG_PROPERTY, groupExpression, m_types.LIDENT);
                    currentScope.complete = true;
                }
            }

            //
            else if (tokenType == m_types.UIDENT) {
                if (currentScope.resolution == open) {
                    // It is a module name/path
                    currentScope.complete = true;
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    currentScope = markComplete(builder, scopes, openModulePath, m_types.MODULE_PATH);
                    PsiBuilder.Marker mark = builder.mark();
                    dontMove = advance(builder);
                    mark.done(m_types.MODULE_NAME);
                } else if (currentScope.resolution == module) {
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    ParserScope scope = markComplete(builder, scopes, moduleNamed, m_types.MODULE_NAME);
                    dontMove = advance(builder);
                    scope.end();
                    currentScope.resolution = moduleNamed;
                } else {
                    // !! variant
                    builder.remapCurrentToken(m_types.VALUE_NAME);
                    ParserScope scope = markComplete(builder, scopes, moduleNamed, m_types.MODULE_NAME);
                    dontMove = advance(builder);
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
                    currentScope = markScope(builder, scopes, startTag, m_types.TAG_START, groupExpression, m_types.TAG_LT);
                    currentScope.complete = true;

                    builder.advanceLexer();
                    dontMove = true;
                    builder.remapCurrentToken(m_types.TAG_NAME);
                } else if (nextTokenType == m_types.SLASH) {
                    builder.remapCurrentToken(m_types.TAG_LT);
                    currentScope = markScope(builder, scopes, closeTag, m_types.TAG_CLOSE, any, m_types.TAG_LT);
                    currentScope.complete = true;
                }
            } else if (tokenType == m_types.GT || tokenType == m_types.TAG_AUTO_CLOSE) {
                if (currentScope.tokenType == m_types.TAG_PROPERTY) {
                    currentScope.end();
                    scopes.pop();
                    currentScope = scopes.empty() ? fileScope : scopes.peek();
                }

                if (currentScope.resolution == startTag || currentScope.resolution == closeTag) {
                    builder.remapCurrentToken(m_types.TAG_GT);
                    builder.advanceLexer();
                    dontMove = true;

                    currentScope.end();
                    scopes.pop();

                    currentScope = scopes.empty() ? fileScope : scopes.peek();
                }
            }

            //
            else if (tokenType == m_types.ARROBASE) {
                if (currentScope.resolution == annotation) {
                    currentScope.complete = true;
                    currentScope = markComplete(builder, scopes, annotationName, m_types.MACRO_NAME);
                }
            }

            //
            else if (tokenType == m_types.PERCENT) {
                if (currentScope.resolution == macro) {
                    currentScope.complete = true;
                    currentScope = markComplete(builder, scopes, macroName, m_types.MACRO_NAME);
                    currentScope.complete = true;
                }
            }

            // Starts an open
            else if (tokenType == m_types.OPEN) {
                end(scopes);
                currentScope = markScope(builder, scopes, open, m_types.OPEN_EXPRESSION, startExpression, m_types.OPEN);
            }

            // Starts an external
            else if (tokenType == m_types.EXTERNAL) {
                end(scopes);
                currentScope = markScope(builder, scopes, external, m_types.EXTERNAL_EXPRESSION, startExpression, m_types.EXTERNAL);
            }

            // Starts a type
            else if (tokenType == m_types.TYPE) {
                end(scopes);
                currentScope = markScope(builder, scopes, type, m_types.TYPE_EXPRESSION, startExpression, m_types.TYPE);
            }

            // Starts a module
            else if (tokenType == m_types.MODULE) {
                if (currentScope.resolution != annotationName) {
                    end(scopes);
                    currentScope = markScope(builder, scopes, module, m_types.MODULE_EXPRESSION, startExpression, m_types.MODULE);
                }
            }

            // Starts a let
            else if (tokenType == m_types.LET) {
                end(scopes);
                currentScope = markScope(builder, scopes, let, m_types.LET_EXPRESSION, startExpression, m_types.LET);
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

}
