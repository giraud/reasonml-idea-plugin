package com.reason.lang.dune;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;

public class DuneVarParsingTest extends BaseParsingTestCase {
    public DuneVarParsingTest() {
        super("", "", new DuneParserDefinition());
    }

    public void testBasic() {
        PsiElement e = firstElement(parseRawCode("%{x}", true));

        assertEquals(DuneTypes.INSTANCE.C_VAR, e.getNode().getElementType());
        assertEquals("%{x}", e.getText());
    }
}