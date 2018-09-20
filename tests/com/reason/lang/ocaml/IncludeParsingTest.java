package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInclude;

public class IncludeParsingTest extends BaseParsingTestCase {
    public IncludeParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testInclude() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertNotNull(e);
        assertEquals("Belt", e.getName());
    }

}
