package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ClassParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiClass e = firstOfType(parseCode("class foo = { as _; }"), RPsiClass.class);

        assertEquals("foo", e.getName());
        assertEquals("{ as _; }", e.getClassBody().getText());
    }

    @Test
    public void test_classType() {
        RPsiClass e = firstOfType(parseCode("class type restricted_point_type = { pub get_x: int; pub bump: unit; }"), RPsiClass.class);

        assertEquals("restricted_point_type", e.getName());
    }

    @Test
    public void test_fields() {
        RPsiClass e = firstOfType(parseCode("class foo = { as _; val mutable a = []; val b = 2; }"), RPsiClass.class);

        assertSize(2, e.getFields());
    }

    @Test
    public void test_methods() {
        RPsiClass e = firstOfType(parseCode("class foo = { as _; pub get_x = x; pub get_y = y; }"), RPsiClass.class);

        assertSize(2, e.getMethods());
    }

    @Test
    public void test_both() {
        RPsiClass e = firstOfType(parseCode("class foo = { as _; val mutable x = []; pub get_x = x; }"), RPsiClass.class);

        assertSize(1, e.getFields());
        assertSize(1, e.getMethods());
    }

    @Test
    public void test_classConstruct() {
        RPsiClass e = firstOfType(parseCode("class c (m: int) = { as self; pub m = m; initializer (all_c := [(self :> c), ...all_c^]); }"), RPsiClass.class);

        assertSize(0, e.getParameters());
        assertNotNull(e.getConstructor());
        assertSize(1, PsiTreeUtil.findChildrenOfType(e, RPsiClassConstructor.class));
    }

    @Test
    public void test_classConstraint() {
        RPsiClass e = firstOfType(parseCode("class circle ('a) (c: 'a) = { as _; constraint 'a = #point; val mutable center = c; pub set_center = c => center = c; pub move = center#move; }"), RPsiClass.class);

        assertEquals("circle", e.getName());
        assertNotNull(e.getParameters());
        assertNotNull(e.getConstructor());
        assertSize(1, e.getFields());
        assertSize(2, e.getMethods());
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


    // https://github.com/giraud/reasonml-idea-plugin/issues/491
    @Test
    public void test_GH_491_object_initializer() {
        RPsiClass e = firstOfType(parseCode("""
                class c = {
                  as self;
                  pub tag = { tag_bold: true; };
                  initializer {
                    let x = 1;
                    let fn = y => x + y;
                    ignore(fn(2));
                  };
                };
                """), RPsiClass.class);

        assertSize(0, e.getFields());
        assertSize(1, e.getMethods());
        assertTextEquals("""
                initializer {
                    let x = 1;
                    let fn = y => x + y;
                    ignore(fn(2));
                  }""", e.getInitializer().getText());
    }
}
