package com.reason.lang.reason;

import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class AssertParsingTest extends RmlParsingTestCase {
    public void test_basic() {
        PsiAssert e = firstOfType(parseCode("assert (i < Array.length(t));"), PsiAssert.class);

        assertNotNull(e);
        assertEquals("(i < Array.length(t))", e.getAssertion().getText());
    }
}
