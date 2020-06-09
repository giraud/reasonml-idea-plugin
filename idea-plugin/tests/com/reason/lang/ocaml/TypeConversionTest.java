package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.reason.RmlLanguage;

public class TypeConversionTest extends BaseParsingTestCase {
    public TypeConversionTest() {
        super("", "ml", new OclParserDefinition());
    }


    public void testParameterizedType() {
        PsiType e = first(typeExpressions(parseCode("type 'a t")));

        PsiTypeConstrName cname = e.getConstrName();
        assertEquals("'a t", cname.asText(OclLanguage.INSTANCE));
        assertEquals("t('a)", cname.asText(RmlLanguage.INSTANCE));
    }

    public void testParameterizedType2() {
        PsiType e = first(typeExpressions(parseCode("type ('a, 'b) t")));

        PsiTypeConstrName cname = e.getConstrName();
        assertEquals("('a, 'b) t", cname.asText(OclLanguage.INSTANCE));
        assertEquals("t('a, 'b)", cname.asText(RmlLanguage.INSTANCE));
    }
}
