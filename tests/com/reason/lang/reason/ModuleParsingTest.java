package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInnerModule;

import java.util.Collection;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "re", new RmlParserDefinition());
    }

    public void testEmpty() {
        Collection<PsiInnerModule> modules = moduleExpressions(parseCode("module M = {};"));

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testAlias() {
        PsiInnerModule module = first(moduleExpressions(parseCode("module M = Y;")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

    public void testModuleType() {
        PsiInnerModule module = first(moduleExpressions(parseCode("module type RedFlagsSig = {};")));

        assertEquals("RedFlagsSig", module.getName());
    }

    public void testModule() {
        PsiFile file = parseCode(" module Styles = { open Css; let y = 1 }");
        PsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css; let y = 1 }", module.getBody().getText());
    }

    public void testInlineInterface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID; }");
        PsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID; }", module.getBody().getText());
    }

}
