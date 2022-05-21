package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.PsiAnnotation;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends ResParsingTestCase {
    public void test_empty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = {}"));

        assertEquals(1, modules.size());
        PsiInnerModule e = (PsiInnerModule) first(modules);
        assertEquals("M", e.getName());
        assertEquals("{}", e.getBody().getText());
    }

    public void test_alias() {
        PsiModule module = firstOfType(parseCode("module M = Y"), PsiInnerModule.class);

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
        assertEquals("Y", module.getAliasSymbol().getText());
    }

    public void test_alias_path() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y.Z")));

        assertEquals("M", module.getName());
        assertEquals("Y.Z", module.getAlias());
        assertEquals("Z", module.getAliasSymbol().getText());
    }

    public void test_alias_chaining_include() {
        PsiModule module = first(moduleExpressions(parseCode("module D = B include D.C")));

        assertEquals("D", module.getName());
        assertEquals("B", module.getAlias());
    }

    public void test_alias_chaining_call() {
        PsiModule module = first(moduleExpressions(parseCode("module D = B\n D.C")));

        assertEquals("D", module.getName());
        assertEquals("B", module.getAlias());
    }

    public void test_module_type() {
        PsiInnerModule module = firstOfType(parseCode("module type RedFlagsSig = {}"), PsiInnerModule.class);

        assertEquals("RedFlagsSig", module.getName());
        assertTrue(module.isInterface());
    }

    public void test_module() {
        PsiFile file = parseCode("module Styles = { open Css\n let y = 1 }");
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css\n let y = 1 }", module.getBody().getText());
    }

    public void test_inline_interface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID }");
        PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID }", module.getModuleType().getText());
        assertNull(module.getBody());
        assertNull(PsiTreeUtil.findChildOfType(file, PsiScopedExpr.class));
        PsiLet let = PsiTreeUtil.findChildOfType(file, PsiLet.class);
        assertEquals("(url => unit) => watcherID", let.getSignature().getText());
    }

    public void test_interface_sig_body() {
        PsiInnerModule e = firstOfType(parseCode("module M: MType = { type t = int }"), PsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("MType", e.getModuleType().getText());
        assertEquals("{ type t = int }", e.getBody().getText());
    }

    public void test_inline_interface_body() {
        PsiInnerModule e = firstOfType(parseCode("module M: { type t } = { type t = int }"), PsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("{ type t }", e.getModuleType().getText());
        assertEquals("{ type t = int }", e.getBody().getText());
    }

    public void test_annotation_after() {
        FileBase e = parseCode("module M = {}\n@module(\"x\")");

        PsiModule m = ORUtil.findImmediateFirstChildOfClass(e, PsiModule.class);
        PsiAnnotation a = ORUtil.findImmediateFirstChildOfClass(e, PsiAnnotation.class);

        assertEquals("module M = {}", m.getText());
        assertEquals("@module", a.getName());
    }
}
