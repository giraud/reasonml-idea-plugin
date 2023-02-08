package com.reason.lang.ocaml;

import com.reason.lang.core.psi.RPsiException;
import org.junit.*;

public class ExceptionParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        RPsiException e = firstOfType(parseCode("exception Ex"), RPsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    @Test
    public void test_parameter() {
        RPsiException e = firstOfType(parseCode("exception Ex of string"), RPsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }
}
