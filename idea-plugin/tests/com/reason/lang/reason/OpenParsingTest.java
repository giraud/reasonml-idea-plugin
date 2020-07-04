package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiOpen;

public class OpenParsingTest extends BaseParsingTestCase {
    public OpenParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testOne() {
        PsiOpen e = first(openExpressions(parseCode("open Belt;")));

        assertEquals("Belt", e.getQualifiedName());
    }

    public void testPath() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array;")));

        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void testFunctor() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make({ type t; })", true)));

        assertTrue(e.useFunctor());
        assertEquals("A.Make", e.getQualifiedName());
    }
}
