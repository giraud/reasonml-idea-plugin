package com.reason.lang.napkin;

import com.reason.lang.core.psi.*;

public class IncludeParsingTest extends NsParsingTestCase {
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));
        assertEquals("Belt", e.getPath());
    }

    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));
        assertEquals("Belt.Array", e.getPath());
    }

    public void test_functor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getPath());
    }
}
