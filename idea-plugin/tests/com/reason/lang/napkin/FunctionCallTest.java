package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiLet;

@SuppressWarnings("ConstantConditions")
public class FunctionCallTest extends BaseParsingTestCase {
    public FunctionCallTest() {
        super("", "res", new NsParserDefinition());
    }

    public void testCall() {
        PsiLet e = first(letExpressions(parseCode("let _ = string_of_int(1)")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        Collection<PsiElement> parameters = callParams.getParametersList();
        assertEquals(1, parameters.size());
    }

    public void testCall2() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.Option.map(self.state.timerId^, Js.Global.clearInterval)")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        Collection<PsiElement> parameters = callParams.getParametersList();
        assertEquals(2, parameters.size());
    }

    public void testCall3() {
        PsiLet e = first(letExpressions(parseCode("let _ = subscriber->Topic.unsubscribe()")));

        PsiFunctionCallParams callParams = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class);
        assertEmpty(callParams.getParametersList());
    }

    public void testIssue120() {
        PsiLet e = first(letExpressions(parseCode("let _ = f(x == U.I, 1)")));

        PsiFunctionCallParams params = PsiTreeUtil.findChildOfType(e, PsiFunctionCallParams.class);
        assertSize(2, params.getParametersList());
    }

    public void testUnitLast() {
        PsiLet e = first(letExpressions(parseCode("let _ = f(1, ());")));

        PsiFunctionCallParams params = PsiTreeUtil.findChildOfType(e, PsiFunctionCallParams.class);
        assertSize(2, params.getParametersList());
        assertEquals("()", ORUtil.findImmediateFirstChildOfType(new ArrayList<>(params.getParametersList()).get(1), NsTypes.INSTANCE.C_UNIT).getText());
    }

    public void testParams() {
        FileBase f = parseCode("call(~decode=x => Ok(), ~task=() => y,);");
        PsiFunctionCallParams e = ORUtil.findImmediateFirstChildOfClass(f, PsiFunctionCallParams.class);

        assertSize(2, e.getParametersList());
    }
}
