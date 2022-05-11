package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.PsiIfStatement;
import com.reason.lang.core.psi.impl.*;

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
        PsiFile psiFile = parseCode("let test = x => if (x) 1 else 2");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("1", e.getThenExpression().getText());
        assertEquals("2", e.getElseExpression().getText());
    }

    public void test_ternary_lident() {
        PsiFile psiFile = parseCode("let _ = a ? b : c");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("a", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    public void test_ternary_parens() {
        PsiFile psiFile = parseCode("let _ = (a) ? b : c");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("(a)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    /*
      public void test_ternary_cond() {
        PsiFile psiFile = parseCode("let _ = a == a' || (x < y) ? b : c;");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("a == a' || (x < y)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
      }
    */
    public void test_ternary_call() {
        PsiFile psiFile = parseCode("let _ = fn(a) ? b : c");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("fn(a)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }
}
