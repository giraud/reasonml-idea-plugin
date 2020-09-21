package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiClassField;
import com.reason.lang.core.psi.PsiClassMethod;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ClassParsingTest extends OclParsingTestCase {
  public void test_basic() {
    Collection<PsiClass> classes = classExpressions(parseCode("class foo = object end"));

    assertEquals(1, classes.size());
    assertEquals("foo", first(classes).getName());
  }

  public void test_classType() {
    Collection<PsiClass> classes =
        classExpressions(
            parseCode(
                "class type restricted_point_type = object method get_x : int method bump : unit end"));

    assertEquals(1, classes.size());
    assertEquals("restricted_point_type", first(classes).getName());
  }

  public void test_fields() {
    Collection<PsiClass> classes =
        classExpressions(parseCode("class foo = object val mutable a = [] val b = 2 end"));

    PsiClass clazz = first(classes);
    Collection<PsiClassField> fields = clazz.getFields();
    assertEquals(fields.size(), 2);
  }

  public void test_methods() {
    Collection<PsiClass> classes =
        classExpressions(parseCode("class foo = object method get_x = x method get_y = y end"));

    PsiClass clazz = first(classes);
    Collection<PsiClassMethod> methods = clazz.getMethods();
    assertEquals(methods.size(), 2);
  }

  public void test_both() {
    Collection<PsiClass> classes =
        classExpressions(parseCode("class foo = object val mutable x = [] method get_x = x end"));

    PsiClass clazz = first(classes);
    assertEquals(clazz.getFields().size(), 1);
    assertEquals(clazz.getMethods().size(), 1);
  }

  public void test_classConstraint() {
    Collection<PsiClass> classes =
        classExpressions(
            parseCode(
                "class ['a] circle (c : 'a) = object constraint 'a = #point val mutable center = c method set_center c = center <- c method move = center#move end"));

    PsiClass clazz = first(classes);
    assertEquals("circle", first(classes).getName());
    assertNotNull(clazz.getParameters());
    assertNotNull(clazz.getConstructor());
    assertEquals(clazz.getFields().size(), 1);
    assertEquals(clazz.getMethods().size(), 2);
  }

  public void test_GH_268() {
    PsiClass clazz =
        first(
            classExpressions(
                parseCode(
                    "class tag : text_tag -> object method as_tag : text_tag method connect : tag_signals end")));

    assertSize(2, clazz.getMethods());
  }

  public void test_GH_269() {
    PsiClass e =
        first(
            classExpressions(
                parseCode(
                    "class type ops = object\n method go_to_insert : unit task\n method go_to_mark : GText.mark -> unit task\n method process_next_phrase : unit task\n method get_n_errors : int\n method get_errors : (int * string) list\n method get_slaves_status : int * int * string CString.Map.t\n method handle_failure : handle_exn_rty -> unit task\n method destroy : unit -> unit end")));

    assertSize(8, e.getMethods());
    ArrayList<PsiClassMethod> methods = new ArrayList<>(e.getMethods());
    assertEquals("go_to_insert", methods.get(0).getName());
    assertEquals("unit task", methods.get(0).getSignature().getText());
    assertEquals("go_to_mark", methods.get(1).getName());
    assertEquals("GText.mark -> unit task", methods.get(1).getSignature().getText());
    assertEquals("process_next_phrase", methods.get(2).getName());
    assertEquals("unit task", methods.get(2).getSignature().getText());
    assertEquals("get_n_errors", methods.get(3).getName());
    assertEquals("int", methods.get(3).getSignature().getText());
    assertEquals("get_errors", methods.get(4).getName());
    assertEquals("(int * string) list", methods.get(4).getSignature().getText());
    assertEquals("get_slaves_status", methods.get(5).getName());
    assertEquals("int * int * string CString.Map.t", methods.get(5).getSignature().getText());
    assertEquals("handle_failure", methods.get(6).getName());
    assertEquals("handle_exn_rty -> unit task", methods.get(6).getSignature().getText());
    assertEquals("destroy", methods.get(7).getName());
    assertEquals("unit -> unit", methods.get(7).getSignature().getText());
  }
}
