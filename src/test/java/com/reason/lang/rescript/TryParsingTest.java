package com.reason.lang.rescript;

import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class TryParsingTest extends ResParsingTestCase {
    @Test
    public void test_try_structure() {
        RPsiTry e = firstOfType(parseCode("try x catch { | Not_found => () }"), RPsiTry.class);

        assertEquals("try", e.getFirstChild().getText());
        assertEquals("x", e.getBody().getText());
        assertSize(1, e.getHandlers());
        assertEquals("Not_found => ()", e.getHandlers().iterator().next().getText());
    }
}
