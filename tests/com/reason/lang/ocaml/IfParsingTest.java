package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.impl.PsiIfStatement;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IfParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiFile psiFile = parseCode("let _ = if x then ()");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        PsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, PsiScopedExpr.class);
        assertNotNull(ifScope);
        assertEquals("()", ifScope.getText());
    }

    public void test_basic_then_else() {
        PsiFile psiFile = parseCode("let _ = if x then 1 else 2");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        List<PsiScopedExpr> scopes = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiScopedExpr.class));
        assertEquals(2, scopes.size());
        assertEquals("1", scopes.get(0).getText());
        assertEquals("2", scopes.get(1).getText());
    }

    public void test_with_in() {
        FileBase file = parseCode("let _ = if x then let init = y in let data = z");

        assertEquals(1, letExpressions(file).size());
        assertNotNull(firstOfType(file, PsiIfStatement.class));
    }

    /*
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

     public void test_ternary_cond() {
       PsiFile psiFile = parseCode("let _ = a == a' || (x < y) ? b : c");
       PsiTernary e = firstOfType(psiFile, PsiTernary.class);

       assertNotNull(e);
       assertNotNull(e.getCondition());
       assertEquals("a == a' || (x < y)", e.getCondition().getText());
       assertEquals("b", e.getThenExpression().getText());
       assertEquals("c", e.getElseExpression().getText());
     }

    public void test_ternary_call() {
        PsiLet e = firstOfType(parseCode("let _ = fn(a) ? b : c"), PsiLet.class);
        PsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), PsiTernary.class);

        // assertEquals("fn(a)", t.getCondition().getText());
        assertEquals("b", t.getThenExpression().getText());
        assertEquals("c", t.getElseExpression().getText());
    }
    */
}
