package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiLowerSymbol;
import com.reason.lang.core.psi.impl.PsiScopedExpr;
import com.reason.lang.core.psi.impl.PsiValImpl;

import java.util.*;

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

        assertInstanceOf(((PsiValImpl) e).getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals("x", e.getName());
    }

    public void test_special_name() {
        PsiVal e = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));

        assertInstanceOf(((PsiValImpl) e).getNameIdentifier(), PsiScopedExpr.class);
        assertEquals("(>>=)", e.getName());
    }

    public void test_function() {
        PsiVal e = first(valExpressions(parseCode("val init: int -> (int -> 'a) -> 'a array")));

        assertTrue(e.isFunction());
        assertEquals("init", e.getName());
        assertEquals("int -> (int -> 'a) -> 'a array", e.getSignature().getText());
        List<PsiSignatureItem> is = e.getSignature().getItems();
        assertEquals("int", is.get(0).getText());
        assertEquals("int", is.get(1).getText());
        assertEquals("'a", is.get(2).getText());
        assertEquals("'a array", is.get(3).getText());
    }
}
