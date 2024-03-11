package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("DataFlowIssue")
public class FirstClassModuleParsingTest extends OclParsingTestCase {
    @Test
    public void test_first_class_let() {
        RPsiLet e = firstOfType(parseCode("let three = (module Three : A.I)"), RPsiLet.class);

        assertEquals("Three", e.getFirstClassModule().getFirstClassModuleSymbol().getText());
        RPsiModuleSignature rPsiModuleSignature = ((RPsiModuleSignature) e.getSignature());
        assertEquals("A.I", rPsiModuleSignature.getQName());
    }

    @Test
    public void test_first_class_parameter_no_default() {
        RPsiLet e = firstOfType(parseCode("let fn ~p:(p : (module I))  = p"), RPsiLet.class);
        assertTrue(e.isFunction());
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);

        RPsiFunction ef = e.getFunction();
        assertEquals("p", ef.getBody().getText());

        RPsiSignature eps = ef.getParameters().get(0).getSignature();
        assertInstanceOf(eps, RPsiModuleSignature.class);
    }

    @Test
    public void test_first_class_parameter_with_default() {
        RPsiFunction e = firstOfType(parseCode("let make ?p:((p : (module Intf))= (module Impl)) = p"), RPsiFunction.class);

        RPsiParameterDeclaration p0 = e.getParameters().get(0);
        //assertTrue(p0.isNamed());
        assertDoesntContain(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);

        RPsiSignature eps = p0.getSignature();
        assertInstanceOf(eps, RPsiModuleSignature.class);
        assertEquals("(module Intf)", eps.getText());

        RPsiDefaultValue epv = p0.getDefaultValue();
        assertEquals("(module Impl)", epv.getText());
        PsiElement epvc = epv.getFirstChild();
        assertInstanceOf(epvc, RPsiFirstClass.class);
    }

    @Test
    public void test_unpack() {
        RPsiInnerModule e = firstOfType(parseCode("module New_three = (val three : I)"), RPsiInnerModule.class);

        assertNull(e.getBody());
        assertEquals("(val three : I)", e.getUnpack().getText());
        assertEquals("three", e.getUnpack().getFirstClassSymbol().getText());
        assertEquals("I", e.getUnpack().getModuleReference().getText());
    }

    @Test
    public void test_unpack_no_signature() {
        RPsiInnerModule e = firstOfType(parseCode("module M = (val selectors)"), RPsiInnerModule.class);

        assertNull(e.getBody());
        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val selectors)", e.getUnpack().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test
    public void test_unpack_in_let() {
        RPsiInnerModule e = firstOfType(parseCode("let _ = let module M = (val m : S)"), RPsiInnerModule.class);

        assertNull(e.getBody());
        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val m : S)", e.getUnpack().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

}
