package com.reason.ocaml;

import java.io.*;
import java.util.*;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.ocaml.OclParserDefinition;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "ml", new OclParserDefinition());
    }

    public void testModuleDef() {
        PsiModule psiModule = doMlTest();
        Collection<PsiModule> modules = psiModule.getModules();

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testEmpty() throws IOException {
        Collection<PsiModule> modules = parseCode("module M = struct end").getModules();

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testAlias() throws IOException {
        PsiModule module = first(parseCode("module M = Y").getModules());

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }
}
