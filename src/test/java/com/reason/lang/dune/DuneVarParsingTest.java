package com.reason.lang.dune;

import com.intellij.psi.*;
import org.junit.*;

public class DuneVarParsingTest extends DuneParsingTestCase {
    @Test
    public void test_basic() {
        PsiElement e = firstElement(parseRawCode("%{x}"));

        assertEquals(DuneTypes.INSTANCE.C_VAR, e.getNode().getElementType());
        assertEquals("%{x}", e.getText());
    }
}
