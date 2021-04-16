package com.reason.lang.rescript;

import com.intellij.psi.PsiFile;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiIfStatement;
import com.reason.lang.core.psi.impl.PsiTernary;

@SuppressWarnings("ConstantConditions")
public class IfParsingTest extends ResParsingTestCase {
  public void test_basic_if() {
    PsiFile psiFile = parseCode("if (x) { () }");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getCondition());
    assertEquals("(x)", e.getCondition().getText());
    assertEquals("{ () }", e.getThenExpression().getText());
  }

  public void test_if_else() {
    PsiFile psiFile = parseCode("let test = x => if (x) { 1 } else { 2 }");
    PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

    assertNotNull(e);
    assertNotNull(e.getCondition());
    assertEquals("{ 1 }", e.getThenExpression().getText());
    assertEquals("{ 2 }", e.getElseExpression().getText());
  }

  public void test_if_else_noBrace() {
    PsiLet e = firstOfType(parseCode("let test = x => if (x) 1 else 2"), PsiLet.class);

    assertTrue(e.isFunction());
    PsiIfStatement t =
        ORUtil.findImmediateFirstChildOfClass(e.getFunction().getBody(), PsiIfStatement.class);
    assertEquals("(x)", t.getCondition().getText());
    assertEquals("1", t.getThenExpression().getText());
    assertEquals("2", t.getElseExpression().getText());
  }

  public void test_ternary_lident() {
    PsiLet e = firstOfType(parseCode("let _ = a ? b : c"), PsiLet.class);
    PsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), PsiTernary.class);

    assertEquals("a", t.getCondition().getText());
    assertEquals("b", t.getThenExpression().getText());
    assertEquals("c", t.getElseExpression().getText());
  }

  public void test_ternary_parens() {
    PsiLet e = firstOfType(parseCode("let _ = (a) ? b : c"), PsiLet.class);
    PsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), PsiTernary.class);

    assertEquals("(a)", t.getCondition().getText());
    assertEquals("b", t.getThenExpression().getText());
    assertEquals("c", t.getElseExpression().getText());
  }
  /*
    public void test_ternary_cond() {
      PsiFile psiFile = parseCode("let _ = a == a' || (x < y) ? b : c");
      PsiTernary e = firstOfType(psiFile, PsiTernary.class);

      assertNotNull(e);
      assertNotNull(e.getCondition());
      assertEquals("a == a' || (x < y)", e.getCondition().getText());
      assertEquals("b", e.getThenExpression().getText());
      assertEquals("c", e.getElseExpression().getText());
    }
  */
  public void test_ternary_call() {
    PsiLet e = firstOfType(parseCode("let _ = fn(a) ? b : c"), PsiLet.class);
    PsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), PsiTernary.class);

    // assertEquals("fn(a)", t.getCondition().getText());
    assertEquals("b", t.getThenExpression().getText());
    assertEquals("c", t.getElseExpression().getText());
  }
}
