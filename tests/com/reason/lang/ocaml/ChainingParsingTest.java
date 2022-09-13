package com.reason.lang.ocaml;

import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ChainingParsingTest extends OclParsingTestCase {
    // env.ml L33
    public void test_let_semi_colon() {
        PsiLet e = firstOfType(parseCode("let fail s = Format.eprintf \"%s@\\n%!\" fail_msg; exit 1\n"), PsiLet.class);

        assertTrue(e.isFunction());
        PsiFunction f = e.getFunction();
        PsiFunctionBody b = f.getBody();

        List<PsiFunctionCall> fc = ORUtil.findImmediateChildrenOfClass(b, PsiFunctionCall.class);
        assertEquals("eprintf \"%s@\\n%!\" fail_msg", fc.get(0).getText());
        assertEquals("exit 1", fc.get(1).getText());
    }
}
