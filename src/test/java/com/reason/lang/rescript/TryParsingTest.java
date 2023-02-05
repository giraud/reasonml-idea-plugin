package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class TryParsingTest extends ResParsingTestCase {
    @Test
    public void basic() {
        RPsiTry e = firstOfType(parseCode("try x catch { | Not_found => () }"), RPsiTry.class);
        assertNoParserError(e);

        assertEquals("try", e.getFirstChild().getText());
        assertEquals("x", e.getBody().getText());
        assertSize(1, e.getHandlers());
        RPsiTryHandler eh = e.getHandlers().get(0);
        assertEquals("Not_found => ()", eh.getText());
        assertEquals("()", eh.getBody().getText());
        assertEquals(myTypes.EXCEPTION_NAME, eh.getFirstChild().getNode().getElementType());
    }

    @Test
    public void try_in_let() {
        RPsiLet e = firstOfType(parseCode("let _ = x => try x catch { | Failure(_) => () }"), RPsiLet.class);
        assertNoParserError(e);

        RPsiTry et = PsiTreeUtil.findChildOfType(e, RPsiTry.class);

        assertEquals("try", et.getFirstChild().getText());
        assertEquals("x", et.getBody().getText());
        assertSize(1, et.getHandlers());
        RPsiTryHandler eth = et.getHandlers().get(0);
        assertEquals("Failure(_) => ()", eth.getText());
        assertEquals("()", eth.getBody().getText());
        assertEquals(myTypes.EXCEPTION_NAME, eth.getFirstChild().getNode().getElementType());
    }

    @Test
    public void try_in_let_scoped() {
        RPsiLet e = firstOfType(parseCode("let _ = x => { try x catch { | Failure(_) => () } }"), RPsiLet.class);
        assertNoParserError(e);

        RPsiTry et = PsiTreeUtil.findChildOfType(e, RPsiTry.class);

        assertEquals("try", et.getFirstChild().getText());
        assertEquals("x", et.getBody().getText());
        assertSize(1, et.getHandlers());
        assertEquals("Failure(_) => ()", et.getHandlers().iterator().next().getText());
    }
}
