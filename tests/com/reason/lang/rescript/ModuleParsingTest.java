package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.PsiAnnotation;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends ResParsingTestCase {
    public void test_empty() {
        PsiModule e = firstOfType(parseCode("module M = {}"), PsiModule.class);

        assertEquals("M", e.getName());
        assertEquals(ResTypes.INSTANCE.A_MODULE_NAME, e.getNavigationElement().getNode().getElementType());
        assertEquals("{}", e.getBody().getText());
    }

    public void test_alias() {
        PsiModule e = firstOfType(parseCode("module M = Y"), PsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    public void test_alias_path() {
        PsiModule e = firstOfType(parseCode("module M = Y.Z"), PsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    public void test_alias_inner() {
        PsiModule e = firstOfType(parseCode("module A = { module B = C.D }"), PsiModule.class);

        PsiModule ee = PsiTreeUtil.findChildOfType(e.getBody(), PsiModule.class);
        assertEquals("B", ee.getName());
        assertEquals("C.D", ee.getBody().getText());
        assertEquals("C.D", ee.getAlias());
        assertEquals("D", ee.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, ee.getAliasSymbol().getNode().getElementType());
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
