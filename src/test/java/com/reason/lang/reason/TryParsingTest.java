package com.reason.lang.reason;

import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class TryParsingTest extends RmlParsingTestCase {
    @Test
    public void basic() {
        RPsiTry e = (RPsiTry) firstElement(parseCode("try (x) { | Not_found => () };"));

        assertEquals("try", e.getFirstChild().getText());
        assertNotNull(e.getBody());
        assertEquals("(x)", e.getBody().getText());
        assertSize(1, e.getHandlers());
        RPsiTryHandler eh = e.getHandlers().get(0);
        assertEquals("Not_found => ()", eh.getText());
        assertEquals("()", eh.getBody().getText());
        assertEquals(myTypes.A_EXCEPTION_NAME, eh.getFirstChild().getNode().getElementType());
    }
}
