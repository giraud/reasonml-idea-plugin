package com.reason.lang.reason;

import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class TryWithTest extends RmlParsingTestCase {
    public void test_try_structure() {
        PsiTry e = (PsiTry) firstElement(parseCode("try (x) { | Not_found => () };"));

        assertEquals("try", e.getFirstChild().getText());
        assertNotNull(e.getBody());
        assertEquals("(x)", e.getBody().getText());
        assertSize(1, e.getHandlers());
        assertEquals("| Not_found => ()", e.getHandlers().iterator().next().getText());
    }
}
