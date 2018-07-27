package com.reason.lang.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;

import java.util.Collection;

public class RecursiveTypeTest extends BaseParsingTestCase {
    public RecursiveTypeTest() {
        super("", "re", new RmlParserDefinition());
    }

    /* type update = | NoUpdate and 'state self = {state: 'state;}*/
    public void testAnd() {
        Collection<PsiType> types = typeExpressions(parseCode("type update = | NoUpdate and self('state) = {state: 'state};"));

        assertEquals(2, types.size());
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }
}
