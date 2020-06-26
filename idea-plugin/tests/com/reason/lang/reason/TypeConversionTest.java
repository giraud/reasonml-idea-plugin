package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.ocaml.OclLanguage;

public class TypeConversionTest extends BaseParsingTestCase {
    public TypeConversionTest() {
        super("", "re", new RmlParserDefinition());
    }


    public void testParameterizedType() {
        PsiType e = first(typeExpressions(parseCode("type t('a)")));

        PsiTypeConstrName cname = e.getConstrName();
        assertEquals("t('a)", cname.asText(RmlLanguage.INSTANCE));
        assertEquals("'a t", cname.asText(OclLanguage.INSTANCE));
    }

    public void testParameterizedType2() {
        PsiType e = first(typeExpressions(parseCode("type t('a, 'b)")));

        PsiTypeConstrName cname = e.getConstrName();
        assertEquals("t('a, 'b)", cname.asText(RmlLanguage.INSTANCE));
        assertEquals("('a, 'b) t", cname.asText(OclLanguage.INSTANCE));
    }
}
