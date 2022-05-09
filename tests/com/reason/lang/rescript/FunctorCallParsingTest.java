package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorCallParsingTest extends ResParsingTestCase {
    public void test_instantiation() {
        PsiInnerModule e = (PsiInnerModule) first(moduleExpressions(parseCode("module Printing = Make({ let encode = encode_record })")));

        assertTrue(e.isFunctorCall());
        assertNull(e.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make({ let encode = encode_record })", call.getText());
        assertSize(1, call.getParameters());
        assertEquals("{ let encode = encode_record }", call.getParameters().iterator().next().getText());
        PsiLet let = PsiTreeUtil.findChildOfType(e, PsiLet.class);
        assertEquals("Dummy.Printing.Make[0].encode", let.getQualifiedName());
    }

    public void test_with_path() {
        PsiInnerModule e = (PsiInnerModule) first(moduleExpressions(parseCode("module X = A.B.Make({})")));

        assertTrue(e.isFunctorCall());
        assertNull(e.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(e, PsiFunctorCall.class);
        assertEquals("Make({})", call.getText());
    }

    public void test_chaining() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash)\n type infos");
        List<PsiNamedElement> es = new ArrayList<>(expressions(file));

        assertEquals(2, es.size());

        PsiInnerModule module = (PsiInnerModule) es.get(0);
        assertTrue(module.isFunctorCall());
        assertNull(module.getBody());
        PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
        assertNotNull(call);
        assertEquals("Make(KeyHash)", call.getText());
    }
}
