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
    public void test_abstract_type() {
        PsiType type = first(typeExpressions(parseCode("type t")));
        assertEquals("t", type.getName());
    }

    public void test_recursive_type() {
        PsiType type = first(typeExpressions(parseCode("type rec 'a tree = | Leaf of 'a | Tree of 'a tree  * 'a tree")));
        assertEquals("tree", type.getName());
    }

    public void test_option() {
        PsiType e = first(typeExpressions(parseCode("type t = int option")));

        PsiOption option = PsiTreeUtil.findChildOfType(e, PsiOption.class);
        assertNotNull(option);
        assertEquals("int option", option.getText());
    }

    public void test_bindingWithVariant() {
        PsiType e = first(typeExpressions(parseCode("type t = | Tick")));

        PsiTypeBinding binding = first(findChildrenOfType(e, PsiTypeBinding.class));
        assertNotNull(binding);
    }

    public void test_bindingWithRecord() {
        PsiFile file = parseCode("type t = {count: int;}");

        assertNotNull(first(findChildrenOfType(first(typeExpressions(file)), PsiTypeBinding.class)));
    }

    public void test_closed_object() {
        PsiType e = first(typeExpressions(parseCode("type t = <count: int>\n type x")));

        PsiElement b = e.getBinding();
        assertEquals("<count: int>", b.getText());
        assertInstanceOf(b.getFirstChild(), PsiObject.class);
    }

    public void test_open_object() {
        PsiType e = first(typeExpressions(parseCode("type 'a t = < .. > as 'a\n type x")));

        PsiElement b = e.getBinding();
        assertEquals("< .. > as 'a", b.getText());
        assertInstanceOf(b.getFirstChild(), PsiObject.class);
    }

    public void test_bindingWithRecordAs() {
        PsiTypeBinding typeBinding = first(findChildrenOfType(first(
                        typeExpressions(parseCode("type 'branch_type branch_info = { kind : [> `Master] as 'branch_type; pos : id; }"))),
                PsiTypeBinding.class));
        PsiRecord record = PsiTreeUtil.findChildOfType(typeBinding, PsiRecord.class);
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    public void test_chainDef() {
        FileBase file = parseCode("type 'branch_type branch_info = 'branch_type Vcs_.branch_info = { kind : [> `Master] as 'branch_type; root : id; pos  : id; }");

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

        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiParameters.class));
    }

    public void test_qname_functor() {
        PsiType e = first(typeExpressions(parseCode("module Coll = Hash.Make(struct type nonrec t = t\n let equal = " +
                "Bsb_pkg_types.equal\n let hash (x : t) = Hashtbl.hash x\n end)")));

        assertEquals("Dummy.Coll.Make[0].t", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/295
    public void test_GH_295() {
        Collection<PsiNamedElement> es = expressions(parseCode("module Set = struct end type x = string"));

        assertSize(2, es);
        Iterator<PsiNamedElement> it = es.iterator();
        assertInstanceOf(it.next(), PsiModule.class);
        assertInstanceOf(it.next(), PsiType.class);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/326
    public void test_GH_326() {
        PsiType e = firstOfType(parseCode("type t = { buffer: GText.buffer; mutable breakpoints: breakpoint list }"), PsiType.class);

        PsiRecord r = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> f = r.getFields();
        assertSize(2, f);
        assertEquals("buffer", f.get(0).getName());
        assertEquals("breakpoints", f.get(1).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/360
    public void test_GH_360() {
        PsiVal e = firstOfType(parseCode("val push : exn -> Exninfo.iexn\n [@@ocaml.deprecated \"please use [Exninfo.capture]\"]"), PsiVal.class);

        PsiSignature signature = e.getSignature();
        assertEquals("exn -> Exninfo.iexn", signature.getText());
    }
}
