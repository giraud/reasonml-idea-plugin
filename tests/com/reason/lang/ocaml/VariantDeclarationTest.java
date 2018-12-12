package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVariantConstructor;

import java.util.ArrayList;
import java.util.List;

public class VariantDeclarationTest extends BaseParsingTestCase {
    public VariantDeclarationTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testBasic() {
        PsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(2, variants.size());
        assertEquals("Black", variants.get(0).getName());
        assertEquals("White", variants.get(1).getName());
    }

}
