package com.reason.lang.dune;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class SExprParsingTest extends DuneParsingTestCase {
    @Test
    public void test_s_expr() {
        RPsiDuneStanza e = firstOfType(parseDuneCode("( ( () ) )"), RPsiDuneStanza.class);

        assertNoParserError(e);
        RPsiDuneSExpr e1 = ORUtil.findImmediateFirstChildOfClass(e, RPsiDuneSExpr.class);
        assertEquals("( () )", e1.getText());
        RPsiDuneSExpr e11 = ORUtil.findImmediateFirstChildOfClass(e1, RPsiDuneSExpr.class);
        assertEquals("()", e11.getText());
    }
}
