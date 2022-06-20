package com.reason.lang.rescript;

import com.intellij.psi.*;

import java.util.*;

public class ExpressionChainingParsingTest extends ResParsingTestCase {
    public void test_basic() {
        List<PsiNamedElement> es = expressions(parseCode("type t\n let y = 1"));

        assertSize(2, es);
        assertEquals("type t", es.get(0).getText());
        assertEquals("let y = 1", es.get(1).getText());
    }
}
