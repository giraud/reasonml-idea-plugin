package com.reason.lang.ocaml;

import com.reason.lang.core.psi.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class BeginParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLet exp = firstOfType(parseCode("let _ = begin end"), RPsiLet.class);

        assertNotNull(exp);
        assertEquals("begin end", exp.getBinding().getText());
    }
}
