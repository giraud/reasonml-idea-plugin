package com.reason.lang.napkin;

import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInnerModule;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends NsParsingTestCase {
    public void test_empty() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module M = {}")));

        assertEquals("M", module.getName());
        assertEquals("{}", module.getBody().getText());
    }

    public void test_alias() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module M = Y")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

    public void test_moduleType() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module type RedFlagsSig = {}")));

        assertEquals("RedFlagsSig", module.getName());
        assertTrue(module.isModuleType());
    }

    public void test_moduleOpen() {
        PsiFile file = parseCode("module Styles = { open Css let y = 1 }");
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css let y = 1 }", module.getBody().getText());
    }

    public void test_inlineInterface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID }");
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID }", module.getBody().getText());
    }

    public void test_moduleOpenVariant() {
        FileBase file = parseCode("ModelActions.UserCapabilitiesLoaded.( UserCapabilitiesBuilder.( ) ),");
        assertEquals(6, childrenCount(file));
    }
}
