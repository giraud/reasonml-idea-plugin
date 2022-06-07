package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends RmlParsingTestCase {
    public void test_abstract_type() {
        PsiType e = first(typeExpressions(parseCode("type t;")));
        assertEquals("t", e.getName());
        assertTrue(e.isAbstract());
    }

    public void test_simple_binding() {
        PsiType e = first(typeExpressions(parseCode("type t = int;")));

        assertEquals("t", e.getName());
        assertFalse(e.isAbstract());
        assertEquals("int", e.getBinding().getText());
    }

    public void test_path() {
        PsiType e = first(typeExpressions(parseCode("type t = A.B.other")));

        assertEquals("t", e.getName());
        assertFalse(e.isAbstract());
        assertEquals("A.B.other", e.getBinding().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiVariantDeclaration.class));
    }

    public void test_option() {
        PsiType e = first(typeExpressions(parseCode("type t = option(array(string))")));

        PsiOption option = PsiTreeUtil.findChildOfType(e, PsiOption.class);
        assertNotNull(option);
        assertEquals("option(array(string))", option.getText());
    }

    public void test_recursive_type() {
        PsiType e = first(typeExpressions(parseCode("type tree('a) = | Leaf('a) | Tree(tree('a), tree('a));")));
        assertEquals("tree", e.getName());
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCall.class));
    }

    public void test_type_binding_with_variant() {
        PsiType e = first(typeExpressions(parseCode("type t = | Tick;")));
        assertNotNull(e.getBinding());
    }

    public void test_poly_variant() {
        PsiType e = first(typeExpressions(parseCode("type t = [ | `visible | `hidden | `collapse ];")));
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiPatternMatch.class));
        assertSize(3, PsiTreeUtil.findChildrenOfType(e, PsiVariantDeclaration.class));
    }

    public void test_type_binding_with_record() {
        PsiType e = first(typeExpressions(parseCode("type t = {count: int,\n [@bs.optional] key: string => unit\n};")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(2, fields.size());
    }

    public void test_type_special_props() {
        PsiType e = first(typeExpressions(parseCode(
                "type props = { "
                        + "string: string, "
                        + "ref: Js.nullable(Dom.element) => unit, "
                        + "method: string };")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        Collection<PsiRecordField> fields = record.getFields();
        assertEquals(3, fields.size());
    }

    public void test_binding_with_record_as() {
        PsiType e = first(typeExpressions(parseCode("type branch_info('branch_type) = { kind: [> | `Master] as 'branch_type, pos: id, };")));

        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    public void test_parameterized() {
        PsiType e = first(typeExpressions(parseCode("type declaration_arity('a, 'b) = | RegularArity('a);")));

        assertEquals("declaration_arity", e.getName());
        assertEquals("| RegularArity('a)", e.getBinding().getText());
    }

    public void test_scope() {
        PsiExternal e = first(externalExpressions(parseCode("external createElement : (reactClass, ~props: Js.t({..})=?, array(reactElement)) => reactElement =  \"createElement\"")));

        List<PsiSignatureItem> items = e.getSignature().getItems();

        assertSize(4, items);
        assertEquals("reactClass", items.get(0).getText());
        assertEquals("~props: Js.t({..})=?", items.get(1).getText());
        assertTrue(items.get(1).isNamedItem());
        assertEquals("props", items.get(1).getName());
        assertEquals("Js.t({..})", items.get(1).getSignature().getText());
        assertTrue(items.get(1).isOptional());
        assertEquals("?", items.get(1).getDefaultValue().getText());
        assertEquals("array(reactElement)", items.get(2).getText());
        assertEquals("reactElement", items.get(3).getText());
    }

    public void test_JsObject() {
        PsiType e = first(typeExpressions(parseCode("type t = {. a: string };")));

        assertTrue(e.isJsObject());
        PsiObjectField f = PsiTreeUtil.findChildOfType(e.getBinding(), PsiObjectField.class);
        assertEquals("a", f.getName());
        assertEquals("string", f.getSignature().getText());
    }

    public void test_apply_params() {
        PsiType e = first(typeExpressions(parseCode("type t('value) = Belt.Map.t(key, 'value, Comparator.identity);")));

        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiFunctionCall.class));
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/326
    public void test_GH_326() {
        PsiType e = firstOfType(parseCode("type t = { buffer: GText.buffer, mutable breakpoints: list(breakpoint) }"), PsiType.class);

        PsiRecord r = (PsiRecord) e.getBinding().getFirstChild();
        List<PsiRecordField> f = r.getFields();
        assertSize(2, f);
        assertEquals("buffer", f.get(0).getName());
        assertEquals("breakpoints", f.get(1).getName());
    }
}
