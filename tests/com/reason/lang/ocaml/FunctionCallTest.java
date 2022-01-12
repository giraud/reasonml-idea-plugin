package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class FunctionCallTest extends OclParsingTestCase {
    public void test_call() {
        PsiLetBinding e = first(letExpressions(parseCode("let _ = string_of_int 1"))).getBinding();

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertEquals("string_of_int 1", call.getText());
        assertEquals(1, call.getParameters().size());
    }

    public void test_call_many() {
        PsiLetBinding e = first(letExpressions(parseCode("let _ = fn a b c"))).getBinding();

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertEquals("fn a b c", call.getText());
        assertEquals(3, call.getParameters().size());
        assertEquals("a", call.getParameters().get(0).getText());
        assertEquals("b", call.getParameters().get(1).getText());
        assertEquals("c", call.getParameters().get(2).getText());
    }
}
