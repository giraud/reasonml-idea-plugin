package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.RPsiLowerSymbol;
import com.reason.lang.core.psi.impl.RPsiScopedExpr;
import com.reason.lang.core.psi.impl.RPsiValImpl;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ValParsingTest extends OclParsingTestCase {
    @Test
    public void test_qualified_name() {
        RPsiVal e = first(valExpressions(parseCode("val x : int")));

        assertEquals("Dummy.x", e.getQualifiedName());
        assertFalse(e.isFunction());
        RPsiSignature signature = e.getSignature();
        assertEquals("int", signature.getText());
    }

    @Test
    public void test_name() {
        RPsiVal e = first(valExpressions(parseCode("val x : int")));

        assertInstanceOf(((RPsiValImpl) e).getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals("x", e.getName());
    }

    @Test
    public void test_special_name() {
        RPsiVal e = first(valExpressions(parseCode("val (>>=) : 'a -> 'a t")));

        assertInstanceOf(((RPsiValImpl) e).getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("(>>=)", e.getName());
    }

    @Test
    public void test_function() {
        RPsiVal e = first(valExpressions(parseCode("val init: int -> (int -> 'a) -> 'a array")));

        assertTrue(e.isFunction());
        assertEquals("init", e.getName());
        assertEquals("int -> (int -> 'a) -> 'a array", e.getSignature().getText());
        List<RPsiSignatureItem> is = e.getSignature().getItems();
        assertEquals("int", is.get(0).getText());
        assertEquals("int", is.get(1).getText());
        assertEquals("'a", is.get(2).getText());
        assertEquals("'a array", is.get(3).getText());
    }
}
