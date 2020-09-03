// This is a generated file. Not intended for manual editing.
package com.reason.lang.ocamlyacc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ocamlyacc.OclYaccTypes.*;

public class OclYaccParser implements PsiParser, LightPsiParser {

    @NotNull
    public ASTNode parse(@NotNull IElementType t, @NotNull PsiBuilder b) {
        parseLight(t, b);
        return b.getTreeBuilt();
    }

    public void parseLight(IElementType t, PsiBuilder b) {
        boolean r;
        b = adapt_builder_(t, b, this, null);
        Marker m = enter_section_(b, 0, _COLLAPSE_, null);
        if (t == DECLARATION) {
            r = declaration(b, 0);
        } else if (t == HEADER) {
            r = header(b, 0);
        } else if (t == RULE) {
            r = rule(b, 0);
        } else if (t == RULE_BODY) {
            r = rule_body(b, 0);
        } else if (t == RULE_PATTERN) {
            r = rule_pattern(b, 0);
        } else if (t == TRAILER) {
            r = trailer(b, 0);
        } else {
            r = parse_root_(t, b, 0);
        }
        exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
    }

    protected boolean parse_root_(IElementType t, @NotNull PsiBuilder b, int l) {
        return yacc(b, l + 1);
    }

    /* ********************************************************** */
    // IDENT
    static boolean constr(@NotNull PsiBuilder b, int l) {
        return consumeToken(b, IDENT);
    }

    /* ********************************************************** */
    // token_dcl | start_dcl | type_dcl | left_dcl | right_dcl
    public static boolean declaration(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "declaration")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, DECLARATION, "<declaration>");
        r = token_dcl(b, l + 1);
        if (!r) {
            r = start_dcl(b, l + 1);
        }
        if (!r) {
            r = type_dcl(b, l + 1);
        }
        if (!r) {
            r = left_dcl(b, l + 1);
        }
        if (!r) {
            r = right_dcl(b, l + 1);
        }
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // declaration+
    static boolean declarations(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "declarations")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = declaration(b, l + 1);
        while (r) {
            int c = current_position_(b);
            if (!declaration(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "declarations", c)) {
                break;
            }
        }
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // HEADER_START OCAML_LAZY_NODE HEADER_STOP
    public static boolean header(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "header")) {
            return false;
        }
        if (!nextTokenIs(b, HEADER_START)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, HEADER_START, OCAML_LAZY_NODE, HEADER_STOP);
        exit_section_(b, m, HEADER, r);
        return r;
    }

    /* ********************************************************** */
    // LEFT symbol symbol*
    static boolean left_dcl(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "left_dcl")) {
            return false;
        }
        if (!nextTokenIs(b, LEFT)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, LEFT);
        r = r && symbol(b, l + 1);
        r = r && left_dcl_2(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // symbol*
    private static boolean left_dcl_2(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "left_dcl_2")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!symbol(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "left_dcl_2", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // RIGHT symbol symbol*
    static boolean right_dcl(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "right_dcl")) {
            return false;
        }
        if (!nextTokenIs(b, RIGHT)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, RIGHT);
        r = r && symbol(b, l + 1);
        r = r && right_dcl_2(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // symbol*
    private static boolean right_dcl_2(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "right_dcl_2")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!symbol(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "right_dcl_2", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // IDENT COLON rule_body
    public static boolean rule(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule")) {
            return false;
        }
        if (!nextTokenIs(b, IDENT)) {
            return false;
        }
        boolean r, p;
        Marker m = enter_section_(b, l, _NONE_, RULE, null);
        r = consumeTokens(b, 2, IDENT, COLON);
        p = r; // pin = 2
        r = r && rule_body(b, l + 1);
        exit_section_(b, l, m, r, p, null);
        return r || p;
    }

    /* ********************************************************** */
    // PIPE? rule_pattern (PIPE rule_pattern)* SEMI
    public static boolean rule_body(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule_body")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, RULE_BODY, "<rule body>");
        r = rule_body_0(b, l + 1);
        r = r && rule_pattern(b, l + 1);
        r = r && rule_body_2(b, l + 1);
        r = r && consumeToken(b, SEMI);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // PIPE?
    private static boolean rule_body_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule_body_0")) {
            return false;
        }
        consumeToken(b, PIPE);
        return true;
    }

    // (PIPE rule_pattern)*
    private static boolean rule_body_2(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule_body_2")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!rule_body_2_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "rule_body_2", c)) {
                break;
            }
        }
        return true;
    }

    // PIPE rule_pattern
    private static boolean rule_body_2_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule_body_2_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, PIPE);
        r = r && rule_pattern(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // symbol* LBRACE semantic_action RBRACE
    public static boolean rule_pattern(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule_pattern")) {
            return false;
        }
        if (!nextTokenIs(b, "<rule pattern>", IDENT, LBRACE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, RULE_PATTERN, "<rule pattern>");
        r = rule_pattern_0(b, l + 1);
        r = r && consumeToken(b, LBRACE);
        r = r && semantic_action(b, l + 1);
        r = r && consumeToken(b, RBRACE);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // symbol*
    private static boolean rule_pattern_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rule_pattern_0")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!symbol(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "rule_pattern_0", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // rule+
    static boolean rules(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "rules")) {
            return false;
        }
        if (!nextTokenIs(b, IDENT)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = rule(b, l + 1);
        while (r) {
            int c = current_position_(b);
            if (!rule(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "rules", c)) {
                break;
            }
        }
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // OCAML_LAZY_NODE
    static boolean semantic_action(@NotNull PsiBuilder b, int l) {
        return consumeToken(b, OCAML_LAZY_NODE);
    }

    /* ********************************************************** */
    // START symbol symbol*
    static boolean start_dcl(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "start_dcl")) {
            return false;
        }
        if (!nextTokenIs(b, START)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, START);
        r = r && symbol(b, l + 1);
        r = r && start_dcl_2(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // symbol*
    private static boolean start_dcl_2(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "start_dcl_2")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!symbol(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "start_dcl_2", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // IDENT
    static boolean symbol(@NotNull PsiBuilder b, int l) {
        return consumeToken(b, IDENT);
    }

    /* ********************************************************** */
    // TOKEN (LT type_expr GT)? constr constr*
    static boolean token_dcl(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "token_dcl")) {
            return false;
        }
        if (!nextTokenIs(b, TOKEN)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, TOKEN);
        r = r && token_dcl_1(b, l + 1);
        r = r && constr(b, l + 1);
        r = r && token_dcl_3(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // (LT type_expr GT)?
    private static boolean token_dcl_1(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "token_dcl_1")) {
            return false;
        }
        token_dcl_1_0(b, l + 1);
        return true;
    }

    // LT type_expr GT
    private static boolean token_dcl_1_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "token_dcl_1_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, LT);
        r = r && type_expr(b, l + 1);
        r = r && consumeToken(b, GT);
        exit_section_(b, m, null, r);
        return r;
    }

    // constr*
    private static boolean token_dcl_3(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "token_dcl_3")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!constr(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "token_dcl_3", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // OCAML_LAZY_NODE
    public static boolean trailer(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "trailer")) {
            return false;
        }
        if (!nextTokenIs(b, OCAML_LAZY_NODE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, OCAML_LAZY_NODE);
        exit_section_(b, m, TRAILER, r);
        return r;
    }

    /* ********************************************************** */
    // TYPE LT type_expr GT symbol symbol*
    static boolean type_dcl(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "type_dcl")) {
            return false;
        }
        if (!nextTokenIs(b, TYPE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, TYPE, LT);
        r = r && type_expr(b, l + 1);
        r = r && consumeToken(b, GT);
        r = r && symbol(b, l + 1);
        r = r && type_dcl_5(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // symbol*
    private static boolean type_dcl_5(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "type_dcl_5")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!symbol(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "type_dcl_5", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // IDENT (DOT IDENT)*
    static boolean type_expr(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "type_expr")) {
            return false;
        }
        if (!nextTokenIs(b, IDENT)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, IDENT);
        r = r && type_expr_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // (DOT IDENT)*
    private static boolean type_expr_1(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "type_expr_1")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!type_expr_1_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "type_expr_1", c)) {
                break;
            }
        }
        return true;
    }

    // DOT IDENT
    private static boolean type_expr_1_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "type_expr_1_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeTokens(b, 0, DOT, IDENT);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // COMMENT* header? declarations SECTION_SEPARATOR rules (SECTION_SEPARATOR trailer)?
    static boolean yacc(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "yacc")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = yacc_0(b, l + 1);
        r = r && yacc_1(b, l + 1);
        r = r && declarations(b, l + 1);
        r = r && consumeToken(b, SECTION_SEPARATOR);
        r = r && rules(b, l + 1);
        r = r && yacc_5(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // COMMENT*
    private static boolean yacc_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "yacc_0")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!consumeToken(b, COMMENT)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "yacc_0", c)) {
                break;
            }
        }
        return true;
    }

    // header?
    private static boolean yacc_1(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "yacc_1")) {
            return false;
        }
        header(b, l + 1);
        return true;
    }

    // (SECTION_SEPARATOR trailer)?
    private static boolean yacc_5(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "yacc_5")) {
            return false;
        }
        yacc_5_0(b, l + 1);
        return true;
    }

    // SECTION_SEPARATOR trailer
    private static boolean yacc_5_0(@NotNull PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "yacc_5_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, SECTION_SEPARATOR);
        r = r && trailer(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

}
