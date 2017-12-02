package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.RmlTypes.*;

public class RmlParser2 implements PsiParser, LightPsiParser {

    @NotNull
    public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        parseLight(elementType, builder);
        return builder.getTreeBuilt();
    }

    public void parseLight(IElementType elementType, PsiBuilder builder) {
        boolean r;
        //builder.setDebugMode(true);
        builder = adapt_builder_(elementType, builder, this, null);
        Marker m = enter_section_(builder, 0, _COLLAPSE_, null);
        r = reasonFile(builder);
        exit_section_(builder, 0, m, elementType, r, true, TRUE_CONDITION);
    }

    private boolean reasonFile(PsiBuilder builder) {
        if (!recursion_guard_(builder, 1, "reasonFile")) {
            return false;
        }

        ParserScope fileScope = new ParserScope(file, FILE_MODULE, builder.mark());

        Stack<ParserScope> scopes = new Stack<>();
        ParserScope currentScope = fileScope;
        boolean dontMove = false;

        int c = current_position_(builder);
        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == SEMI) {
                ParserScope scope = null;

                // Loop on all scopes until a top level expression is found
                if (!scopes.empty()) {
                    scope = scopes.pop();
//                    while (scope != null && scope.isExpression) {
//                        scope.done();
//                        scope = scopes.empty() ? null : scopes.pop();
//                    }
                }

                if (scope != null) {
                    scope.done();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            } else if (tokenType == EQ) {
                if (currentScope.resolution == letNamed) {
                    currentScope.resolution = letNamedEq;
                }
                else if (currentScope.resolution == tagProperty) {
                    currentScope.resolution = tagPropertyEq;
                }
                else if (currentScope.resolution == moduleNamed) {
                    currentScope.resolution = moduleNamedEq;
                }
                else if (currentScope.resolution == typeNamed) {
                    currentScope.resolution = typeNamedEq;
                }
            } else if (tokenType == LPAREN) {
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
                        scope.done();
                        if (!scopes.empty()) {
                            currentScope = scopes.peek();
                            currentScope.resolution = letNamedEqParameters;
                        }
                    }
                }
            }
            else if (tokenType == LBRACE) {
                currentScope = markScope(builder, scopes, moduleBinding, SCOPED_EXPR);
            }
            else if (tokenType == RBRACE) {
                ParserScope scope = null;

                // Loop on all scopes until a scoped expression is found
                if (!scopes.empty()) {
                    scope = scopes.pop();
                    while (scope != null && scope.tokenType != SCOPED_EXPR) {
                        scope.end();
                        scope = scopes.empty() ? null : scopes.pop();
                    }
                }

                if (scope != null) {
                    builder.advanceLexer();
                    dontMove = true;
                    scope.done();
                }

                currentScope = scopes.empty() ? fileScope : scopes.peek();
            }
            else if (tokenType == ARROW) {
                if (currentScope.resolution == letNamedEqParameters) {
                    builder.advanceLexer();
                    dontMove = true;
                    currentScope = markScope(builder, scopes, letFunBody, LET_BINDING);
                }
            } else if (tokenType == LIDENT) {
                if (currentScope.resolution == type) {
                    builder.remapCurrentToken(TYPE_CONSTR_NAME);
                    currentScope.resolution = typeNamed;
                    currentScope.complete = true;
                }
                if (currentScope.resolution == let) {
                    builder.remapCurrentToken(VALUE_NAME);
                    currentScope.resolution = letNamed;
                    currentScope.complete = true;
                }
                else if (currentScope.resolution == startTag) {
                    // This is a property
                    builder.remapCurrentToken(PROPERTY_NAME);
                    currentScope = markScope(builder, scopes, tagProperty, TAG_PROPERTY);
                }

            }
            else if (tokenType == UIDENT) {
                if (currentScope.resolution == open) {
                    // It is a module name/path
                    builder.remapCurrentToken(MODULE_NAME);
                    currentScope = markScope(builder, scopes, openModulePath, MODULE_PATH);
                    currentScope.includeSemi = false;
                }
                else if (currentScope.resolution == module) {
                    builder.remapCurrentToken(MODULE_NAME);
                    currentScope.resolution = moduleNamed;
                    currentScope.complete = true;
                }
            }
            else if (tokenType == LT) {
                // Can be a symbol or a JSX tag
                IElementType nextTokenType = builder.rawLookup(1);
                if (nextTokenType == LIDENT || nextTokenType == UIDENT) {
                    // Surely a tag
                    builder.remapCurrentToken(TAG_LT);
                    currentScope = markScope(builder, scopes, startTag, TAG_START);

                    builder.advanceLexer();
                    dontMove = true;
                    builder.remapCurrentToken(TAG_NAME);
                }
                else if (nextTokenType == SLASH) {
                    builder.remapCurrentToken(TAG_LT);
                    currentScope = markScope(builder, scopes, closeTag, TAG_CLOSE);
                }
            }
            else if (tokenType == GT || tokenType == TAG_AUTO_CLOSE) {
                if (currentScope.tokenType == TAG_PROPERTY) {
                    currentScope.done();
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
                    currentScope.done();
                    scopes.pop();
                    if (!scopes.empty()) {
                        currentScope = scopes.peek();
                    }
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

                currentScope = markScope(builder, scopes, open, OPEN_EXPRESSION);
            }

            // Starts a type
            else if (tokenType == TYPE) {
                // clear scopes
                endScopes(scopes);

                currentScope = markScope(builder, scopes, type, TYPE_EXPRESSION);
            }

            // Starts a module
            else if (tokenType == MODULE) {
                // clear scopes
                endScopes(scopes);

                currentScope = markScope(builder, scopes, module, MODULE_EXPRESSION);
            }

            // Starts a let
            else if (tokenType == LET) {
                // clear scopes
                endScopes(scopes);

                currentScope = markScope(builder, scopes, let, LET_EXPRESSION);
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

        // if we have a scope at last position in file, wihtout SEMI, we need to handle it here
        if (!scopes.empty()) {
            ParserScope scope = scopes.pop();
            while (scope != null) {
                scope.end();
                scope = scopes.empty() ? null : scopes.pop();
            }
        }

        fileScope.done();
        return true;
    }

    private void endScopes(Stack<ParserScope> scopes) {
        if (!scopes.empty()) {
            ParserScope latestScope = scopes.peek();
            while (latestScope != null && latestScope.resolution != file && latestScope.tokenType != SCOPED_EXPR) {
                ParserScope scope = scopes.pop();
                scope.end();
                latestScope = getLatestScope(scopes);
            }
        }
    }

    @Nullable
    private ParserScope getLatestScope(Stack<ParserScope> scopes) {
        return scopes.empty() ? null : scopes.peek();
    }

    private ParserScope markScope(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType) {
        ParserScope currentScope = new ParserScope(resolution, tokenType, builder.mark());
        scopes.push(currentScope);
        return currentScope;
    }


    private static class WhitespaceNotifier {
        private boolean m_skipped;

        WhitespaceNotifier(boolean skipped) {
            m_skipped = skipped;
        }

        @SuppressWarnings("unused")
        void notify(IElementType type, int start, int end) {
            m_skipped = true;
        }

        boolean isSkipped() {
            return m_skipped;
        }

        void setSkipped() {
            m_skipped = false;
        }
    }
}
