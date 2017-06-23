package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ReasonMLTypes.*;

public class ReasonMLParser implements PsiParser, LightPsiParser {

    private static final String ERR_SEMI_EXPECTED = "';' expected";
    public static final String ERR_NAME_LIDENT = "Name must start with a lower case";

    public ASTNode parse(IElementType t, PsiBuilder b) {
        parseLight(t, b);
        return b.getTreeBuilt();
    }

    public void parseLight(IElementType t, PsiBuilder b) {
        boolean r;
        b = adapt_builder_(t, b, this, null);
        Marker m = enter_section_(b, 0, _COLLAPSE_, null);
        r = reasonFile(b);
        exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
    }

    static boolean reasonFile(PsiBuilder builder) {
        if (!recursion_guard_(builder, 1, "reasonFile")) {
            return false;
        }

        int c = current_position_(builder);
        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (OPEN == tokenType) {
                openExpression(builder, 1);
            } else if (TYPE == tokenType) {
                typeExpression(builder, 1);
            } else if (MODULE == tokenType) {
                moduleExpression(builder, 1);
            } else if (EXTERNAL == tokenType) {
                externalExpression(builder, 1);
            } else if (LET == tokenType) {
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
    // OPEN module_path ;
    // **********
    static boolean openExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "openExpression")) {
            return false;
        }

        // We found an 'open' keyword, continue until a ';' or another toplevel expression is found
        Marker moduleMarker = enter_section_(builder);
        builder.advanceLexer();

        IElementType tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            module_path(builder, recLevel + 1);
        }

        endExpression(builder);
        exit_section_(builder, moduleMarker, OPEN_EXPRESSION, true);

        return true;
    }

    // **********
    // TYPE type_name EQ (scoped_expression | expr) ;
    private static boolean typeExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "type expression")) {
            return false;
        }

        // enter type
        Marker exprMarker = enter_section_(builder);
        builder.advanceLexer();

        // type name
        IElementType tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            Marker nameMarker = enter_section_(builder);
            Marker errorMarker = null;
            boolean isNameCorrect = tokenType == LIDENT;

            if (!isNameCorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (!isNameCorrect) {
                errorMarker.error("Type name must start with a lower case");
            }

            exit_section_(builder, nameMarker, TYPE_CONSTR_NAME, true);
        }

        tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            // =
            if (tokenType != EQ) {
                builder.mark().error("'=' expected");
            } else {
                builder.advanceLexer();
            }

            tokenType = builder.getTokenType();
            if (tokenType == LBRACE) {
                // scoped body, anything inside braces
                scopedExpression(builder, recLevel + 1);
            } else {
                // anything but semi or start expression
                Marker marker = enter_section_(builder);

                while (true) {
                    builder.advanceLexer();
                    tokenType = builder.getTokenType();
                    if (isStartExpression(tokenType)) {
                        break;
                    }
                }

                exit_section_(builder, marker, SCOPED_EXPR, true);
            }
        }

        // end of type
        endExpression(builder);
        exit_section_(builder, exprMarker, TYPE_STATEMENT/*EXPR*/, true);

        return true;
    }

    // **********
    // MODULE module_name EQ (module_alias | scoped_expression) SEMI
    private static boolean moduleExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "module expression")) {
            return false;
        }

        // enter module
        Marker moduleMarker = enter_section_(builder);
        builder.advanceLexer();

        IElementType tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            // module name
            boolean isNameIncorrect = tokenType != UIDENT;
            Marker errorMarker = null;

            Marker moduleNameMarker = enter_section_(builder);

            if (isNameIncorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (isNameIncorrect) {
                errorMarker.error("Module name must start with upper case");
            }

            exit_section_(builder, moduleNameMarker, MODULE_NAME, true);

            // =
            tokenType = builder.getTokenType();
            if (tokenType == EQ) {
                builder.advanceLexer();

                tokenType = builder.getTokenType();
                if (tokenType == LBRACE) {
                    // scoped body, anything inside braces
                    scopedExpression(builder, recLevel + 1);
                } else if (tokenType != SEMI) {
                    // module alias
                    module_path(builder, recLevel + 1);
                }
            } else {
                builder.mark().error("'=' expected");
            }
        }


        endExpression(builder);
        exit_section_(builder, moduleMarker, MODULE_EXPRESSION, true);

        return true;
    }

    // **********
    // EXTERNAL external_name COLON expression* SEMI
    private static boolean externalExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "external expression")) {
            return false;
        }

        // enter external
        Marker exprMarker = enter_section_(builder);
        builder.advanceLexer();

        // name
        IElementType tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            Marker nameMarker = enter_section_(builder);
            Marker errorMarker = null;
            boolean isNameCorrect = tokenType == LIDENT;

            if (!isNameCorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (!isNameCorrect) {
                errorMarker.error("External name must start with a lower case");
            }

            exit_section_(builder, nameMarker, VALUE_NAME, true);
        }

        tokenType = builder.getTokenType();
        // :
        if (tokenType == COLON) {
            builder.advanceLexer();
        } else {
            builder.mark().error("':' expected");
        }

        tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            // anything but semi
            while (true) {
                builder.advanceLexer();
                tokenType = builder.getTokenType();
                if (tokenType == null || tokenType == SEMI) {
                    break;
                }
            }
        }

        // end of external
        endExpression(builder);
        exit_section_(builder, exprMarker, EXTERNAL_EXPRESSION, true);

        return true;
    }

    // **********
    // LET value_name expression* (EQ expression | ARROW scoped_expression) SEMI
    private static boolean letExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "let expression")) {
            return false;
        }

        // enter LET
        Marker exprMarker = enter_section_(builder);
        builder.advanceLexer();

        // value name
        IElementType tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            Marker nameMarker = enter_section_(builder);
            Marker errorMarker = null;
            boolean isNameCorrect = tokenType == LIDENT;

            if (!isNameCorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (!isNameCorrect) {
                errorMarker.error(ERR_NAME_LIDENT);
            }

            exit_section_(builder, nameMarker, VALUE_NAME, true);
        }

        // Anything before EQ|ARROW
        // anything but semi or start expression
        boolean[] skipped = new boolean[]{false}; // faire une classe
        builder.setWhitespaceSkippedCallback((type, start, end) -> skipped[0] = true);

        while (true) {
            tokenType = builder.getTokenType();
            if ((skipped[0] && (tokenType == EQ || tokenType == ARROW)) || isStartExpression(tokenType)) {
                break;
            } else {
                skipped[0] = false;
            }
            builder.advanceLexer();
        }

        builder.setWhitespaceSkippedCallback(null);

        tokenType = builder.getTokenType();
        if (EQ != tokenType && ARROW != tokenType) {
            builder.mark().error("'=' or '=>' expected");
        } else {
            if (EQ == tokenType) {
                builder.advanceLexer();
                Marker bindingMarker = enter_section_(builder);
                advanceUntilNextStart(builder, recLevel + 1);
                exit_section_(builder, bindingMarker, LET_BINDING, true);
            } else if (ARROW == tokenType) {
                // function detected
                builder.advanceLexer();
                Marker funMarker = enter_section_(builder);

                tokenType = builder.getTokenType();
                if (LBRACE == tokenType) {
                    scopedExpression(builder, recLevel + 1);
                } else {
                    advanceUntilNextStart(builder, recLevel + 1);
                }

                exit_section_(builder, funMarker, FUN_BODY, true);
            }
        }

        // end of LET
        endExpression(builder);
        exit_section_(builder, exprMarker, LET_EXPRESSION, true);

        return true;
    }

    private static void advanceUntilNextStart(PsiBuilder builder, int recLevel) {
        IElementType tokenType;
        while (true) {
            builder.advanceLexer();
            tokenType = builder.getTokenType();
            if (LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
            }
            if (isStartExpression(tokenType)) {
                break;
            }
        }
    }


    static boolean module_path(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "module path")) {
            return false;
        }

        Marker marker = enter_section_(builder);
        Marker errorMarker = null;

        boolean incorrectName = builder.getTokenType() != UIDENT;
        if (incorrectName) {
            errorMarker = builder.mark();
        }

        builder.advanceLexer();

        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == EQ || isStartExpression(tokenType)) {
                break;
            }

            builder.advanceLexer();
            tokenType = builder.getTokenType();
            if (tokenType != SEMI && tokenType != UIDENT && tokenType != DOT && !incorrectName) {
                incorrectName = true;
                errorMarker = builder.mark();
            }
        }

        if (incorrectName) {
            errorMarker.error("Module path expected: module names start with an uppercase");
        }

        exit_section_(builder, marker, MODULE_PATH, true);
        return true;
    }

    // **********
    // Pattern: LBRACE expression* RBRACE
    static boolean scopedExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "scoped expression")) {
            return false;
        }

        // Mark the start of the scoped expression
        Marker marker = enter_section_(builder);
        builder.advanceLexer();

        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null || tokenType == RBRACE) {
                break;
            }

            // start expressions ?
            if (tokenType == OPEN) {
                openExpression(builder, recLevel + 1);
            } else if (tokenType == TYPE) {
                typeExpression(builder, recLevel + 1);
            } else if (tokenType == MODULE) {
                moduleExpression(builder, recLevel + 1);
            } else if (tokenType == EXTERNAL) {
                externalExpression(builder, recLevel + 1);
            } else if (tokenType == LET) {
                letExpression(builder, recLevel + 1);
            } else if (tokenType == LBRACE) {
                scopedExpression(builder, recLevel + 1);
            } else {
                builder.advanceLexer();
            }
        }

        IElementType tokenType = builder.getTokenType();
        if (tokenType != RBRACE) {
            builder.mark().error("'}' expected");
        } else {
            builder.advanceLexer();
        }

        exit_section_(builder, marker, SCOPED_EXPR, true);
        return true;
    }

    private static boolean isStartExpression(IElementType tokenType) {
        return tokenType == null || tokenType == SEMI || tokenType == MODULE || tokenType == OPEN || tokenType == TYPE || tokenType == LET;
    }

    private static void endExpression(PsiBuilder builder) {
        IElementType tokenType;
        tokenType = builder.getTokenType();
        if (tokenType == SEMI) {
            builder.advanceLexer();
        } else {
            builder.mark().error(ERR_SEMI_EXPECTED);
        }
    }
}
