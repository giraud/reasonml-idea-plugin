package com.reason.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiAssert;
import com.reason.lang.ocaml.OclParserDefinition;

public class AssertParsingTest extends BaseParsingTestCase {
    public AssertParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiAssert assertExp = firstOfType(parseCode("assert (i < Array.length t);"), PsiAssert.class);

        assertNotNull(assertExp);
    }

}
