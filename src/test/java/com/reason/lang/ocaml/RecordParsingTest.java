package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordParsingTest extends OclParsingTestCase {
    @Test
    public void test_declaration() {
        RPsiType e = firstOfType(parseCode("type r = { a: int; b: string list }"), RPsiType.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = record.getFields();
        assertEquals("a", fields.get(0).getName());
        assertEquals("int", fields.get(0).getSignature().asText(getLangProps()));
        assertEquals("b", fields.get(1).getName());
        assertEquals("string list", fields.get(1).getSignature().asText(getLangProps()));
    }

    @Test
    public void test_usage() {
        RPsiLet e = firstOfType(parseCode("let r = { a = 1; b = 2; c = 3 }"), RPsiLet.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = record.getFields();
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertEquals("1", fields.get(0).getValue().getText());
        assertNull(fields.get(0).getSignature());
        assertEquals("b", fields.get(1).getName());
        assertEquals("2", fields.get(1).getValue().getText());
        assertNull(fields.get(1).getSignature());
        assertEquals("c", fields.get(2).getName());
        assertEquals("3", fields.get(2).getValue().getText());
        assertNull(fields.get(2).getSignature());
    }

    @Test
    public void test_usage_deep() {
        RPsiLet e = firstOfType(parseCode("let r = { a = [| 1; 2 |]; b = { b1 = { b11 = 3 } }; c = 4 }"), RPsiLet.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertEquals("[| 1; 2 |]", fields.get(0).getValue().getText());
        assertEquals("b", fields.get(1).getName());
        assertEquals("{ b1 = { b11 = 3 } }", fields.get(1).getValue().getText());
        assertEquals("c", fields.get(2).getName());
        assertEquals("4", fields.get(2).getValue().getText());

        List<RPsiRecordField> allFields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(fields.get(1), RPsiRecordField.class));
        assertEquals("b1", allFields.get(0).getName());
        assertEquals("b11", allFields.get(1).getName());
    }

    @Test
    public void test_mixin() {
        RPsiRecord e = firstOfType(parseCode("let x = { component with otherField = 1}"), RPsiRecord.class);

        assertEquals("component", PsiTreeUtil.findChildOfType(e, RPsiMixinField.class).getText());
        RPsiRecordField field = e.getFields().iterator().next();
        assertEquals("otherField", field.getName());
    }

    @Test
    public void test_annotations() {
        RPsiType e = firstOfType(parseCode("type props = { key: string [@bs.optional]; ariaLabel: string [@bs.optional] [@bs.as \"aria-label\"]; }"), RPsiType.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(2, fields);
        assertEquals("key", fields.get(0).getName());
        assertEquals("ariaLabel", fields.get(1).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/440
    @Test
    public void test_GH_440() {
        RPsiFunction e = firstOfType(parseCode("let fn {a; b=(x,y)} = a"), RPsiFunction.class);

        assertSize(1, e.getParameters());
        RPsiParameterDeclaration ep0 = e.getParameters().get(0);
        RPsiRecord r = (RPsiRecord) ep0.getFirstChild();
        assertSize(2, r.getFields());
        RPsiRecordField f0 = r.getFields().get(0);
        assertEquals("a", f0.getName());
        assertNull(f0.getValue());
        RPsiRecordField f1 = r.getFields().get(1);
        assertEquals("b", f1.getName());
        RPsiFieldValue value = f1.getValue();
        RPsiTuple f1t = (RPsiTuple) value.getFirstChild();
        assertEquals("(x,y)", f1t.getText());
        assertEquals("a", e.getBody().getText());
    }
}
