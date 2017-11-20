package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

public class OclParser implements PsiParser, LightPsiParser {

    @NotNull
    public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        parseLight(elementType, builder);
        return builder.getTreeBuilt();
    }

    public void parseLight(IElementType elementType, PsiBuilder builder) {
        boolean r;
        //b.setDebugMode(true);
        builder = adapt_builder_(elementType, builder, this, null);
        Marker m = enter_section_(builder, 0, _COLLAPSE_, null);
        r = reasonFile(builder);
        exit_section_(builder, 0, m, elementType, r, true, TRUE_CONDITION);
    }

    private boolean reasonFile(PsiBuilder builder) {
        if (!recursion_guard_(builder, 1, "reasonFile")) {
            return false;
        }

        int c = current_position_(builder);
        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == RmlTypes.OPEN) {
                openExpression(builder, 1);
            } else if (tokenType == RmlTypes.INCLUDE) {
                includeExpression(builder, 1);
            } else if (tokenType == RmlTypes.TYPE) {
                typeExpression(builder, 1);
            } else if (tokenType == RmlTypes.MODULE) {
                moduleExpression(builder, 1);
            } else if (tokenType == RmlTypes.EXTERNAL) {
                externalExpression(builder, 1);
            } else if (tokenType == RmlTypes.LET) {
                letExpression(builder, 1);
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "reasonFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
        return true;
    }

    // **********
    // OPEN EXCLAMATION_MARK? module_path
    // **********
    private void openExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "openExpression")) {
            return;
        }

        Marker exprMarker = enter_section_(builder);

        IElementType nextTokenType = builder.lookAhead(1);
        if (nextTokenType == RmlTypes.EXCLAMATION_MARK) {
            advance(builder);
        }

        // Continue until another expression is found
        advance(builder);
        modulePath(builder, recLevel + 1);

        exit_section_(builder, exprMarker, RmlTypes.OPEN_EXPRESSION, true);
    }

    // **********
    // TYPE type_name any* EQ (scoped_expression | expr) ;
    // **********
    private void typeExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "type expression")) {
            return;
        }

        // enter type
        Marker exprMarker = enter_section_(builder);

        String constrName = "";

        // type name
        IElementType tokenType = advance(builder);
        if (tokenType != RmlTypes.SEMI) {
            Marker nameMarker = enter_section_(builder);
            constrName = builder.getTokenText();
            builder.advanceLexer();
            exit_section_(builder, nameMarker, RmlTypes.TYPE_CONSTR_NAME, true);
        }

        // anything until EQ or SEMI
        tokenType = builder.getTokenType();
        if (tokenType != RmlTypes.EQ && tokenType != RmlTypes.SEMI) {
            advanceUntil(builder, recLevel++, RmlTypes.EQ);
        }

        tokenType = builder.getTokenType();
        if (tokenType != RmlTypes.SEMI) {
            // =
            if (tokenType == RmlTypes.EQ) {
                builder.advanceLexer();
            }

            tokenType = builder.getTokenType();
            if (tokenType == RmlTypes.LBRACE) {
                // scoped body, anything inside braces
                scopedTypeExpression(builder, recLevel + 1, constrName);
            } else {
                // anything but semi or start expression
                Marker marker = enter_section_(builder);

                while (true) {
                    tokenType = advance(builder);
                    if (RmlTypes.LIDENT.equals(tokenType) && constrName != null && !constrName.isEmpty() && constrName.equals(builder.getTokenText())) {
                        Marker constrMarker = builder.mark();
                        tokenType = advance(builder);
                        exit_section_(builder, constrMarker, RmlTypes.TYPE_CONSTR_NAME, true);
                    }

                    if (isStartExpression(tokenType)) {
                        break;
                    }
                }

                exit_section_(builder, marker, RmlTypes.SCOPED_EXPR, true);
            }
        }

        // end of type
        endExpression(builder);
        exit_section_(builder, exprMarker, RmlTypes.TYPE_EXPRESSION, true);
    }

    // **********
    // INCLUDE module_path (scoped_expression)? SEMI
    // **********
    private void includeExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "include expression")) {
            return;
        }

        // enter
        Marker exprMarker = enter_section_(builder);
        IElementType tokenType = advance(builder);
        if (tokenType != RmlTypes.SEMI) {
            // module path
            modulePath(builder, recLevel + 1);
            tokenType = builder.getTokenType();
            if (tokenType == RmlTypes.LBRACE) {
                scopedExpression(builder, recLevel + 1);
            }
        }

        endExpression(builder);
        exit_section_(builder, exprMarker, RmlTypes.INCLUDE_EXPRESSION, true);
    }

    // **********
    // MODULE TYPE? module_name | SEMI
    //                          | (COLON module_type)? EQ                          (module_alias | scoped_expression) SEMI
    //                          | (LPAREN any RPAREN)+ (COLON module_type)? ARROW
    // **********
    private void moduleExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "module expression")) {
            return;
        }

        // enter module
        Marker moduleMarker = enter_section_(builder);
        IElementType tokenType = advance(builder);
        if (tokenType != RmlTypes.SEMI) {
            // might be a module definition only
            if (tokenType == RmlTypes.TYPE) {
                tokenType = advance(builder);
            }

            // module name
            boolean isNameIncorrect = tokenType != RmlTypes.UIDENT;
            Marker errorMarker = null;

            Marker moduleNameMarker = enter_section_(builder);

            if (isNameIncorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (isNameIncorrect) {
                errorMarker.error("Module name must start with upper case");
            }

            exit_section_(builder, moduleNameMarker, RmlTypes.MODULE_NAME, true);

            tokenType = builder.getTokenType();
            if (tokenType != RmlTypes.SEMI) {
                if (tokenType == RmlTypes.LPAREN) {
                    // module constructor (function)
                    advanceUntil(builder, recLevel + 1, RmlTypes.ARROW);

                    tokenType = builder.getTokenType();
                    if (tokenType == RmlTypes.ARROW) {
                        // module definition
                        tokenType = advance(builder);
                        if (tokenType == RmlTypes.LBRACE) {
                            // scoped body, anything inside braces
                            scopedExpression(builder, recLevel + 1);
                        } else if (tokenType != RmlTypes.SEMI) {
                            // module alias
                            modulePath(builder, recLevel + 1);
                        }
                    }
                } else if (tokenType == RmlTypes.EQ || tokenType == RmlTypes.COLON) {
                    if (tokenType == RmlTypes.COLON) {
                        // module type
                        advanceUntil(builder, recLevel + 1, RmlTypes.EQ);
                        tokenType = builder.getTokenType();
                    }

                    if (tokenType == RmlTypes.EQ) {
                        // module definition
                        tokenType = advance(builder);
                        if (tokenType == RmlTypes.LBRACE) {
                            // scoped body, anything inside braces
                            scopedExpression(builder, recLevel + 1);
                        } else if (tokenType != RmlTypes.SEMI) {
                            // module alias
                            modulePath(builder, recLevel + 1);
                        }
                    }
                }
            }
        }

        endExpression(builder);
        exit_section_(builder, moduleMarker, RmlTypes.MODULE_EXPRESSION, true);
    }

    // ***** UIDENT (DOT UIDENT|scoped_expression)* ( (.*) )::constr
    private void modulePath(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "module path")) {
            return;
        }

        Marker marker = enter_section_(builder);

        // First element must be a module name
        boolean incorrectName = builder.getTokenType() != RmlTypes.UIDENT;
        Marker nameMarker = enter_section_(builder);
        builder.advanceLexer();
        if (incorrectName) {
            //if (ParserOptions.Validation == m_options) {
            //    nameMarker.error(ERR_NAME_UPPERCASE);
            //} else {
            nameMarker.drop();
            //}
        } else {
            exit_section_(builder, nameMarker, RmlTypes.MODULE_NAME, true);

            // Then we can have other modules with dot notation
            while (true) {
                nameMarker = enter_section_(builder);
                IElementType tokenType = builder.getTokenType();

                if (tokenType == RmlTypes.LPAREN) {
                    nameMarker.drop();
                    // Anything until we get to closing paren or semi
                    parenExpression(builder, recLevel + 1);
                    continue;
                } else if (tokenType == RmlTypes.LBRACE) {
                    nameMarker.drop();
                    // scoped body, anything inside braces
                    scopedExpression(builder, recLevel + 1);
                }


                // a semi or scope is found, stop module path exploration
                if (tokenType == RmlTypes.LBRACE || isStartExpression(tokenType)) {
                    nameMarker.drop();
                    break;
                }

                if (tokenType == RmlTypes.DOT) {
                    nameMarker.drop();
                    builder.advanceLexer();
                    continue;
                }

                if (tokenType != RmlTypes.UIDENT) {
                    builder.advanceLexer();
                    //nameMarker.error(ERR_NAME_UPPERCASE);
                    nameMarker.drop();
                    break;
                }

                builder.advanceLexer();
                exit_section_(builder, nameMarker, RmlTypes.MODULE_NAME, true);
            }
        }

        exit_section_(builder, marker, RmlTypes.MODULE_PATH, true);
    }

    // **********
    // EXTERNAL external_name COLON expression* SEMI
    private void externalExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "external expression")) {
            return;
        }

        // enter external
        Marker exprMarker = enter_section_(builder);

        // name
        IElementType tokenType = advance(builder);
        if (tokenType != RmlTypes.SEMI) {
            Marker nameMarker = enter_section_(builder);
            Marker errorMarker = null;
            boolean isNameCorrect = tokenType == RmlTypes.LIDENT;

            if (!isNameCorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (!isNameCorrect) {
                errorMarker.error("External name must start with a lower case");
            }

            exit_section_(builder, nameMarker, RmlTypes.VALUE_NAME, true);
        }

        tokenType = builder.getTokenType();
        // :
        if (tokenType == RmlTypes.COLON) {
            builder.advanceLexer();
        }

        tokenType = builder.getTokenType();
        if (tokenType != RmlTypes.SEMI) {
            // anything but semi
            while (true) {
                tokenType = advance(builder);
                if (tokenType == null || tokenType == RmlTypes.SEMI) {
                    break;
                }
            }
        }

        // end of external
        endExpression(builder);
        exit_section_(builder, exprMarker, RmlTypes.EXTERNAL_EXPRESSION, true);
    }

    // **********
    // NEW!
    // LET REC? MODULE? (destructure|value_name) expression* (EQ (LPAREN expression RPAREN ARROW scoped_expression | (scoped_)expression) SEMI
    //
    // OLD!
    // LET REC? MODULE? (destructure|value_name) expression* (EQ (scoped_)expression | ARROW scoped_expression) SEMI
    // LET REC? MODULE? value_name COLON expression+ SEMI
    // **********
    private void letExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "let expression")) {
            return;
        }

        // enter LET
        Marker exprMarker = enter_section_(builder);

        // Might be recursive
        IElementType tokenType = advance(builder);
        if (tokenType == RmlTypes.REC) {
            builder.advanceLexer();
        }

        // might be a module alias
        tokenType = builder.getTokenType();
        if (tokenType == RmlTypes.MODULE) {
            builder.advanceLexer();
        }

        tokenType = builder.getTokenType();
        if (tokenType != RmlTypes.SEMI) {
            if (tokenType == RmlTypes.UNIT) {
                builder.advanceLexer();
            } else if (tokenType == RmlTypes.LPAREN) {
                // we are dealing with destructuring
                advanceUntil(builder, recLevel + 1, RmlTypes.RPAREN);
                builder.advanceLexer();
            } else if (tokenType == RmlTypes.LBRACE) {
                // we are dealing with destructuring
                advanceUntil(builder, recLevel + 1, RmlTypes.RBRACE);
                builder.advanceLexer();
            } else {
                // value name
                Marker nameMarker = enter_section_(builder);
                builder.advanceLexer();
                exit_section_(builder, nameMarker, RmlTypes.VALUE_NAME, true);
            }
        }

        builder.getTokenType();

        // Anything before EQ|ARROW
        // anything but semi or start expression
        WhitespaceNotifier whitespace = new WhitespaceNotifier();
        builder.setWhitespaceSkippedCallback(whitespace::notify);

        while (true) {
            tokenType = builder.getTokenType();
            if ((whitespace.isSkipped() && (tokenType == RmlTypes.EQ || tokenType == RmlTypes.ARROW)) || isStartExpression(tokenType)) {
                break;
            } else {
                whitespace.setSkipped();
            }
            builder.advanceLexer();
        }

        builder.setWhitespaceSkippedCallback(null);

        tokenType = builder.getTokenType();
        if (RmlTypes.EQ == tokenType || RmlTypes.ARROW == tokenType) {
            boolean isFunction = RmlTypes.ARROW == tokenType;

            builder.advanceLexer();
            Marker bindMarker = enter_section_(builder);

            tokenType = builder.getTokenType();
            if (RmlTypes.LPAREN == tokenType || RmlTypes.UNIT == tokenType) {
                // New function syntax ?
                Marker paramsMarker = enter_section_(builder);
                if (RmlTypes.LPAREN == tokenType) {
                    parenExpression(builder, recLevel + 1);
                } else {
                    builder.advanceLexer();
                }

                tokenType = builder.getTokenType();
                isFunction = RmlTypes.ARROW == tokenType;
                if (isFunction) {
                    bindMarker.drop();
                    exit_section_(builder, paramsMarker, RmlTypes.FUN_PARAMS, true);
                } else {
                    paramsMarker.drop();
                }

                tokenType = advance(builder);

                if (isFunction) {
                    bindMarker = enter_section_(builder);
                }
            }

            if (RmlTypes.LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
            } else if (RmlTypes.IF == tokenType) {
                ifExpression(builder, recLevel + 1, true);
            } else {
                advanceUntilNextStart(builder, recLevel + 1);
            }

            exit_section_(builder, bindMarker, isFunction ? RmlTypes.FUN_BODY : RmlTypes.LET_BINDING, true);
        }

        // end of LET
        endExpression(builder);
        exit_section_(builder, exprMarker, RmlTypes.LET_EXPRESSION, true);

    }

    private void ifExpression(PsiBuilder builder, int recLevel, boolean containsIf) {
        if (!recursion_guard_(builder, recLevel, "if expression")) {
            return;
        }

        IElementType tokenType = builder.getTokenType();

        if (containsIf) {
            tokenType = advance(builder);
            if (tokenType != RmlTypes.LPAREN) {
                //fail(builder, "'(' expected");
                return;
            }

            parenExpression(builder, recLevel + 1);
            tokenType = builder.getTokenType();
        }

        if (tokenType == RmlTypes.LBRACE) {
            scopedExpression(builder, recLevel + 1);
            tokenType = builder.getTokenType();
        }

        if (tokenType == RmlTypes.ELSE) {
            tokenType = advance(builder);
            ifExpression(builder, recLevel + 1, tokenType == RmlTypes.IF);
        }
    }

    // **********
    // Pattern: LBRACE expression* RBRACE
    // **********
    private void scopedExpression(PsiBuilder builder, int recLevel) {
        scopedTypeExpression(builder, recLevel, null);
    }

    private void scopedTypeExpression(PsiBuilder builder, int recLevel, String constrName) {
        if (!recursion_guard_(builder, recLevel, "scoped expression")) {
            return;
        }

        // Mark the start of the scoped expression
        Marker marker = enter_section_(builder);
        builder.advanceLexer();

        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null || tokenType == RmlTypes.RBRACE) {
                break;
            }

            // start expressions ?
            if (tokenType == RmlTypes.OPEN) {
                openExpression(builder, recLevel + 1);
            } else if (tokenType == RmlTypes.INCLUDE) {
                includeExpression(builder, recLevel + 1);
            } else if (tokenType == RmlTypes.TYPE) {
                typeExpression(builder, recLevel + 1);
            } else if (tokenType == RmlTypes.MODULE) {
                moduleExpression(builder, recLevel + 1);
            } else if (tokenType == RmlTypes.EXTERNAL) {
                externalExpression(builder, recLevel + 1);
            } else if (tokenType == RmlTypes.LET) {
                letExpression(builder, recLevel + 1);
            } else if (tokenType == RmlTypes.LBRACE) {
                scopedTypeExpression(builder, recLevel + 1, constrName);
            } else if (tokenType == RmlTypes.LPAREN) {
                parenTypeExpression(builder, recLevel + 1, constrName);
            } else {
                tokenType = advance(builder);
                if (RmlTypes.LIDENT.equals(tokenType) && constrName != null && !constrName.isEmpty() && constrName.equals(builder.getTokenText())) {
                    Marker constrMarker = builder.mark();
                    advance(builder);
                    exit_section_(builder, constrMarker, RmlTypes.TYPE_CONSTR_NAME, true);
                }

            }
        }

        IElementType tokenType = builder.getTokenType();
        if (tokenType == RmlTypes.RBRACE) {
            builder.advanceLexer();
        }

        exit_section_(builder, marker, RmlTypes.SCOPED_EXPR, true);
    }

    private void advanceUntilNextStart(PsiBuilder builder, int recLevel) {
        IElementType tokenType;
        while (true) {
            tokenType = advance(builder);
            if (RmlTypes.LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else if (RmlTypes.LPAREN == tokenType) {
                parenExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            }
            if (isStartExpression(tokenType) || tokenType == RmlTypes.RBRACE) {
                break;
            }
        }
    }


    // **********
    // Pattern: LPAREN {{token-start}} expression* RPAREN {{token-end}}
    // **********
    private void parenExpression(PsiBuilder builder, int recLevel) {
        parenTypeExpression(builder, recLevel, null);
    }

    private void parenTypeExpression(PsiBuilder builder, int recLevel, String constrName) {
        if (!recursion_guard_(builder, recLevel, "skip paren")) {
            return;
        }

        IElementType tokenType;
        while (true) {
            tokenType = advance(builder);

            // If a new scope is found, recursively process it
            if (RmlTypes.LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else if (RmlTypes.LPAREN == tokenType) {
                parenExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else {
                if (RmlTypes.LIDENT.equals(tokenType) && constrName != null && !constrName.isEmpty() && constrName.equals(builder.getTokenText())) {
                    Marker constrMarker = builder.mark();
                    tokenType = advance(builder);
                    exit_section_(builder, constrMarker, RmlTypes.TYPE_CONSTR_NAME, true);
                }
            }

            // Advance until a right paren is found, or nothing else is found
            if (RmlTypes.RPAREN == tokenType || tokenType == null) {
                builder.advanceLexer();
                return;
            }
        }
    }

    private static IElementType advance(PsiBuilder builder) {
        builder.advanceLexer();
        return builder.getTokenType();
    }

    private void advanceUntil(PsiBuilder builder, int recLevel, IElementType nextTokenType) {
        if (!recursion_guard_(builder, recLevel, "advance until")) {
            return;
        }

        IElementType tokenType;
        while (true) {
            tokenType = advance(builder);
            if (RmlTypes.LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else if (RmlTypes.LPAREN == tokenType) {
                parenExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            }

            if (tokenType == null || tokenType == RmlTypes.SEMI || tokenType == nextTokenType) {
                break;
            }
        }
    }

    private static boolean isStartExpression(IElementType tokenType) {
        return tokenType == null || tokenType == RmlTypes.SEMI || tokenType == RmlTypes.MODULE || tokenType == RmlTypes.OPEN || tokenType == RmlTypes.TYPE || tokenType == RmlTypes.LET;
    }

    private void endExpression(PsiBuilder builder) {
        // Last expression in the file can omit semi
        if (builder.eof()) {
            return;
        }

        IElementType tokenType = builder.getTokenType();
        if (tokenType == RmlTypes.SEMI) {
            builder.advanceLexer();
        }
    }

    private static class WhitespaceNotifier {
        private boolean skipped = true;

        @SuppressWarnings("unused")
        void notify(IElementType type, int start, int end) {
            this.skipped = true;
        }

        boolean isSkipped() {
            return this.skipped;
        }

        void setSkipped() {
            this.skipped = false;
        }
    }
}
