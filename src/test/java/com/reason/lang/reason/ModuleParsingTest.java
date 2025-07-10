package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends RmlParsingTestCase {
    @Test
    public void test_empty() {
        Collection<RPsiInnerModule> modules = moduleExpressions(parseCode("module M = {};"));

        assertEquals(1, modules.size());
        RPsiInnerModule e = first(modules);
        assertEquals("M", e.getName());
        assertEquals(RmlTypes.INSTANCE.A_MODULE_NAME, e.getNameIdentifier().getNode().getElementType());
        assertEquals("{}", e.getBody().getText());
    }

    @Test
    public void test_alias() {
        RPsiInnerModule e = firstOfType(parseCode("module M = Y;"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(RmlTypes.INSTANCE.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_path() {
        RPsiInnerModule e = firstOfType(parseCode("module M = Y.Z;"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(RmlTypes.INSTANCE.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_module_type() {
        RPsiInnerModule module = first(moduleExpressions(parseCode("module type Intf = { let x: bool; };")));

        assertEquals("Intf", module.getName());
        assertTrue(module.isModuleType());
        assertInstanceOf(module.getBody(), RPsiModuleBinding.class);

    }

    @Test
    public void test_module() {
        PsiFile file = parseCode(" module Styles = { open Css; let y = 1 }");
        RPsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css; let y = 1 }", module.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(file, RPsiScopedExpr.class));
    }

    @Test
    public void test_inline_interface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID; }");
        RPsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID; }", module.getModuleSignature().getText());
        assertNull(PsiTreeUtil.findChildOfType(file, RPsiScopedExpr.class));
        assertNull(module.getBody());
        RPsiLet let = PsiTreeUtil.findChildOfType(file, RPsiLet.class);
        assertEquals("(url => unit) => watcherID", let.getSignature().getText());
    }

    @Test
    public void test_interface_sig_body() {
        RPsiInnerModule e = firstOfType(parseCode("module M: MType = { type t = int; };"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("MType", e.getModuleSignature().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getModuleSignature().getFirstChild().getNode().getElementType());
        assertEquals("{ type t = int; }", e.getBody().getText());
    }

    @Test
    public void test_interface_with_constraints() {
        RPsiInnerModule e = firstOfType(parseCode("module M: I\n with type t = X.t = {};"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("I", e.getModuleSignature().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getModuleSignature().getFirstChild().getNode().getElementType());
        assertSize(1, e.getConstraints());
        assertEquals("type t = X.t", e.getConstraints().get(0).getText());
        assertEquals("{}", e.getBody().getText());
    }

    @Test
    public void test_interface_with_many_constraints() {
        RPsiInnerModule e = firstOfType(parseCode("""
                module M: I with type s := t and type u := v = {
                  type t;
                };"""), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("I", e.getModuleSignature().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getModuleSignature().getFirstChild().getNode().getElementType());
        assertSize(2, e.getConstraints());
        assertEquals("type s := t", e.getConstraints().get(0).getText());
        assertEquals("type u := v", e.getConstraints().get(1).getText());
        assertEquals("{\n  type t;\n}", e.getBody().getText());
    }

    @Test
    public void test_inline_interface_body() {
        RPsiInnerModule e = firstOfType(parseCode("module M: { type t; } = { type t = int; };"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("{ type t; }", e.getModuleSignature().getText());
        assertEquals("{ type t = int; }", e.getBody().getText());
    }

}
