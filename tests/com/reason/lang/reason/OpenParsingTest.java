package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class OpenParsingTest extends RmlParsingTestCase {
    public void test_one() {
        PsiOpen e = first(openExpressions(parseCode("open Belt;")));

        assertNull(PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class));
        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array;")));

        assertEquals("Belt.Array", e.getPath());
    }

    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open Make({ type t; })")));

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("Make", e.getPath());
    }

    public void test_functor_with_path() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t; })")));

        assertTrue(e.useFunctor());
        assertEquals("Make", PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class).getName());
        assertEquals("A.Make", e.getPath());
    }
}
