package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiFunctionParameter;
import com.reason.lang.core.psi.PsiLet;

import java.util.Collection;

@SuppressWarnings("ConstantConditions")
public class FunctionCallTest extends BaseParsingTestCase {
    public FunctionCallTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testCall() {
        PsiLet e = first(letExpressions(parseCode("let _ = string_of_int(1)")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        Collection<PsiFunctionParameter> parameters = callParams.getParameterList();
        assertEquals(1, parameters.size());
    }

    public void testCall2() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.Option.map(self.state.timerId^, Js.Global.clearInterval)")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        Collection<PsiFunctionParameter> parameters = callParams.getParameterList();
        assertEquals(2, parameters.size());
    }

    public void testCall3() {
        PsiLet e = first(letExpressions(parseCode("let _ = subscriber->Topic.unsubscribe()")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        assertEmpty(callParams.getParameterList());
    }

}
