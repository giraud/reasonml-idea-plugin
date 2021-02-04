package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiModule;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends RmlParsingTestCase {
  public void test_empty() {
    Collection<PsiModule> modules = moduleExpressions(parseCode("module M = {};"));

    assertEquals(1, modules.size());
    PsiInnerModule e = (PsiInnerModule) first(modules);
    assertEquals("M", e.getName());
    assertEquals("{}", e.getBody().getText());
  }

  public void test_alias() {
    PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module M = Y;")));

    assertEquals("M", module.getName());
    assertEquals("Y", module.getAlias());
  }

  public void test_module_type() {
    PsiInnerModule module =
        (PsiInnerModule) first(moduleExpressions(parseCode("module type RedFlagsSig = {};")));

    assertEquals("RedFlagsSig", module.getName());
    assertTrue(module.isModuleType());
  }

  public void test_module() {
    PsiFile file = parseCode(" module Styles = { open Css; let y = 1 }");
    PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

    assertEquals(1, expressions(file).size());
    assertEquals("Styles", module.getName());
    assertEquals("{ open Css; let y = 1 }", module.getBody().getText());
  }

  public void test_inline_interface() {
    PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID; }");
    PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

    assertEquals(1, expressions(file).size());
    assertEquals("Router", module.getName());
    assertEquals("{ let watchUrl: (url => unit) => watcherID; }", module.getModuleType().getText());
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
