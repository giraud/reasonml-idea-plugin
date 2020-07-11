package com.reason.lang.napkin;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiAssert;

public class AssertParsingTest extends NsParsingTestCase {

    public void testBasic() {
        PsiAssert assertExp = firstOfType(parseCode("assert (i < Array.length(t))"), PsiAssert.class);

        assertNotNull(assertExp);
        assertNotNull(assertExp.getAssertion());
    }

}
