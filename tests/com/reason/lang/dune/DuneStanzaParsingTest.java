package com.reason.lang.dune;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DuneStanzaParsingTest extends BaseParsingTestCase {
    public DuneStanzaParsingTest() {
        super("", "", new DuneParserDefinition());
    }

    public void test_stanza() {
        DuneFile e = parseDuneCode("(library (name x)) (version 1)");

        Collection<PsiStanza> stanzas = ORUtil.findImmediateChildrenOfClass(e, PsiStanza.class);
        assertSize(2, stanzas);
        assertEquals("library", e.getStanza("library").getName());
        assertEquals("version", e.getStanza("version").getName());
    }

    public void test_stanza_fields() {
        PsiStanza e = parseDuneCode("(library (name x) (wrapped true))").getStanza("library");

        assertEquals("library", e.getName());
        assertSize(2, e.getFields());
        assertNotNull(e.getField("name"));
        assertEquals("(name x)", first(e.getFields()).getText());
        assertEquals("x", e.getField("name").getValue());
        assertNotNull(e.getField("wrapped"));
        assertEquals("(wrapped true)", second(e.getFields()).getText());
        assertEquals("true", e.getField("wrapped").getValue());
    }

    public void test_chain() {
        PsiFile e = parseRawCode("(library (name x)) (version 1)");

        Collection<PsiStanza> stanzas = ORUtil.findImmediateChildrenOfClass(e, PsiStanza.class);
        assertSize(2, stanzas);
    }
}
