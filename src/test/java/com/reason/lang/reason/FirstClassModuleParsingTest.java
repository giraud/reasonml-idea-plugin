package com.reason.lang.reason;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("DataFlowIssue")
public class FirstClassModuleParsingTest extends RmlParsingTestCase {
    @Test
    public void test_first_class_let() {
        RPsiLet e = firstOfType(parseCode("let three: module A.I = (module Three);"), RPsiLet.class);

        assertEquals("Three", e.getFirstClassModule().getFirstClassModuleSymbol().getText());
        RPsiModuleSignature es = ((RPsiModuleSignature) e.getSignature());
        assertEquals("A.I", es.getQName());
        assertEquals("I", es.getNameIdentifier().getText());
    }

    @Test
    public void test_first_class_parameter_no_default() {
        RPsiLet e = firstOfType(parseCode("let fn = (~p: (module A.I)) => p;"), RPsiLet.class);
        assertTrue(e.isFunction());
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);

        RPsiFunction ef = e.getFunction();
        assertEquals("p", ef.getBody().getText());

        RPsiSignature eps = ef.getParameters().get(0).getSignature();
        assertInstanceOf(eps, RPsiModuleSignature.class);
        assertEquals("A.I", ((RPsiModuleSignature) eps).getQName());
    }

    @Test
    public void test_first_class_parameter_with_default() {
        RPsiFunction e = firstOfType(parseCode("let make = (~selectors: (module SelectorsIntf)=(module Selectors)) => {};"), RPsiFunction.class);

        RPsiParameterDeclaration p0 = e.getParameters().get(0);
        assertTrue(p0.isNamed());
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);

        RPsiSignature eps = p0.getSignature();
        assertInstanceOf(eps, RPsiModuleSignature.class);
        assertEquals("(module SelectorsIntf)", eps.getText());

        RPsiDefaultValue epv = p0.getDefaultValue();
        assertEquals("(module Selectors)", epv.getText());
        PsiElement epvc = epv.getFirstChild();
        assertInstanceOf(epvc, RPsiFirstClass.class);
    }

    @Test
    public void test_unpack() {
        RPsiInnerModule e = firstOfType(parseCode("module New_three = (val three : I);"), RPsiInnerModule.class);

        assertNull(e.getBody());
        assertEquals("(val three : I)", e.getUnpack().getText());
        assertEquals("I", e.getUnpack().getModuleReference().getText());
    }

    @Test
    public void test_unpack_no_signature() {
        RPsiInnerModule e = firstOfType(parseCode("module M = (val selectors);"), RPsiInnerModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val selectors)", e.getUnpack().getText());
    }
}
