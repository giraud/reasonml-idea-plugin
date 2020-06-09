package com.reason.lang.dune;

import com.intellij.psi.PsiElement;
import com.reason.lang.BaseParsingTestCase;

public class DuneVarParsingTest extends BaseParsingTestCase {
    public DuneVarParsingTest() {
        super("", "", new DuneParserDefinition());
    }

    public void testBasic() {
        PsiElement e = firstElement(parseRawCode("%{x}", false));

        assertEquals(DuneTypes.INSTANCE.C_VAR, e.getNode().getElementType());
        assertEquals("%{x}", e.getText());
    }
}