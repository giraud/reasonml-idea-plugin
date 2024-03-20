package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ValParsingTest extends OclParsingTestCase {
    @Test
    public void test_qualified_name() {
        RPsiVal e = firstOfType(parseCode("val x : int"), RPsiVal.class);

        assertEquals("Dummy.x", e.getQualifiedName());
        assertFalse(e.isFunction());
        RPsiSignature signature = e.getSignature();
        assertEquals("int", signature.getText());
    }

    @Test
    public void test_name() {
        RPsiVal e = firstOfType(parseCode("val x : int"), RPsiVal.class);

        assertInstanceOf(e.getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals("x", e.getName());
    }

    @Test
    public void test_special_name() {
        RPsiVal e = firstOfType(parseCode("val (>>=) : 'a -> 'a t"), RPsiVal.class);

        assertInstanceOf(e.getNameIdentifier(), RPsiScopedExpr.class);
        assertEquals("(>>=)", e.getName());
    }

    @Test
    public void test_function() {
        RPsiVal e = firstOfType(parseCode("val init: int -> (int -> 'a) -> 'a array"), RPsiVal.class);

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
