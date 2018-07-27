package com.reason.lang.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiAssert;

public class AssertParsingTest extends BaseParsingTestCase {
    public AssertParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiAssert assertExp = firstOfType(parseCode("assert (i < Array.length(t));"), PsiAssert.class);

        assertNotNull(assertExp);
        assertNotNull(assertExp.getAssertion());
    }

}
