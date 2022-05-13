package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.impl.PsiIfStatement;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IfParsingTest extends RmlParsingTestCase {
    public void test_basic_if() {
        PsiFile psiFile = parseCode("if (x) { (); }");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("(x)", e.getCondition().getText());
        assertEquals("{ (); }", e.getThenExpression().getText());
    }

    public void test_if_else() {
        PsiFile psiFile = parseCode("let test = x => if (x) { 1; } else { 2; };");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("{ 1; }", e.getThenExpression().getText());
        assertEquals("{ 2; }", e.getElseExpression().getText());
    }

    public void test_if_else_noBrace() {
        PsiFile psiFile = parseCode("let test = x => if (x) 1 else 2;");
        PsiIfStatement e = firstOfType(psiFile, PsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("1", e.getThenExpression().getText());
        assertEquals("2", e.getElseExpression().getText());
    }

    public void test_ternary_lident() {
        PsiFile psiFile = parseCode("let _ = a ? b : c;");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("a", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    public void test_ternary_parens() {
        PsiFile psiFile = parseCode("let _ = (a) ? b : c;");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("(a)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    public void test_ternary_cond() {
        PsiFile psiFile = parseCode("let _ = a == a' || (x < y) ? b : c;");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("a == a' || (x < y)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    public void test_ternary_call() {
        PsiFile psiFile = parseCode("let _ = fn(a) ? b : c;");
        PsiTernary e = firstOfType(psiFile, PsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("fn(a)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    public void test_ternary_fun_record() {
        PsiRecord e = firstOfType(parseCode("let x = (x) => {a: [| X.y ? true : false |] };"), PsiRecord.class);

        assertEquals("{a: [| X.y ? true : false |] }", e.getText());
        PsiTernary t = PsiTreeUtil.findChildOfType(e, PsiTernary.class);
        assertEquals("X.y", t.getCondition().getText());
        assertEquals("true", t.getThenExpression().getText());
        assertEquals("false", t.getElseExpression().getText());
    }

    public void test_ternary_array() {
        PsiScopedExpr e = firstOfType(parseCode("let x = [| x ? a : b, y ? c : d  |];"), PsiScopedExpr.class);

        List<PsiTernary> ts = ORUtil.findImmediateChildrenOfClass(e, PsiTernary.class);
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    public void test_ternary_list() {
        PsiScopedExpr e = firstOfType(parseCode("let x = [ x ? a : b, y ? c : d  ];"), PsiScopedExpr.class);

        List<PsiTernary> ts = ORUtil.findImmediateChildrenOfClass(e, PsiTernary.class);
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    public void test_ternary_tuple() {
        PsiScopedExpr e = firstOfType(parseCode("let x = ( x ? a : b, y ? c : d  );"), PsiScopedExpr.class);

        List<PsiTernary> ts = ORUtil.findImmediateChildrenOfClass(e, PsiTernary.class);
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    public void test_ternary_function_parameters() {
        PsiParameters e = firstOfType(parseCode("let x = fn( x ? a : b, y ? c : d  );"), PsiParameters.class);

        assertSize(2, PsiTreeUtil.findChildrenOfType(e, PsiParameter.class));
        List<PsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    public void test_ternary_functor_parameters() {
        PsiParameters e = firstOfType(parseCode("module M = Make( x ? a : b, y ? c : d  );"), PsiParameters.class);

        assertSize(2, PsiTreeUtil.findChildrenOfType(e, PsiParameter.class));
        List<PsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    public void test_ternary_ternary() {
        PsiLet e = firstOfType(parseCode("let x = x ? a : y ? c : d;"), PsiLet.class);

        List<PsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
        assertEquals("x ? a : y ? c : d", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    public void test_ternary_switch() {
        PsiLet e = firstOfType(parseCode("let compare = switch (index) { | 0 => appliedCount > appliedCount' ? (-1) : 0 };"), PsiLet.class);

        List<PsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
        assertEquals("appliedCount > appliedCount' ? (-1) : 0", ts.get(0).getText());
    }

    public void test_ternary_fun() {
        PsiLet e = firstOfType(parseCode("let fn = x => x ? Some(x) : None"), PsiLet.class);

        List<PsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
        assertEquals("x ? Some(x) : None", ts.get(0).getText());
    }

    public void test_ternary_let_binding() {
        PsiLet e = firstOfType(parseCode("let x = { let a = 1; x ? 1 : 2; }"), PsiLet.class);

        List<PsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
        assertEquals("x ? 1 : 2", ts.get(0).getText());
    }
}
