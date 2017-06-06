// This is a generated file. Not intended for manual editing.
package com.reason.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.reason.psi.ReasonMLTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ReasonMLParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ARGUMENT) {
      r = argument(b, 0);
    }
    else if (t == ARGUMENT_VALUE) {
      r = argument_value(b, 0);
    }
    else if (t == ATTRIBUTE) {
      r = attribute(b, 0);
    }
    else if (t == ATTRIBUTE_ID) {
      r = attribute_id(b, 0);
    }
    else if (t == ATTRIBUTE_ID_REC) {
      r = attribute_id_rec(b, 0);
    }
    else if (t == ATTRIBUTE_PAYLOAD) {
      r = attribute_payload(b, 0);
    }
    else if (t == CONSTANT) {
      r = constant(b, 0);
    }
    else if (t == END_TAG) {
      r = end_tag(b, 0);
    }
    else if (t == EXPR_STATEMENT) {
      r = expr_statement(b, 0);
    }
    else if (t == EXTERNAL_ALIAS) {
      r = external_alias(b, 0);
    }
    else if (t == EXTERNAL_STATEMENT) {
      r = external_statement(b, 0);
    }
    else if (t == FIELD) {
      r = field(b, 0);
    }
    else if (t == FIELD_DECL) {
      r = field_decl(b, 0);
    }
    else if (t == FIELD_NAME) {
      r = field_name(b, 0);
    }
    else if (t == FIELD_TYPE_DECL) {
      r = field_type_decl(b, 0);
    }
    else if (t == INCLUDE_STATEMENT) {
      r = include_statement(b, 0);
    }
    else if (t == ITEM_ATTRIBUTE) {
      r = item_attribute(b, 0);
    }
    else if (t == JSX) {
      r = jsx(b, 0);
    }
    else if (t == JSX_CONTENT) {
      r = jsxContent(b, 0);
    }
    else if (t == LABEL_NAME) {
      r = label_name(b, 0);
    }
    else if (t == LET_BINDING) {
      r = let_binding(b, 0);
    }
    else if (t == LET_NAME) {
      r = let_name(b, 0);
    }
    else if (t == LET_STATEMENT) {
      r = let_statement(b, 0);
    }
    else if (t == MODULE_BODY) {
      r = module_body(b, 0);
    }
    else if (t == MODULE_NAME) {
      r = module_name(b, 0);
    }
    else if (t == MODULE_PATH) {
      r = module_path(b, 0);
    }
    else if (t == MODULE_STATEMENT) {
      r = module_statement(b, 0);
    }
    else if (t == OPEN_STATEMENT) {
      r = open_statement(b, 0);
    }
    else if (t == PARAMETER) {
      r = parameter(b, 0);
    }
    else if (t == PARAMETER_EXPR) {
      r = parameter_expr(b, 0);
    }
    else if (t == PATTERN) {
      r = pattern(b, 0);
    }
    else if (t == PATTERN_MATCHING) {
      r = pattern_matching(b, 0);
    }
    else if (t == POLY_TYPE) {
      r = poly_type(b, 0);
    }
    else if (t == POLYMORPHIC_VARIANT_TYPE) {
      r = polymorphic_variant_type(b, 0);
    }
    else if (t == RECORD_DECL) {
      r = record_decl(b, 0);
    }
    else if (t == SIGNED_CONSTANT) {
      r = signed_constant(b, 0);
    }
    else if (t == START_TAG) {
      r = start_tag(b, 0);
    }
    else if (t == TAG_NAME) {
      r = tag_name(b, 0);
    }
    else if (t == TAG_PROPERTY) {
      r = tag_property(b, 0);
    }
    else if (t == TAG_SPEC_FIRST) {
      r = tag_spec_first(b, 0);
    }
    else if (t == TYPE_CONSTR) {
      r = type_constr(b, 0);
    }
    else if (t == TYPE_CONSTR_NAME) {
      r = type_constr_name(b, 0);
    }
    else if (t == TYPE_EXPR) {
      r = type_expr(b, 0);
    }
    else if (t == TYPE_STATEMENT) {
      r = type_statement(b, 0);
    }
    else if (t == UNTYPED_OBJECT) {
      r = untyped_object(b, 0);
    }
    else if (t == VALUE_EXPR) {
      r = value_expr(b, 0);
    }
    else if (t == VALUE_NAME) {
      r = value_name(b, 0);
    }
    else if (t == VALUE_PATH) {
      r = value_path(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return reasonFile(b, l + 1);
  }

  /* ********************************************************** */
  // SHORTCUT argument_value
  //     | value_name SHORTCUT argument_value
  public static boolean argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument")) return false;
    if (!nextTokenIs(b, "<argument>", LIDENT, SHORTCUT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARGUMENT, "<argument>");
    r = argument_0(b, l + 1);
    if (!r) r = argument_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SHORTCUT argument_value
  private static boolean argument_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SHORTCUT);
    r = r && argument_value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // value_name SHORTCUT argument_value
  private static boolean argument_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_name(b, l + 1);
    r = r && consumeToken(b, SHORTCUT);
    r = r && argument_value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // value_name (untyped_object | DOT field)?
  public static boolean argument_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_value")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_name(b, l + 1);
    r = r && argument_value_1(b, l + 1);
    exit_section_(b, m, ARGUMENT_VALUE, r);
    return r;
  }

  // (untyped_object | DOT field)?
  private static boolean argument_value_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_value_1")) return false;
    argument_value_1_0(b, l + 1);
    return true;
  }

  // untyped_object | DOT field
  private static boolean argument_value_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_value_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = untyped_object(b, l + 1);
    if (!r) r = argument_value_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOT field
  private static boolean argument_value_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_value_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET ARROBASE attribute_id_rec attribute_payload? RBRACKET
  public static boolean attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, ARROBASE);
    r = r && attribute_id_rec(b, l + 1);
    r = r && attribute_3(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, ATTRIBUTE, r);
    return r;
  }

  // attribute_payload?
  private static boolean attribute_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_3")) return false;
    attribute_payload(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // MODULE /* bs */
  //     | LIDENT
  //     | UIDENT
  public static boolean attribute_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_id")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_ID, "<attribute id>");
    r = consumeToken(b, MODULE);
    if (!r) r = consumeToken(b, LIDENT);
    if (!r) r = consumeToken(b, UIDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // attribute_id (DOT attribute_id)*
  public static boolean attribute_id_rec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_id_rec")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_ID_REC, "<attribute id rec>");
    r = attribute_id(b, l + 1);
    r = r && attribute_id_rec_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (DOT attribute_id)*
  private static boolean attribute_id_rec_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_id_rec_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!attribute_id_rec_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attribute_id_rec_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DOT attribute_id
  private static boolean attribute_id_rec_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_id_rec_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && attribute_id(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON type_expr
  //     | expr
  public static boolean attribute_payload(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_payload")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_PAYLOAD, "<attribute payload>");
    r = attribute_payload_0(b, l + 1);
    if (!r) r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // COLON type_expr
  private static boolean attribute_payload_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_payload_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INT
  //     | FLOAT
  //     | STRING
  //     | FALSE
  //     | TRUE
  //     | UNIT
  //     | LBRACKET RBRACKET
  //     | LPAREN RPAREN
  public static boolean constant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONSTANT, "<constant>");
    r = consumeToken(b, INT);
    if (!r) r = consumeToken(b, FLOAT);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, UNIT);
    if (!r) r = parseTokens(b, 0, LBRACKET, RBRACKET);
    if (!r) r = parseTokens(b, 0, LPAREN, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CLOSE_TAG tag_name GT
  public static boolean end_tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "end_tag")) return false;
    if (!nextTokenIs(b, CLOSE_TAG)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CLOSE_TAG);
    r = r && tag_name(b, l + 1);
    r = r && consumeToken(b, GT);
    exit_section_(b, m, END_TAG, r);
    return r;
  }

  /* ********************************************************** */
  // NONE
  //     | SOME expr
  //     | FUN parameter* ARROW let_binding_body
  //     | LPAREN sequenced_expr? RPAREN untyped_object?
  //     | LBRACE (DOT DOT DOT value_name COMMA?)? sequenced_expr RBRACE
  //     | LBRACKET sequenced_expr RBRACKET
  //     | IF LPAREN expr RPAREN LBRACE sequenced_expr RBRACE (ELSE (IF LPAREN expr RPAREN)? LBRACE sequenced_expr RBRACE)*
  //     | SWITCH expr LBRACE pattern_matching+ RBRACE
  //     | SHORTCUT expr
  //     | module_path DOT expr
  //     | jsx
  //     | let_binding
  //     | label_name SHORTCUT expr
  //     | recursive_expr
  static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NONE);
    if (!r) r = expr_1(b, l + 1);
    if (!r) r = expr_2(b, l + 1);
    if (!r) r = expr_3(b, l + 1);
    if (!r) r = expr_4(b, l + 1);
    if (!r) r = expr_5(b, l + 1);
    if (!r) r = expr_6(b, l + 1);
    if (!r) r = expr_7(b, l + 1);
    if (!r) r = expr_8(b, l + 1);
    if (!r) r = expr_9(b, l + 1);
    if (!r) r = jsx(b, l + 1);
    if (!r) r = let_binding(b, l + 1);
    if (!r) r = expr_12(b, l + 1);
    if (!r) r = recursive_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SOME expr
  private static boolean expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SOME);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FUN parameter* ARROW let_binding_body
  private static boolean expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FUN);
    r = r && expr_2_1(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && let_binding_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter*
  private static boolean expr_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!parameter(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_2_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // LPAREN sequenced_expr? RPAREN untyped_object?
  private static boolean expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr_3_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && expr_3_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sequenced_expr?
  private static boolean expr_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_3_1")) return false;
    sequenced_expr(b, l + 1);
    return true;
  }

  // untyped_object?
  private static boolean expr_3_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_3_3")) return false;
    untyped_object(b, l + 1);
    return true;
  }

  // LBRACE (DOT DOT DOT value_name COMMA?)? sequenced_expr RBRACE
  private static boolean expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && expr_4_1(b, l + 1);
    r = r && sequenced_expr(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT DOT DOT value_name COMMA?)?
  private static boolean expr_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_4_1")) return false;
    expr_4_1_0(b, l + 1);
    return true;
  }

  // DOT DOT DOT value_name COMMA?
  private static boolean expr_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, DOT, DOT);
    r = r && value_name(b, l + 1);
    r = r && expr_4_1_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean expr_4_1_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_4_1_0_4")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // LBRACKET sequenced_expr RBRACKET
  private static boolean expr_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && sequenced_expr(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // IF LPAREN expr RPAREN LBRACE sequenced_expr RBRACE (ELSE (IF LPAREN expr RPAREN)? LBRACE sequenced_expr RBRACE)*
  private static boolean expr_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeTokens(b, 0, RPAREN, LBRACE);
    r = r && sequenced_expr(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    r = r && expr_6_7(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE (IF LPAREN expr RPAREN)? LBRACE sequenced_expr RBRACE)*
  private static boolean expr_6_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_6_7")) return false;
    int c = current_position_(b);
    while (true) {
      if (!expr_6_7_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_6_7", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ELSE (IF LPAREN expr RPAREN)? LBRACE sequenced_expr RBRACE
  private static boolean expr_6_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_6_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && expr_6_7_0_1(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && sequenced_expr(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (IF LPAREN expr RPAREN)?
  private static boolean expr_6_7_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_6_7_0_1")) return false;
    expr_6_7_0_1_0(b, l + 1);
    return true;
  }

  // IF LPAREN expr RPAREN
  private static boolean expr_6_7_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_6_7_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IF, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // SWITCH expr LBRACE pattern_matching+ RBRACE
  private static boolean expr_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SWITCH);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    r = r && expr_7_3(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // pattern_matching+
  private static boolean expr_7_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_7_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pattern_matching(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!pattern_matching(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_7_3", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // SHORTCUT expr
  private static boolean expr_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SHORTCUT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // module_path DOT expr
  private static boolean expr_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_9")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // label_name SHORTCUT expr
  private static boolean expr_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_12")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = label_name(b, l + 1);
    r = r && consumeToken(b, SHORTCUT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // sequenced_expr SEMI
  public static boolean expr_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_STATEMENT, "<expr statement>");
    r = sequenced_expr(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STRING
  public static boolean external_alias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "external_alias")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, EXTERNAL_ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // EXTERNAL value_name COLON type_expr EQ external_alias item_attribute* SEMI
  public static boolean external_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "external_statement")) return false;
    if (!nextTokenIs(b, EXTERNAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXTERNAL);
    r = r && value_name(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && external_alias(b, l + 1);
    r = r && external_statement_6(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, EXTERNAL_STATEMENT, r);
    return r;
  }

  // item_attribute*
  private static boolean external_statement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "external_statement_6")) return false;
    int c = current_position_(b);
    while (true) {
      if (!item_attribute(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "external_statement_6", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // (module_path DOT)? field_name
  public static boolean field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD, "<field>");
    r = field_0(b, l + 1);
    r = r && field_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (module_path DOT)?
  private static boolean field_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_0")) return false;
    field_0_0(b, l + 1);
    return true;
  }

  // module_path DOT
  private static boolean field_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DOT DOT DOT field_name
  //     | field_name COLON expr
  public static boolean field_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_DECL, "<field decl>");
    r = field_decl_0(b, l + 1);
    if (!r) r = field_decl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // DOT DOT DOT field_name
  private static boolean field_decl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_decl_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, DOT, DOT);
    r = r && field_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // field_name COLON expr
  private static boolean field_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_decl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = field_name(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STRING | LIDENT
  public static boolean field_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_name")) return false;
    if (!nextTokenIs(b, "<field name>", LIDENT, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_NAME, "<field name>");
    r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, LIDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MUTABLE? field_name (COLON type_expr)?
  public static boolean field_type_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_type_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_TYPE_DECL, "<field type decl>");
    r = field_type_decl_0(b, l + 1);
    r = r && field_name(b, l + 1);
    r = r && field_type_decl_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // MUTABLE?
  private static boolean field_type_decl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_type_decl_0")) return false;
    consumeToken(b, MUTABLE);
    return true;
  }

  // (COLON type_expr)?
  private static boolean field_type_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_type_decl_2")) return false;
    field_type_decl_2_0(b, l + 1);
    return true;
  }

  // COLON type_expr
  private static boolean field_type_decl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_type_decl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INCLUDE module_expr (module_expr)* SEMI
  public static boolean include_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_statement")) return false;
    if (!nextTokenIs(b, INCLUDE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INCLUDE);
    r = r && module_expr(b, l + 1);
    r = r && include_statement_2(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, INCLUDE_STATEMENT, r);
    return r;
  }

  // (module_expr)*
  private static boolean include_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_statement_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!include_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "include_statement_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (module_expr)
  private static boolean include_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET ARROBASE ARROBASE attribute_id_rec attribute_payload? RBRACKET
  public static boolean item_attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_attribute")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, ARROBASE, ARROBASE);
    r = r && attribute_id_rec(b, l + 1);
    r = r && item_attribute_4(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, ITEM_ATTRIBUTE, r);
    return r;
  }

  // attribute_payload?
  private static boolean item_attribute_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_attribute_4")) return false;
    attribute_payload(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // start_tag tag_property* ( AUTO_CLOSE_TAG | GT jsxContent* end_tag )
  public static boolean jsx(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsx")) return false;
    if (!nextTokenIs(b, LT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = start_tag(b, l + 1);
    r = r && jsx_1(b, l + 1);
    r = r && jsx_2(b, l + 1);
    exit_section_(b, m, JSX, r);
    return r;
  }

  // tag_property*
  private static boolean jsx_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsx_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!tag_property(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "jsx_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // AUTO_CLOSE_TAG | GT jsxContent* end_tag
  private static boolean jsx_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsx_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AUTO_CLOSE_TAG);
    if (!r) r = jsx_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // GT jsxContent* end_tag
  private static boolean jsx_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsx_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GT);
    r = r && jsx_2_1_1(b, l + 1);
    r = r && end_tag(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // jsxContent*
  private static boolean jsx_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsx_2_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!jsxContent(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "jsx_2_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // COMMENT
  //     | jsx
  //     | expr
  public static boolean jsxContent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsxContent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JSX_CONTENT, "<jsx content>");
    r = consumeToken(b, COMMENT);
    if (!r) r = jsx(b, l + 1);
    if (!r) r = expr(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LIDENT
  public static boolean label_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "label_name")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIDENT);
    exit_section_(b, m, LABEL_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // LET REC? let_name (
  //           (COLON type_expr)? EQ (
  //               FUN value_name parameter* ARROW let_binding_body
  //             | FUN pattern_matching+
  //             | let_binding_body
  //           )
  //         | parameter* (COLON type_expr)? ARROW let_binding_body/*scoped_expr*/
  //     )
  public static boolean let_binding(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding")) return false;
    if (!nextTokenIs(b, LET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LET);
    r = r && let_binding_1(b, l + 1);
    r = r && let_name(b, l + 1);
    r = r && let_binding_3(b, l + 1);
    exit_section_(b, m, LET_BINDING, r);
    return r;
  }

  // REC?
  private static boolean let_binding_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_1")) return false;
    consumeToken(b, REC);
    return true;
  }

  // (COLON type_expr)? EQ (
  //               FUN value_name parameter* ARROW let_binding_body
  //             | FUN pattern_matching+
  //             | let_binding_body
  //           )
  //         | parameter* (COLON type_expr)? ARROW let_binding_body
  private static boolean let_binding_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_3_0(b, l + 1);
    if (!r) r = let_binding_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COLON type_expr)? EQ (
  //               FUN value_name parameter* ARROW let_binding_body
  //             | FUN pattern_matching+
  //             | let_binding_body
  //           )
  private static boolean let_binding_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_3_0_0(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && let_binding_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COLON type_expr)?
  private static boolean let_binding_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_0")) return false;
    let_binding_3_0_0_0(b, l + 1);
    return true;
  }

  // COLON type_expr
  private static boolean let_binding_3_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FUN value_name parameter* ARROW let_binding_body
  //             | FUN pattern_matching+
  //             | let_binding_body
  private static boolean let_binding_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_3_0_2_0(b, l + 1);
    if (!r) r = let_binding_3_0_2_1(b, l + 1);
    if (!r) r = let_binding_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FUN value_name parameter* ARROW let_binding_body
  private static boolean let_binding_3_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FUN);
    r = r && value_name(b, l + 1);
    r = r && let_binding_3_0_2_0_2(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && let_binding_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter*
  private static boolean let_binding_3_0_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_2_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!parameter(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_binding_3_0_2_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // FUN pattern_matching+
  private static boolean let_binding_3_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FUN);
    r = r && let_binding_3_0_2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // pattern_matching+
  private static boolean let_binding_3_0_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_0_2_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pattern_matching(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!pattern_matching(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_binding_3_0_2_1_1", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter* (COLON type_expr)? ARROW let_binding_body
  private static boolean let_binding_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_3_1_0(b, l + 1);
    r = r && let_binding_3_1_1(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && let_binding_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter*
  private static boolean let_binding_3_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_1_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!parameter(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_binding_3_1_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (COLON type_expr)?
  private static boolean let_binding_3_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_1_1")) return false;
    let_binding_3_1_1_0(b, l + 1);
    return true;
  }

  // COLON type_expr
  private static boolean let_binding_3_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_3_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE expr (SEMI expr)* RBRACE
  //     | expr
  static boolean let_binding_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_body_0(b, l + 1);
    if (!r) r = expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE expr (SEMI expr)* RBRACE
  private static boolean let_binding_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && expr(b, l + 1);
    r = r && let_binding_body_0_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SEMI expr)*
  private static boolean let_binding_body_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!let_binding_body_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_binding_body_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // SEMI expr
  private static boolean let_binding_body_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE field (COMMA field)* RBRACE
  static boolean let_destructuring(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_destructuring")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && field(b, l + 1);
    r = r && let_destructuring_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA field)*
  private static boolean let_destructuring_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_destructuring_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!let_destructuring_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_destructuring_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA field
  private static boolean let_destructuring_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_destructuring_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // tuple_value | LPAREN RPAREN | let_destructuring | value_name
  public static boolean let_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LET_NAME, "<let name>");
    r = tuple_value(b, l + 1);
    if (!r) r = parseTokens(b, 0, LPAREN, RPAREN);
    if (!r) r = let_destructuring(b, l + 1);
    if (!r) r = value_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // let_binding SEMI
  public static boolean let_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_statement")) return false;
    if (!nextTokenIs(b, LET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, LET_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // (  module_statement
  //     | external_statement
  //     | include_statement
  //     | type_statement
  //     | let_statement )*
  public static boolean module_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_body")) return false;
    Marker m = enter_section_(b, l, _NONE_, MODULE_BODY, "<module body>");
    int c = current_position_(b);
    while (true) {
      if (!module_body_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "module_body", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // module_statement
  //     | external_statement
  //     | include_statement
  //     | type_statement
  //     | let_statement
  private static boolean module_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_statement(b, l + 1);
    if (!r) r = external_statement(b, l + 1);
    if (!r) r = include_statement(b, l + 1);
    if (!r) r = type_statement(b, l + 1);
    if (!r) r = let_statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // module_path
  static boolean module_expr(PsiBuilder b, int l) {
    return module_path(b, l + 1);
  }

  /* ********************************************************** */
  // UIDENT
  public static boolean module_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_name")) return false;
    if (!nextTokenIs(b, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UIDENT);
    exit_section_(b, m, MODULE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // module_name (DOT module_name)*
  public static boolean module_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path")) return false;
    if (!nextTokenIs(b, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_name(b, l + 1);
    r = r && module_path_1(b, l + 1);
    exit_section_(b, m, MODULE_PATH, r);
    return r;
  }

  // (DOT module_name)*
  private static boolean module_path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!module_path_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "module_path_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DOT module_name
  private static boolean module_path_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_path_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && module_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // MODULE module_name EQ (module_path/*alias*/ | LBRACE module_body RBRACE) SEMI
  public static boolean module_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_statement")) return false;
    if (!nextTokenIs(b, MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MODULE);
    r = r && module_name(b, l + 1);
    r = r && consumeToken(b, EQ);
    r = r && module_statement_3(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, MODULE_STATEMENT, r);
    return r;
  }

  // module_path/*alias*/ | LBRACE module_body RBRACE
  private static boolean module_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_statement_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path(b, l + 1);
    if (!r) r = module_statement_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE module_body RBRACE
  private static boolean module_statement_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_statement_3_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && module_body(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // OPEN module_path SEMI
  public static boolean open_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "open_statement")) return false;
    if (!nextTokenIs(b, OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPEN);
    r = r && module_path(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, OPEN_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // label_name? SHORTCUT LPAREN? value_name (COLON type_expr)? RPAREN? (EQ (constant | QUESTION_MARK))?
  //     | parameter_expr
  //     | LPAREN parameter_expr (COLON type_expr)? RPAREN
  //     | LBRACE field (COMMA field)* RBRACE
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, "<parameter>");
    r = parameter_0(b, l + 1);
    if (!r) r = parameter_expr(b, l + 1);
    if (!r) r = parameter_2(b, l + 1);
    if (!r) r = parameter_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // label_name? SHORTCUT LPAREN? value_name (COLON type_expr)? RPAREN? (EQ (constant | QUESTION_MARK))?
  private static boolean parameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_0(b, l + 1);
    r = r && consumeToken(b, SHORTCUT);
    r = r && parameter_0_2(b, l + 1);
    r = r && value_name(b, l + 1);
    r = r && parameter_0_4(b, l + 1);
    r = r && parameter_0_5(b, l + 1);
    r = r && parameter_0_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // label_name?
  private static boolean parameter_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_0")) return false;
    label_name(b, l + 1);
    return true;
  }

  // LPAREN?
  private static boolean parameter_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2")) return false;
    consumeToken(b, LPAREN);
    return true;
  }

  // (COLON type_expr)?
  private static boolean parameter_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4")) return false;
    parameter_0_4_0(b, l + 1);
    return true;
  }

  // COLON type_expr
  private static boolean parameter_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // RPAREN?
  private static boolean parameter_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_5")) return false;
    consumeToken(b, RPAREN);
    return true;
  }

  // (EQ (constant | QUESTION_MARK))?
  private static boolean parameter_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_6")) return false;
    parameter_0_6_0(b, l + 1);
    return true;
  }

  // EQ (constant | QUESTION_MARK)
  private static boolean parameter_0_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && parameter_0_6_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // constant | QUESTION_MARK
  private static boolean parameter_0_6_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_6_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = constant(b, l + 1);
    if (!r) r = consumeToken(b, QUESTION_MARK);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN parameter_expr (COLON type_expr)? RPAREN
  private static boolean parameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && parameter_expr(b, l + 1);
    r = r && parameter_2_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COLON type_expr)?
  private static boolean parameter_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2_2")) return false;
    parameter_2_2_0(b, l + 1);
    return true;
  }

  // COLON type_expr
  private static boolean parameter_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE field (COMMA field)* RBRACE
  private static boolean parameter_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && field(b, l + 1);
    r = r && parameter_3_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA field)*
  private static boolean parameter_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!parameter_3_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_3_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA field
  private static boolean parameter_3_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // constant
  //     | value_name
  //     | LBRACE field (COMMA field)* RBRACE (AS value_name)?
  public static boolean parameter_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_EXPR, "<parameter expr>");
    r = constant(b, l + 1);
    if (!r) r = value_name(b, l + 1);
    if (!r) r = parameter_expr_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LBRACE field (COMMA field)* RBRACE (AS value_name)?
  private static boolean parameter_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && field(b, l + 1);
    r = r && parameter_expr_2_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    r = r && parameter_expr_2_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA field)*
  private static boolean parameter_expr_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_expr_2_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!parameter_expr_2_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_expr_2_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA field
  private static boolean parameter_expr_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_expr_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (AS value_name)?
  private static boolean parameter_expr_2_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_expr_2_4")) return false;
    parameter_expr_2_4_0(b, l + 1);
    return true;
  }

  // AS value_name
  private static boolean parameter_expr_2_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_expr_2_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AS);
    r = r && value_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NONE
  //     | SOME (constant | value_path)
  //     | LBRACKET pattern (COMMA pattern)* (COMMA DOT DOT DOT value_path)? RBRACKET
  //     | BACKTICK tag_name pattern
  //     | tuple_value
  //     | value_path
  //     | module_path
  //     | constant
  public static boolean pattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATTERN, "<pattern>");
    r = consumeToken(b, NONE);
    if (!r) r = pattern_1(b, l + 1);
    if (!r) r = pattern_2(b, l + 1);
    if (!r) r = pattern_3(b, l + 1);
    if (!r) r = tuple_value(b, l + 1);
    if (!r) r = value_path(b, l + 1);
    if (!r) r = module_path(b, l + 1);
    if (!r) r = constant(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SOME (constant | value_path)
  private static boolean pattern_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SOME);
    r = r && pattern_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // constant | value_path
  private static boolean pattern_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = constant(b, l + 1);
    if (!r) r = value_path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACKET pattern (COMMA pattern)* (COMMA DOT DOT DOT value_path)? RBRACKET
  private static boolean pattern_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && pattern(b, l + 1);
    r = r && pattern_2_2(b, l + 1);
    r = r && pattern_2_3(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA pattern)*
  private static boolean pattern_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_2_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!pattern_2_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pattern_2_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA pattern
  private static boolean pattern_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && pattern(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA DOT DOT DOT value_path)?
  private static boolean pattern_2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_2_3")) return false;
    pattern_2_3_0(b, l + 1);
    return true;
  }

  // COMMA DOT DOT DOT value_path
  private static boolean pattern_2_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_2_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, DOT, DOT, DOT);
    r = r && value_path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BACKTICK tag_name pattern
  private static boolean pattern_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BACKTICK);
    r = r && tag_name(b, l + 1);
    r = r && pattern(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PIPE pattern ARROW sequenced_expr
  public static boolean pattern_matching(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_matching")) return false;
    if (!nextTokenIs(b, PIPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PIPE);
    r = r && pattern(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && sequenced_expr(b, l + 1);
    exit_section_(b, m, PATTERN_MATCHING, r);
    return r;
  }

  /* ********************************************************** */
  // PIPE UIDENT (PIPE UIDENT)*
  public static boolean poly_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "poly_type")) return false;
    if (!nextTokenIs(b, PIPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PIPE, UIDENT);
    r = r && poly_type_2(b, l + 1);
    exit_section_(b, m, POLY_TYPE, r);
    return r;
  }

  // (PIPE UIDENT)*
  private static boolean poly_type_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "poly_type_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!poly_type_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "poly_type_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // PIPE UIDENT
  private static boolean poly_type_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "poly_type_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PIPE, UIDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACKET PIPE tag_spec_first (PIPE tag_spec_first)* RBRACKET
  public static boolean polymorphic_variant_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "polymorphic_variant_type")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, PIPE);
    r = r && tag_spec_first(b, l + 1);
    r = r && polymorphic_variant_type_3(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, POLYMORPHIC_VARIANT_TYPE, r);
    return r;
  }

  // (PIPE tag_spec_first)*
  private static boolean polymorphic_variant_type_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "polymorphic_variant_type_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!polymorphic_variant_type_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "polymorphic_variant_type_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // PIPE tag_spec_first
  private static boolean polymorphic_variant_type_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "polymorphic_variant_type_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PIPE);
    r = r && tag_spec_first(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ( COMMENT | open_statement | module_statement | include_statement | let_statement | expr_statement | type_statement | external_statement )*
  static boolean reasonFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reasonFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!reasonFile_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reasonFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMENT | open_statement | module_statement | include_statement | let_statement | expr_statement | type_statement | external_statement
  private static boolean reasonFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reasonFile_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT);
    if (!r) r = open_statement(b, l + 1);
    if (!r) r = module_statement(b, l + 1);
    if (!r) r = include_statement(b, l + 1);
    if (!r) r = let_statement(b, l + 1);
    if (!r) r = expr_statement(b, l + 1);
    if (!r) r = type_statement(b, l + 1);
    if (!r) r = external_statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE field_decl (COMMA field_decl)* RBRACE
  public static boolean record_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_decl")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && field_decl(b, l + 1);
    r = r && record_decl_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, RECORD_DECL, r);
    return r;
  }

  // (COMMA field_decl)*
  private static boolean record_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_decl_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!record_decl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "record_decl_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA field_decl
  private static boolean record_decl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_decl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && field_decl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE (DOT DOT?)? (field_type_decl (COMMA field_type_decl)*)? RBRACE
  static boolean record_type_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && record_type_decl_1(b, l + 1);
    r = r && record_type_decl_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT DOT?)?
  private static boolean record_type_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_1")) return false;
    record_type_decl_1_0(b, l + 1);
    return true;
  }

  // DOT DOT?
  private static boolean record_type_decl_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && record_type_decl_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DOT?
  private static boolean record_type_decl_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_1_0_1")) return false;
    consumeToken(b, DOT);
    return true;
  }

  // (field_type_decl (COMMA field_type_decl)*)?
  private static boolean record_type_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_2")) return false;
    record_type_decl_2_0(b, l + 1);
    return true;
  }

  // field_type_decl (COMMA field_type_decl)*
  private static boolean record_type_decl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = field_type_decl(b, l + 1);
    r = r && record_type_decl_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA field_type_decl)*
  private static boolean record_type_decl_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_2_0_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!record_type_decl_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "record_type_decl_2_0_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA field_type_decl
  private static boolean record_type_decl_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_type_decl_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && field_type_decl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // value_expr (
  //               expr
  //             | LPAREN expr RPAREN
  //             | EQEQEQ expr
  //             | EQEQ expr
  //             | EQ expr
  //             | QUESTION_MARK expr
  //             | PLUS expr
  //             | PLUSDOT expr
  //             | MINUS expr
  //             | MINUSDOT expr
  //             | MUL expr
  //             | MULDOT expr
  //             | SLASH expr
  //             | SLASHDOT expr
  //             | STAR expr
  //             | STARDOT expr
  //             | LT expr
  //             | GT expr
  //             | CARRET expr
  //             | COMMA expr
  //             | COLON expr )*
  static boolean recursive_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_expr(b, l + 1);
    r = r && recursive_expr_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (
  //               expr
  //             | LPAREN expr RPAREN
  //             | EQEQEQ expr
  //             | EQEQ expr
  //             | EQ expr
  //             | QUESTION_MARK expr
  //             | PLUS expr
  //             | PLUSDOT expr
  //             | MINUS expr
  //             | MINUSDOT expr
  //             | MUL expr
  //             | MULDOT expr
  //             | SLASH expr
  //             | SLASHDOT expr
  //             | STAR expr
  //             | STARDOT expr
  //             | LT expr
  //             | GT expr
  //             | CARRET expr
  //             | COMMA expr
  //             | COLON expr )*
  private static boolean recursive_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!recursive_expr_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "recursive_expr_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // expr
  //             | LPAREN expr RPAREN
  //             | EQEQEQ expr
  //             | EQEQ expr
  //             | EQ expr
  //             | QUESTION_MARK expr
  //             | PLUS expr
  //             | PLUSDOT expr
  //             | MINUS expr
  //             | MINUSDOT expr
  //             | MUL expr
  //             | MULDOT expr
  //             | SLASH expr
  //             | SLASHDOT expr
  //             | STAR expr
  //             | STARDOT expr
  //             | LT expr
  //             | GT expr
  //             | CARRET expr
  //             | COMMA expr
  //             | COLON expr
  private static boolean recursive_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    if (!r) r = recursive_expr_1_0_1(b, l + 1);
    if (!r) r = recursive_expr_1_0_2(b, l + 1);
    if (!r) r = recursive_expr_1_0_3(b, l + 1);
    if (!r) r = recursive_expr_1_0_4(b, l + 1);
    if (!r) r = recursive_expr_1_0_5(b, l + 1);
    if (!r) r = recursive_expr_1_0_6(b, l + 1);
    if (!r) r = recursive_expr_1_0_7(b, l + 1);
    if (!r) r = recursive_expr_1_0_8(b, l + 1);
    if (!r) r = recursive_expr_1_0_9(b, l + 1);
    if (!r) r = recursive_expr_1_0_10(b, l + 1);
    if (!r) r = recursive_expr_1_0_11(b, l + 1);
    if (!r) r = recursive_expr_1_0_12(b, l + 1);
    if (!r) r = recursive_expr_1_0_13(b, l + 1);
    if (!r) r = recursive_expr_1_0_14(b, l + 1);
    if (!r) r = recursive_expr_1_0_15(b, l + 1);
    if (!r) r = recursive_expr_1_0_16(b, l + 1);
    if (!r) r = recursive_expr_1_0_17(b, l + 1);
    if (!r) r = recursive_expr_1_0_18(b, l + 1);
    if (!r) r = recursive_expr_1_0_19(b, l + 1);
    if (!r) r = recursive_expr_1_0_20(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean recursive_expr_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQEQEQ expr
  private static boolean recursive_expr_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQEQEQ);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQEQ expr
  private static boolean recursive_expr_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQEQ);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ expr
  private static boolean recursive_expr_1_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // QUESTION_MARK expr
  private static boolean recursive_expr_1_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUESTION_MARK);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PLUS expr
  private static boolean recursive_expr_1_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_6")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUS);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PLUSDOT expr
  private static boolean recursive_expr_1_0_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_7")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PLUSDOT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MINUS expr
  private static boolean recursive_expr_1_0_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MINUS);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MINUSDOT expr
  private static boolean recursive_expr_1_0_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_9")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MINUSDOT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MUL expr
  private static boolean recursive_expr_1_0_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_10")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MUL);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MULDOT expr
  private static boolean recursive_expr_1_0_11(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_11")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MULDOT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SLASH expr
  private static boolean recursive_expr_1_0_12(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_12")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SLASH);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SLASHDOT expr
  private static boolean recursive_expr_1_0_13(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_13")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SLASHDOT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // STAR expr
  private static boolean recursive_expr_1_0_14(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_14")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // STARDOT expr
  private static boolean recursive_expr_1_0_15(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_15")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STARDOT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LT expr
  private static boolean recursive_expr_1_0_16(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_16")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // GT expr
  private static boolean recursive_expr_1_0_17(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_17")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GT);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CARRET expr
  private static boolean recursive_expr_1_0_18(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_18")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CARRET);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA expr
  private static boolean recursive_expr_1_0_19(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_19")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COLON expr
  private static boolean recursive_expr_1_0_20(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursive_expr_1_0_20")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr (PIPE_FORWARD expr)? (SEMI expr (PIPE_FORWARD expr)?)*
  static boolean sequenced_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    r = r && sequenced_expr_1(b, l + 1);
    r = r && sequenced_expr_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PIPE_FORWARD expr)?
  private static boolean sequenced_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr_1")) return false;
    sequenced_expr_1_0(b, l + 1);
    return true;
  }

  // PIPE_FORWARD expr
  private static boolean sequenced_expr_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PIPE_FORWARD);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SEMI expr (PIPE_FORWARD expr)?)*
  private static boolean sequenced_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!sequenced_expr_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sequenced_expr_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // SEMI expr (PIPE_FORWARD expr)?
  private static boolean sequenced_expr_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    r = r && expr(b, l + 1);
    r = r && sequenced_expr_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PIPE_FORWARD expr)?
  private static boolean sequenced_expr_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr_2_0_2")) return false;
    sequenced_expr_2_0_2_0(b, l + 1);
    return true;
  }

  // PIPE_FORWARD expr
  private static boolean sequenced_expr_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenced_expr_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PIPE_FORWARD);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // constant
  //   | MINUS INT
  //   | MINUS FLOAT
  //   | PLUS INT
  //   | PLUS FLOAT
  public static boolean signed_constant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "signed_constant")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIGNED_CONSTANT, "<signed constant>");
    r = constant(b, l + 1);
    if (!r) r = parseTokens(b, 0, MINUS, INT);
    if (!r) r = parseTokens(b, 0, MINUS, FLOAT);
    if (!r) r = parseTokens(b, 0, PLUS, INT);
    if (!r) r = parseTokens(b, 0, PLUS, FLOAT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // UNIT
  //     | OPTION (LPAREN type_expr RPAREN | type_constr)
  //     | LIST type_constr
  //     | QUOTE type_constr_name
  //     | poly_type
  //     | polymorphic_variant_type
  //     | tuple_type_decl
  //     | record_type_decl
  //     | type_constr (record_type_decl | QUOTE LIDENT | value_path)?
  static boolean single_type_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UNIT);
    if (!r) r = single_type_expr_1(b, l + 1);
    if (!r) r = single_type_expr_2(b, l + 1);
    if (!r) r = single_type_expr_3(b, l + 1);
    if (!r) r = poly_type(b, l + 1);
    if (!r) r = polymorphic_variant_type(b, l + 1);
    if (!r) r = tuple_type_decl(b, l + 1);
    if (!r) r = record_type_decl(b, l + 1);
    if (!r) r = single_type_expr_8(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OPTION (LPAREN type_expr RPAREN | type_constr)
  private static boolean single_type_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OPTION);
    r = r && single_type_expr_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN type_expr RPAREN | type_constr
  private static boolean single_type_expr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = single_type_expr_1_1_0(b, l + 1);
    if (!r) r = type_constr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN type_expr RPAREN
  private static boolean single_type_expr_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && type_expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // LIST type_constr
  private static boolean single_type_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIST);
    r = r && type_constr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // QUOTE type_constr_name
  private static boolean single_type_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUOTE);
    r = r && type_constr_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // type_constr (record_type_decl | QUOTE LIDENT | value_path)?
  private static boolean single_type_expr_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type_constr(b, l + 1);
    r = r && single_type_expr_8_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (record_type_decl | QUOTE LIDENT | value_path)?
  private static boolean single_type_expr_8_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_8_1")) return false;
    single_type_expr_8_1_0(b, l + 1);
    return true;
  }

  // record_type_decl | QUOTE LIDENT | value_path
  private static boolean single_type_expr_8_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_type_expr_8_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = record_type_decl(b, l + 1);
    if (!r) r = parseTokens(b, 0, QUOTE, LIDENT);
    if (!r) r = value_path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LT tag_name
  public static boolean start_tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "start_tag")) return false;
    if (!nextTokenIs(b, LT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LT);
    r = r && tag_name(b, l + 1);
    exit_section_(b, m, START_TAG, r);
    return r;
  }

  /* ********************************************************** */
  // LIDENT | UIDENT
  public static boolean tag_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_name")) return false;
    if (!nextTokenIs(b, "<tag name>", LIDENT, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TAG_NAME, "<tag name>");
    r = consumeToken(b, LIDENT);
    if (!r) r = consumeToken(b, UIDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // value_name (EQ (LPAREN expr RPAREN | record_decl | constant | value_name (DOT value_name)*))?
  public static boolean tag_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_name(b, l + 1);
    r = r && tag_property_1(b, l + 1);
    exit_section_(b, m, TAG_PROPERTY, r);
    return r;
  }

  // (EQ (LPAREN expr RPAREN | record_decl | constant | value_name (DOT value_name)*))?
  private static boolean tag_property_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1")) return false;
    tag_property_1_0(b, l + 1);
    return true;
  }

  // EQ (LPAREN expr RPAREN | record_decl | constant | value_name (DOT value_name)*)
  private static boolean tag_property_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && tag_property_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN | record_decl | constant | value_name (DOT value_name)*
  private static boolean tag_property_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tag_property_1_0_1_0(b, l + 1);
    if (!r) r = record_decl(b, l + 1);
    if (!r) r = constant(b, l + 1);
    if (!r) r = tag_property_1_0_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean tag_property_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // value_name (DOT value_name)*
  private static boolean tag_property_1_0_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1_0_1_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_name(b, l + 1);
    r = r && tag_property_1_0_1_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT value_name)*
  private static boolean tag_property_1_0_1_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1_0_1_3_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!tag_property_1_0_1_3_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tag_property_1_0_1_3_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DOT value_name
  private static boolean tag_property_1_0_1_3_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_1_0_1_3_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && value_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BACKTICK tag_name
  public static boolean tag_spec_first(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_spec_first")) return false;
    if (!nextTokenIs(b, BACKTICK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BACKTICK);
    r = r && tag_name(b, l + 1);
    exit_section_(b, m, TAG_SPEC_FIRST, r);
    return r;
  }

  /* ********************************************************** */
  // constant | value_name | SOME value_name | NONE | UNDERSCORE
  static boolean tuple_field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_field")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = constant(b, l + 1);
    if (!r) r = value_name(b, l + 1);
    if (!r) r = tuple_field_2(b, l + 1);
    if (!r) r = consumeToken(b, NONE);
    if (!r) r = consumeToken(b, UNDERSCORE);
    exit_section_(b, m, null, r);
    return r;
  }

  // SOME value_name
  private static boolean tuple_field_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_field_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SOME);
    r = r && value_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN type_expr (COMMA type_expr)* RPAREN
  static boolean tuple_type_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_type_decl")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && type_expr(b, l + 1);
    r = r && tuple_type_decl_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA type_expr)*
  private static boolean tuple_type_decl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_type_decl_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!tuple_type_decl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tuple_type_decl_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA type_expr
  private static boolean tuple_type_decl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_type_decl_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN tuple_field (COMMA tuple_field)* RPAREN
  static boolean tuple_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_value")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && tuple_field(b, l + 1);
    r = r && tuple_value_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA tuple_field)*
  private static boolean tuple_value_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_value_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!tuple_value_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tuple_value_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA tuple_field
  private static boolean tuple_value_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_value_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && tuple_field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (module_path DOT)? type_constr_name
  public static boolean type_constr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_constr")) return false;
    if (!nextTokenIs(b, "<type constr>", LIDENT, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_CONSTR, "<type constr>");
    r = type_constr_0(b, l + 1);
    r = r && type_constr_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (module_path DOT)?
  private static boolean type_constr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_constr_0")) return false;
    type_constr_0_0(b, l + 1);
    return true;
  }

  // module_path DOT
  private static boolean type_constr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_constr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LIDENT
  public static boolean type_constr_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_constr_name")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIDENT);
    exit_section_(b, m, TYPE_CONSTR_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // (label_name SHORTCUT)? single_type_expr+ attribute? (ARROW (label_name SHORTCUT)? single_type_expr+ attribute?)*
  public static boolean type_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, TYPE_EXPR, "<type expr>");
    r = type_expr_0(b, l + 1);
    r = r && type_expr_1(b, l + 1);
    r = r && type_expr_2(b, l + 1);
    r = r && type_expr_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (label_name SHORTCUT)?
  private static boolean type_expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_0")) return false;
    type_expr_0_0(b, l + 1);
    return true;
  }

  // label_name SHORTCUT
  private static boolean type_expr_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = label_name(b, l + 1);
    r = r && consumeToken(b, SHORTCUT);
    exit_section_(b, m, null, r);
    return r;
  }

  // single_type_expr+
  private static boolean type_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = single_type_expr(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!single_type_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_expr_1", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // attribute?
  private static boolean type_expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_2")) return false;
    attribute(b, l + 1);
    return true;
  }

  // (ARROW (label_name SHORTCUT)? single_type_expr+ attribute?)*
  private static boolean type_expr_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!type_expr_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_expr_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ARROW (label_name SHORTCUT)? single_type_expr+ attribute?
  private static boolean type_expr_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ARROW);
    r = r && type_expr_3_0_1(b, l + 1);
    r = r && type_expr_3_0_2(b, l + 1);
    r = r && type_expr_3_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (label_name SHORTCUT)?
  private static boolean type_expr_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_3_0_1")) return false;
    type_expr_3_0_1_0(b, l + 1);
    return true;
  }

  // label_name SHORTCUT
  private static boolean type_expr_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = label_name(b, l + 1);
    r = r && consumeToken(b, SHORTCUT);
    exit_section_(b, m, null, r);
    return r;
  }

  // single_type_expr+
  private static boolean type_expr_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_3_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = single_type_expr(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!single_type_expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_expr_3_0_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // attribute?
  private static boolean type_expr_3_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr_3_0_3")) return false;
    attribute(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // TYPE type_constr_name (QUOTE LIDENT)? (EQ type_expr)? item_attribute? SEMI
  public static boolean type_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement")) return false;
    if (!nextTokenIs(b, TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE);
    r = r && type_constr_name(b, l + 1);
    r = r && type_statement_2(b, l + 1);
    r = r && type_statement_3(b, l + 1);
    r = r && type_statement_4(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, TYPE_STATEMENT, r);
    return r;
  }

  // (QUOTE LIDENT)?
  private static boolean type_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement_2")) return false;
    type_statement_2_0(b, l + 1);
    return true;
  }

  // QUOTE LIDENT
  private static boolean type_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, QUOTE, LIDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQ type_expr)?
  private static boolean type_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement_3")) return false;
    type_statement_3_0(b, l + 1);
    return true;
  }

  // EQ type_expr
  private static boolean type_statement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ);
    r = r && type_expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // item_attribute?
  private static boolean type_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement_4")) return false;
    item_attribute(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // SHARP SHARP value_name (SHARP SHARP value_name)*
  public static boolean untyped_object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "untyped_object")) return false;
    if (!nextTokenIs(b, SHARP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SHARP, SHARP);
    r = r && value_name(b, l + 1);
    r = r && untyped_object_3(b, l + 1);
    exit_section_(b, m, UNTYPED_OBJECT, r);
    return r;
  }

  // (SHARP SHARP value_name)*
  private static boolean untyped_object_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "untyped_object_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!untyped_object_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "untyped_object_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // SHARP SHARP value_name
  private static boolean untyped_object_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "untyped_object_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SHARP, SHARP);
    r = r && value_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // signed_constant
  //     | value_path (DOT value_path)* (untyped_object () | argument*)
  //     | module_path
  public static boolean value_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_EXPR, "<value expr>");
    r = signed_constant(b, l + 1);
    if (!r) r = value_expr_1(b, l + 1);
    if (!r) r = module_path(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value_path (DOT value_path)* (untyped_object () | argument*)
  private static boolean value_expr_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_path(b, l + 1);
    r = r && value_expr_1_1(b, l + 1);
    r = r && value_expr_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT value_path)*
  private static boolean value_expr_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr_1_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!value_expr_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "value_expr_1_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // DOT value_path
  private static boolean value_expr_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && value_path(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // untyped_object () | argument*
  private static boolean value_expr_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_expr_1_2_0(b, l + 1);
    if (!r) r = value_expr_1_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // untyped_object ()
  private static boolean value_expr_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = untyped_object(b, l + 1);
    r = r && value_expr_1_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ()
  private static boolean value_expr_1_2_0_1(PsiBuilder b, int l) {
    return true;
  }

  // argument*
  private static boolean value_expr_1_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_expr_1_2_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "value_expr_1_2_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // LIDENT
  public static boolean value_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_name")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIDENT);
    exit_section_(b, m, VALUE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // value_name
  //     | module_path DOT value_name
  public static boolean value_path(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_path")) return false;
    if (!nextTokenIs(b, "<value path>", LIDENT, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE_PATH, "<value path>");
    r = value_name(b, l + 1);
    if (!r) r = value_path_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // module_path DOT value_name
  private static boolean value_path_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_path_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path(b, l + 1);
    r = r && consumeToken(b, DOT);
    r = r && value_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
