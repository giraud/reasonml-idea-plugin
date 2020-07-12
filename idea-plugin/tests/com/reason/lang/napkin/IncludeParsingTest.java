package com.reason.lang.napkin;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInclude;

public class IncludeParsingTest extends BaseParsingTestCase {
    public IncludeParsingTest() {
        super("", "res", new NsParserDefinition());
    }

    public void testOne() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt;")));

        assertEquals("Belt", e.getQualifiedName());
    }

    public void testPath() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array;")));

        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void testFunctor() {
        PsiInclude e = first(includeExpressions(parseCode("include A.Make({ type t; })")));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getQualifiedName());
    }
}
