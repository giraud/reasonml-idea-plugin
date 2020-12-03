package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

import static com.intellij.psi.util.PsiTreeUtil.*;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends OclParsingTestCase {
  public void test_abstractType() {
    PsiType type = first(typeExpressions(parseCode("type t")));
    assertEquals("t", type.getName());
  }

  public void test_recursiveType() {
    PsiType type =
        first(
            typeExpressions(parseCode("type 'a tree = | Leaf of 'a | Tree of 'a tree  * 'a tree")));
    assertEquals("tree", type.getName());
  }

  public void test_bindingWithVariant() {
    assertNotNull(
        first(
            findChildrenOfType(
                first(typeExpressions(parseCode("type t = | Tick"))), PsiTypeBinding.class)));
  }

  public void test_bindingWithRecord() {
    PsiFile file = parseCode("type t = {count: int;}");

    assertNotNull(first(findChildrenOfType(first(typeExpressions(file)), PsiTypeBinding.class)));
  }

  public void test_bindingWithObject() {
    PsiType e = first(typeExpressions(parseCode("type t = <count: int>")));

    assertInstanceOf(e.getBinding().getFirstChild(), PsiObject.class);
  }

  public void test_bindingWithRecordAs() {
    PsiTypeBinding typeBinding =
        first(
            findChildrenOfType(
                first(
                    typeExpressions(
                        parseCode(
                            "type 'branch_type branch_info = { kind : [> `Master] as 'branch_type; pos : id; }"))),
                PsiTypeBinding.class));
    PsiRecord record = PsiTreeUtil.findChildOfType(typeBinding, PsiRecord.class);
    List<PsiRecordField> fields = new ArrayList<>(record.getFields());
    assertEquals(2, fields.size());
    assertEquals("kind", fields.get(0).getName());
    assertEquals("pos", fields.get(1).getName());
  }

  public void test_chainDef() {
    FileBase file =
        parseCode(
            "type 'branch_type branch_info = 'branch_type Vcs_.branch_info = { kind : [> `Master] as 'branch_type; root : id; pos  : id; }");

    assertEquals(1, childrenCount(file));
  }

  public void test_parameterizedType() {
    PsiType e = first(typeExpressions(parseCode("type ('a, 'b) declaration_arity = | RegularArity of 'a")));

    assertEquals("declaration_arity", e.getName());
    assertEquals("| RegularArity of 'a", e.getBinding().getText());

    // zzz PsiTypeConstrName cname = e.getConstrName();
    // assertTrue(cname.hasParameters());
  }

  public void test_apply_params() {
    PsiType e = first(typeExpressions(parseCode("type 'value t = (key,'value,Comparator.identity) Belt.Map.t")));

    assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class));
  }

  public void test_qname_functor() {
    PsiType e = first(typeExpressions(parseCode("module Coll = Hash.Make(struct type nonrec t = t\n let equal = " +
                                                    "Bsb_pkg_types.equal\n let hash (x : t) = Hashtbl.hash x\n end)")));

    assertEquals("Dummy.Coll.Make[0].t", e.getQualifiedName());
  }
}
