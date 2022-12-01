package com.reason.lang.ocaml;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ChainingParsingTest extends OclParsingTestCase {
    // env.ml L33
    @Test
    public void test_let_semi_colon() {
        RPsiLet e = firstOfType(parseCode("let fail s = Format.eprintf \"%s@\\n%!\" fail_msg; exit 1\n"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction f = e.getFunction();
        RPsiFunctionBody b = f.getBody();

        List<RPsiFunctionCall> fc = ORUtil.findImmediateChildrenOfClass(b, RPsiFunctionCall.class);
        assertEquals("eprintf \"%s@\\n%!\" fail_msg", fc.get(0).getText());
        assertEquals("exit 1", fc.get(1).getText());
    }

    @Test
    public void test_while() {
        RPsiWhile e = firstOfType(parseCode("let _ = while true do printf \"File format: %ld\\n%!\" version; exit 1 done"), RPsiWhile.class);

        List<RPsiFunctionCall> fc = ORUtil.findImmediateChildrenOfClass(e.getBody(), RPsiFunctionCall.class);
        assertEquals("printf \"File format: %ld\\n%!\" version", fc.get(0).getText());
        assertEquals("exit 1", fc.get(1).getText());
    }
}
