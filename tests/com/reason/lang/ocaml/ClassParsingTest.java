package com.reason.lang.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiClass;

import java.util.Collection;

public class ClassParsingTest extends BaseParsingTestCase {
    public ClassParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object end"));

        assertEquals(1, classes.size());
        assertEquals("foo", first(classes).getName());
    }

    public void testFields() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object end"));

        assertEquals(1, classes.size());
        assertEquals("foo", first(classes).getName());
    }

}
