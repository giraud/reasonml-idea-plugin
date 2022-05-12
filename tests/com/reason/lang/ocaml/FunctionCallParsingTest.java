package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionCallParsingTest extends OclParsingTestCase {
    public void test_call() {
        PsiLetBinding e = first(letExpressions(parseCode("let _ = string_of_int 1"))).getBinding();

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertEquals("string_of_int 1", call.getText());
        assertEquals(1, call.getParameters().size());
        assertEquals("1", call.getParameters().get(0).getText());
    }

    public void test_call_ints() {
        PsiFunctionCall e = PsiTreeUtil.findChildOfType(parseCode("add 1 2"), PsiFunctionCall.class);

        assertEquals("add 1 2", e.getText());
        assertEquals(2, e.getParameters().size());
        assertEquals("1", e.getParameters().get(0).getText());
        assertEquals("2", e.getParameters().get(1).getText());
    }

    public void test_call_floats() {
        PsiFunctionCall e = PsiTreeUtil.findChildOfType(parseCode("add 1. 2."), PsiFunctionCall.class);

        assertEquals("add 1. 2.", e.getText());
        assertEquals(2, e.getParameters().size());
        assertEquals("1.", e.getParameters().get(0).getText());
        assertEquals("2.", e.getParameters().get(1).getText());
    }

    public void test_call_many() {
        PsiLetBinding e = first(letExpressions(parseCode("let _ = fn a b c"))).getBinding();

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertEquals("fn a b c", call.getText());
        assertEquals(3, call.getParameters().size());
        assertEquals("a", call.getParameters().get(0).getText());
        assertEquals("b", call.getParameters().get(1).getText());
        assertEquals("c", call.getParameters().get(2).getText());
    }

    public void test_inner_call() {
        PsiLetBinding e = first(letExpressions(parseCode("let _ = fn a (b \"{\" c) d"))).getBinding();

        PsiFunctionCall f = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        List<PsiParameter> p = f.getParameters();
        assertEquals("fn a (b \"{\" c) d", f.getText());
        assertEquals(3, p.size());
        assertEquals("a", p.get(0).getText());
        assertEquals("(b \"{\" c)", p.get(1).getText());
        assertEquals("d", p.get(2).getText());
        PsiFunctionCall f1 = PsiTreeUtil.findChildOfType(p.get(1), PsiFunctionCall.class);
        assertEquals("b \"{\" c", f1.getText());
        assertEquals(2, f1.getParameters().size());
        assertEquals("\"{\"", f1.getParameters().get(0).getText());
        assertEquals("c", f1.getParameters().get(1).getText());
    }
}
