package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInclude;

public class IncludeParsingTest extends BaseParsingTestCase {
    public IncludeParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testOne() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt")));

        assertEquals("Belt", e.getQualifiedName());
    }

    public void testPath() {
        PsiInclude e = first(includeExpressions(parseCode("include Belt.Array")));

        assertEquals("Belt.Array", e.getQualifiedName());
    }
}
