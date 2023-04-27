package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import org.junit.*;

import java.util.*;

public class ExpressionChainingParsingTest extends ResParsingTestCase {
    @Test
    public void test_basic() {
        List<PsiNamedElement> es = expressions(parseCode("type t\n let y = 1"));

        assertSize(2, es);
        assertEquals("type t", es.get(0).getText());
        assertEquals("let y = 1", es.get(1).getText());
    }

    @Test
    public void test_alias_chaining_include() {
        RPsiInnerModule module = firstOfType(parseCode("module D = B\n include D.C"), RPsiInnerModule.class);

        assertEquals("D", module.getName());
        assertEquals("B", module.getAlias());
    }

    @Test
    public void test_alias_chaining_call() {
        RPsiInnerModule module = firstOfType(parseCode("module D = B\n D.C"), RPsiInnerModule.class);

        assertEquals("D", module.getName());
        assertEquals("B", module.getAlias());
    }
}
