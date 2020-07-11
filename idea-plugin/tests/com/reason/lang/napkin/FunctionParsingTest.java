package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiFunctionBody;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiSwitch;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends NsParsingTestCase {
    public void test_anonFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.map(items, (. item) => value)")));

        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertSize(1, function.getParameters());
        assertEquals("item", first(function.getParameters()).getText());
        assertEquals("value", function.getBody().getText());
    }

    public void test_braceFunction() {
        PsiLet e = first(letExpressions(parseCode("let x = (x, y) => { x + y }")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertSize(2, function.getParameters());
        assertEquals("(x, y) => { x + y }", function.getText());
        assertNotNull(function.getBody());
    }

    public void test_destructuration() {
        PsiLet e = first(letExpressions(parseCode("let _ = (a, {b, _}) => b")));

        assertTrue(e.isFunction());
        assertSize(2, e.getFunction().getParameters());
    }

    public void test_parenlessFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = x => x + 10")));

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();

        assertSize(1, function.getParameters());
        assertInstanceOf(first(function.getParameters()), PsiParameter.class);
        assertNotNull(function.getBody());
    }

    public void test_dotFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = (. x) => x")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(1, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("x", function.getBody().getText());
    }

    public void test_GH_Issue113() {
        PsiElement e = firstElement(parseCode("() => switch (isBuggy()) { | _ => \\\"buggy\\\" }\""));

        assertInstanceOf(e, PsiFunction.class);
        PsiFunction f = (PsiFunction) e;
        assertSize(1, f.getParameters());
        PsiFunctionBody fb = f.getBody();
        assertInstanceOf(fb.getFirstChild(), PsiSwitch.class);
        PsiSwitch s = (PsiSwitch) fb.getFirstChild();
        assertEquals("(isBuggy())", s.getCondition().getText());
    }

    public void test_innerFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = error => Belt.Array.mapU(errors, (. error) => error##message)")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("Belt.Array.mapU(errors, (. error) => error##message)", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error##message", functionInner.getBody().getText());
    }

    public void test_innerFunctionBraces() {
        PsiLet e = first(letExpressions(parseCode("let _ = error => { Belt.Array.mapU(errors, (. error) => error##message) }")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("{ Belt.Array.mapU(errors, (. error) => error##message) }", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error##message", functionInner.getBody().getText());
    }

    public void test_innerFunctionNoParens() {
        PsiLet e = first(letExpressions(parseCode("let _ = funcall(result => 2)")));

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEquals("2", functionInner.getBody().getText());
    }

    public void test_parameterAnonFunction() {
        FileBase e = parseCode("describe('a', () => test('b', () => true));");

        List<PsiFunction> funcs = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiFunction.class));
        assertSize(2, funcs);
        assertEquals("() => test('b', () => true)", funcs.get(0).getText());
        assertEquals("() => true", funcs.get(1).getText());
    }

    public void test_parametersNamedSymbols() {
        PsiLet e = first(letExpressions(parseCode("let make = (~id:string, ~values: option<Js.t<'a>>, children) => null")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    public void test_parametersNamedSymbols2() {
        PsiLet e = first(letExpressions(parseCode(
                "let make = (~text, ~id=?, ~values=?, ~className=\"\", ~tag=\"span\", ~transform=\"unset\", ~marginLeft=\"0\", ~onClick=?, ~onKeyPress=?, _children, ) => {}")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertSize(10, function.getParameters());
    }

    public void test_parenFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = (x,y) => x + y;")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(2, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("y", second(function.getParameters()).getText());
        assertEquals("x + y", function.getBody().getText());
    }

    public void test_unitFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = () => 1")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(1, function.getParameters());
        assertEquals("()", first(function.getParameters()).getText());
        assertEquals("1", function.getBody().getText());
    }

    public void test_parametersLIdent() {
        PsiLet e = first(letExpressions(parseCode("let make = (id, values, children) => null")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    public void test_recordFunction() {
        PsiLet e = first(letExpressions(parseCode("let make = (children) => { ...component, render: self => <div/>, }")));
        PsiFunctionBody body = e.getFunction().getBody();
        PsiFunction innerFunction = PsiTreeUtil.findChildOfType(body, PsiFunction.class);

        assertSize(1, innerFunction.getParameters());
        assertEquals("self", first(innerFunction.getParameters()).getName());
        assertEquals("<div/>", innerFunction.getBody().getText());
    }
}
