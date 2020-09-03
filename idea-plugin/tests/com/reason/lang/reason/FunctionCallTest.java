package com.reason.lang.reason;

import java.util.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameter;

@SuppressWarnings("ConstantConditions")
public class FunctionCallTest extends RmlParsingTestCase {
    public void test_call() {
        PsiLet e = first(letExpressions(parseCode("let _ = string_of_int(1)")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        Collection<PsiParameter> parameters = callParams.getParametersList();
        assertEquals(1, parameters.size());
    }

    public void test_call2() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.Option.map(self.state.timerId^, Js.Global.clearInterval)")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        Collection<PsiParameter> parameters = callParams.getParametersList();
        assertEquals(2, parameters.size());
    }

    public void test_call3() {
        PsiLet e = first(letExpressions(parseCode("let _ = subscriber->Topic.unsubscribe()")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        assertEmpty(callParams.getParametersList());
    }

    public void test_unitLast() {
        PsiLet e = first(letExpressions(parseCode("let _ = f(1, ());")));

        PsiFunctionCallParams params = PsiTreeUtil.findChildOfType(e, PsiFunctionCallParams.class);
        assertSize(2, params.getParametersList());
    }

    public void test_params() {
        FileBase f = parseCode("call(~decode=x => Ok(), ~task=() => y,);");
        PsiFunctionCallParams e = ORUtil.findImmediateFirstChildOfClass(f, PsiFunctionCallParams.class);

        assertSize(2, e.getParametersList());
    }

    public void test_GH_120() {
        PsiLet e = first(letExpressions(parseCode("let _ = f(x == U.I, 1)")));

        PsiFunctionCallParams params = PsiTreeUtil.findChildOfType(e, PsiFunctionCallParams.class);
        assertSize(2, params.getParametersList());
    }
}
