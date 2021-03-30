package com.reason.lang.reason;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

public class ClassParsingTest extends RmlParsingTestCase {
    public void test_basic() {
        Collection<PsiClass> classes = classExpressions(parseCode("class foo = { as _; }"));

        assertEquals(1, classes.size());
        assertEquals("foo", first(classes).getName());
        assertEquals("{ as _; }", first(classes).getClassBody().getText());
    }

    public void test_classType() {
        Collection<PsiClass> classes =
                classExpressions(
                        parseCode("class type restricted_point_type = { pub get_x: int; pub bump: unit; }"));

        assertEquals(1, classes.size());
        assertEquals("restricted_point_type", first(classes).getName());
    }

    public void test_fields() {
        Collection<PsiClass> classes =
                classExpressions(parseCode("class foo = { as _; val mutable a = []; val b = 2; }"));

        PsiClass clazz = first(classes);
        Collection<PsiClassField> fields = clazz.getFields();
        assertEquals(fields.size(), 2);
    }

    public void test_methods() {
        Collection<PsiClass> classes =
                classExpressions(parseCode("class foo = { as _; pub get_x = x; pub get_y = y; }"));

        PsiClass clazz = first(classes);
        Collection<PsiClassMethod> methods = clazz.getMethods();
        assertEquals(methods.size(), 2);
    }

    public void test_both() {
        Collection<PsiClass> classes =
                classExpressions(parseCode("class foo = { as _; val mutable x = []; pub get_x = x; }"));

        PsiClass clazz = first(classes);
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 1);
    }

    public void test_classConstruct() {
        Collection<PsiClass> classes =
                classExpressions(
                        parseCode(
                                "class c (m: int) = { as self;pub m = m;initializer (all_c := [(self :> c), ...all_c^]); }"));

        PsiClass clazz = first(classes);
        assertEquals(clazz.getParameters().size(), 0);
        assertNotNull(clazz.getConstructor());
    }

    public void test_classConstraint() {
        Collection<PsiClass> classes =
                classExpressions(
                        parseCode(
                                "class circle ('a) (c: 'a) = { as _; constraint 'a = #point; val mutable center = c; pub set_center = c => center = c; pub move = center#move; }"));

        PsiClass clazz = first(classes);
        assertEquals("circle", first(classes).getName());
        assertNotNull(clazz.getParameters());
        assertNotNull(clazz.getConstructor());
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 2);
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/310
    public void test_GH_310() {
        FileBase file = parseCode("class type control = { pub detach: unit => unit; };\n" +
                "type errpage = page(list((int, string)));");
        List<PsiNamedElement> es = (List<PsiNamedElement>) expressions(file);

        assertSize(2, es);
        assertInstanceOf(es.get(0), PsiClass.class);
        assertEquals("control", es.get(0).getName());
        assertInstanceOf(es.get(1), PsiType.class);
        assertEquals("errpage", es.get(1).getName());
    }
}
