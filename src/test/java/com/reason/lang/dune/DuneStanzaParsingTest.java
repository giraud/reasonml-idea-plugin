package com.reason.lang.dune;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DuneStanzaParsingTest extends DuneParsingTestCase {
    @Test
    public void test_stanza() {
        DuneFile e = parseDuneCode("(library (name x)) (version 1)");

        Collection<RPsiDuneStanza> stanzas = ORUtil.findImmediateChildrenOfClass(e, RPsiDuneStanza.class);
        assertSize(2, stanzas);
        assertEquals("library", e.getStanza("library").getName());
        assertEquals("version", e.getStanza("version").getName());
    }

    @Test
    public void test_stanza_fields() {
        RPsiDuneStanza e = parseDuneCode("(library (name x) (wrapped true))").getStanza("library");

        assertEquals("library", e.getName());
        assertSize(2, e.getFields());
        assertNotNull(e.getField("name"));
        assertEquals("(name x)", first(e.getFields()).getText());
        assertEquals("x", e.getField("name").getValue());
        assertNotNull(e.getField("wrapped"));
        assertEquals("(wrapped true)", second(e.getFields()).getText());
        assertEquals("true", e.getField("wrapped").getValue());
    }

    @Test
    public void test_chain() {
        PsiFile e = parseRawCode("(library (name x)) (version 1)");

        Collection<RPsiDuneStanza> stanzas = ORUtil.findImmediateChildrenOfClass(e, RPsiDuneStanza.class);
        assertSize(2, stanzas);
    }
}
