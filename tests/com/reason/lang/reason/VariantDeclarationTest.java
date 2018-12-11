package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiType;

public class VariantDeclarationTest extends BaseParsingTestCase {
    public VariantDeclarationTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiType e = first(typeExpressions(parseCode("type color = | Black | White")));

        assertEquals(2, e.getVariants().size());
    }
}
