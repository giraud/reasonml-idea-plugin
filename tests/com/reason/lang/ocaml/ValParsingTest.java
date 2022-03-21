package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiScopedExpr;
import com.reason.lang.core.psi.impl.PsiValImpl;

@SuppressWarnings("ConstantConditions")
public class ValParsingTest extends OclParsingTestCase {
    public void test_qualified_name() {
        PsiVal e = first(valExpressions(parseCode("val x : int")));

        assertEquals("Dummy.x", e.getQualifiedName());
        assertFalse(e.isFunction());
        PsiSignature signature = e.getSignature();
        assertEquals("int", signature.getText());
    }

    public void test_name() {
        PsiVal e = first(valExpressions(parseCode("val x : int")));

        assertInstanceOf(((PsiValImpl) e).getNameIdentifier(), PsiLowerIdentifier.class);
        assertEquals("x", e.getName());
    }

    public void test_special_name() {
        PsiVal e = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));

        assertInstanceOf(((PsiValImpl) e).getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("(>>=)", e.getName());
    }

    public void test_function() {
        PsiVal e = first(valExpressions(parseCode("val get: 'v t -> key -> 'v option")));

        assertTrue(e.isFunction());
        assertEquals("get", e.getName());
        assertEquals("'v t -> key -> 'v option", e.getSignature().getText());
    }
}
