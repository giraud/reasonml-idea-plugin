package com.reason.lang.rescript;

import com.reason.lang.core.psi.*;
import org.junit.*;

public class ExceptionParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiException e = firstOfType(parseCode("exception Ex"), RPsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    @Test
    public void test_parameter() {
        RPsiException e = firstOfType(parseCode("exception Ex(string)"), RPsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }
}
