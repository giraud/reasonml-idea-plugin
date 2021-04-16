package com.reason.lang.rescript;

import com.reason.lang.core.psi.*;

public class IncludeParsingTest extends ResParsingTestCase {
    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));
        assertEquals("Belt", e.getIncludePath());
    }

    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));
        assertEquals("Belt.Array", e.getIncludePath());
    }

    public void test_functor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getIncludePath());
    }
}
