package com.reason.lang.rescript;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings({"ConstantConditions"})
public class FunctionParsingTest extends ResParsingTestCase {
    @Test
    public void test_anonymous_function() {
        RPsiLet e = firstOfType(parseCode("let _ = Belt.map(items, (. item) => value)"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction function = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertSize(1, function.getParameters());
        assertEquals("item", first(function.getParameters()).getText());
        assertInstanceOf(first(function.getParameters()).getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals("value", function.getBody().getText());
    }

    @Test
    public void test_brace_function() {
        RPsiLet e = firstOfType(parseCode("let x = (x, y) => { x + y }"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        assertSize(2, function.getParameters());
        assertEquals("(x, y) => { x + y }", function.getText());
        assertNotNull(function.getBody());
    }

    @Test
    public void result_function_braces() {
        RPsiLet e = firstOfType(parseCode("""
                let fn = () => {
                  let result: (. unit) => unit = (. ()) => ()
                }
                """), RPsiLet.class);
        assertNoParserError(e);

        RPsiLet el = PsiTreeUtil.findChildOfType(e, RPsiLet.class);
        assertEquals("(. unit) => unit", el.getSignature().getText());
        assertEquals("(. ()) => ()", el.getBinding().getText());
    }

    @Test
    public void test_destructuration() {
        RPsiLet e = firstOfType(parseCode("let _ = (a, {b, _}) => b"), RPsiLet.class);
        assertNoParserError(e);

        assertTrue(e.isFunction());
        assertSize(2, e.getFunction().getParameters());
    }

    @Test
    public void test_parenless_function() {
        RPsiLet e = firstOfType(parseCode("let _ = x => x + 10"), RPsiLet.class);
        assertNoParserError(e);

        assertTrue(e.isFunction());
        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();

        assertSize(1, function.getParameters());
        assertInstanceOf(first(function.getParameters()), RPsiParameterDeclaration.class);
        assertNotNull(function.getBody());
    }

    @Test
    public void test_dot_function() {
        RPsiLet e = firstOfType(parseCode("let _ = (. x) => x"), RPsiLet.class);
        assertNoParserError(e);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();

        assertSize(1, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("x", function.getBody().getText());
    }

    @Test
    public void test_inner_function() {
        RPsiLet e = firstOfType(parseCode("let _ = error => Belt.Array.mapU(errors, (. error) => error[\"message\"])"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction functionOuter = (RPsiFunction) e.getBinding().getFirstChild();
        assertEquals("Belt.Array.mapU(errors, (. error) => error[\"message\"])", functionOuter.getBody().getText());

        RPsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, RPsiFunction.class);
        assertEquals("error[\"message\"]", functionInner.getBody().getText());
    }

    @Test
    public void test_inner_function_braces() {
        RPsiLet e = firstOfType(parseCode("let _ = error => { Belt.Array.mapU(errors, (. error) => error[\"message\"]) }"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction functionOuter = (RPsiFunction) e.getBinding().getFirstChild();
        assertEquals("{ Belt.Array.mapU(errors, (. error) => error[\"message\"]) }", functionOuter.getBody().getText());

        RPsiFunction functionInner = PsiTreeUtil.findChildOfType(functionOuter, RPsiFunction.class);
        assertEquals("error[\"message\"]", functionInner.getBody().getText());
    }

    @Test
    public void test_inner_function_no_parens() {
        RPsiLet e = firstOfType(parseCode("let _ = funcall(result => 2)"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction functionInner = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertEquals("2", functionInner.getBody().getText());
    }

    @Test
    public void test_parameter_anon_function() {
        FileBase e = parseCode("describe('a', () => test('b', () => true))");
        assertNoParserError(e);

        List<RPsiFunction> funcs = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiFunction.class));
        assertSize(2, funcs);
        assertEquals("() => test('b', () => true)", funcs.get(0).getText());
        assertEquals("() => true", funcs.get(1).getText());
    }

    @Test
    public void test_option_anon_function() {
        RPsiFunction e = firstOfType(parseCode("let _ = { onCancelCreation: Some(_ => navigate(\".\")) }"), RPsiFunction.class);

        assertSize(1, e.getParameters());
        assertEquals("_", e.getParameters().getFirst().getText());
        assertEquals("navigate(\".\")", e.getBody().getText());
    }

    @Test
    public void test_parameters_named_symbols() {
        RPsiLet e = firstOfType(parseCode("let make = (~p1: (. int) => unit, ~p2: option<'a>, children) => null"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("p1", parameters.get(0).getName());
        assertSize(2, parameters.get(0).getSignature().getItems());
        assertEquals("p2", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    @Test
    public void test_parameters_named_symbols2() {
        RPsiLet e = firstOfType(parseCode(
                "let make = (~text, ~id=?, ~values=?, ~className=\"\", ~tag=\"span\", ~transform=\"unset\", ~marginLeft=\"0\", ~onClick=?, ~onKeyPress=?, _children, ) => {}"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        assertSize(10, function.getParameters());
        assertEmpty(PsiTreeUtil.findChildrenOfType(e, RPsiTernary.class));
    }

    @Test
    public void test_parameters_named_symbols3() {
        RPsiLet e = firstOfType(parseCode("let fn = (~a:t, ~b=2, ~c) => ();"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        assertSize(3, function.getParameters());
    }

    @Test
    public void test_paren_function() {
        RPsiLet e = firstOfType(parseCode("let _ = (x,y) => x + y"), RPsiLet.class);
        assertNoParserError(e);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();

        assertSize(2, function.getParameters());
        assertEquals("x", first(function.getParameters()).getText());
        assertEquals("y", second(function.getParameters()).getText());
        assertEquals("x + y", function.getBody().getText());
    }

    @Test
    public void test_unit_function() {
        RPsiLet e = firstOfType(parseCode("let _ = () => 1"), RPsiLet.class);
        assertNoParserError(e);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();

        assertSize(0, function.getParameters());
        assertEquals("1", function.getBody().getText());
    }

    @Test
    public void test_parameters_LIdent() {
        RPsiLet e = firstOfType(parseCode("let make = (id, values, children) => null;"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    @Test
    public void test_record_function() {
        RPsiLet e = firstOfType(parseCode("let make = (children) => { ...component, render: self => <div/>, }"), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunctionBody body = e.getFunction().getBody();
        RPsiFunction innerFunction = PsiTreeUtil.findChildOfType(body, RPsiFunction.class);

        assertSize(1, innerFunction.getParameters());
        assertEquals("self", first(innerFunction.getParameters()).getName());
        assertEquals("<div/>", innerFunction.getBody().getText());
    }

    @Test
    public void test_underscore() {
        RPsiLet e = firstOfType(parseCode("let onCancel = _ => setUpdatedAttribute(_ => initialAttribute)"), RPsiLet.class);
        assertNoParserError(e);

        assertTrue(e.isFunction());
        RPsiFunction f1 = e.getFunction();
        assertSize(1, f1.getParameters());
        assertEquals("setUpdatedAttribute(_ => initialAttribute)", f1.getBody().getText());
        assertEquals("_ => setUpdatedAttribute(_ => initialAttribute)", e.getBinding().getText());
        RPsiFunctionCall call = PsiTreeUtil.findChildOfType(e.getBinding(), RPsiFunctionCall.class);
        RPsiParameterReference callParam = call.getParameters().get(0);
        RPsiFunction f2 = PsiTreeUtil.findChildOfType(callParam, RPsiFunction.class);
        assertSize(1, f2.getParameters());
        assertEquals("initialAttribute", f2.getBody().getText());
    }

    @Test
    public void test_signature() {
        RPsiFunction e = firstOfType(parseCode("let _ = (~p: (option(string), option(int)) => unit) => p;"), RPsiFunction.class);
        assertNoParserError(e);

        RPsiParameterDeclaration p0 = e.getParameters().get(0);
        RPsiSignature p0s = p0.getSignature();
        assertEquals("(option(string), option(int)) => unit", p0s.getText());
        assertSize(2, p0s.getItems());
        assertEquals("(option(string), option(int))", p0s.getItems().get(0).getText());
        assertEquals("unit", p0s.getItems().get(1).getText());
    }

    @Test
    public void test_curry_uncurry() {
        RPsiFunction e = firstOfType(parseCode("let fn = p => (. p1) => p + p1"), RPsiFunction.class);
        assertNoParserError(e);

        assertEquals("p", e.getParameters().get(0).getText());
        assertEquals("(. p1) => p + p1", e.getBody().getText());
    }

    @Test
    public void test_rollback_01() {
        RPsiFunction f = firstOfType(parseCode("""
                let _ = {
                  let x = 1
                  let y = 2
                  () => 3
                }
                """), RPsiFunction.class); // test infinite rollback

        assertEquals("() => 3", f.getText());
    }

    @Test
    public void test_rollback_02() {
        RPsiLet e = firstOfType(parseCode("let _ = (() => 1, () => 2);"), RPsiLet.class); // test infinite rollback

        assertFalse(e.isFunction());

        List<RPsiFunction> efs = childrenOfType(e, RPsiFunction.class);
        assertEquals("() => 1", efs.get(0).getText());
        assertEquals("() => 2", efs.get(1).getText());
    }

    @Test
    public void test_rollback_03() {
        RPsiInnerModule e = firstOfType(parseCode("""
                module M: I with type t = t = {
                  let fn = p => (. p1) => { let _ = a let _ = x => x }
                }
                """), RPsiInnerModule.class);
        assertNoParserError(e);

        assertSize(1, e.getConstraints());
        RPsiFunction f = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertEquals("p", f.getParameters().get(0).getText());
        assertEquals("(. p1) => { let _ = a let _ = x => x }", f.getBody().getText());
    }

    @Test
    public void test_rollback_04() {
        RPsiFunction e = firstOfType(parseCode("""
                let fn = () => {
                  let _ = 1
                  (x) => 2
                }
                """), RPsiFunction.class);
        assertNoParserError(e);

        assertEquals("{\n  let _ = 1\n  (x) => 2\n}", e.getBody().getText());
    }

    @Test
    public void test_in_Some() {
        RPsiSwitch e = firstOfType(parseCode("let _ = switch (a, b) { | (Some(_), None) => Some((. ()) => 1) | (_, _) => None }"), RPsiSwitch.class);
        assertNoParserError(e);

        assertSize(2, e.getPatterns());
        RPsiFunction f = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertEquals("(. ()) => 1", f.getText());
    }

    @Test
    public void test_complex() {
        RPsiLet e = firstOfType(parseCode("""
                let _ = (. s) =>
                  memoize2(
                    x,
                    fnCall(. s),
                    ((
                      types,
                      aMap: Belt.Map.String.t<array<MyMod.Function.t>>,
                    )) => {
                      types
                      ->Belt.List.sort((type1, type2) => compare(type1.value, type2.value))
                    },
                  )
                """), RPsiLet.class);
        assertNoParserError(e);

        RPsiFunctionCall efc = (RPsiFunctionCall) e.getFunction().getBody().getFirstChild();
        assertEquals("memoize2", efc.getName());
        assertSize(3, efc.getParameters());
    }

    @Test
    public void test_ternary() {
        RPsiFunction e = firstOfType(parseCode("let fn = (p) => p == true ? Time.H12 : Time.H24\nMod.fn()"), RPsiFunction.class);

        RPsiTernary et = PsiTreeUtil.findChildOfType(e, RPsiTernary.class);
        assertEquals("p == true", et.getCondition().getText());
        assertEquals("Time.H12", et.getThenExpression().getText());
        assertEquals("Time.H24", et.getElseExpression().getText());
    }

    @Test
    public void test_current() {
        RPsiLet e = firstOfType(parseCode("let _ = () => { v.current = () => fn(p) }"), RPsiLet.class);

        List<RPsiFunction> efs = childrenOfType(e, RPsiFunction.class);
        assertEquals("() => { v.current = () => fn(p) }", efs.get(0).getText());
        assertEquals("() => fn(p)", efs.get(1).getText());
    }

    @Test
    public void test_call_uncurried() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = call1(call2(a)(. [b, c]), () => d)"), RPsiFunctionCall.class);

        assertEquals("call1", e.getName());
        assertSize(2, e.getParameters());
        RPsiParameterReference ref0 = e.getParameters().get(0);
        assertEquals("call2(a)(. [b, c])", ref0.getText());
        assertInstanceOf(ref0.getFirstChild(), RPsiFunctionCall.class);
        RPsiFunctionCall ref0c = (RPsiFunctionCall) ref0.getFirstChild();
        assertSize(1, ref0c.getParameters());
        assertEquals(("[b, c]"), ref0c.getParameters().get(0).getText());
        RPsiParameterReference ref1 = e.getParameters().get(1);
        assertEquals("() => d", ref1.getText());
        assertInstanceOf(ref1.getFirstChild(), RPsiFunction.class);
    }

    @Test
    public void test_anonymous() {
        FileBase e = parseCode("let _ = x => fn((x), (_) => 1)");

    }

    @Test
    public void test_anonymous_1() {
        FileBase e = parseCode("let _ = fn(((_), (_)) => true)");

        RPsiFunctionCall ec = firstOfType(e, RPsiFunctionCall.class);
        assertSize(1, ec.getParameters());
        RPsiFunction ecf = firstOfType(ec.getParameters().get(0), RPsiFunction.class);
        assertSize(2, ecf.getParameters());
        assertEquals("(_)", ecf.getParameters().get(0).getText());
        assertEquals("(_)", ecf.getParameters().get(1).getText());
        assertEquals("true", ecf.getBody().getText());
    }

    @Test
    public void test_nested() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = useMemo(() => (p: string) => ())"), RPsiFunctionCall.class);

        RPsiFunction ep0 = (RPsiFunction) e.getParameters().get(0).getFirstChild();
        assertSize(0, ep0.getParameters());
        assertEquals("(p: string) => ()", ep0.getBody().getText());
        RPsiFunction ep0b = (RPsiFunction) ep0.getBody().getFirstChild();
        assertSize(1, ep0b.getParameters());
        assertEquals("(p: string) => ()", ep0b.getText());
    }

    @Test
    public void test_arrow_signature() {
        RPsiLet e = firstOfType(parseCode("let fn = (p0: Js.nullable<'a>, p1: 'a => unit) => p0->Js.toOption->forEach(p1)"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction ef = e.getFunction();
        List<RPsiParameterDeclaration> efps = ef.getParameters();
        assertSize(2, efps);
        assertEquals("p0", efps.get(0).getName());
        assertEquals("p1", efps.get(1).getName());
        assertEquals("p0->Js.toOption->forEach(p1)", ef.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/113
    @Test
    public void test_GH_113() {
        RPsiFunction e = firstOfType(parseCode("let x = () => switch isBuggy() { | _ => \"buggy\" }"), RPsiFunction.class);
        assertNoParserError(e);

        assertSize(0, e.getParameters());
        RPsiFunctionBody b = e.getBody();
        assertInstanceOf(b.getFirstChild(), RPsiSwitch.class);
        RPsiSwitch s = (RPsiSwitch) b.getFirstChild();
        assertEquals("isBuggy()", s.getCondition().getText());
    }
}
