package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordParsingTest extends ResParsingTestCase {
    @Test
    public void test_declaration() {
        RPsiType e = first(typeExpressions(parseCode("type r = {a: int, b: option<string>}")));
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = record.getFields();
        assertEquals("a", fields.get(0).getName());
        assertEquals("int", fields.get(0).getSignature().asText(getLangProps()));
        assertEquals("b", fields.get(1).getName());
        assertEquals("option<string>", fields.get(1).getSignature().asText(getLangProps()));
    }

    @Test
    public void test_usage() {
        RPsiLet e = firstOfType(parseCode("let r = {a: 1, b: 2, c: 3}"), RPsiLet.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
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
    public void test_usage_with_sig() {
        RPsiLet e = firstOfType(parseCode("let r: M.t = {a: 1, b: 2, c: 3}"), RPsiLet.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertNull(fields.get(0).getSignature());
        assertEquals("b", fields.get(1).getName());
        assertNull(fields.get(1).getSignature());
        assertEquals("c", fields.get(2).getName());
        assertNull(fields.get(2).getSignature());
    }

    @Test
    public void test_usage_deep() {
        RPsiLet e = firstOfType(parseCode("let r = { a: [ 1, 2 ], b: { b1: { b11: 3 } }, c: 4 }"), RPsiLet.class);
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertEquals("b", fields.get(1).getName());
        assertEquals("c", fields.get(2).getName());

        List<RPsiRecordField> allFields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(fields.get(1), RPsiRecordField.class));
        assertEquals("b1", allFields.get(0).getName());
        assertEquals("b11", allFields.get(1).getName());
    }

    @Test
    public void test_mixin() {
        RPsiLet let = firstOfType(parseCode("let x = {...component, otherField: 1}"), RPsiLet.class);

        RPsiRecord record = (RPsiRecord) let.getBinding().getFirstChild();
        RPsiRecordField field = record.getFields().iterator().next();
        assertEquals(field.getName(), "otherField");
    }

    @Test
    public void test_annotations() {
        RPsiType e = first(typeExpressions(parseCode("type props = { @optional key: string, @optional @as(\"aria-label\") ariaLabel: string }")));
        RPsiRecord record = (RPsiRecord) e.getBinding().getFirstChild();

        List<RPsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(2, fields);
        assertEquals("key", fields.get(0).getName());
        assertEquals("ariaLabel", fields.get(1).getName());
    }

    @Test
    public void test_annotation_after() {
        FileBase e = parseCode("type t = { key: string }\n@module(\"x\")");

        RPsiType t = ORUtil.findImmediateFirstChildOfClass(e, RPsiType.class);
        RPsiAnnotation a = ORUtil.findImmediateFirstChildOfClass(e, RPsiAnnotation.class);

        assertEquals("{ key: string }", t.getBinding().getText());
        assertEquals("@module", a.getName());
    }

    @Test
    public void test_inside_module() {
        RPsiInnerModule e = firstOfType(parseCode("module M = { let _ = (x) => { ...x, } }"), RPsiInnerModule.class);

        RPsiFunction ef = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertEquals("{ ...x, }", ef.getBody().getText());
        assertEquals("{ let _ = (x) => { ...x, } }", e.getBody().getText());
    }

    @Test
    public void test_if() {
        RPsiRecord e = firstOfType(parseCode("""
                let _ = {
                  sortable: Manual({ ascending: idx == 0 ? Some(dir > 0) : None })
                }
                """), RPsiRecord.class);

        assertSize(1, e.getFields());
        RPsiRecordField f0 = e.getFields().get(0);
        assertEquals("sortable", f0.getName());
        RPsiFieldValue v0 = f0.getValue();
        RPsiRecordField f00 = PsiTreeUtil.findChildOfType(v0, RPsiRecordField.class);
        assertEquals("ascending", f00.getName());
        RPsiTernary t = ORUtil.findImmediateFirstChildOfClass(f00.getValue(), RPsiTernary.class);
        assertEquals("idx == 0 ? Some(dir > 0) : None", t.getText());
        assertEquals("Manual({ ascending: idx == 0 ? Some(dir > 0) : None })", f0.getValue().getText());
    }

    @Test
    public void test_inside_function() {
        RPsiFunction e = firstOfType(parseCode("let fn = () => {\n let r = {\n f: 1\n }\n }"), RPsiFunction.class);

        assertEquals("{\n let r = {\n f: 1\n }\n }", e.getBody().getText());
    }

    @Test
    public void test_optional_keys() {
        RPsiRecord e = firstOfType(parseCode("""
                type domProps = {
                  key?: string,
                  children?: JsxU.element,
                  ref?: domRef,
                  @as("aria-current")
                  ariaCurrent?: [#page | #step | #location | #date | #time | #"true" | #"false"],
                  className?: string /* substitute for "class" */,
                  @as("data-testid") dataTestId?: string,
                  onClick?: JsxEventU.Mouse.t => unit,
                }
                """), RPsiRecord.class);

        List<RPsiRecordField> fields = e.getFields();
        assertSize(7, fields);
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiTernary.class));
        assertEquals("key", fields.get(0).getName());
        assertEquals("children", fields.get(1).getName());
        assertEquals("ref", fields.get(2).getName());
        assertEquals("ariaCurrent", fields.get(3).getName());
        assertEquals("className", fields.get(4).getName());
        assertEquals("dataTestId", fields.get(5).getName());
        assertEquals("onClick", fields.get(6).getName());
    }
}
