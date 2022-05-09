package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionCallParsingTest extends ResParsingTestCase {
    public void test_call() {
        PsiLet e = first(letExpressions(parseCode("let _ = string_of_int(1)")));

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCall.class);
        assertEquals("string_of_int(1)", call.getText());
        assertEquals(1, call.getParameters().size());
    }

    public void test_call2() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.Option.map(self.state.timerId.contents, Js.Global.clearInterval)")));

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCall.class);
        List<PsiParameter> parameters = call.getParameters();
        assertEquals(2, parameters.size());
        assertEquals("self.state.timerId.contents", parameters.get(0).getText());
        assertNull(PsiTreeUtil.getChildrenOfType(parameters.get(0), PsiLowerIdentifier.class));
        assertEquals("Js.Global.clearInterval", parameters.get(1).getText());
        assertNull(PsiTreeUtil.getChildrenOfType(parameters.get(1), PsiLowerIdentifier.class));
    }

    public void test_call3() {
        PsiLet e = first(letExpressions(parseCode("let _ = subscriber->Topic.unsubscribe()")));

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCall.class);
        assertEmpty(call.getParameters());
    }

    public void test_unitLast() {
        PsiLet e = first(letExpressions(parseCode("let _ = f(1, ())")));

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertSize(2, call.getParameters());
    }

    public void test_params() {
        FileBase f = parseCode("call(~decode=x => Ok(), ~task=() => y)");
        PsiFunctionCall e = ORUtil.findImmediateFirstChildOfClass(f, PsiFunctionCall.class);

        assertSize(2, e.getParameters());
    }

    public void test_GH_120() {
        PsiLet e = first(letExpressions(parseCode("let _ = f(x == U.I, 1)")));

        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertSize(2, call.getParameters());
    }

    public void test_param_name() {
        List<PsiLet> expressions = letAllExpressions(parseCode("describe(\"context\", () => { test(\"should do something\", () => { let inner = 1 }) })"));
        PsiLet e = first(expressions);

        assertEquals("Dummy.describe[1].test[1].inner", e.getQualifiedName());
    }

    public void test_body() {
        PsiLet e = first(letExpressions(parseCode("let _ = x => { open M\n {k: v} }")));

        PsiFunctionBody body = PsiTreeUtil.findChildOfType(e, PsiFunctionBody.class);
        assertEquals("{ open M\n {k: v} }", body.getText());
    }
}
