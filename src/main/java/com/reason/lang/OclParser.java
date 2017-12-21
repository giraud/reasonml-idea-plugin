package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.*;
import static com.reason.lang.RmlTypes.*;

public class OclParser extends CommonParser {
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
            if (tokenType == SEMI) {
                // A SEMI operator ends the start expression, not the group or scope
                ParserScope scope = end(scopes);
                if (scope != null && scope.scopeType == startExpression) {
                    scopes.pop();
                    scope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // in
            else if (tokenType == IN) {
                // End current start-expression scope
                ParserScope scope = endUntilStart(scopes);
                if (scope != null && scope.scopeType == startExpression) {
                    scopes.pop();
                    scope.end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // end (like a })
            else if (tokenType == END) {
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
            else if (tokenType == EQ) {
                if (currentScope.resolution == typeNamed) {
                    currentScope.resolution = typeNamedEq;
                } else if (currentScope.resolution == letNamed) {
                    currentScope.resolution = letNamedEq;
                    builder.advanceLexer();
                    dontMove = true;
                    currentScope = markScope(builder, scopes, letNamedEq, LET_BINDING, groupExpression, EQ);
                    currentScope.complete = true;
                } else if (currentScope.resolution == tagProperty) {
                    currentScope.resolution = tagPropertyEq;
                } else if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedEq;
                    currentScope.complete = true;
                }
            }

            // :
            else if (tokenType == COLON) {
                if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedColon;
                    currentScope.complete = true;
                }
            }

            // ( ... )
            else if (tokenType == LPAREN) {
                end(scopes);
                currentScope = markScope(builder, scopes, paren, SCOPED_EXPR, scopeExpression, LPAREN);
            } else if (tokenType == RPAREN) {
                ParserScope scope = endUntilScopeExpression(scopes, LPAREN);

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
            else if (tokenType == LBRACE) {
                end(scopes);
                currentScope = markScope(builder, scopes, brace, SCOPED_EXPR, scopeExpression, LBRACE);
            } else if (tokenType == RBRACE) {
                ParserScope scope = endUntilScopeExpression(scopes, LBRACE);

                builder.advanceLexer();
                dontMove = true;

                if (scope != null) {
                    scope.complete = true;
                    scopes.pop().end();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }

            // [ ... ]
            else if (tokenType == LBRACKET) {
                IElementType nextTokenType = builder.rawLookup(1);
                if (nextTokenType == ARROBASE) {
                    // This is an annotation
                    currentScope = markScope(builder, scopes, annotation, ANNOTATION_EXPRESSION, scopeExpression, LBRACKET);
                } else {
                    currentScope = markScope(builder, scopes, bracket, SCOPED_EXPR, scopeExpression, LBRACKET);
                }
            } else if (tokenType == RBRACKET) {
                ParserScope scope = endUntilScopeExpression(scopes, LBRACKET);

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
            else if (tokenType == LIDENT) {
                if (currentScope.resolution == type) {
                    builder.remapCurrentToken(TYPE_CONSTR_NAME);
                    currentScope.resolution = typeNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == external) {
                    builder.remapCurrentToken(VALUE_NAME);
                    currentScope.resolution = externalNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == let) {
                    builder.remapCurrentToken(VALUE_NAME);
                    currentScope.resolution = letNamed;
                    currentScope.complete = true;
                } else if (currentScope.resolution == val) {
                    builder.remapCurrentToken(VALUE_NAME);
                    currentScope.resolution = valNamed;
                    currentScope.complete = true;
                    //                } else if (currentScope.resolution == startTag) {
                    //                    // This is a property
                    //                    end(scopes);
                    //                    builder.remapCurrentToken(PROPERTY_NAME);
                    //                    currentScope = markScope(builder, scopes, tagProperty, TAG_PROPERTY, groupExpression, LIDENT);
                    //                    currentScope.complete = true;
                }
            } else if (tokenType == UIDENT) {
                if (currentScope.resolution == open) {
                    // It is a module name/path
                    currentScope.complete = true;
                    builder.remapCurrentToken(VALUE_NAME);
                    currentScope = markComplete(builder, scopes, openModulePath, MODULE_PATH);
                    dontMove = advance(builder, MODULE_NAME);
                } else if (currentScope.resolution == include) {
                    // It is a module name/path
                    currentScope.complete = true;
                    builder.remapCurrentToken(MODULE_NAME);
                    currentScope = markComplete(builder, scopes, openModulePath, MODULE_PATH);
                } else if (currentScope.resolution == exception) {
                    currentScope.complete = true;
                    builder.remapCurrentToken(VALUE_NAME);
                    dontMove = markToken(builder, EXCEPTION_NAME);
                    currentScope.resolution = exceptionNamed;
                } else if (currentScope.resolution == module) {
                    // Module definition
                    builder.remapCurrentToken(VALUE_NAME);
                    dontMove = markToken(builder, MODULE_NAME);
                    currentScope.resolution = moduleNamed;
                }
            }

            // module signature
            else if (tokenType == SIG) {
                if (currentScope.resolution == moduleNamedEq || currentScope.resolution == moduleNamedColon) {
                    end(scopes);
                    currentScope = markScope(builder, scopes, moduleSignature, SIG_SCOPE, scopeExpression, SIG);
                }
            }

            // module body
            else if (tokenType == STRUCT) {
                if (currentScope.resolution == moduleNamedEq) {
                    end(scopes);
                    currentScope = markScope(builder, scopes, moduleBinding, SCOPED_EXPR, scopeExpression, STRUCT);
                }
            }

            //
            else if (tokenType == LT) {
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
            } else if (tokenType == GT || tokenType == TAG_AUTO_CLOSE) {
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
            } else if (tokenType == ARROBASE) {
                //                if (currentScope.resolution == annotation) {
                //                    currentScope.complete = true;
                //                    currentScope = mark(builder, scopes, annotationName, ANNOTATION_NAME, any);
                //                    currentScope.complete = true;
                //                }
            }

            // Starts an open
            else if (tokenType == OPEN) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, open, OPEN_EXPRESSION, startExpression, OPEN);
            }

            // Starts an include
            else if (tokenType == INCLUDE) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, include, INCLUDE_EXPRESSION, startExpression, INCLUDE);
            }

            // Starts an external
            else if (tokenType == EXTERNAL) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, external, EXTERNAL_EXPRESSION, startExpression, EXTERNAL);
            }

            // Starts a type
            else if (tokenType == TYPE) {
                if (currentScope.resolution != module) {
                    endLikeSemi(previousTokenType, scopes, fileScope);
                    currentScope = markScope(builder, scopes, type, TYPE_EXPRESSION, startExpression, TYPE);
                }
            }

            // Starts a module
            else if (tokenType == MODULE) {
                if (currentScope.resolution != annotationName) {
                    endLikeSemi(previousTokenType, scopes, fileScope);
                    currentScope = markScope(builder, scopes, module, MODULE_EXPRESSION, startExpression, MODULE);
                }
            }

            // Starts a let
            else if (tokenType == LET) {
                if (previousTokenType != EQ) {
                    endLikeSemi(previousTokenType, scopes, fileScope);
                }
                currentScope = markScope(builder, scopes, let, LET_EXPRESSION, startExpression, LET);
            }

            // Starts a val
            else if (tokenType == VAL) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, val, VAL_EXPRESSION, startExpression, VAL);
            }

            // Starts an exception
            else if (tokenType == EXCEPTION) {
                endLikeSemi(previousTokenType, scopes, fileScope);
                currentScope = markScope(builder, scopes, exception, EXCEPTION_EXPRESSION, startExpression, EXCEPTION);
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
        if (previousTokenType != IN && previousTokenType != SIG && previousTokenType != STRUCT) {
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
