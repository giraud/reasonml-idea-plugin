package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class RecordParsingTest extends BaseParsingTestCase {
    public RecordParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testDeclaration() {
        PsiType e = first(typeExpressions(parseCode("type r = { a: int };")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertEquals("a", fields.get(0).getName());
        assertEquals("int", fields.get(0).getPsiSignature().asString(RmlLanguage.INSTANCE));
    }

    public void testUsage() {
        PsiLet e = first(letExpressions(parseCode("let r = { a: 1, b: 2, c: 3, };")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(3, fields);
        assertEquals("a", fields.get(0).getName());
        assertNull(fields.get(0).getPsiSignature());
        assertEquals("b", fields.get(1).getName());
        assertNull(fields.get(1).getPsiSignature());
        assertEquals("c", fields.get(2).getName());
        assertNull(fields.get(2).getPsiSignature());
    }

    public void testMixin() {
        PsiLet let = first(letExpressions(parseCode("let x = {...component, otherField: 1};")));

        PsiRecord record = (PsiRecord) let.getBinding().getFirstChild();
        PsiRecordField field = record.getFields().iterator().next();
        assertEquals(field.getName(), "otherField");
    }

    public void testAnnotations() {
        PsiType e = first(typeExpressions(parseCode("type props = { [@bs.optional] key: string, [@bs.optional] [@bs.as \"aria-label\"] ariaLabel: string, };")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = new ArrayList<>(record.getFields());
        assertSize(2, fields);
        assertEquals("key", fields.get(0).getName());
        assertEquals("ariaLabel", fields.get(1).getName());
    }

}
