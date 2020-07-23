package com.reason.lang.napkin;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiOpen;

public class OpenParsingTest extends BaseParsingTestCase {
    public OpenParsingTest() {
        super("", "res", new NsParserDefinition());
    }

    public void test_basic() {
        PsiOpen e = first(openExpressions(parseCode("open Belt")));
        assertEquals("Belt", e.getQualifiedName());
    }

    public void test_path() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array")));
        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void test_force() {
        PsiOpen e = first(openExpressions(parseCode("open! Belt.Array")));
        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void test_functor() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getQualifiedName());
    }
}
