package com.reason.lang.ocaml;

import com.reason.lang.core.psi.impl.*;

public class AssertParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiAssert assertExp = firstOfType(parseCode("assert (x > 2)"), PsiAssert.class);

        assertNotNull(assertExp);
        assertNotNull(assertExp.getAssertion());
    }
}
