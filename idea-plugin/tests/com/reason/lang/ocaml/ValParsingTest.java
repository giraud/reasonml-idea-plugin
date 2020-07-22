package com.reason.lang.ocaml;

import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiVal;

public class ValParsingTest extends OclParsingTestCase {
    public void test_qualifiedName() {
        PsiVal e = first(valExpressions(parseCode("val x : int")));

        assertEquals("Dummy.x", e.getQualifiedName());
        assertFalse(e.isFunction());
    }

    public void test_name() {
        PsiVal e = first(valExpressions(parseCode("val x : int")));

        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals("x", e.getName());
    }

    public void test_specialName() {
        PsiVal e = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));

        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("(>>=)", e.getName());
    }

    public void test_function() {
        PsiVal e = first(valExpressions(parseCode("val x : 'a -> 'a t")));

        assertTrue(e.isFunction());
    }
}
