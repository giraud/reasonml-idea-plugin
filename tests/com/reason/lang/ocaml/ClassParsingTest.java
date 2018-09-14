package com.reason.lang.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiClassField;
import com.reason.lang.core.psi.PsiClassMethod;

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
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object val mutable a = [] val b = 2 end"));

        PsiClass clazz = first(classes);
        Collection<PsiClassField> fields = clazz.getFields();
        assertEquals(fields.size(), 2);
    }

    public void testMethods() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object method get_x = x method get_y = y end"));

        PsiClass clazz = first(classes);
        Collection<PsiClassMethod> methods = clazz.getMethods();
        assertEquals(methods.size(), 2);
    }

    public void testBoth() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object val mutable x = [] method get_x = x end"));

        PsiClass clazz = first(classes);
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 1);
    }

}
