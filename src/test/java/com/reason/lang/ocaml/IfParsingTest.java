package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.RPsiIfStatement;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class IfParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        PsiFile psiFile = parseCode("let _ = if x then ()");
        RPsiIfStatement e = firstOfType(psiFile, RPsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        RPsiScopedExpr ifScope = PsiTreeUtil.findChildOfType(e, RPsiScopedExpr.class);
        assertNotNull(ifScope);
        assertEquals("()", ifScope.getText());
    }

    @Test
    public void test_basic_then_else() {
        PsiFile psiFile = parseCode("let _ = if x then 1 else 2");
        RPsiIfStatement e = firstOfType(psiFile, RPsiIfStatement.class);

        assertNotNull(e);
        assertNotNull(e.getCondition());
        List<RPsiScopedExpr> scopes = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiScopedExpr.class));
        assertEquals(2, scopes.size());
        assertEquals("1", scopes.get(0).getText());
        assertEquals("2", scopes.get(1).getText());
    }

    @Test
    public void test_with_in() {
        FileBase file = parseCode("let _ = if x then let init = y in let data = z");

        assertEquals(1, letExpressions(file).size());
        assertNotNull(firstOfType(file, RPsiIfStatement.class));
    }

    @Test
    public void test_function() {
        RPsiLet e = firstOfType(parseCode("let init l f = if l = 0 then [||] else x"), RPsiLet.class);

        RPsiIfStatement i = PsiTreeUtil.findChildOfType(e, RPsiIfStatement.class);
        assertEquals("l = 0", i.getCondition().getText());
        assertEquals("[||]", i.getThenExpression().getText());
        assertEquals("x", i.getElseExpression().getText());
    }

    @Test
    public void test_comment_else() {
        RPsiIfStatement e = firstOfType(parseCode("let _ = if cond then 1 else (* !! *) let z = true in 2"), RPsiIfStatement.class);

        assertEquals("cond", e.getCondition().getText());
        assertEquals("1", e.getThenExpression().getText());
        assertEquals("let z = true in 2", e.getElseExpression().getText());
    }

    @Test
    public void test_ternary_lident() {
        RPsiLet e = firstOfType(parseCode("let _ = a ? b : c"), RPsiLet.class);
        RPsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), RPsiTernary.class);

        assertEquals("a", t.getCondition().getText());
        assertEquals("b", t.getThenExpression().getText());
        assertEquals("c", t.getElseExpression().getText());
    }

    @Test
    public void test_ternary_parens() {
        RPsiLet e = firstOfType(parseCode("let _ = (a) ? b : c"), RPsiLet.class);
        RPsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), RPsiTernary.class);

        assertEquals("(a)", t.getCondition().getText());
        assertEquals("b", t.getThenExpression().getText());
        assertEquals("c", t.getElseExpression().getText());
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
        RPsiLet e = firstOfType(parseCode("let _ = fn(a) ? b : c"), RPsiLet.class);
        RPsiTernary t = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), RPsiTernary.class);

        // assertEquals("fn(a)", t.getCondition().getText());
        assertEquals("b", t.getThenExpression().getText());
        assertEquals("c", t.getElseExpression().getText());
    }

    @Test
    public void test_named_param() {
        RPsiLet e = firstOfType(parseCode("let add_rec_path ~unix_path ~coq_root =\n" +
                "  if exists_dir unix_path then\n" +
                "    let dirs = all_subdirs ~unix_path \n" +
                "  else\n" +
                "    Feedback.msg_warning (str \"Cannot open \" ++ str unix_path)"), RPsiLet.class);

        RPsiIfStatement i = PsiTreeUtil.findChildOfType(e, RPsiIfStatement.class);
        assertEquals("exists_dir unix_path", i.getCondition().getText());
        assertEquals("let dirs = all_subdirs ~unix_path", i.getThenExpression().getText());
        assertEquals("Feedback.msg_warning (str \"Cannot open \" ++ str unix_path)", i.getElseExpression().getText());
    }

    @Test
    public void test_GH_xxx() {
        RPsiClassMethod e = firstOfType(parseCode("module M = struct\n" +
                "  let o = object\n" +
                "  method m =\n" +
                "      if 2 > 1 then ()\n" +
                "  end\n" +
                "  let x = 0\n" +
                "end"), RPsiClassMethod.class);

        assertEquals("method m =\n      if 2 > 1 then ()", e.getText());
    }
}
