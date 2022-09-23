package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class LocalOpenParsingTest extends RmlParsingTestCase {
    @Test
    public void test_local_paren() {
        PsiElement expression = parseCode("ModA.ModB.(call());");
        RPsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, RPsiLocalOpen.class);
        assertEquals("(call())", o.getText());
    }

    @Test
    public void test_local_paren_2() {
        PsiElement expression = parseCode("let x = Js.Promise.(Api.call());");
        RPsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, RPsiLocalOpen.class);
        assertEquals("(Api.call())", o.getText());
    }

    @Test
    public void test_local_list() {
        PsiElement expression = parseCode("ModA.ModB.[call(), 1];");
        RPsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, RPsiLocalOpen.class);
        assertEquals("[call(), 1]", o.getText());
        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(o, RPsiFunctionCall.class);
        assertEquals("call()", f.getText());
    }

    @Test
    public void test_local_list_2() {
        PsiElement expression = parseCode("let x = ModA.ModB.[call(), true];");
        RPsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, RPsiLocalOpen.class);
        assertEquals("[call(), true]", o.getText());
        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(o, RPsiFunctionCall.class);
        assertEquals("call()", f.getText());
    }

    @Test
    public void test_local_record() {
        PsiElement expression = parseCode("let x = [| ModA.ModB.{x:1}, {x:2} |];");
        RPsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, RPsiLocalOpen.class);
        assertEquals("{x:1}", o.getText());
    }

    @Test
    public void test_not_local() {
        PsiElement expression = firstElement(parseCode("Js.log(\"nok\");"));
        assertFalse(expression instanceof RPsiLocalOpen);
    }
}
