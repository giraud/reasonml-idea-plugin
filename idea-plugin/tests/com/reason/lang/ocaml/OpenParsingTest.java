package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiOpen;

public class OpenParsingTest extends BaseParsingTestCase {
    public OpenParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testOne() {
        PsiOpen e = first(openExpressions(parseCode("open Belt")));

        assertEquals("Belt", e.getQualifiedName());
    }

    public void testPath() {
        PsiOpen e = first(openExpressions(parseCode("open Belt.Array")));

        assertEquals("Belt.Array", e.getQualifiedName());
    }

    public void testChaining() {
        PsiOpen e = first(openExpressions(parseCode("open Belt Array")));

        assertEquals("Belt", e.getQualifiedName());
    }

    public void testFunctor() {
        PsiOpen e = first(openExpressions(parseCode("open A.Make(struct type t end)")));

        assertEquals(true, e.useFunctor());
        assertEquals("A.Make", e.getQualifiedName());
    }
}
