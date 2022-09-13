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
        PsiType e = first(typeExpressions(parseCode("type r = {a: int, b: option<string>}")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = record.getFields();
        assertEquals("a", fields.get(0).getName());
        assertEquals("int", fields.get(0).getSignature().asText(getLangProps()));
        assertEquals("b", fields.get(1).getName());
        assertEquals("option<string>", fields.get(1).getSignature().asText(getLangProps()));
    }

    @Test
    public void test_usage() {
        PsiLet e = first(letExpressions(parseCode("let r = {a: 1, b: 2, c: 3}")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertNull(fields.get(0).getSignature());
        assertEquals("b", fields.get(1).getName());
        assertNull(fields.get(1).getSignature());
        assertEquals("c", fields.get(2).getName());
        assertNull(fields.get(2).getSignature());
    }

    @Test
    public void test_usage_with_sig() {
        PsiLet e = first(letExpressions(parseCode("let r: M.t = {a: 1, b: 2, c: 3}")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
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
        PsiLet e = first(letExpressions(parseCode("let r = { a: [ 1, 2 ], b: { b1: { b11: 3 } }, c: 4 }")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertEquals("b", fields.get(1).getName());
        assertEquals("c", fields.get(2).getName());

        List<PsiRecordField> allFields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(fields.get(1), PsiRecordField.class));
        assertEquals("b1", allFields.get(0).getName());
        assertEquals("b11", allFields.get(1).getName());
    }

    @Test
    public void test_mixin() {
        PsiLet let = first(letExpressions(parseCode("let x = {...component, otherField: 1}")));

        PsiRecord record = (PsiRecord) let.getBinding().getFirstChild();
        PsiRecordField field = record.getFields().iterator().next();
        assertEquals(field.getName(), "otherField");
    }

    @Test
    public void test_annotations() {
        PsiType e = first(typeExpressions(parseCode("type props = { @optional key: string, @optional @as(\"aria-label\") ariaLabel: string }")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(2, fields);
        assertEquals("key", fields.get(0).getName());
        assertEquals("ariaLabel", fields.get(1).getName());
    }

    @Test
    public void test_annotation_after() {
        FileBase e = parseCode("type t = { key: string }\n@module(\"x\")");

        PsiType t = ORUtil.findImmediateFirstChildOfClass(e, PsiType.class);
        PsiAnnotation a = ORUtil.findImmediateFirstChildOfClass(e, PsiAnnotation.class);

        assertEquals("{ key: string }", t.getBinding().getText());
        assertEquals("@module", a.getName());
    }

    @Test
    public void test_inside_module() {
        PsiModule e = firstOfType(parseCode("module M = { let _ = (x) => { ...x, } }"), PsiModule.class);

        PsiFunction ef = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEquals("{ ...x, }", ef.getBody().getText());
        assertEquals("{ let _ = (x) => { ...x, } }", e.getBody().getText());
    }
}
