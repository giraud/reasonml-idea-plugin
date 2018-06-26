package com.reason.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiAssert;
import com.reason.lang.reason.RmlParserDefinition;

public class AssertParsingTest extends BaseParsingTestCase {
    public AssertParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiAssert assertExp = firstOfType(parseCode("assert (i < Array.length(t));"), PsiAssert.class);

        assertNotNull(assertExp);
    }

}
