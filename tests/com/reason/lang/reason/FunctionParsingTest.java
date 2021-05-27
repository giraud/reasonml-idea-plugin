package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends RmlParsingTestCase {
    public void test_anonymous_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.map(items, (. item) => value)")));

        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertSize(1, function.getParameters());
        assertEquals("item", first(function.getParameters()).getText());
        assertInstanceOf(first(function.getParameters()).getNameIdentifier(), PsiLowerIdentifier.class);
        assertEquals("value", function.getBody().getText());
    }

    public void test_braceFunction() {
        PsiLet e = first(letExpressions(parseCode("let x = (x, y) => { x + y; }")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertSize(2, function.getParameters());
        assertEquals("(x, y) => { x + y; }", function.getText());
        assertNotNull(function.getBody());
    }

    public void test_destructuration() {
        PsiLet e = first(letExpressions(parseCode("let _ = (a, {b, _}) => b;")));

        assertTrue(e.isFunction());
        assertSize(2, e.getFunction().getParameters());
    }

    public void test_parenlessFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = x => x + 10;")));

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();

        assertSize(1, function.getParameters());
        assertInstanceOf(first(function.getParameters()), PsiParameter.class);
        assertNotNull(function.getBody());
    }

    public void test_dotFunction() {
        PsiLet e = first(letExpressions(parseCode("let _ = (. x) => x;")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(1, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("x", function.getBody().getText());
    }

    public void test_innerFunction() {
        PsiLet e =
                first(
                        letExpressions(
                                parseCode(
                                        "let _ = error => Belt.Array.mapU(errors, (. error) => error##message);")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals(
                "Belt.Array.mapU(errors, (. error) => error##message)", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error##message", functionInner.getBody().getText());
    }

    public void test_innerFunctionBraces() {
        PsiLet e =
                first(
                        letExpressions(
                                parseCode(
                                        "let _ = error => { Belt.Array.mapU(errors, (. error) => error##message); };")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals(
                "{ Belt.Array.mapU(errors, (. error) => error##message); }",
                functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error##message", functionInner.getBody().getText());
    }

    public void test_innerFunctionNoParens() {
        PsiLet e = first(letExpressions(parseCode("let _ = funcall(result => 2);")));

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
        PsiLet e =
                first(
                        letExpressions(
                                parseCode(
                                        "let make = (~id:string, ~values: option(Js.t('a)), children) => null;")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameter> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    public void test_parametersNamedSymbols2() {
        PsiLet e =
                first(
                        letExpressions(
                                parseCode(
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
        PsiLet e = first(letExpressions(parseCode("let _ = () => 1;")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(0, function.getParameters());
        assertEquals("1", function.getBody().getText());
    }

    public void test_parametersLIdent() {
        PsiLet e = first(letExpressions(parseCode("let make = (id, values, children) => null;")));

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

    public void test_underscore() {
        PsiLet e = first(letExpressions(parseCode("let onCancel = _ => { setUpdatedAttribute(_ => initialAttribute); };")));

        assertEquals("_ => { setUpdatedAttribute(_ => initialAttribute); }", e.getBinding().getText());
        assertEquals("(_ => initialAttribute)", PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCallParams.class).getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/113
    public void test_GH_113() {
        PsiFunction e = (PsiFunction) firstElement(parseCode("() => switch (isBuggy()) { | _ => \"buggy\" };"));

        assertSize(0, e.getParameters());
        PsiFunctionBody b = e.getBody();
        assertInstanceOf(b.getFirstChild(), PsiSwitch.class);
        PsiSwitch s = (PsiSwitch) b.getFirstChild();
        assertEquals("(isBuggy())", s.getCondition().getText());
    }
}
