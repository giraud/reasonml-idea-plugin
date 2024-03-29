package com.reason.lang.rescript;

import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class AssertParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        RPsiAssert e = firstOfType(parseCode("assert (i < Array.length(t))"), RPsiAssert.class);

        assertEquals("(i < Array.length(t))", e.getAssertion().getText());
    }
}
