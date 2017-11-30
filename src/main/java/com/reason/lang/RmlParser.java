package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.RmlTypes.*;

public class RmlParser implements PsiParser, LightPsiParser {

    enum ParserOptions {
        Validation, NoValidation
    }

    private static final String ERR_SEMI_EXPECTED = "';' expected";
    private static final String ERR_RBRACE_EXPECTED = "'}' expected";
    private static final String ERR_EQ_EXPECTED = "'=' expected";
    private static final String ERR_ARROW_EXPECTED = "'=>' expected";
    private static final String ERR_COLON_EXPECTED = "':' expected";
    private static final String ERR_NAME_UPPERCASE = "Name must start with an uppercase";

    private final ParserOptions m_options;

    RmlParser(ParserOptions options) {
        m_options = options;
    }

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

        Marker fileAsModuleMarker = builder.mark();

        int c = current_position_(builder);
        while (true) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == OPEN) {
                openExpression(builder, 1);
            } else if (tokenType == INCLUDE) {
                includeExpression(builder, 1);
            } else if (tokenType == TYPE) {
                typeExpression(builder, 1);
            } else if (tokenType == MODULE) {
                moduleExpression(builder, 1);
            } else if (tokenType == EXTERNAL) {
                externalExpression(builder, 1);
            } else if (tokenType == LET) {
                letExpression(builder, 1);
            } else if (tokenType == LBRACKET) {
                annotationExpression(builder, 1);
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "reasonFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }

        fileAsModuleMarker.done(FILE_MODULE);
        return true;
    }

    // **********
    // OPEN EXCLAMATION_MARK? module_path SEMI
    // **********
    private IElementType openExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "openExpression")) {
            return builder.getTokenType();
        }

        IElementType nextTokenType = builder.lookAhead(1);
        if (nextTokenType == EXCLAMATION_MARK) {
            builder.advanceLexer();
        }

        Marker moduleMarker = enter_section(builder);
        advance(builder);
        modulePath(builder, recLevel + 1);

        endExpression(builder); // Continue until a ';' or another top level expression is found
        return exit_section(builder, moduleMarker, OPEN_EXPRESSION);
    }

    // **********
    // TYPE type_name any* EQ (scoped_expression | expr) ;
    // **********
    private IElementType typeExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "type expression")) {
            return builder.getTokenType();
        }

        // enter type
        Marker typeMarker = enter_section(builder);

        String constrName = "";

        // type name
        IElementType tokenType = advance(builder);
        if (tokenType != SEMI) {
            Marker nameMarker = enter_section(builder);
            constrName = builder.getTokenText();
            builder.advanceLexer();
            exit_section(builder, nameMarker, TYPE_CONSTR_NAME);
        }

        // anything until EQ or SEMI
        tokenType = builder.getTokenType();
        if (tokenType != EQ && tokenType != SEMI) {
            advanceUntil(builder, recLevel++, EQ);
        }

        tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            // =
            if (tokenType != EQ) {
                fail(builder, ERR_EQ_EXPECTED);
            } else {
                builder.advanceLexer();
            }

            tokenType = builder.getTokenType();
            if (tokenType == LBRACE) {
                // scoped body, anything inside braces
                scopedTypeExpression(builder, recLevel + 1, constrName);
            } else {
                // anything but semi or start expression
                Marker marker = enter_section(builder);

                while (true) {
                    tokenType = advance(builder);
                    if (LIDENT.equals(tokenType) && constrName != null && !constrName.isEmpty() && constrName.equals(builder.getTokenText())) {
                        Marker constrMarker = builder.mark();
                        tokenType = advance(builder);
                        exit_section(builder, constrMarker, TYPE_CONSTR_NAME);
                    }

                    if (isStartExpression(tokenType)) {
                        break;
                    }
                }

                exit_section(builder, marker, SCOPED_EXPR);
            }
        }

        endExpression(builder);// Continue until a ';' or another toplevel expression is found
        return exit_section(builder, typeMarker, TYPE_EXPRESSION);
    }

    // **********
    // INCLUDE module_path (scoped_expression)? SEMI
    // **********
    private IElementType includeExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "include expression")) {
            return builder.getTokenType();
        }

        Marker includeMarker = enter_section(builder);

        IElementType tokenType = advance(builder);
        if (tokenType != SEMI) {
            // module path
            tokenType = modulePath(builder, recLevel + 1);
            if (tokenType == LBRACE) {
                tokenType = scopedExpression(builder, recLevel + 1);
            }
            if (tokenType != SEMI) {
                fail(builder, ERR_SEMI_EXPECTED);
            }
        }

        endExpression(builder);
        return exit_section(builder, includeMarker, INCLUDE_EXPRESSION);
    }

    // **********
    // MODULE TYPE? module_name |                                              |   SEMI
    //                          | EQ module_alias                              |
    //             ::functor::  | EQ (COLON module_type)? scoped_expression)   |
    // ???
    //                          | (LPAREN any RPAREN)+ (COLON module_type)? ARROW
    // **********
    private IElementType moduleExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "module expression")) {
            return builder.getTokenType();
        }

        // enter module
        Marker moduleMarker = enter_section(builder);

        IElementType tokenType = advance(builder);
        if (tokenType != SEMI) {
            // might be a module definition only
            if (tokenType == TYPE) {
                tokenType = advance(builder);
            }

            // module name
            boolean isNameIncorrect = tokenType != UIDENT;
            Marker errorMarker = null;

            Marker moduleNameMarker = enter_section(builder);

            if (isNameIncorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (isNameIncorrect) {
                errorMarker.error("Module name must start with upper case");
            }

            exit_section(builder, moduleNameMarker, MODULE_NAME);

            tokenType = builder.getTokenType();
            if (tokenType != SEMI) {
                if (tokenType == LPAREN) {
                    // module constructor (function)
                    advanceUntil(builder, recLevel + 1, ARROW);

                    tokenType = builder.getTokenType();
                    if (tokenType != ARROW) {
                        fail(builder, ERR_ARROW_EXPECTED);
                    } else {
                        // module definition
                        tokenType = advance(builder);
                        if (tokenType == LBRACE) {
                            // scoped body, anything inside braces
                            scopedExpression(builder, recLevel + 1);
                        } else if (tokenType != SEMI) {
                            // module alias
                            modulePath(builder, recLevel + 1);
                        }
                    }
                } else if (tokenType == EQ || tokenType == COLON) {
                    if (tokenType == COLON) {
                        // module type
                        advanceUntil(builder, recLevel + 1, EQ);
                        tokenType = builder.getTokenType();
                    }

                    if (tokenType == EQ) {
                        tokenType = advance(builder);

                        // If there is a parenthesis, it's a functor
                        if (tokenType == LPAREN) {
                            Marker functorParamsMarker = enter_section(builder);

                            parenExpression(builder, recLevel + 1);

                            exit_section(builder, functorParamsMarker, FUNCTOR_PARAMS);
                            tokenType = advance(builder);
                            if (tokenType == ARROW) {
                                tokenType = advance(builder);
                            }
                        }

                        // module definition
                        if (tokenType == LBRACE) {
                            // scoped body, anything inside braces
                            scopedExpression(builder, recLevel + 1);
                        } else if (tokenType != SEMI) {
                            // module alias
                            modulePath(builder, recLevel + 1);
                        }
                    }
                } else {
                    fail(builder, ERR_EQ_EXPECTED);
                }
            }
        }

        endExpression(builder);
        return exit_section(builder, moduleMarker, MODULE_EXPRESSION);
    }

    // ***** UIDENT (DOT UIDENT|scoped_expression)* ( (.*) )::constr
    private IElementType modulePath(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "module path")) {
            return builder.getTokenType();
        }

        Marker marker = enter_section(builder);

        // First element must be a module name
        boolean incorrectName = builder.getTokenType() != UIDENT;
        Marker nameMarker = enter_section(builder);
        builder.advanceLexer();
        if (incorrectName) {
            if (ParserOptions.Validation == m_options) {
                nameMarker.error(ERR_NAME_UPPERCASE);
            } else {
                nameMarker.drop();
            }
        } else {
            exit_section(builder, nameMarker, MODULE_NAME);

            // Then we can have other modules with dot notation
            while (true) {
                nameMarker = enter_section(builder);
                IElementType tokenType = builder.getTokenType();

                if (tokenType == LPAREN) {
                    nameMarker.drop();
                    // Anything until we get to closing paren or semi
                    parenExpression(builder, recLevel + 1);
                    continue;
                } else if (tokenType == LBRACE) {
                    nameMarker.drop();
                    // scoped body, anything inside braces
                    scopedExpression(builder, recLevel + 1);
                }

                // a semi or scope is found, stop module path exploration
                if (tokenType == SEMI || tokenType == LBRACE) {
                    nameMarker.drop();
                    break;
                }

                if (tokenType == DOT) {
                    nameMarker.drop();
                    builder.advanceLexer();
                    continue;
                }

                if (tokenType != UIDENT) {
                    builder.advanceLexer();
                    nameMarker.error(ERR_NAME_UPPERCASE);
                    break;
                }

                builder.advanceLexer();
                exit_section(builder, nameMarker, MODULE_NAME);
            }
        }

        return exit_section(builder, marker, MODULE_PATH);
    }

    // **********
    // EXTERNAL external_name COLON expression* SEMI
    private IElementType externalExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "external expression")) {
            return builder.getTokenType();
        }

        // enter external
        Marker externalMarker = enter_section(builder);

        // name
        IElementType tokenType = advance(builder);
        if (tokenType != SEMI) {
            Marker nameMarker = enter_section(builder);
            Marker errorMarker = null;
            boolean isNameCorrect = tokenType == LIDENT;

            if (!isNameCorrect) {
                errorMarker = builder.mark();
            }

            builder.advanceLexer();

            if (!isNameCorrect) {
                errorMarker.error("External name must start with a lower case");
            }

            exit_section(builder, nameMarker, VALUE_NAME);
        }

        tokenType = builder.getTokenType();
        // :
        if (tokenType == COLON) {
            builder.advanceLexer();
        } else {
            fail(builder, ERR_COLON_EXPECTED);
        }

        tokenType = builder.getTokenType();
        if (tokenType != SEMI) {
            // anything but semi
            while (true) {
                tokenType = advance(builder);
                if (tokenType == null || tokenType == SEMI) {
                    break;
                }
            }
        }

        // end of external
        endExpression(builder);
        return exit_section(builder, externalMarker, EXTERNAL_EXPRESSION);
    }

    // **********
    // [@bs.* any*?] any*
    private IElementType annotationExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "annotation expression")) {
            return builder.getTokenType();
        }

        // enter ANNOTATION
        Marker annotationMarker = enter_section(builder);

        IElementType tokenType = advance(builder);
        if (tokenType != ARROBASE) {
            // oops, not an annotation, revert
            annotationMarker.drop();
            return advanceUntilNextStart(builder, recLevel + 1);
        }

        Marker annMarker = enter_section(builder);

        // Anything before SPACE or RBRACKET
        WhitespaceNotifier whitespace = new WhitespaceNotifier(false);
        builder.setWhitespaceSkippedCallback(whitespace::notify);
        tokenType = advance(builder);
        while (true) {
            if (whitespace.isSkipped() || tokenType == RBRACKET) {
                break;
            } else {
                whitespace.setSkipped();
            }
            tokenType = advance(builder);
        }
        builder.setWhitespaceSkippedCallback(null);

        exit_section(builder, annMarker, ANNOTATION_NAME);
        if (tokenType != RBRACKET) {
            advanceUntil(builder, recLevel + 1, RBRACKET);
        }

        advance(builder);

        // end of ANNOTATION
        return exit_section(builder, annotationMarker, ANNOTATION_EXPRESSION);
    }

    // **********
    // NEW!
    // LET REC? MODULE? (destructure|value_name) expression* (EQ (LPAREN expression RPAREN ARROW scoped_expression | (scoped_)expression) SEMI
    //
    // OLD!
    // LET REC? MODULE? (destructure|value_name) expression* (EQ (scoped_)expression | ARROW scoped_expression) SEMI
    // LET REC? MODULE? value_name COLON expression+ SEMI
    // **********
    private IElementType letExpression(PsiBuilder builder, int recLevel) {
        if (!recursion_guard_(builder, recLevel, "let expression")) {
            return builder.getTokenType();
        }

        // Enter LET
        Marker letMarker = enter_section(builder);

        // Might be recursive
        IElementType tokenType = advance(builder);
        if (tokenType == REC) {
            tokenType = advance(builder);
        }

        // Might be a module alias
        if (tokenType == MODULE) {
            tokenType = advance(builder);
        }

        if (tokenType != SEMI) {
            if (tokenType == UNIT) {
                tokenType = advance(builder);
            } else if (tokenType == LPAREN) {
                // we are dealing with destructuring
                advanceUntil(builder, recLevel + 1, RPAREN);
                tokenType = advance(builder);
            } else if (tokenType == LBRACE) {
                // we are dealing with destructuring
                advanceUntil(builder, recLevel + 1, RBRACE);
                tokenType = advance(builder);
            } else {
                // value name
                Marker nameMarker = enter_section(builder);
                tokenType = advance(builder);
                exit_section(builder, nameMarker, VALUE_NAME);
            }
        }

        // COLON means we are dealing with a definition, = or => is not needed, we can end with ;
        boolean hasTypeDefinition = tokenType == COLON;

        // Anything before EQ|ARROW
        // anything but semi or start expression
        WhitespaceNotifier whitespace = new WhitespaceNotifier(true);
        builder.setWhitespaceSkippedCallback(whitespace::notify);

        while (true) {
            tokenType = builder.getTokenType();
            if ((whitespace.isSkipped() && (tokenType == EQ || tokenType == ARROW)) || isStartExpression(tokenType)) {
                break;
            } else {
                whitespace.setSkipped();
            }
            builder.advanceLexer();
        }

        builder.setWhitespaceSkippedCallback(null);

        tokenType = builder.getTokenType();
        if (EQ != tokenType && ARROW != tokenType) {
            if (!hasTypeDefinition) {
                fail(builder, "'=' or '=>' expected");
            }
        } else {
            boolean isFunction = ARROW == tokenType;

            builder.advanceLexer();
            Marker bindMarker = enter_section(builder);

            tokenType = builder.getTokenType();
            if (LPAREN == tokenType || UNIT == tokenType) {
                // New function syntax ?
                Marker paramsMarker = enter_section(builder);
                if (LPAREN == tokenType) {
                    parenExpression(builder, recLevel + 1);
                } else {
                    builder.advanceLexer();
                }

                tokenType = builder.getTokenType();
                isFunction = ARROW == tokenType;
                if (isFunction) {
                    bindMarker.drop();
                    exit_section(builder, paramsMarker, FUN_PARAMS);
                } else {
                    paramsMarker.drop();
                }

                tokenType = advance(builder);

                if (isFunction) {
                    bindMarker = enter_section(builder);
                }
            }

            if (LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
            } else if (IF == tokenType) {
                ifExpression(builder, recLevel + 1, true);
            } else {
                advanceUntilNextStart(builder, recLevel + 1);
            }

            exit_section(builder, bindMarker, isFunction ? FUN_BODY : LET_BINDING);
        }

        // end of LET
        endExpression(builder);
        return exit_section(builder, letMarker, LET_EXPRESSION);
    }

    private void ifExpression(PsiBuilder builder, int recLevel, boolean containsIf) {
        if (!recursion_guard_(builder, recLevel, "if expression")) {
            return;
        }

        IElementType tokenType = builder.getTokenType();

        if (containsIf) {
            tokenType = advance(builder);
            if (tokenType != LPAREN) {
                fail(builder, "'(' expected");
                return;
            }

            parenExpression(builder, recLevel + 1);
            tokenType = builder.getTokenType();
        }

        if (tokenType == LBRACE) {
            scopedExpression(builder, recLevel + 1);
            tokenType = builder.getTokenType();
        }

        if (tokenType == ELSE) {
            tokenType = advance(builder);
            ifExpression(builder, recLevel + 1, tokenType == IF);
        }
    }

    // **********
    // Pattern: LBRACE expression* RBRACE
    // **********
    private IElementType scopedExpression(PsiBuilder builder, int recLevel) {
        return scopedTypeExpression(builder, recLevel, null);
    }

    private IElementType scopedTypeExpression(PsiBuilder builder, int recLevel, String constrName) {
        if (!recursion_guard_(builder, recLevel, "scoped expression")) {
            return builder.getTokenType();
        }

        // Mark the start of the scoped expression
        Marker marker = enter_section(builder);
        IElementType tokenType = advance(builder);

        while (true) {
            if (tokenType == null || tokenType == RBRACE) {
                break;
            }

            // start expressions ?
            if (tokenType == OPEN) {
                tokenType = openExpression(builder, recLevel + 1);
            } else if (tokenType == INCLUDE) {
                tokenType = includeExpression(builder, recLevel + 1);
            } else if (tokenType == TYPE) {
                tokenType = typeExpression(builder, recLevel + 1);
            } else if (tokenType == MODULE) {
                tokenType = moduleExpression(builder, recLevel + 1);
            } else if (tokenType == EXTERNAL) {
                tokenType = externalExpression(builder, recLevel + 1);
            } else if (tokenType == LET) {
                tokenType = letExpression(builder, recLevel + 1);
            } else if (tokenType == LBRACE) {
                tokenType = scopedTypeExpression(builder, recLevel + 1, constrName);
            } else if (tokenType == LPAREN) {
                tokenType = parenTypeExpression(builder, recLevel + 1, constrName);
            } else if (tokenType == LBRACKET) {
                tokenType = annotationExpression(builder, recLevel + 1);
            } else {
                tokenType = advance(builder);
                if (LIDENT.equals(tokenType) && constrName != null && !constrName.isEmpty() && constrName.equals(builder.getTokenText())) {
                    Marker constrMarker = builder.mark();
                    tokenType = advance(builder);
                    exit_section(builder, constrMarker, TYPE_CONSTR_NAME);
                }
            }
        }

        tokenType = builder.getTokenType();
        if (tokenType != RBRACE) {
            fail(builder, ERR_RBRACE_EXPECTED);
        } else {
            advance(builder);
        }

        return exit_section(builder, marker, SCOPED_EXPR);
    }

    private IElementType advanceUntilNextStart(PsiBuilder builder, int recLevel) {
        IElementType tokenType;

        while (true) {
            tokenType = advance(builder);

            if (LBRACE == tokenType) {
                tokenType = scopedExpression(builder, recLevel + 1);
            } else if (LPAREN == tokenType) {
                tokenType = parenExpression(builder, recLevel + 1);
            }

            if (isStartExpression(tokenType) || tokenType == RBRACE) {
                break;
            }
        }

        return tokenType;
    }

    // **********
    // Pattern: LPAREN {{token-start}} expression* RPAREN {{token-end}}
    // **********
    private IElementType parenExpression(PsiBuilder builder, int recLevel) {
        return parenTypeExpression(builder, recLevel, null);
    }

    private IElementType parenTypeExpression(PsiBuilder builder, int recLevel, String constrName) {
        if (!recursion_guard_(builder, recLevel, "skip paren")) {
            return builder.getTokenType();
        }

        IElementType tokenType;
        while (true) {
            tokenType = advance(builder);

            // If a new scope is found, recursively process it
            if (LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else if (LPAREN == tokenType) {
                parenExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else {
                if (LIDENT.equals(tokenType) && constrName != null && !constrName.isEmpty() && constrName.equals(builder.getTokenText())) {
                    Marker constrMarker = builder.mark();
                    tokenType = advance(builder);
                    exit_section(builder, constrMarker, TYPE_CONSTR_NAME);
                }
            }

            // Advance until a right paren is found, or nothing else is found
            if (RPAREN == tokenType || tokenType == null) {
                return advance(builder);
            }
        }
    }

    private void advanceUntil(PsiBuilder builder, int recLevel, IElementType nextTokenType) {
        if (!recursion_guard_(builder, recLevel, "advance until")) {
            return;
        }

        IElementType tokenType;
        while (true) {
            tokenType = advance(builder);
            if (LBRACE == tokenType) {
                scopedExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            } else if (LPAREN == tokenType) {
                parenExpression(builder, recLevel + 1);
                tokenType = builder.getTokenType();
            }

            if (tokenType == null || tokenType == SEMI || tokenType == nextTokenType) {
                break;
            }
        }
    }

    private static boolean isStartExpression(IElementType tokenType) {
        return tokenType == null || tokenType == SEMI || tokenType == MODULE || tokenType == OPEN || tokenType == TYPE || tokenType == LET;
    }

    private void endExpression(PsiBuilder builder) {
        // Last expression in the file can omit semi
        if (builder.eof()) {
            return;
        }

        IElementType tokenType = builder.getTokenType();
        if (tokenType == SEMI) {
            builder.advanceLexer();
        } else if (tokenType != RBRACE) { // Last expression in a scope can also omit semi
            fail(builder, ERR_SEMI_EXPECTED);
        }
    }

    private void fail(PsiBuilder builder, String message) {
        if (ParserOptions.Validation == m_options) {
            builder.mark().error(message);
        }
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

    private static IElementType advance(PsiBuilder builder) {
        builder.advanceLexer();
        return builder.getTokenType();
    }

    private static Marker enter_section(PsiBuilder builder) {
        return builder.mark();
    }

    private static IElementType exit_section(PsiBuilder builder, Marker marker, IElementType scopedExpr) {
        marker.done(scopedExpr);
        return builder.getTokenType();
    }
}
