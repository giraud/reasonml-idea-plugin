package com.reason.lang.dune;

import com.intellij.psi.PsiFile;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiStanza;

import java.util.Collection;

public class DuneStanzaParsingTest extends BaseParsingTestCase {
    public DuneStanzaParsingTest() {
        super("", "", new DuneParserDefinition());
    }

    public void testBasic() {
        PsiStanza e = (PsiStanza) firstElement(parseRawCode("(library (name x) (wrapped true))"));

        assertEquals("library", e.getName());
        assertSize(2, e.getFields());
        assertEquals("(name x)", first(e.getFields()).getText());
        assertEquals("(wrapped true)", second(e.getFields()).getText());
    }

    public void testChain() {
        PsiFile e = parseRawCode("(library (name x)) (version 1)");

        Collection<PsiStanza> stanzas = ORUtil.findImmediateChildrenOfClass(e, PsiStanza.class);
        assertSize(2, stanzas);
    }
}