package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ClassParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class foo = { as _; }"));

        assertEquals(1, classes.size());
        assertEquals("foo", first(classes).getName());
        assertEquals("{ as _; }", first(classes).getClassBody().getText());
    }

    @Test
    public void test_classType() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class type restricted_point_type = { pub get_x: int; pub bump: unit; }"));

        assertEquals(1, classes.size());
        assertEquals("restricted_point_type", first(classes).getName());
    }

    @Test
    public void test_fields() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class foo = { as _; val mutable a = []; val b = 2; }"));

        RPsiClass clazz = first(classes);
        Collection<RPsiClassField> fields = clazz.getFields();
        assertEquals(fields.size(), 2);
    }

    @Test
    public void test_methods() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class foo = { as _; pub get_x = x; pub get_y = y; }"));

        RPsiClass clazz = first(classes);
        Collection<RPsiClassMethod> methods = clazz.getMethods();
        assertEquals(methods.size(), 2);
    }

    @Test
    public void test_both() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class foo = { as _; val mutable x = []; pub get_x = x; }"));

        RPsiClass clazz = first(classes);
        assertEquals(clazz.getFields().size(), 1);
        assertEquals(clazz.getMethods().size(), 1);
    }

    @Test
    public void test_classConstruct() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class c (m: int) = { as self; pub m = m; initializer (all_c := [(self :> c), ...all_c^]); }"));

        RPsiClass clazz = first(classes);
        assertEquals(clazz.getParameters().size(), 0);
        assertNotNull(clazz.getConstructor());
        assertSize(1, PsiTreeUtil.findChildrenOfType(clazz, RPsiClassConstructor.class));
    }

    @Test
    public void test_classConstraint() {
        Collection<RPsiClass> classes = classExpressions(parseCode("class circle ('a) (c: 'a) = { as _; constraint 'a = #point; val mutable center = c; pub set_center = c => center = c; pub move = center#move; }"));

        RPsiClass clazz = first(classes);
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
        assertInstanceOf(es.get(0), RPsiClass.class);
        assertEquals("control", es.get(0).getName());
        assertInstanceOf(es.get(1), RPsiType.class);
        assertEquals("errpage", es.get(1).getName());
    }


    // https://github.com/giraud/reasonml-idea-plugin/issues/269
    @Test
    public void test_GH_269() {
        RPsiClass e = firstOfType(parseCode("""
                class type ops = {
                  pub go_to_insert: task(unit);
                  pub go_to_mark: GText.mark => task(unit);
                  pub process_next_phrase: task(unit);
                  pub get_n_errors: int;
                  pub get_errors: list((int, string));
                  pub get_slaves_status: (int, int, CString.Map.t(string));
                  pub handle_failure: handle_exn_rty => task(unit);
                  pub destroy: unit => unit;
                };
                """), RPsiClass.class);

        assertSize(8, e.getMethods());
        ArrayList<RPsiClassMethod> methods = new ArrayList<>(e.getMethods());

        assertEquals("go_to_insert", methods.get(0).getName());
        assertEquals("task(unit)", methods.get(0).getSignature().getText());
        assertEquals("go_to_mark", methods.get(1).getName());
        assertEquals("GText.mark => task(unit)", methods.get(1).getSignature().getText());
        assertEquals("process_next_phrase", methods.get(2).getName());
        assertEquals("task(unit)", methods.get(2).getSignature().getText());
        assertEquals("get_n_errors", methods.get(3).getName());
        assertEquals("int", methods.get(3).getSignature().getText());
        assertEquals("get_errors", methods.get(4).getName());
        assertEquals("list((int, string))", methods.get(4).getSignature().getText());
        assertEquals("get_slaves_status", methods.get(5).getName());
        assertEquals("(int, int, CString.Map.t(string))", methods.get(5).getSignature().getText());
        assertEquals("handle_failure", methods.get(6).getName());
        assertEquals("handle_exn_rty => task(unit)", methods.get(6).getSignature().getText());
        assertEquals("destroy", methods.get(7).getName());
        assertEquals("unit => unit", methods.get(7).getSignature().getText());
    }

}
