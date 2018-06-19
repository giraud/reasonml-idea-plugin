package com.reason.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.reason.RmlParserDefinition;

public class IncludeParsingTest extends BaseParsingTestCase {
    public IncludeParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testInclude() {
        PsiInclude e = first(parseCode("include Belt;").getIncludeExpressions());

        assertNotNull(e);
        assertEquals("Belt", e.getName());
    }
}
