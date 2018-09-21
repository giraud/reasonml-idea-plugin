package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiRecord;
import com.reason.lang.core.psi.PsiRecordField;

public class RecordParsingTest extends BaseParsingTestCase {
    public RecordParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testMixin() {
        PsiLet let = first(letExpressions(parseCode("let x = {...component, otherField: 1};", true)));

        PsiRecord record = (PsiRecord) let.getBinding().getFirstChild();
        PsiRecordField field = record.getFields().iterator().next();
        assertEquals(field.getName(), "otherField");
    }

}
