package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.testFramework.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

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
        PsiModule module = first(moduleExpressions(parseCode("module M = Y;")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
        assertEquals("Y", module.getAliasSymbol().getText());
    }

    public void test_alias_path() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y.Z;")));

        assertEquals("M", module.getName());
        assertEquals("Y.Z", module.getAlias());
        assertEquals("Z", module.getAliasSymbol().getText());
    }

    public void test_module_type() {
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(parseCode("module type Intf = { let x: bool; };")));

        assertEquals("Intf", module.getName());
        assertTrue(module.isInterface());
        assertInstanceOf(module.getBody(), PsiModuleBinding.class);

    }

    public void test_module() {
        PsiFile file = parseCode(" module Styles = { open Css; let y = 1 }");
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css; let y = 1 }", module.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(file, PsiScopedExpr.class));
    }

    public void test_inline_interface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID; }");
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID; }", module.getModuleType().getText());
        assertNull(PsiTreeUtil.findChildOfType(file, PsiScopedExpr.class));
        assertNull(module.getBody());
        PsiLet let = PsiTreeUtil.findChildOfType(file, PsiLet.class);
        assertEquals("(url => unit) => watcherID", let.getSignature().getText());
    }

    public void test_interface_sig_body() {
        PsiInnerModule e = firstOfType(parseCode("module M: MType = { type t = int; };"), PsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("MType", e.getModuleType().getText());
        assertEquals("{ type t = int; }", e.getBody().getText());
    }

    public void test_inline_interface_body() {
        PsiInnerModule e = firstOfType(parseCode("module M: { type t; } = { type t = int; };"), PsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("{ type t; }", e.getModuleType().getText());
        assertEquals("{ type t = int; }", e.getBody().getText());
    }
}
