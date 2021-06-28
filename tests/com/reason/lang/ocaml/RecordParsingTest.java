package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class RecordParsingTest extends OclParsingTestCase {
    public void test_declaration() {
        PsiType e = first(typeExpressions(parseCode("type r = { a: int; b: string list }")));
        PsiRecord record = (PsiRecord) e.getBinding().getFirstChild();

        List<PsiRecordField> fields = record.getFields();
        assertEquals("a", fields.get(0).getName());
        assertEquals("int", fields.get(0).getSignature().asText(myLanguage));
        assertEquals("b", fields.get(1).getName());
        assertEquals("string list", fields.get(1).getSignature().asText(myLanguage));
    }
}
