package com.reason.lang.rescript;

import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class AssertParsingTest extends ResParsingTestCase {
    public void test_basic() {
        PsiAssert e = firstOfType(parseCode("assert (i < Array.length(t))"), PsiAssert.class);

        assertEquals("(i < Array.length(t))", e.getAssertion().getText());
    }
}
