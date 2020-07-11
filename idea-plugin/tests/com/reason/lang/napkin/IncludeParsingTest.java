package com.reason.lang.napkin;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInclude;

public class IncludeParsingTest extends BaseParsingTestCase {
    public IncludeParsingTest() {
        super("", "res", new NsParserDefinition());
    }

    public void test_one() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));
        assertEquals("Belt", e.getQualifiedName());
    }

    public void test_path() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));
        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void test_functor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make({ type t })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getQualifiedName());
    }
}
