package com.reason.ocaml;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.ocaml.OclParserDefinition;
import junit.framework.Assert;

import java.io.IOException;
import java.util.Collection;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "ml", new OclParserDefinition());
    }

    public void testModuleDef() {
        PsiModule psiModule = doMlTest();
        Collection<PsiModule> modules = psiModule.getModules();

        Assert.assertEquals(1, modules.size());
        Assert.assertEquals("M", first(modules).getName());
    }

    public void testEmpty() throws IOException {
        Collection<PsiModule> modules = parseCode("module M = struct end").getModules();

        Assert.assertEquals(1, modules.size());
        Assert.assertEquals("M", first(modules).getName());
    }

    public void testAlias() throws IOException {
        PsiModule module = first(parseCode("module M = Y").getModules());

        Assert.assertEquals("M", module.getName());
        Assert.assertEquals("Y", module.getAlias());
    }

}
