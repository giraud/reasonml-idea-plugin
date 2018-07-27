package com.reason.lang.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AndParsingTest extends BaseParsingTestCase {
    public AndParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testLetChaining() {
        List<PsiLet> lets = new ArrayList(letExpressions(parseCode("let rec lx x = x + 1 and ly y = 3 + (lx y)")));

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    public void testModuleChaining() {
        List<PsiModule> mods = new ArrayList(moduleExpressions(parseCode("module rec X : sig end = struct end and  Y : sig end = struct end")));

        assertEquals(2, mods.size());
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

}
