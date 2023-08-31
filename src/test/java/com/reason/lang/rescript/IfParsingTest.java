package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.RPsiIfStatement;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IfParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic_if() {
        RPsiIfStatement e = firstOfType(parseCode("if (x) { () }"), RPsiIfStatement.class);
        assertNoParserError(e);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("(x)", e.getCondition().getText());
        assertEquals("{ () }", e.getThenExpression().getText());
    }

    @Test
    public void test_many_parens() {
        RPsiIfStatement e = firstOfType(parseCode("if ((x)) { () }"), RPsiIfStatement.class);
        assertNoParserError(e);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("((x))", e.getCondition().getText());
        assertEquals("{ () }", e.getThenExpression().getText());
    }

    @Test
    public void test_if_else() {
        PsiFile psiFile = parseCode("let test = x => if (x) { 1 } else { 2 }");
        RPsiIfStatement e = firstOfType(psiFile, RPsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("{ 1 }", e.getThenExpression().getText());
        assertEquals("{ 2 }", e.getElseExpression().getText());
    }

    @Test
    public void test_if_else_noBrace() {
        PsiFile psiFile = parseCode("let test = x => if (x) 1 else 2");
        RPsiIfStatement e = firstOfType(psiFile, RPsiIfStatement.class);

        assertEquals("(x)", e.getCondition().getText());
        assertEquals("1", e.getThenExpression().getText());
        assertEquals("2", e.getElseExpression().getText());
    }

    @Test
    public void test_ternary_lident() {
        PsiFile psiFile = parseCode("let _ = a ? b : c");
        RPsiTernary e = firstOfType(psiFile, RPsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("a", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    @Test
    public void test_ternary_parens() {
        PsiFile psiFile = parseCode("let _ = (a) ? b : c");
        RPsiTernary e = firstOfType(psiFile, RPsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("(a)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    @Test
    public void test_ternary_cond() {
        PsiFile psiFile = parseCode("let _ = a == a' || (x < y) ? b : c");
        RPsiTernary e = firstOfType(psiFile, RPsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("a == a' || (x < y)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    @Test
    public void test_ternary_call() {
        PsiFile psiFile = parseCode("let _ = fn(a) ? b : c");
        RPsiTernary e = firstOfType(psiFile, RPsiTernary.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        assertEquals("fn(a)", e.getCondition().getText());
        assertEquals("b", e.getThenExpression().getText());
        assertEquals("c", e.getElseExpression().getText());
    }

    @Test
    public void test_ternary_fun_record() {
        RPsiRecord e = firstOfType(parseCode("let x = (x) => {a: [ X.y ? true : false ] }"), RPsiRecord.class);

        assertEquals("{a: [ X.y ? true : false ] }", e.getText());
        RPsiTernary t = PsiTreeUtil.findChildOfType(e, RPsiTernary.class);
        assertEquals("X.y", t.getCondition().getText());
        assertEquals("true", t.getThenExpression().getText());
        assertEquals("false", t.getElseExpression().getText());
    }

    @Test
    public void test_ternary_array() {
        RPsiScopedExpr e = firstOfType(parseCode("let x = [ x ? a : b, y ? c : d  ]"), RPsiScopedExpr.class);

        List<RPsiTernary> ts = ORUtil.findImmediateChildrenOfClass(e, RPsiTernary.class);
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    @Test
    public void test_ternary_list() {
        RPsiScopedExpr e = firstOfType(parseCode("let x = list{ x ? a : b, y ? c : d  }"), RPsiScopedExpr.class);

        List<RPsiTernary> ts = ORUtil.findImmediateChildrenOfClass(e, RPsiTernary.class);
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    @Test
    public void test_ternary_tuple() {
        RPsiScopedExpr e = firstOfType(parseCode("let x = ( x ? a : b, y ? c : d  );"), RPsiScopedExpr.class);

        List<RPsiTernary> ts = ORUtil.findImmediateChildrenOfClass(e, RPsiTernary.class);
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    @Test
    public void test_ternary_function_parameters() {
        RPsiParameters e = firstOfType(parseCode("let x = fn( x ? a : b, y ? c : d  )"), RPsiParameters.class);

        assertSize(2, PsiTreeUtil.findChildrenOfType(e, RPsiParameterReference.class));
        List<RPsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiTernary.class));
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    @Test
    public void test_ternary_functor_parameters() {
        RPsiParameters e = firstOfType(parseCode("module M = Make( x ? a : b, y ? c : d  )"), RPsiParameters.class);

        assertSize(2, PsiTreeUtil.findChildrenOfType(e, RPsiParameterReference.class));
        List<RPsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiTernary.class));
        assertEquals("x ? a : b", ts.get(0).getText());
        assertEquals("y ? c : d", ts.get(1).getText());
    }

    @Test
    public void test_ternary_switch() {
        RPsiLet e = firstOfType(parseCode("let compare = switch index { | 0 => appliedCount > appliedCount' ? (-1) : 0 }"), RPsiLet.class);

        List<RPsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiTernary.class));
        assertEquals("appliedCount > appliedCount' ? (-1) : 0", ts.get(0).getText());
    }

    @Test
    public void test_ternary_fun() {
        RPsiLet e = firstOfType(parseCode("let fn = x => x ? Some(x) : None"), RPsiLet.class);

        List<RPsiTernary> ts = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiTernary.class));
        assertEquals("x ? Some(x) : None", ts.get(0).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/424
    @Test
    public void test_ternary_new_line() {
        RPsiLet e = firstOfType(parseCode("""
                let fn = x => x
                  ? "a"
                  : "b" ++
                    "c"
                """), RPsiLet.class);

        RPsiTernary t = PsiTreeUtil.findChildOfType(e, RPsiTernary.class);
        assertEquals("x\n  ? \"a\"\n  : \"b\" ++\n    \"c\"", t.getText());
    }
}
