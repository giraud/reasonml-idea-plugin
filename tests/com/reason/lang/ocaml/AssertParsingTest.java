package com.reason.lang.ocaml;

import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class AssertParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        RPsiAssert assertExp = firstOfType(parseCode("assert (x > 2)"), RPsiAssert.class);

        assertNotNull(assertExp);
        assertNotNull(assertExp.getAssertion());
    }
}
