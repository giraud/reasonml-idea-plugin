package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends ResParsingTestCase {
    @Test
    public void test_anonymous_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = Belt.map(items, (. item) => value)")));

        PsiFunction function = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertSize(1, function.getParameters());
        assertEquals("item", first(function.getParameters()).getText());
        assertInstanceOf(first(function.getParameters()).getNameIdentifier(), PsiLowerSymbol.class);
        assertEquals("value", function.getBody().getText());
    }

    @Test
    public void test_brace_function() {
        PsiLet e = first(letExpressions(parseCode("let x = (x, y) => { x + y }")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertSize(2, function.getParameters());
        assertEquals("(x, y) => { x + y }", function.getText());
        assertNotNull(function.getBody());
    }

    @Test
    public void test_destructuration() {
        PsiLet e = first(letExpressions(parseCode("let _ = (a, {b, _}) => b")));

        assertTrue(e.isFunction());
        assertSize(2, e.getFunction().getParameters());
    }

    @Test
    public void test_parenless_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = x => x + 10")));

        assertTrue(e.isFunction());
        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();

        assertSize(1, function.getParameters());
        assertInstanceOf(first(function.getParameters()), PsiParameterDeclaration.class);
        assertNotNull(function.getBody());
    }

    @Test
    public void test_dot_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = (. x) => x")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(1, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("x", function.getBody().getText());
    }

    @Test
    public void test_inner_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = error => Belt.Array.mapU(errors, (. error) => error[\"message\"])")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("Belt.Array.mapU(errors, (. error) => error[\"message\"])", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error[\"message\"]", functionInner.getBody().getText());
    }

    @Test
    public void test_inner_function_braces() {
        PsiLet e = first(letExpressions(parseCode("let _ = error => { Belt.Array.mapU(errors, (. error) => error[\"message\"]) }")));

        PsiFunction functionOuter = (PsiFunction) e.getBinding().getFirstChild();
        assertEquals("{ Belt.Array.mapU(errors, (. error) => error[\"message\"]) }", functionOuter.getBody().getText());

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, PsiFunction.class);
        assertEquals("error[\"message\"]", functionInner.getBody().getText());
    }

    @Test
    public void test_inner_function_no_parens() {
        PsiLet e = first(letExpressions(parseCode("let _ = funcall(result => 2)")));

        PsiFunction functionInner = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEquals("2", functionInner.getBody().getText());
    }

    @Test
    public void test_parameter_anon_function() {
        FileBase e = parseCode("describe('a', () => test('b', () => true))");

        List<PsiFunction> funcs = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiFunction.class));
        assertSize(2, funcs);
        assertEquals("() => test('b', () => true)", funcs.get(0).getText());
        assertEquals("() => true", funcs.get(1).getText());
    }

    @Test
    public void test_parameters_named_symbols() {
        PsiLet e = first(letExpressions(parseCode("let make = (~id:string, ~values: option<'a>, children) => null")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    @Test
    public void test_parameters_named_symbols2() {
        PsiLet e = first(letExpressions(parseCode(
                "let make = (~text, ~id=?, ~values=?, ~className=\"\", ~tag=\"span\", ~transform=\"unset\", ~marginLeft=\"0\", ~onClick=?, ~onKeyPress=?, _children, ) => {}")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertSize(10, function.getParameters());
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, PsiTernary.class));
    }

    @Test
    public void test_parameters_named_symbols3() {
        PsiLet e = firstOfType(parseCode("let fn = (~a:t, ~b=2, ~c) => ();"), PsiLet.class);

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        assertSize(3, function.getParameters());
    }

    @Test
    public void test_paren_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = (x,y) => x + y")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(2, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("y", second(function.getParameters()).getText());
        assertEquals("x + y", function.getBody().getText());
    }

    @Test
    public void test_unit_function() {
        PsiLet e = first(letExpressions(parseCode("let _ = () => 1")));

        assertTrue(e.isFunction());
        PsiFunction function = e.getFunction();

        assertSize(0, function.getParameters());
        assertEquals("1", function.getBody().getText());
    }

    @Test
    public void test_parameters_LIdent() {
        PsiLet e = first(letExpressions(parseCode("let make = (id, values, children) => null;")));

        PsiFunction function = (PsiFunction) e.getBinding().getFirstChild();
        List<PsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    @Test
    public void test_record_function() {
        PsiLet e = first(letExpressions(parseCode("let make = (children) => { ...component, render: self => <div/>, }")));
        PsiFunctionBody body = e.getFunction().getBody();
        PsiFunction innerFunction = PsiTreeUtil.findChildOfType(body, PsiFunction.class);

        assertSize(1, innerFunction.getParameters());
        assertEquals("self", first(innerFunction.getParameters()).getName());
        assertEquals("<div/>", innerFunction.getBody().getText());
    }

    @Test
    public void test_underscore() {
        PsiLet e = first(letExpressions(parseCode("let onCancel = _ => setUpdatedAttribute(_ => initialAttribute)")));

        assertTrue(e.isFunction());
        PsiFunction f1 = e.getFunction();
        assertSize(1, f1.getParameters());
        assertEquals("setUpdatedAttribute(_ => initialAttribute)", f1.getBody().getText());
        assertEquals("_ => setUpdatedAttribute(_ => initialAttribute)", e.getBinding().getText());
        PsiFunctionCall call = PsiTreeUtil.findChildOfType(e.getBinding(), PsiFunctionCall.class);
        PsiParameterReference callParam = call.getParameters().get(0);
        PsiFunction f2 = PsiTreeUtil.findChildOfType(callParam, PsiFunction.class);
        assertSize(1, f2.getParameters());
        assertEquals("initialAttribute", f2.getBody().getText());
    }

    @Test
    public void test_first_class_module() {
        PsiFunction e = firstOfType(parseCode("let make = (~selectors: (module SelectorsIntf)=(module Selectors)) => {}"), PsiFunction.class);

        PsiParameterDeclaration p0 = e.getParameters().get(0);
        PsiSignature s = p0.getSignature();
        PsiSignatureItem s0 = s.getItems().get(0);
        assertEquals("(module SelectorsIntf)", s0.getText());
        assertNull(PsiTreeUtil.findChildOfType(s, PsiModule.class));
        assertSize(2, PsiTreeUtil.findChildrenOfType(p0, PsiModuleValue.class));
        assertEquals("(module Selectors)", p0.getDefaultValue().getText());
        assertNull(ORUtil.findImmediateFirstChildOfType(PsiTreeUtil.findChildOfType(s0, PsiModuleValue.class), myTypes.A_VARIANT_NAME));
    }

    @Test
    public void test_signature() {
        PsiFunction e = firstOfType(parseCode("let _ = (~p: (option(string), option(int)) => unit) => p;"), PsiFunction.class);

        PsiParameterDeclaration p0 = e.getParameters().get(0);
        assertEquals("(option(string), option(int)) => unit", p0.getSignature().getText());
        //assertEquals("option(string)", p0.getSignature().getItems().get(0).getText());       ??
        //assertEquals("option(int)", p0.getSignature().getItems().get(1).getText());
        //assertEquals("unit", p0.getSignature().getItems().get(2).getText());
    }

    @Test
    public void test_curry_uncurry() {
        PsiFunction e = firstOfType(parseCode("let fn = p => (. p1) => p + p1"), PsiFunction.class);

        assertEquals("p", e.getParameters().get(0).getText());
        assertEquals("(. p1) => p + p1", e.getBody().getText());
    }

    @Test
    public void test_rollback_01() {
        PsiFunction f = firstOfType(parseCode("let _ = { let x = 1\n let y = 2\n () => 3 }"), PsiFunction.class); // test infinite rollback
        assertEquals("() => 3", f.getText());
    }

    @Test
    public void test_rollback_02() {
        List<PsiFunction> es = children(parseCode("let _ = (() => 1, () => 2);"), PsiFunction.class); // test infinite rollback
        assertEquals("() => 1", es.get(0).getText());
        assertEquals("() => 2", es.get(1).getText());
    }

    @Test
    public void test_rollback_03() {
        PsiInnerModule e = firstOfType(parseCode("module M: I with type t = t = {" +
                " let fn = p => (. p1) => { let _ = a let _ = x => x } " +
                "}"), PsiInnerModule.class);

        assertSize(1, e.getConstraints());
        PsiFunction f = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEquals("p", f.getParameters().get(0).getText());
        assertEquals("(. p1) => { let _ = a let _ = x => x }", f.getBody().getText());
    }

    @Test
    public void test_rollback_04() {
        PsiFunction e = firstOfType(parseCode("let fn = () => { let _ = 1\n (x) => 2 }"), PsiFunction.class);
        assertEquals("{ let _ = 1\n (x) => 2 }", e.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/113
    @Test
    public void test_GH_113() {
        PsiFunction e = firstOfType(parseCode("let x = () => switch isBuggy() { | _ => \"buggy\" }"), PsiFunction.class);

        assertSize(0, e.getParameters());
        PsiFunctionBody b = e.getBody();
        assertInstanceOf(b.getFirstChild(), PsiSwitch.class);
        PsiSwitch s = (PsiSwitch) b.getFirstChild();
        assertEquals("isBuggy()", s.getCondition().getText());
    }
}
