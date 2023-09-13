package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ChainingParsingTest extends ResParsingTestCase {
    @Test
    public void test_let_function_call() {
        RPsiLetBinding e = firstOfType(parseCode("""
                let _ = if true {
                  let _ = 1
                  z->fn
                }
                """), RPsiLetBinding.class);

        assertNoParserError(e);
        RPsiIfStatement i = PsiTreeUtil.findChildOfType(e, RPsiIfStatement.class);
        PsiElement it = i.getThenExpression();
        RPsiLet itl = PsiTreeUtil.findChildOfType(it, RPsiLet.class);
        assertEquals("let _ = 1", itl.getText());
    }

    @Test
    public void test_let_switch_chaining() {
        FileBase f = parseCode("""
                let x = M.fn()
                switch x { | _ => () }
                """);
        RPsiLet l = firstOfType(f, RPsiLet.class);
        RPsiSwitch s = firstOfType(f, RPsiSwitch.class);

        assertEquals("M.fn()", l.getBinding().getText());
        assertEquals("switch x { | _ => () }", s.getText());
    }

    @Test
    public void test_let_object_call() {
        FileBase f = parseCode("""
                let a = { "b": 1, "c": 2 }
                a["b"]
                """);

        RPsiLet l = firstOfType(f, RPsiLet.class);
        assertEquals("{ \"b\": 1, \"c\": 2 }", l.getBinding().getText());
    }
}
