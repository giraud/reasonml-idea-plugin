package com.reason.lang.reason;

import com.reason.lang.core.psi.*;

public class OpenParsingTest extends RmlParsingTestCase {
    public void test_one() {
        PsiOpen e = first(openExpressions(parseCode("open Belt;")));

        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array;")));

        assertEquals("Belt.Array", e.getPath());
    }

    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t; })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getPath());
    }
}
