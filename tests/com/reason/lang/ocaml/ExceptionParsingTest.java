package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiException;
import org.junit.*;

public class ExceptionParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        PsiException e = firstOfType(parseCode("exception Ex"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    @Test
    public void test_parameter() {
        PsiException e = firstOfType(parseCode("exception Ex of string"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }
}
