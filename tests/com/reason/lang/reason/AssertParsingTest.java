package com.reason.lang.reason;

import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class AssertParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiAssert e = firstOfType(parseCode("assert (i < Array.length(t));"), RPsiAssert.class);

        assertNotNull(e);
        assertEquals("(i < Array.length(t))", e.getAssertion().getText());
    }
}
