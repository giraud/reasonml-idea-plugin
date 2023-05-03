package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionCallParsingTest extends RmlParsingTestCase {
    @Test
    public void test_call() {
        RPsiLetBinding e = firstOfType(parseCode("let _ = string_of_int(1)"), RPsiLet.class).getBinding();

        RPsiFunctionCall call = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(call, RPsiLowerSymbol.class));
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiParameterDeclaration.class));
        assertEquals("string_of_int(1)", call.getText());
        assertEquals(1, call.getParameters().size());
    }

    @Test
    public void test_call2() {
        RPsiLet e = firstOfType(parseCode("let _ = Belt.Option.map(self.state.timerId^, Js.Global.clearInterval)"), RPsiLet.class);

        RPsiFunctionCall fnCall = PsiTreeUtil.findChildOfType(e.getBinding(), RPsiFunctionCall.class);
        List<RPsiParameterReference> parameters = fnCall.getParameters();
        assertEquals(2, parameters.size());
        assertEquals("self.state.timerId^", parameters.get(0).getText());
        assertEquals("Js.Global.clearInterval", parameters.get(1).getText());
    }

    @Test
    public void test_call3() {
        RPsiLet e = firstOfType(parseCode("let _ = subscriber->Topic.unsubscribe()"), RPsiLet.class);

        RPsiFunctionCall fnCall = PsiTreeUtil.findChildOfType(e.getBinding(), RPsiFunctionCall.class);
        assertEmpty(fnCall.getParameters());
    }

    @Test
    public void test_end_comma() {
        RPsiLet e = firstOfType(parseCode("let _ = style([ color(red), ])"), RPsiLet.class);

        assertEquals("style([ color(red), ])", e.getBinding().getText());
        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(e.getBinding(), RPsiFunctionCall.class);
        assertNull(PsiTreeUtil.findChildOfType(f, RPsiDeconstruction.class));
        assertSize(1, f.getParameters());
    }

    @Test
    public void test_unit_last() {
        RPsiLetBinding e = firstOfType(parseCode("let _ = f(1, ());"), RPsiLet.class).getBinding();

        RPsiFunctionCall fnCall = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertSize(2, fnCall.getParameters());
    }

    @Test
    public void test_optional_param() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = fn(~margin?, ());"), RPsiFunctionCall.class);
        assertSize(2, e.getParameters());
        assertEquals("~margin?", e.getParameters().get(0).getText());
        assertEquals("()", e.getParameters().get(1).getText());
    }

    @Test
    public void test_inner_parenthesis() {
        RPsiLet e = firstOfType(parseCode("let _ = f(a, (b, c));"), RPsiLet.class);

        RPsiFunctionCall call = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertSize(2, call.getParameters());
        RPsiParameterReference p1 = call.getParameters().get(1);
        assertEquals("(b, c)", p1.getText());
        assertNull(PsiTreeUtil.findChildOfType(p1, RPsiParameters.class));
        assertNull(PsiTreeUtil.findChildOfType(p1, RPsiParameterReference.class));
    }

    @Test
    public void test_params() {
        FileBase f = parseCode("call(~decode=x => Ok(), ~task=() => y,);");
        RPsiFunctionCall fnCall = ORUtil.findImmediateFirstChildOfClass(f, RPsiFunctionCall.class);

        assertSize(2, fnCall.getParameters());
    }

    @Test
    public void test_param_name() {
        List<RPsiLet> expressions = letAllExpressions(parseCode("describe(\"context\", () => { test(\"should do something\", () => { let inner = 1; }) })"));
        RPsiLet e = first(expressions);

        assertEquals("Dummy.describe[1].test[1].inner", e.getQualifiedName());
    }

    @Test
    public void test_nested_parenthesis() {
        RPsiFunctionCall f = firstOfType(parseCode("set(x->keep(((y, z)) => y), xx);"), RPsiFunctionCall.class);

        assertEquals("set(x->keep(((y, z)) => y), xx)", f.getText());
        assertEquals("x->keep(((y, z)) => y)", f.getParameters().get(0).getText());
        assertEquals("xx", f.getParameters().get(1).getText());
    }

    @Test
    public void test_body() {
        RPsiLet e = firstOfType(parseCode("let _ = x => { M.{k: v} };"), RPsiLet.class);

        RPsiFunctionBody body = PsiTreeUtil.findChildOfType(e, RPsiFunctionBody.class);
        assertEquals("{ M.{k: v} }", body.getText());
    }

    @Test
    public void test_in_functor() {
        //                                    0        |         |          |         |         |        |         |         |          |
        RPsiFunctor e = firstOfType(parseCode("module Make = (M: Intf) : Result => { let fn = target => (. store) => call(input, item => item); };"), RPsiFunctor.class);

        RPsiFunctionCall fc = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEquals("call(input, item => item)", fc.getText());
    }

    @Test
    public void test_ternary_in_named_param() {
        RPsiFunctionCall e = firstOfType(parseCode("fn(~x=a ? b : c);"), RPsiFunctionCall.class);

        assertSize(1, e.getParameters());
        RPsiParameterReference p0 = e.getParameters().get(0);
        assertEquals("x", p0.getName());
        assertEquals("a ? b : c", p0.getValue().getText());
        assertInstanceOf(p0.getValue().getFirstChild(), RPsiTernary.class);
    }

    @Test
    public void test_assignment() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = fn(x => myRef.current = x);"), RPsiFunctionCall.class);

        assertEquals("x => myRef.current = x", e.getParameters().get(0).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/120
    @Test
    public void test_GH_120() {
        RPsiLet e = firstOfType(parseCode("let _ = f(x == U.I, 1)"), RPsiLet.class);

        RPsiFunctionCall fnCall = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertSize(2, fnCall.getParameters());
    }
}
