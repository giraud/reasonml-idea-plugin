package com.reason.lang.reason;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class ExceptionParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiException e = firstOfType(parseCode("exception Ex;"), RPsiException.class);

        assertNoParserError(e);
        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
        assertInstanceOf(e.getNameIdentifier(), RPsiUpperSymbol.class);
    }

    @Test
    public void test_parameter() {
        RPsiException e = firstOfType(parseCode("exception Ex(string);"), RPsiException.class);

        assertNoParserError(e);
        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
        assertInstanceOf(e.getNameIdentifier(), RPsiUpperSymbol.class);
    }
}
