package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

// Keep it, might be reintroduced later
@SuppressWarnings("ConstantConditions")
public class LocalOpenParsingTest extends ResParsingTestCase {
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
        PsiElement e = parseCode("[ModA.ModB.call(), 1];");
        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEquals("call()", f.getText());
    }

    @Test
    public void test_local_list_2() {
        PsiElement e = parseCode("let x = { open ModA.ModB [call(), true] }");
        RPsiOpen o = firstOfType(e, RPsiOpen.class);
        assertEquals("ModA.ModB", o.getPath());
        RPsiFunctionCall f = firstOfType(e, RPsiFunctionCall.class);
        assertEquals("call()", f.getText());
    }

    @Test
    public void test_not_local() {
        PsiElement expression = parseCode("Js.log(\"nok\");").getFirstChild();
        assertFalse(expression instanceof RPsiLocalOpen);
    }
}
