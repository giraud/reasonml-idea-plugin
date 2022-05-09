package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.*;

// Keep it, might be reintroduced later
@SuppressWarnings("ConstantConditions")
public class LocalOpenParsingTest extends ResParsingTestCase {
    public void test_local_paren() {
        PsiElement expression = parseCode("ModA.ModB.(call());");
        PsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, PsiLocalOpen.class);
        assertEquals("(call())", o.getText());
    }

    public void test_local_paren_2() {
        PsiElement expression = parseCode("let x = Js.Promise.(Api.call());");
        PsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, PsiLocalOpen.class);
        assertEquals("(Api.call())", o.getText());
    }

    public void test_local_list() {
        PsiElement expression = parseCode("ModA.ModB.[call()];");
        PsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, PsiLocalOpen.class);
        assertEquals("[call()]", o.getText());
    }

    public void test_local_list_2() {
        PsiElement expression = parseCode("let x = ModA.ModB.[call()];");
        PsiLocalOpen o = PsiTreeUtil.findChildOfType(expression, PsiLocalOpen.class);
        assertEquals("[call()]", o.getText());
    }

    public void test_not_local() {
        PsiElement expression = firstElement(parseCode("Js.log(\"nok\");"));
        assertFalse(expression instanceof PsiLocalOpen);
    }
}
