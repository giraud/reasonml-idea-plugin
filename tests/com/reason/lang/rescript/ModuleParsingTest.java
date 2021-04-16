package com.reason.lang.rescript;

import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInnerModule;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends ResParsingTestCase {
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

  public void test_module_type() {
    PsiInnerModule module =
        (PsiInnerModule) first(moduleExpressions(parseCode("module type RedFlagsSig = {}")));

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
    assertEquals("{ let watchUrl: (url => unit) => watcherID }", module.getModuleType().getText());
    assertNull(module.getBody());
  }

  public void test_inline_interface_body() {
    PsiInnerModule e =
        firstOfType(parseCode("module M: { type t; } = { type t = int; };"), PsiInnerModule.class);

    assertEquals("M", e.getName());
    assertEquals("{ type t; }", e.getModuleType().getText());
    assertEquals("{ type t = int; }", e.getBody().getText());
  }

  public void test_moduleOpenVariant() {
    FileBase file =
        parseCode("ModelActions.UserCapabilitiesLoaded.( UserCapabilitiesBuilder.( ) ),");
    assertEquals(6, childrenCount(file));
  }
}
