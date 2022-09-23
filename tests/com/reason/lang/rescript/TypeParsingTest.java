package com.reason.lang.rescript;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class TypeParsingTest extends ResParsingTestCase {
    @Test
    public void test_abstract_type() {
        RPsiType e = first(typeExpressions(parseCode("type t")));

        assertEquals("t", e.getName());
        assertTrue(e.isAbstract());
    }

    @Test
    public void test_simple_binding() {
        RPsiType e = first(typeExpressions(parseCode("type t = int")));

        assertEquals("t", e.getName());
        assertFalse(e.isAbstract());
        assertEquals("int", e.getBinding().getText());
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
        RPsiType e = first(typeExpressions(parseCode("type t = option<array<string>>")));

        RPsiOption option = PsiTreeUtil.findChildOfType(e, RPsiOption.class);
        assertNotNull(option);
        assertEquals("option<array<string>>", option.getText());
    }

    @Test
    public void test_recursive_type() {
        RPsiType e = first(typeExpressions(parseCode("type rec tree<'a> = Leaf('a) | Tree(tree<'a>, tree<'a>)")));

        assertEquals("tree", e.getName());
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiFunctionCall.class));
    }

    @Test
    public void test_type_binding_with_variant() {
        RPsiType e = first(typeExpressions(parseCode("type t = | Tick")));

        assertNotNull(e.getBinding());
    }

    @Test
    public void test_poly_variant() {
        RPsiType e = first(typeExpressions(parseCode("type t = [#visible | #hidden | #collapse]")));
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiPatternMatch.class));
        assertSize(3, PsiTreeUtil.findChildrenOfType(e, RPsiVariantDeclaration.class));
    }

    @Test
    public void test_type_binding_with_record() {
        RPsiType e = first(typeExpressions(parseCode("type t = {count: int, @optional key: string => unit,}")));

        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();
        Collection<RPsiRecordField> fields = record.getFields();
        assertEquals(2, fields.size());
    }

    @Test
    public void test_type_special_props() {
        RPsiType e = first(typeExpressions(parseCode("type props = { "
                + "string: string, "
                + "ref: Js.nullable<Dom.element> => unit, "
                + "method: string, }")));

        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();
        List<RPsiRecordField> fields = record.getFields();
        assertEquals(3, fields.size());
        assertEquals("string", fields.get(0).getName());
        assertEquals(myTypes.LIDENT, fields.get(0).getNameIdentifier().getNode().getElementType());
        assertEquals("string", fields.get(0).getSignature().getText());
        assertEquals("ref", fields.get(1).getName());
        assertEquals(myTypes.LIDENT, fields.get(1).getNameIdentifier().getNode().getElementType());
        assertEquals("Js.nullable<Dom.element> => unit", fields.get(1).getSignature().getText());
        assertEquals("method", fields.get(2).getName());
        assertEquals(myTypes.LIDENT, fields.get(2).getNameIdentifier().getNode().getElementType());
        assertEquals("string", fields.get(2).getSignature().getText());
    }

    @Test
    public void test_binding_with_record_as() {
        RPsiType e = first(typeExpressions(parseCode("type branch_info<'branch_type> = {kind: [> #Master] as 'branch_type, pos: [< #Other ] as 'id}")));

        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();
        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());

        assertEquals(2, fields.size());
        assertEquals("kind", fields.get(0).getName());
        assertEquals("pos", fields.get(1).getName());
    }

    @Test
    public void test_parameterized() {
        RPsiType e = first(typeExpressions(parseCode("type declaration_arity<'a, 'b> = RegularArity<'a>")));

        assertEquals("declaration_arity", e.getName());
        assertNotNull(PsiTreeUtil.findChildOfType(e, RPsiParameters.class));
        assertEquals("RegularArity<'a>", e.getBinding().getText());
    }

    @Test
    public void test_scope() {
        RPsiExternal e = first(externalExpressions(parseCode("external createElement : (reactClass, ~props: {..}=?, array<reactElement>,) => reactElement = \"createElement\"")));

        List<RPsiSignatureItem> items = e.getSignature().getItems();

        assertSize(4, items);
        assertEquals("reactClass", items.get(0).getText());
        assertEquals("~props: {..}=?", items.get(1).getText());
        assertTrue(items.get(1).isNamedItem());
        assertEquals("props", items.get(1).getName());
        assertEquals("{..}", items.get(1).getSignature().getText());
        assertTrue(items.get(1).isOptional());
        assertEquals("?", items.get(1).getDefaultValue().getText());
        assertEquals("array<reactElement>", items.get(2).getText());
        assertEquals("reactElement", items.get(3).getText());
    }

    @Test
    public void test_JsObject() {
        RPsiType e = first(typeExpressions(parseCode("type t = {\"a\": string}")));

        assertTrue(e.isJsObject());
        RPsiObjectField f = PsiTreeUtil.findChildOfType(e.getBinding(), RPsiObjectField.class);
        assertEquals("a", f.getName());
        assertEquals("string", f.getSignature().getText());
    }

    @Test
    public void test_apply_params() {
        RPsiType e = first(typeExpressions(parseCode("type t<'value> = Belt.Map.t<key, 'value, Comparator.identity>")));

        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiFunctionCall.class));
    }

    @Test
    public void test_abstract_annotation() {
        RPsiType t = first(typeExpressions(parseCode("type t\n@module")));

        assertEquals("type t", t.getText());
    }

    @Test
    public void test_not_a_tag() {
        RPsiLet e = firstOfType(parseCode("let make = (x: array<int>) => x"), RPsiLet.class);

        assertNull(PsiTreeUtil.findChildOfType(e, RPsiUpperTagName.class));
        RPsiParameterDeclaration param = e.getFunction().getParameters().get(0);
        RPsiSignatureItem sigItem = param.getSignature().getItems().get(0);
        assertEquals("array<int>", sigItem.getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/326
    @Test
    public void test_GH_326() {
        RPsiType e = firstOfType(parseCode("type t = {buffer: GText.buffer, mutable breakpoints: list<breakpoint>}"), RPsiType.class);

        RPsiRecord r = (RPsiRecord) e.getBinding().getFirstChild();
        List<RPsiRecordField> f = r.getFields();
        assertSize(2, f);
        assertEquals("buffer", f.get(0).getName());
        assertEquals("breakpoints", f.get(1).getName());
    }
}
