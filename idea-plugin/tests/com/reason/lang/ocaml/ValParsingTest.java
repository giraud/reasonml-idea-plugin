package com.reason.lang.ocaml;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiVal;

public class ValParsingTest extends BaseParsingTestCase {
    public ValParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testQualifiedName() {
        PsiVal e = first(valExpressions(parseCode("val x : int")));

        assertEquals("Dummy.x", e.getQualifiedName());
        assertFalse(e.isFunction());
    }

    public void testName() {
        PsiVal e = first(valExpressions(parseCode("val x : int")));

        assertInstanceOf(e.getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals("x", e.getName());
    }

    public void testSpecialName() {
        PsiVal e = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));

        assertInstanceOf(e.getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("(>>=)", e.getName());
    }

    public void testFunction() {
        PsiVal e = first(valExpressions(parseCode("val x : 'a -> 'a t")));

        assertTrue(e.isFunction());
    }
}
