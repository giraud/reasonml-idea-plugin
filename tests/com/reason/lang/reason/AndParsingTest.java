package com.reason.lang.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AndParsingTest extends BaseParsingTestCase {
    public AndParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testLetChaining() {
        List<PsiLet> lets = new ArrayList(letExpressions(parseCode("let rec lx = x => x + 1 and ly = y => 3 + lx(y)")));

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

}
