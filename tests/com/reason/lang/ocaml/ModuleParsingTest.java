package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiStruct;

import java.util.Collection;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "ml", new OclParserDefinition());
    }

    public void testEmpty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testAlias() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

    public void testModuleFunctor() {
        PsiModule module = first(moduleExpressions(parseCode("module Printing = Make (struct let encode = encode_record end)")));
        PsiStruct struct = PsiTreeUtil.findChildOfType(module.getBody(), PsiStruct.class);
        assertNotNull(struct);
    }
}
