package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends ResParsingTestCase {
    public void test_abstract_type() {
        PsiType e = first(typeExpressions(parseCode("type t")));
        assertEquals("t", e.getName());
        assertTrue(e.isAbstract());
    }

    public void test_recursive_type() {
        PsiType e = first(typeExpressions(parseCode("type rec tree<'a> = Leaf('a) | Tree(tree<'a>, tree<'a>)")));
        assertEquals("tree", e.getName());
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class));
    }

    public void test_type_binding_with_variant() {
        PsiType e = first(typeExpressions(parseCode("type t = | Tick")));
        assertNotNull(e.getBinding());
    }

    public void test_type_binding_with_record() {
        PsiType e = first(typeExpressions(parseCode("type t = {count: int, @optional key: string => unit,}")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(2, fields.size());
    }

    public void test_type_special_props() {
        PsiType e = first(typeExpressions(
                parseCode(
                        "type props = { "
                                + "string: string, "
                                + "ref: Js.nullable<Dom.element> => unit, "
                                + "method: string, }")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(3, fields.size());
    }

    public void test_binding_with_record_as() {
        PsiType e = first(typeExpressions(parseCode("type branch_info<'branch_type> = {kind: [> #Master] as 'branch_type, pos: [< #Other ] as 'id}")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    public void test_parameterized() {
        PsiType e = first(typeExpressions(parseCode("type declaration_arity<'a, 'b> = RegularArity<'a>")));

        assertEquals("declaration_arity", e.getName());
        assertEquals("RegularArity<'a>", e.getBinding().getText());
    }

    public void test_scope() {
        PsiExternal e = first(externalExpressions(parseCode("external createElement : (reactClass, ~props: {..}=?, array<reactElement>,) => reactElement = \"createElement\"")));

        List<PsiSignatureItem> items = e.getSignature().getItems();

        assertSize(4, items);
        assertEquals("reactClass", items.get(0).getText());
        assertEquals("~props: {..}=?", items.get(1).getText());
        assertTrue(items.get(1).isNamedItem());
        PsiNamedParam namedParam = items.get(1).getNamedParam();
        assertEquals("props", namedParam.getName());
        assertEquals("{..}", namedParam.getSignature().getText());
        assertTrue(namedParam.isOptional());
        assertEquals("?", namedParam.getDefaultValue());
        assertEquals("array<reactElement>", items.get(2).getText());
        assertEquals("reactElement", items.get(3).getText());
    }

    public void test_JsObject() {
        PsiType e = first(typeExpressions(parseCode("type t = {\"a\": string}")));

        assertTrue(e.isJsObject());
        PsiObjectField f = PsiTreeUtil.findChildOfType(e.getBinding(), PsiObjectField.class);
        assertEquals("a", f.getName());
        assertEquals("string", f.getSignature().getText());
    }

    public void test_apply_params() {
        PsiType e = first(typeExpressions(parseCode("type t<'value> = Belt.Map.t<key, 'value, Comparator.identity>")));

        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCallParams.class));
    }

    public void test_abstract_annotation() {
        PsiType t = first(typeExpressions(parseCode("type t\n@module")));

        assertEquals("type t", t.getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/326
    public void test_GH_326() {
        PsiType e = firstOfType(parseCode("type t = {buffer: GText.buffer, mutable breakpoints: list<breakpoint>}"), PsiType.class);

        PsiRecord r = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> f = r.getFields();
        assertSize(2, f);
        assertEquals("buffer", f.get(0).getName());
        assertEquals("breakpoints", f.get(1).getName());
    }
}
