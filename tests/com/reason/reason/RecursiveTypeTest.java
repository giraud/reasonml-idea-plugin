package com.reason.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.reason.RmlParserDefinition;

import java.util.Collection;

public class RecursiveTypeTest extends BaseParsingTestCase {
    public RecursiveTypeTest() {
        super("", "re", new RmlParserDefinition());
    }

    /* type update = | NoUpdate and 'state self = {state: 'state;}*/
    public void testAnd() {
        Collection<PsiNamedElement> expressions = parseCode("type update = | NoUpdate and self('state) = {state: 'state};").getExpressions();

        assertEquals(2, expressions.size());
        assertEquals("update", first(expressions).getName());
        assertEquals("self", second(expressions).getName());
    }
}
