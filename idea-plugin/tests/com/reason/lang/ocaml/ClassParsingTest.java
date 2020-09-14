package com.reason.lang.ocaml;

import java.util.*;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiClassField;
import com.reason.lang.core.psi.PsiClassMethod;

public class ClassParsingTest extends OclParsingTestCase {
    public void test_basic() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object end"));

        assertEquals(1, classes.size());
        assertEquals("foo", first(classes).getName());
    }

    public void test_classType() {
        Collection<PsiClass> classes = classExpressions(parseCode("class type restricted_point_type = object method get_x : int method bump : unit end"));

        assertEquals(1, classes.size());
        assertEquals("restricted_point_type", first(classes).getName());
    }

    public void test_fields() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object val mutable a = [] val b = 2 end"));

        PsiClass clazz = first(classes);
        Collection<PsiClassField> fields = clazz.getFields();
        assertEquals(fields.size(), 2);
    }

    public void test_methods() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object method get_x = x method get_y = y end"));

        PsiClass clazz = first(classes);
        Collection<PsiClassMethod> methods = clazz.getMethods();
        assertEquals(methods.size(), 2);
    }

    public void test_both() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = object val mutable x = [] method get_x = x end"));

        PsiClass clazz = first(classes);
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 1);
    }

    public void test_classConstraint() {
        Collection<PsiClass> classes = classExpressions(parseCode(
                "class ['a] circle (c : 'a) = object constraint 'a = #point val mutable center = c method set_center c = center <- c method move = center#move end"));

        PsiClass clazz = first(classes);
        assertEquals("circle", first(classes).getName());
        assertNotNull(clazz.getParameters());
        assertNotNull(clazz.getConstructor());
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 2);
    }

    public void test_GH_268() {
        PsiClass clazz = first(classExpressions(parseCode("class tag : text_tag -> object method as_tag : text_tag method connect : tag_signals end")));

        assertEquals(clazz.getMethods().size(), 2);
    }
}
