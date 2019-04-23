package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiException;

@SuppressWarnings("unchecked")
public class ExceptionParsingTest extends BaseParsingTestCase {

    public ExceptionParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiException e = firstOfType(parseCode("exception Ex"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

    public void testParameter() {
        PsiException e = firstOfType(parseCode("exception Ex of string"), PsiException.class);

        assertEquals("Ex", e.getName());
        assertEquals("Dummy.Ex", e.getQualifiedName());
    }

}
