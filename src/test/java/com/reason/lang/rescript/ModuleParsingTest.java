package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.RPsiAnnotation;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends ResParsingTestCase {
    @Test
    public void test_empty() {
        RPsiModule e = firstOfType(parseCode("module M = {}"), RPsiModule.class);

        assertEquals("M", e.getName());
        assertEquals(ResTypes.INSTANCE.A_MODULE_NAME, e.getNavigationElement().getNode().getElementType());
        assertEquals("{}", e.getBody().getText());
    }

    @Test
    public void test_alias() {
        RPsiModule e = firstOfType(parseCode("module M = Y"), RPsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_path() {
        RPsiModule e = firstOfType(parseCode("module M = Y.Z"), RPsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_inner() {
        RPsiModule e = firstOfType(parseCode("module A = { module B = C.D }"), RPsiModule.class);

        RPsiModule ee = PsiTreeUtil.findChildOfType(e.getBody(), RPsiModule.class);
        assertEquals("B", ee.getName());
        assertEquals("C.D", ee.getBody().getText());
        assertEquals("C.D", ee.getAlias());
        assertEquals("D", ee.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, ee.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_module_type() {
        RPsiInnerModule module = firstOfType(parseCode("module type RedFlagsSig = {}"), RPsiInnerModule.class);

        assertEquals("RedFlagsSig", module.getName());
        assertTrue(module.isInterface());
    }

    @Test
    public void test_module() {
        PsiFile file = parseCode("module Styles = { open Css\n let y = 1 }");
        RPsiInnerModule module = (RPsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css\n let y = 1 }", module.getBody().getText());
    }

    @Test
    public void test_inline_interface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID }");
        RPsiInnerModule module = (RPsiInnerModule) first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID }", module.getModuleType().getText());
        assertNull(module.getBody());
        assertNull(PsiTreeUtil.findChildOfType(file, RPsiScopedExpr.class));
        RPsiLet let = PsiTreeUtil.findChildOfType(file, RPsiLet.class);
        assertEquals("(url => unit) => watcherID", let.getSignature().getText());
    }

    @Test
    public void test_interface_sig_body() {
        RPsiInnerModule e = firstOfType(parseCode("module M: MType = { type t = int }"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("MType", e.getModuleType().getText());
        assertEquals("{ type t = int }", e.getBody().getText());
    }

    @Test
    public void test_inline_interface_body() {
        RPsiInnerModule e = firstOfType(parseCode("module M: { type t } = { type t = int }"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("{ type t }", e.getModuleType().getText());
        assertEquals("{ type t = int }", e.getBody().getText());
    }

    @Test
    public void test_annotation_after() {
        FileBase e = parseCode("module M = {}\n@module(\"x\")");

        RPsiModule m = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        RPsiAnnotation a = ORUtil.findImmediateFirstChildOfClass(e, RPsiAnnotation.class);

        assertEquals("module M = {}", m.getText());
        assertEquals("@module", a.getName());
    }

    @Test
    public void test_decode_first_class_module() {
        RPsiModule e = firstOfType(parseCode("module M = (unpack selectors)"), RPsiModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(unpack selectors)", e.getBody().getText());
    }
}