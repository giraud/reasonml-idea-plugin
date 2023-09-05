package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends ResParsingTestCase {
    @Test
    public void test_empty() {
        RPsiInnerModule e = firstOfType(parseCode("module M = {}"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals(ResTypes.INSTANCE.A_MODULE_NAME, e.getNameIdentifier().getNode().getElementType());
        assertEquals("{}", e.getBody().getText());
    }

    @Test
    public void test_alias() {
        RPsiInnerModule e = firstOfType(parseCode("module M = Y"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_path() {
        RPsiInnerModule e = firstOfType(parseCode("module M = Y.Z"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_inner() {
        RPsiInnerModule e = firstOfType(parseCode("module A = { module B = C.D }"), RPsiInnerModule.class);

        RPsiInnerModule ee = PsiTreeUtil.findChildOfType(e.getBody(), RPsiInnerModule.class);
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
        assertTrue(module.isModuleType());
    }

    @Test
    public void test_module() {
        PsiFile file = parseCode("module Styles = { open Css\n let y = 1 }");
        RPsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Styles", module.getName());
        assertEquals("{ open Css\n let y = 1 }", module.getBody().getText());
    }

    @Test
    public void test_inline_interface() {
        PsiFile file = parseCode("module Router: { let watchUrl: (url => unit) => watcherID }");
        RPsiInnerModule module = first(moduleExpressions(file));

        assertEquals(1, expressions(file).size());
        assertEquals("Router", module.getName());
        assertEquals("{ let watchUrl: (url => unit) => watcherID }", module.getModuleSignature().getText());
        assertNull(module.getBody());
        assertNull(PsiTreeUtil.findChildOfType(file, RPsiScopedExpr.class));
        RPsiLet let = PsiTreeUtil.findChildOfType(file, RPsiLet.class);
        assertEquals("(url => unit) => watcherID", let.getSignature().getText());
    }

    @Test
    public void test_interface_sig_body() {
        RPsiInnerModule e = firstOfType(parseCode("module M: MType = { type t = int }"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("MType", e.getModuleSignature().getText());
        assertEquals("{ type t = int }", e.getBody().getText());
    }

    @Test
    public void test_interface_with_constraints() {
        RPsiInnerModule e = firstOfType(parseCode("module M: I with type t = X.t = {}"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("I", e.getModuleSignature().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getModuleSignature().getFirstChild().getNode().getElementType());
        assertSize(1, e.getConstraints());
        assertEquals("type t = X.t", e.getConstraints().get(0).getText());
        assertEquals("{}", e.getBody().getText());
    }

    @Test
    public void test_inline_interface_body() {
        RPsiInnerModule e = firstOfType(parseCode("module M: { type t } = { type t = int }"), RPsiInnerModule.class);

        assertNoParserError(e);
        assertEquals("M", e.getName());
        assertEquals("{ type t }", e.getModuleSignature().getText());
        assertEquals("{ type t = int }", e.getBody().getText());
    }

    @Test
    public void test_annotation_after() {
        FileBase e = parseCode("module M = {}\n@module(\"x\")");

        RPsiInnerModule m = ORUtil.findImmediateFirstChildOfClass(e, RPsiInnerModule.class);
        RPsiAnnotation a = ORUtil.findImmediateFirstChildOfClass(e, RPsiAnnotation.class);

        assertEquals("module M = {}", m.getText());
        assertEquals("@module", a.getName());
    }

    @Test
    public void test_decode_first_class_module() {
        RPsiInnerModule e = firstOfType(parseCode("module M = (unpack selectors)"), RPsiInnerModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(unpack selectors)", e.getBody().getText());
    }
}
