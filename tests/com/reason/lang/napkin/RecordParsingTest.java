package com.reason.lang.napkin;

import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.PsiRecord;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordParsingTest extends NsParsingTestCase {
  public void test_declaration() {
    PsiType e = first(typeExpressions(parseCode("type r = { a: int }")));
    PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

    List<PsiRecordField> fields = new ArrayList<>(record.getFields());
    assertEquals("a", fields.get(0).getName());
    assertEquals("int", fields.get(0).getSignature().asText(myLanguage));
  }

  public void test_usage() {
    PsiLet e = first(letExpressions(parseCode("let r = { a: 1, b: 2, c: 3, }")));
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

  public void test_mixin() {
    PsiLet let = first(letExpressions(parseCode("let x = {...component, otherField: 1}")));

    PsiRecord record = (PsiRecord) let.getBinding().getFirstChild();
    PsiRecordField field = record.getFields().iterator().next();
    assertEquals(field.getName(), "otherField");
  }

  public void test_annotations() {
    PsiType e =
        first(
            typeExpressions(
                parseCode(
                    "type props = { @bs.optional key: string, @bs.optional @bs.as(\"aria-label\") ariaLabel: string, }")));
    PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

    List<PsiRecordField> fields = new ArrayList<>(record.getFields());
    assertSize(2, fields);
    assertEquals("key", fields.get(0).getName());
    assertEquals("ariaLabel", fields.get(1).getName());
  }
}
