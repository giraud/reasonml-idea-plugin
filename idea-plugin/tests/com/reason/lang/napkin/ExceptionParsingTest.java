package com.reason.lang.napkin;

import com.reason.lang.core.psi.PsiException;

public class ExceptionParsingTest extends NsParsingTestCase {
    public void test_basic() {
        PsiException e = firstOfType(parseCode("exception Ex"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    public void test_parameter() {
        PsiException e = firstOfType(parseCode("exception Ex(string)"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    public void test_alias() {
        PsiException e = firstOfType(parseCode("exception Exit = Terminate"), PsiException.class);

        assertEquals("Exit", e.getName());
        assertEquals("Dummy.Exit", e.getQualifiedName());
        assertEquals("Terminate", e.getAlias());
    }

    public void test_aliasPath() {
        PsiException e = firstOfType(parseCode("exception Exit = Lib.Terminate"), PsiException.class);

        assertEquals("Exit", e.getName());
        assertEquals("Dummy.Exit", e.getQualifiedName());
        assertEquals("Lib.Terminate", e.getAlias());
    }
}
