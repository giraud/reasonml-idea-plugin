package com.reason.reason;

import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.reason.RmlParserDefinition;

import java.util.Collection;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "re", new RmlParserDefinition());
    }

    public void testEmpty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = {};"));

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testAlias() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y;")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

}
