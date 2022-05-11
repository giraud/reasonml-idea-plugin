package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordParsingTest extends RmlParsingTestCase {
    public void test_declaration() {
        PsiType e = first(typeExpressions(parseCode("type r = { a: int, b: option(string) };")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = record.getFields();
        assertEquals("a", fields.get(0).getName());
        assertEquals("int", fields.get(0).getSignature().asText(getLangProps()));
        assertEquals("b", fields.get(1).getName());
        assertEquals("option(string)", fields.get(1).getSignature().asText(getLangProps()));
    }

    public void test_usage() {
        PsiLet e = first(letExpressions(parseCode("let r = { a: 1, b: 2, c: 3, };")));
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

    public void test_usage_with_sig() {
        PsiLet e = first(letExpressions(parseCode("let r: M.t = { a: 1, b: 2, c: 3, };")));
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

    public void test_usage_deep() {
        PsiLet e = first(letExpressions(parseCode("let r = { a: { b: { c: 3 } } };")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(1, fields);
        assertEquals("a", fields.get(0).getName());

        List<PsiRecordField> allFields = new ArrayList<>(PsiTreeUtil.findChildrenOfType(record, PsiRecordField.class));
        assertEquals("a", allFields.get(0).getName());
        assertEquals("b", allFields.get(1).getName());
        assertEquals("c", allFields.get(2).getName());
    }

    public void test_mixin() {
        PsiLet let = first(letExpressions(parseCode("let x = {...component, otherField: 1};")));

        PsiRecord record = (PsiRecord) let.getBinding().getFirstChild();
        PsiRecordField field = record.getFields().iterator().next();
        assertEquals(field.getName(), "otherField");
    }

    public void test_annotations() {
        PsiType e = first(typeExpressions(parseCode("type props = { [@bs.optional] key: string, [@bs.optional] [@bs.as \"aria-label\"] ariaLabel: string, };")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(2, fields);
        assertEquals("key", fields.get(0).getName());
        assertEquals("ariaLabel", fields.get(1).getName());
    }
}
