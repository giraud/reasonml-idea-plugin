package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiLet;

@SuppressWarnings("ConstantConditions")
public class BeginParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiLet exp = (PsiLet) first(expressions(parseCode("let _ = begin end")));

        assertNotNull(exp);
        assertEquals("begin end", exp.getBinding().getText());
    }
}
