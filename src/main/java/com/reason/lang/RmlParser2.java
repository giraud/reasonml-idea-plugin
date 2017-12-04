package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeEnum.any;
import static com.reason.lang.ParserScopeType.*;
import static com.reason.lang.RmlTypes.*;

public class RmlParser2 extends CommonParser {

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

            if (tokenType == SEMI) {
                // End current start-expression scope
                ParserScope startScope = endScopesUntilStartExpression(scopes);
                if (startScope != null) {
                    builder.advanceLexer();
                    dontMove = true;
                    scopes.pop();
                    startScope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // =
            else if (tokenType == EQ) {
                if (currentScope.resolution == letNamed) {
                    currentScope.resolution = letNamedEq;
                    builder.advanceLexer();
                    dontMove = true;
                    currentScope = markScope(builder, scopes, letBody, LET_BINDING, groupExpression);
                } else if (currentScope.resolution == tagProperty) {
                    currentScope.resolution = tagPropertyEq;
                } else if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedEq;
                } else if (currentScope.resolution == typeNamed) {
                    currentScope.resolution = typeNamedEq;
                }
            }

            // ( ... )
            else if (tokenType == LPAREN) {
                if (currentScope.resolution == letNamedEq) {
                    // function parameters
                    currentScope = markScope(builder, scopes, letParameters, LET_FUN_PARAMS);
                }
            } else if (tokenType == RPAREN) {
                if (currentScope.resolution == letParameters) {
                    builder.advanceLexer();
                    dontMove = true;

                    if (!scopes.empty()) {
                        ParserScope scope = scopes.pop();
                        scope.end();
                        if (!scopes.empty()) {
                            currentScope = scopes.peek();
                            currentScope.resolution = letNamedEqParameters;
                        }
                    }
                }
            }

            // { ... }
            else if (tokenType == LBRACE) {
                // end all on going scopes
                endScopes(scopes);

                if (currentScope.resolution == typeNamedEq) {
                    currentScope = markScope(builder, scopes, objectBinding, OBJECT_EXPR, scopeExpression);
                } else if (currentScope.resolution == moduleNamedEq) {
                    currentScope = markScope(builder, scopes, moduleBinding, SCOPED_EXPR, scopeExpression);
                } else {
                    IElementType nextTokenType = builder.lookAhead(1);
                    if (nextTokenType == DOTDOTDOT) {
                        // object destructuring
                        currentScope = markScope(builder, scopes, objectBinding, OBJECT_EXPR, scopeExpression);
                    } else {
                        currentScope = markScope(builder, scopes, any, null, scopeExpression);
                    }
                }
            } else if (tokenType == RBRACE) {
                builder.advanceLexer();
                dontMove = true;

                ParserScope scope = endScopes(scopes);
                if (scope != null && scope.scopeType == scopeExpression) {
                    scopes.pop().end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // [ ... ]
            else if (tokenType == LBRACKET) {
                IElementType nextTokenType = builder.rawLookup(1);
                if (nextTokenType == ARROBASE) {
                    // This is an annotation
                    currentScope = markScope(builder, scopes, annotation, ANNOTATION_EXPRESSION, startExpression);
                }
            } else if (tokenType == RBRACKET) { // same than rbrace
                ParserScope scope = null;

                // Loop on all scopes until a scoped expression is found
                if (!scopes.empty()) {
                    scope = scopes.pop();
                    while (scope != null && scope.scopeType != scopeExpression && scope.scopeType != startExpression) {
                        scope.end();
                        scope = scopes.empty() ? null : scopes.pop();
                    }
                }

                if (scope != null) {
                    builder.advanceLexer();
                    dontMove = true;
                    scope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            //
            else if (tokenType == ARROW) {
                if (currentScope.resolution == letNamedEqParameters) {
                    builder.advanceLexer();
                    dontMove = true;
                    currentScope = markScope(builder, scopes, letFunBody, LET_BINDING, groupExpression);
                }
            }

            //
            else if (tokenType == PIPE) {
                if (currentScope.resolution == typeNamedEq) {
                    currentScope = markScope(builder, scopes, typeNamedEqPatternMatch, PATTERN_MATCH_EXPR, scopeExpression);
                }
            }

            //
            else if (tokenType == LIDENT) {
                if (currentScope.resolution == type) {
                    builder.remapCurrentToken(TYPE_CONSTR_NAME);
                    currentScope.resolution = typeNamed;
                    currentScope.complete = true;
                }
                if (currentScope.resolution == let) {
                    builder.remapCurrentToken(VALUE_NAME);
                    currentScope.resolution = letNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == startTag) {
                    // This is a property
                    builder.remapCurrentToken(PROPERTY_NAME);
                    currentScope = markScope(builder, scopes, tagProperty, TAG_PROPERTY);
                }

            } else if (tokenType == UIDENT) {
                if (currentScope.resolution == open) {
                    // It is a module name/path
                    builder.remapCurrentToken(MODULE_NAME);
                    currentScope = markScope(builder, scopes, openModulePath, MODULE_PATH);
                } else if (currentScope.resolution == module) {
                    builder.remapCurrentToken(MODULE_NAME);
                    currentScope.resolution = moduleNamed;
                    currentScope.complete = true;
                }
            } else if (tokenType == LT) {
                // Can be a symbol or a JSX tag
                IElementType nextTokenType = builder.rawLookup(1);
                if (nextTokenType == LIDENT || nextTokenType == UIDENT) {
                    // Surely a tag
                    builder.remapCurrentToken(TAG_LT);
                    currentScope = markScope(builder, scopes, startTag, TAG_START);

                    builder.advanceLexer();
                    dontMove = true;
                    builder.remapCurrentToken(TAG_NAME);
                } else if (nextTokenType == SLASH) {
                    builder.remapCurrentToken(TAG_LT);
                    currentScope = markScope(builder, scopes, closeTag, TAG_CLOSE);
                }
            } else if (tokenType == GT || tokenType == TAG_AUTO_CLOSE) {
                if (currentScope.tokenType == TAG_PROPERTY) {
                    currentScope.end();
                    // factorise ?
                    scopes.pop();
                    if (!scopes.empty()) {
                        currentScope = scopes.peek();
                    }
                }

                if (currentScope.resolution == startTag || currentScope.resolution == closeTag) {
                    builder.remapCurrentToken(TAG_GT);
                    builder.advanceLexer();
                    dontMove = true;
                    currentScope.end();
                    scopes.pop();
                    if (!scopes.empty()) {
                        currentScope = scopes.peek();
                    }
                }
            } else if (tokenType == ARROBASE) {
                if (currentScope.resolution == annotation) {
                    currentScope = markScope(builder, scopes, annotationName, ANNOTATION_NAME);
                }
            }

            // Starts an open
            else if (tokenType == OPEN) {
                // clear incorrect scopes
                if (!scopes.empty()) {
                    ParserScope latestScope = scopes.peek();
                    while (latestScope != null && latestScope.resolution != file) {
                        ParserScope scope = scopes.pop();
                        scope.end();
                        latestScope = getLatestScope(scopes);
                    }
                }

                currentScope = markScope(builder, scopes, open, OPEN_EXPRESSION, startExpression);
            }

            // Starts a type
            else if (tokenType == TYPE) {
                // clear scopes
                endScopes(scopes);

                currentScope = markScope(builder, scopes, type, TYPE_EXPRESSION, startExpression);
                currentScope.complete = false;
            }

            // Starts a module
            else if (tokenType == MODULE) {
                if (currentScope.resolution != annotationName) {
                    // clear scopes
                    endScopes(scopes);

                    currentScope = markScope(builder, scopes, module, MODULE_EXPRESSION, startExpression);
                    currentScope.complete = false;
                }
            }

            // Starts a let
            else if (tokenType == LET) {
                // clear scopes
                endScopes(scopes);

                currentScope = markScope(builder, scopes, let, LET_EXPRESSION, startExpression);
                currentScope.complete = false;
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
