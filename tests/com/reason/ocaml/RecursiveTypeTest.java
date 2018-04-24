package com.reason.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.ocaml.OclParserDefinition;

import java.util.Collection;

public class RecursiveTypeTest extends BaseParsingTestCase {
    public RecursiveTypeTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testAnd() {
        Collection<PsiNamedElement> expressions = parseCode("type update = | NoUpdate and 'state self = {state: 'state;}").getExpressions();

        assertEquals(2, expressions.size());
        assertEquals("update", first(expressions).getName());
        assertEquals("self", second(expressions).getName());
    }
}
