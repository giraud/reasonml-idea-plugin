package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVariantConstructor;

import java.util.ArrayList;
import java.util.List;

public class VariantDeclarationTest extends BaseParsingTestCase {
    public VariantDeclarationTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(2, variants.size());
        assertEquals("Black", variants.get(0).getName());
        assertEquals("White", variants.get(1).getName());
    }

    public void testConstructor() {
        PsiType e = first(typeExpressions(parseCode("type color = | Hex(string) | Rgb(int, int, int)")));

        List<PsiVariantConstructor> variants = new ArrayList<>(e.getVariants());
        assertEquals(2, variants.size());
        assertEquals("Hex", variants.get(0).getName());
        assertEquals(1, variants.get(0).getParameterList().size());
        assertEquals("Rgb", variants.get(1).getName());
        assertEquals(3, variants.get(1).getParameterList().size());
    }

}
