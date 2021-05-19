package com.reason.lang.rescript;

import com.reason.lang.core.psi.*;

import java.util.*;

public class OpenParsingTest extends ResParsingTestCase {
    public void test_one() {
        PsiOpen e = first(openExpressions(parseCode("open Belt")));

        assertEquals("Belt", e.getPath());
    }

    public void test_many() {
        List<PsiOpen> es = openExpressions(parseCode("open Belt\n open Css"));

        assertSize(2, es);
        assertEquals("Belt", es.get(0).getPath());
        assertEquals("Css", es.get(1).getPath());
    }

    public void test_path() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array")));

        assertEquals("Belt.Array", e.getPath());
    }

    public void test_many_paths() {
        List<PsiOpen> es = openExpressions(parseCode("open Belt.Array\n open Css.Types"));

        assertEquals("Belt.Array", es.get(0).getPath());
        assertEquals("Css.Types", es.get(1).getPath());
    }

    /* GH_328 ?
    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t; })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getPath());
    }
    */
}
