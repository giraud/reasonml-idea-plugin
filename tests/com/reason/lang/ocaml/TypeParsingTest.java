package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;
import java.util.stream.*;

import static com.intellij.psi.util.PsiTreeUtil.*;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends OclParsingTestCase {
    @Test
    public void test_abstract_type() {
        RPsiType type = first(typeExpressions(parseCode("type t")));
        assertEquals("t", type.getName());
    }

    @Test
    public void test_recursive_type() {
        RPsiType type = first(typeExpressions(parseCode("type rec 'a tree = | Leaf of 'a | Tree of 'a tree  * 'a tree")));
        assertEquals("tree", type.getName());
    }

    @Test
    public void test_path() {
        RPsiType e = first(typeExpressions(parseCode("type t = A.B.other")));

        assertEquals("t", e.getName());
        assertFalse(e.isAbstract());
        assertEquals("A.B.other", e.getBinding().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVariantDeclaration.class));
        List<RPsiUpperSymbol> modules = ORUtil.findImmediateChildrenOfClass(e.getBinding(), RPsiUpperSymbol.class);
        assertSize(2, modules);
        List<IElementType> es = modules.stream().map(u -> u.getNode().getElementType()).collect(Collectors.toList());
        assertEquals(List.of(myTypes.A_MODULE_NAME, myTypes.A_MODULE_NAME), es);
    }

    @Test
    public void test_option() {
        RPsiType e = first(typeExpressions(parseCode("type t = string array option")));

        RPsiOption option = PsiTreeUtil.findChildOfType(e, RPsiOption.class);
        assertNotNull(option);
        assertEquals("string array option", option.getText());
    }

    @Test
    public void test_bindingWithVariant() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Tick")));

        RPsiTypeBinding binding = first(findChildrenOfType(e, RPsiTypeBinding.class));
        assertNotNull(binding);
    }

    @Test
    public void test_binding_with_record() {
        PsiFile file = parseCode("type t = {count: int;}");

        assertNotNull(first(findChildrenOfType(first(typeExpressions(file)), RPsiTypeBinding.class)));
    }

    @Test
    public void test_type_special_props() {
        RPsiType e = first(typeExpressions(parseCode("type props = { "
                + "string: string; "
                + "ref: Dom.element Js.nullable => unit; "
                + "method: string; }")));

        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();
        List<RPsiRecordField> fields = record.getFields();
        assertEquals(3, fields.size());
        assertEquals("string", fields.get(0).getName());
        assertEquals(myTypes.LIDENT, fields.get(0).getNameIdentifier().getNode().getElementType());
        assertEquals("string", fields.get(0).getSignature().getText());
        assertEquals("ref", fields.get(1).getName());
        assertEquals(myTypes.LIDENT, fields.get(1).getNameIdentifier().getNode().getElementType());
        assertEquals("Dom.element Js.nullable => unit", fields.get(1).getSignature().getText());
        assertEquals("method", fields.get(2).getName());
        assertEquals(myTypes.LIDENT, fields.get(2).getNameIdentifier().getNode().getElementType());
        assertEquals("string", fields.get(2).getSignature().getText());
    }

    @Test
    public void test_closed_object() {
        RPsiType e = first(typeExpressions(parseCode("type t = <count: int>\n type x")));

        PsiElement b = e.getBinding();
        assertEquals("<count: int>", b.getText());
        assertInstanceOf(b.getFirstChild(), RPsiObject.class);
    }

    @Test
    public void test_open_object() {
        RPsiType e = first(typeExpressions(parseCode("type 'a t = < .. > as 'a\n type x")));

        PsiElement b = e.getBinding();
        assertEquals("< .. > as 'a", b.getText());
        assertInstanceOf(b.getFirstChild(), RPsiObject.class);
    }

    @Test
    public void test_binding_with_record_as() {
        RPsiTypeBinding typeBinding = first(findChildrenOfType(first(typeExpressions(parseCode(
                "type 'branch_type branch_info = { kind : [> `Master] as 'branch_type; pos : id; }"))), RPsiTypeBinding.class));

        RPsiRecord record = PsiTreeUtil.findChildOfType(typeBinding, RPsiRecord.class);
        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    @Test
    public void test_chain_definitions() {
        FileBase file = parseCode("type 'branch_type branch_info = 'branch_type Vcs_.branch_info = { kind: [> `Master] as 'branch_type; root: id; pos: id; }");

        assertEquals(1, childrenCount(file));
    }

    @Test
    public void test_parameterizedType() {
        RPsiType e = first(typeExpressions(parseCode("type ('a, 'b) declaration_arity = | RegularArity of 'a")));

        assertEquals("declaration_arity", e.getName());
        assertEquals("| RegularArity of 'a", e.getBinding().getText());
    }

    @Test
    public void test_apply_params() {
        RPsiType e = first(typeExpressions(parseCode("type 'value t = (key,'value,Comparator.identity) Belt.Map.t")));

        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiParameters.class));
    }

    @Test
    public void test_qname_functor() {
        RPsiType e = first(typeExpressions(parseCode("module Coll = Hash.Make(struct type nonrec t = t\n let equal = " +
                "Bsb_pkg_types.equal\n let hash (x : t) = Hashtbl.hash x\n end)")));

        assertEquals("Dummy.Coll.Make[0].t", e.getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/295
    @Test
    public void test_GH_295() {
        Collection<PsiNamedElement> es = expressions(parseCode("module Set = struct end type x = string"));

        assertSize(2, es);
        Iterator<PsiNamedElement> it = es.iterator();
        assertInstanceOf(it.next(), RPsiModule.class);
        assertInstanceOf(it.next(), RPsiType.class);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/326
    @Test
    public void test_GH_326() {
        RPsiType e = firstOfType(parseCode("type t = { buffer: GText.buffer; mutable breakpoints: breakpoint list }"), RPsiType.class);

        RPsiRecord r = (RPsiRecord) e.getBinding().getFirstChild();
        List<RPsiRecordField> f = r.getFields();
        assertSize(2, f);
        assertEquals("buffer", f.get(0).getName());
        assertEquals("breakpoints", f.get(1).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/360
    @Test
    public void test_GH_360() {
        RPsiVal e = firstOfType(parseCode("val push : exn -> Exninfo.iexn\n [@@ocaml.deprecated \"please use [Exninfo.capture]\"]"), RPsiVal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("exn -> Exninfo.iexn", signature.getText());
    }
}
