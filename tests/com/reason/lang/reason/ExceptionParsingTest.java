package com.reason.lang.reason;

import com.reason.lang.core.psi.*;
import org.junit.*;

public class ExceptionParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        PsiException e = firstOfType(parseCode("exception Ex;"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    @Test
    public void test_parameter() {
        PsiException e = firstOfType(parseCode("exception Ex(string);"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }
}
