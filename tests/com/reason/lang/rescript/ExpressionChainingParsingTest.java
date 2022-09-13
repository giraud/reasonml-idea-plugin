package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;

import java.util.*;

public class ExpressionChainingParsingTest extends ResParsingTestCase {
    public void test_basic() {
        List<PsiNamedElement> es = expressions(parseCode("type t\n let y = 1"));

        assertSize(2, es);
        assertEquals("type t", es.get(0).getText());
        assertEquals("let y = 1", es.get(1).getText());
    }

    public void test_alias_chaining_include() {
        PsiModule module = first(moduleExpressions(parseCode("module D = B\n include D.C")));

        assertEquals("D", module.getName());
        assertEquals("B", module.getAlias());
    }

    public void test_alias_chaining_call() {
        PsiModule module = first(moduleExpressions(parseCode("module D = B\n D.C")));

        assertEquals("D", module.getName());
        assertEquals("B", module.getAlias());
    }
}
