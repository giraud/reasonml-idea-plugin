package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ClassParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        Collection<RsiClass> classes = classExpressions(parseCode("class foo = { as _; }"));

        assertEquals(1, classes.size());
        assertEquals("foo", first(classes).getName());
        assertEquals("{ as _; }", first(classes).getClassBody().getText());
    }

    @Test
    public void test_classType() {
        Collection<RsiClass> classes = classExpressions(parseCode("class type restricted_point_type = { pub get_x: int; pub bump: unit; }"));

        assertEquals(1, classes.size());
        assertEquals("restricted_point_type", first(classes).getName());
    }

    @Test
    public void test_fields() {
        Collection<RsiClass> classes = classExpressions(parseCode("class foo = { as _; val mutable a = []; val b = 2; }"));

        RsiClass clazz = first(classes);
        Collection<RsiClassField> fields = clazz.getFields();
        assertEquals(fields.size(), 2);
    }

    @Test
    public void test_methods() {
        Collection<RsiClass> classes = classExpressions(parseCode("class foo = { as _; pub get_x = x; pub get_y = y; }"));

        RsiClass clazz = first(classes);
        Collection<RsiClassMethod> methods = clazz.getMethods();
        assertEquals(methods.size(), 2);
    }

    @Test
    public void test_both() {
        Collection<RsiClass> classes = classExpressions(parseCode("class foo = { as _; val mutable x = []; pub get_x = x; }"));

        RsiClass clazz = first(classes);
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 1);
    }

    @Test
    public void test_classConstruct() {
        Collection<RsiClass> classes = classExpressions(parseCode("class c (m: int) = { as self; pub m = m; initializer (all_c := [(self :> c), ...all_c^]); }"));

        RsiClass clazz = first(classes);
        assertEquals(clazz.getParameters().size(), 0);
        assertNotNull(clazz.getConstructor());
        assertSize(1, PsiTreeUtil.findChildrenOfType(clazz, RsiClassConstructor.class));
    }

    @Test
    public void test_classConstraint() {
        Collection<RsiClass> classes = classExpressions(parseCode("class circle ('a) (c: 'a) = { as _; constraint 'a = #point; val mutable center = c; pub set_center = c => center = c; pub move = center#move; }"));

        RsiClass clazz = first(classes);
        assertEquals("circle", first(classes).getName());
        assertNotNull(clazz.getParameters());
        assertNotNull(clazz.getConstructor());
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 2);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/310
    @Test
    public void test_GH_310() {
        FileBase file = parseCode("class type control = { pub detach: unit => unit; };\n type errpage = page(list((int, string)));");
        List<PsiNamedElement> es = expressions(file);

        assertSize(2, es);
        assertInstanceOf(es.get(0), RsiClass.class);
        assertEquals("control", es.get(0).getName());
        assertInstanceOf(es.get(1), PsiType.class);
        assertEquals("errpage", es.get(1).getName());
    }
}
