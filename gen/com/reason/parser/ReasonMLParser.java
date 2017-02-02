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
    else if (t == CONSTANT) {
      r = constant(b, 0);
    }
    else if (t == END_TAG) {
      r = end_tag(b, 0);
    }
    else if (t == EXPR) {
      r = expr(b, 0);
    }
    else if (t == EXPR_STATEMENT) {
      r = expr_statement(b, 0);
    }
    else if (t == FIELD) {
      r = field(b, 0);
    }
    else if (t == FIELD_NAME) {
      r = field_name(b, 0);
    }
    else if (t == INCLUDE_STATEMENT) {
      r = include_statement(b, 0);
    }
    else if (t == JSX) {
      r = jsx(b, 0);
    }
    else if (t == JSX_CONTENT) {
      r = jsxContent(b, 0);
    }
    else if (t == LET_BINDING) {
      r = let_binding(b, 0);
    }
    else if (t == LET_BINDING_BODY) {
      r = let_binding_body(b, 0);
    }
    else if (t == MODULE_BODY) {
      r = module_body(b, 0);
    }
    else if (t == MODULE_EXPR) {
      r = module_expr(b, 0);
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
    else if (t == PARAMETER) {
      r = parameter(b, 0);
    }
    else if (t == PATTERN_EXPR) {
      r = pattern_expr(b, 0);
    }
    else if (t == RECORD_FIELD) {
      r = record_field(b, 0);
    }
    else if (t == SHORT_ID) {
      r = short_id(b, 0);
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
    else if (t == TYPE_CONSTR) {
      r = type_constr(b, 0);
    }
    else if (t == TYPE_CONSTR_NAME) {
      r = type_constr_name(b, 0);
    }
    else if (t == TYPE_DEFINITION) {
      r = type_definition(b, 0);
    }
    else if (t == TYPE_EXPR) {
      r = type_expr(b, 0);
    }
    else if (t == TYPE_STATEMENT) {
      r = type_statement(b, 0);
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
  // constant
  //     | value_name (DOT field)?
  //     | jsx
  //     | LBRACE record_field RBRACE
  //     | SHORTCUT value_name
  public static boolean argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARGUMENT, "<argument>");
    r = constant(b, l + 1);
    if (!r) r = argument_1(b, l + 1);
    if (!r) r = jsx(b, l + 1);
    if (!r) r = argument_3(b, l + 1);
    if (!r) r = argument_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value_name (DOT field)?
  private static boolean argument_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_name(b, l + 1);
    r = r && argument_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT field)?
  private static boolean argument_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_1_1")) return false;
    argument_1_1_0(b, l + 1);
    return true;
  }

  // DOT field
  private static boolean argument_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
    r = r && field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE record_field RBRACE
  private static boolean argument_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && record_field(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // SHORTCUT value_name
  private static boolean argument_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SHORTCUT);
    r = r && value_name(b, l + 1);
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
  // constant
  //     | NONE
  //     | value_path argument+
  //     | jsx
  //     | LPAREN expr RPAREN
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR, "<expr>");
    r = constant(b, l + 1);
    if (!r) r = consumeToken(b, NONE);
    if (!r) r = expr_2(b, l + 1);
    if (!r) r = jsx(b, l + 1);
    if (!r) r = expr_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value_path argument+
  private static boolean expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_path(b, l + 1);
    r = r && expr_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // argument+
  private static boolean expr_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = argument(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_2_1", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean expr_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expr+ SEMI
  public static boolean expr_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_STATEMENT, "<expr statement>");
    r = expr_statement_0(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expr+
  private static boolean expr_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!expr(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expr_statement_0", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (module_path DOT)? field_name
  public static boolean field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field")) return false;
    if (!nextTokenIs(b, "<field>", LIDENT, UIDENT)) return false;
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
  // LIDENT
  public static boolean field_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_name")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIDENT);
    exit_section_(b, m, FIELD_NAME, r);
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
  // LPAREN expr RPAREN
  public static boolean jsxContent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsxContent")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, JSX_CONTENT, r);
    return r;
  }

  /* ********************************************************** */
  // LET value_name let_binding_body SEMI
  public static boolean let_binding(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding")) return false;
    if (!nextTokenIs(b, LET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LET);
    r = r && value_name(b, l + 1);
    r = r && let_binding_body(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, LET_BINDING, r);
    return r;
  }

  /* ********************************************************** */
  // EQUAL (expr | FUN)
  //     | (parameter)* ARROW (LBRACE expr (SEMI expr)* RBRACE | expr)
  public static boolean let_binding_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LET_BINDING_BODY, "<let binding body>");
    r = let_binding_body_0(b, l + 1);
    if (!r) r = let_binding_body_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EQUAL (expr | FUN)
  private static boolean let_binding_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUAL);
    r = r && let_binding_body_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expr | FUN
  private static boolean let_binding_body_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expr(b, l + 1);
    if (!r) r = consumeToken(b, FUN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (parameter)* ARROW (LBRACE expr (SEMI expr)* RBRACE | expr)
  private static boolean let_binding_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_body_1_0(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && let_binding_body_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (parameter)*
  private static boolean let_binding_body_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!let_binding_body_1_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_binding_body_1_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // (parameter)
  private static boolean let_binding_body_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE expr (SEMI expr)* RBRACE | expr
  private static boolean let_binding_body_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = let_binding_body_1_2_0(b, l + 1);
    if (!r) r = expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE expr (SEMI expr)* RBRACE
  private static boolean let_binding_body_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && expr(b, l + 1);
    r = r && let_binding_body_1_2_0_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SEMI expr)*
  private static boolean let_binding_body_1_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1_2_0_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!let_binding_body_1_2_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "let_binding_body_1_2_0_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // SEMI expr
  private static boolean let_binding_body_1_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_binding_body_1_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEMI);
    r = r && expr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (  module_statement
  //     | include_statement
  //     | type_statement
  //     | let_binding )*
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
  //     | include_statement
  //     | type_statement
  //     | let_binding
  private static boolean module_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_statement(b, l + 1);
    if (!r) r = include_statement(b, l + 1);
    if (!r) r = type_statement(b, l + 1);
    if (!r) r = let_binding(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // module_path
  public static boolean module_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_expr")) return false;
    if (!nextTokenIs(b, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = module_path(b, l + 1);
    exit_section_(b, m, MODULE_EXPR, r);
    return r;
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
  // MODULE module_name EQUAL LBRACE module_body RBRACE SEMI
  public static boolean module_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_statement")) return false;
    if (!nextTokenIs(b, MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MODULE);
    r = r && module_name(b, l + 1);
    r = r && consumeTokens(b, 0, EQUAL, LBRACE);
    r = r && module_body(b, l + 1);
    r = r && consumeTokens(b, 0, RBRACE, SEMI);
    exit_section_(b, m, MODULE_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // pattern_expr
  //     | SHORTCUT value_name
  //     | LPAREN pattern_expr (DOT type_expr)? RPAREN
  //     | LBRACE field (COMMA field)* RBRACE
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, "<parameter>");
    r = pattern_expr(b, l + 1);
    if (!r) r = parameter_1(b, l + 1);
    if (!r) r = parameter_2(b, l + 1);
    if (!r) r = parameter_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SHORTCUT value_name
  private static boolean parameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SHORTCUT);
    r = r && value_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN pattern_expr (DOT type_expr)? RPAREN
  private static boolean parameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && pattern_expr(b, l + 1);
    r = r && parameter_2_2(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DOT type_expr)?
  private static boolean parameter_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2_2")) return false;
    parameter_2_2_0(b, l + 1);
    return true;
  }

  // DOT type_expr
  private static boolean parameter_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOT);
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
  public static boolean pattern_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pattern_expr")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PATTERN_EXPR, "<pattern expr>");
    r = constant(b, l + 1);
    if (!r) r = value_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ( COMMENT | module_statement | include_statement | let_binding | expr_statement | type_statement )*
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

  // COMMENT | module_statement | include_statement | let_binding | expr_statement | type_statement
  private static boolean reasonFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reasonFile_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT);
    if (!r) r = module_statement(b, l + 1);
    if (!r) r = include_statement(b, l + 1);
    if (!r) r = let_binding(b, l + 1);
    if (!r) r = expr_statement(b, l + 1);
    if (!r) r = type_statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // short_id (COLON short_id)?
  public static boolean record_field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_field")) return false;
    if (!nextTokenIs(b, "<record field>", LIDENT, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RECORD_FIELD, "<record field>");
    r = short_id(b, l + 1);
    r = r && record_field_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COLON short_id)?
  private static boolean record_field_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_field_1")) return false;
    record_field_1_0(b, l + 1);
    return true;
  }

  // COLON short_id
  private static boolean record_field_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "record_field_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && short_id(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // UIDENT | LIDENT
  public static boolean short_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "short_id")) return false;
    if (!nextTokenIs(b, "<short id>", LIDENT, UIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SHORT_ID, "<short id>");
    r = consumeToken(b, UIDENT);
    if (!r) r = consumeToken(b, LIDENT);
    exit_section_(b, l, m, r, false, null);
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
  // value_name EQUAL (LPAREN expr RPAREN | constant)
  public static boolean tag_property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_name(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && tag_property_2(b, l + 1);
    exit_section_(b, m, TAG_PROPERTY, r);
    return r;
  }

  // LPAREN expr RPAREN | constant
  private static boolean tag_property_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tag_property_2_0(b, l + 1);
    if (!r) r = constant(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPAREN expr RPAREN
  private static boolean tag_property_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag_property_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && expr(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // type_constr_name
  public static boolean type_constr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_constr")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type_constr_name(b, l + 1);
    exit_section_(b, m, TYPE_CONSTR, r);
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
  // EQUAL LBRACE record_field (COMMA record_field)* RBRACE
  public static boolean type_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_definition")) return false;
    if (!nextTokenIs(b, EQUAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, EQUAL, LBRACE);
    r = r && record_field(b, l + 1);
    r = r && type_definition_3(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, TYPE_DEFINITION, r);
    return r;
  }

  // (COMMA record_field)*
  private static boolean type_definition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_definition_3")) return false;
    int c = current_position_(b);
    while (true) {
      if (!type_definition_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_definition_3", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // COMMA record_field
  private static boolean type_definition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_definition_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && record_field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // type_constr
  public static boolean type_expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expr")) return false;
    if (!nextTokenIs(b, LIDENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type_constr(b, l + 1);
    exit_section_(b, m, TYPE_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // TYPE short_id type_definition? SEMI
  public static boolean type_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement")) return false;
    if (!nextTokenIs(b, TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE);
    r = r && short_id(b, l + 1);
    r = r && type_statement_2(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, TYPE_STATEMENT, r);
    return r;
  }

  // type_definition?
  private static boolean type_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_statement_2")) return false;
    type_definition(b, l + 1);
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
